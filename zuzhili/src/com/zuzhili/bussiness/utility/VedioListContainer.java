package com.zuzhili.bussiness.utility;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Video;
import android.util.Log;

import com.zuzhili.bussiness.helper.VedioHelper;
/**
 * Created by addison on 2/21/14.
 */
public class VedioListContainer {
	 private static final String TAG = "VedioListContainer";
	    private final String[] sProjection = new String[] {
	            Video.Media._ID, Video.Media.DATA, Video.Media.DATE_MODIFIED, Video.Media.BUCKET_ID,
	            Video.Media.TITLE, Video.Media.MINI_THUMB_MAGIC, Video.Media.MIME_TYPE,
	            Video.Media.DURATION, Video.Media.SIZE, Video.Media.DATE_ADDED,
	    };
	    
	    
	    final public int INDEX_ID = indexOf(sProjection, Video.Media._ID);
	    final public int INDEX_DATA = indexOf(sProjection, Video.Media.DATA);
	    final public int INDEX_DATE_MODIFIED = indexOf(sProjection, Video.Media.DATE_MODIFIED);
	    final public int INDEX_BUCKET_ID = indexOf(sProjection, Video.Media.BUCKET_ID);
	    final public int INDEX_TITLE = indexOf(sProjection, Video.Media.TITLE);
	    final public int INDEX_MINI_THUMB_MAGIC = indexOf(sProjection, Video.Media.MINI_THUMB_MAGIC);
	    final public int INDEX_MIME_TYPE = indexOf(sProjection, Video.Media.MIME_TYPE);
	    final public int INDEX_DURATION = indexOf(sProjection, Video.Media.DURATION);
	    final public int INDEX_SIZE = indexOf(sProjection, Video.Media.SIZE);
	    final public int INDEX_DATE_ADDED = indexOf(sProjection, Video.Media.DATE_ADDED);

	    Context mContext;
	    ContentResolver mContentResolver;
	    Uri mStroageUri;
	    int mSort;
	    Cursor mCursor;
	    public HashMap<Long, VedioHelper> mCache = new HashMap<Long, VedioHelper>();
	    public RandomAccessFile mMiniThumbData;

	    public VedioListContainer(Context ctx, ContentResolver cr, Uri uri, int sort) {
	        mContext = ctx;
	        mContentResolver = cr;
	        mStroageUri = uri;
	        mSort = sort;

	        mCursor = createCursor();
	        if (mCursor == null) {
	            throw new UnsupportedOperationException();
	        }

	        if (mCursor != null && mCursor.moveToFirst()) {
	            int row = 0;

	            do {
	                long imageId = mCursor.getLong(INDEX_ID);
	                mCache.put(imageId, new VedioHelper(imageId, mContentResolver, this, row++));
	            } while (mCursor.moveToNext());
	        }
	    } // end VideoList

	    private Cursor createCursor() {
	        Cursor c = Images.Media.query(mContentResolver, mStroageUri, sProjection, null, null, sortOrder());
	        return c;
	    }

	    public Cursor getCursor() {
	        if(mCursor == null || mCursor.isClosed()){
	            mCursor = createCursor();
	        }
	        synchronized (mCursor) {
	            return mCursor;
	        }
	    }

	    private void requery() {
	        if(mCursor == null || mCursor.isClosed()){
	            mCursor = createCursor();
	        }
	        mCursor.requery();
	    }

	    private void refreshCache() {
	        Cursor c = getCursor();

	        synchronized (c) {
	            try {
	                int i = 0;
	                c.moveToFirst();
	                while (!c.isAfterLast()) {
	                    long idFromCursor = c.getLong(INDEX_ID);

	                    if (mCache.get(idFromCursor) != null) {
	                        mCache.get(idFromCursor).mCursorRow = i++;
	                    }

	                    c.moveToNext();
	                }
	            } catch (Exception ex) {
	            }
	        }
	    }

	    public int getCount() {
	        Cursor c = getCursor();
	        synchronized (c) {
	            try {
	                return c.getCount();
	            } catch (Exception ex) {
	            }
	            return 0;
	        }
	    }

	    public VedioHelper getImageAt(int i) {
	        Cursor c = getCursor();
	        synchronized (c) {
	            boolean moved;

	            try {
	                moved = c.moveToPosition(i);
	            } catch (Exception ex) {
	                return null;
	            }

	            if (moved) {
	                try {
	                    long id = c.getLong(INDEX_ID);

                        VedioHelper img = mCache.get(id);
	                    if (img == null) {
	                        img = new VedioHelper(id, mContentResolver, this, i);
	                        mCache.put(id, img);
	                    }
	                    return img;
	                } catch (Exception ex) {
	                    return null;
	                }
	            } else {
	                return null;
	            }
	        }
	    }

	    public Uri contentUri(long id) {
	        try {
	            long existingId = ContentUris.parseId(mStroageUri);
	            if (existingId != id) {
	                Log.e(TAG, "id mismatch");
	            }
	            return mStroageUri;
	        } catch (NumberFormatException ex) {
	            return ContentUris.withAppendedId(mStroageUri, id);
	        }
	    }

	    public boolean removeImage(VedioHelper image) {
	        if (null == image)
	            return false;

	        if (isIdExist(image.mId)) {
	            if (PublicTools.isFileExist(image.getMediapath())) {
	                File file = new File(image.getMediapath());
	                if (!file.delete()) {
	                    Log.i(TAG, "delete file failure");
	                    return false;
	                }
	            }

	            Uri u = image.fullSizeImageUri();
	            mContentResolver.delete(u, null, null);
	            image.onRemove();
	            requery();
	            refreshCache();
	            return true;
	        } else {
	            return false;
	        }
	    }

	    public boolean renameImage(Context context, VedioHelper image, String name) {

	        if (!isIdExist(image.mId)) {
	            return false;
	        }

	        String oldpath = image.getMediapath();
	        String newPath = PublicTools.replaceFilename(oldpath, name);

	        if (name != null && name.length() > 0) {
	            // rename file first
	            File oldfile = new File(oldpath);
	            File newFile = new File(newPath);
	            if (!oldfile.renameTo(newFile)) {
	                return false;
	            }
	            String id= String.valueOf(image.getMediaId()).toString();
	            ContentValues values = new ContentValues();
	            values.put(MediaColumns.TITLE, name);
	            values.put(MediaColumns.DATA, newPath);
	            String fileName = newPath.substring(newPath.lastIndexOf("/") + 1, newPath.length());
	            values.put(MediaColumns.DISPLAY_NAME, fileName);
	            mContentResolver.update(Video.Media.EXTERNAL_CONTENT_URI, values,
	                    BaseColumns._ID + "=?", new String[] {
	            		Integer.valueOf(image.getMediaId()).toString()
	                    });
	            
	            requery();

	            return true;
	        } else
	            return false;
	    }

	    public boolean isFilenameExist(VedioHelper image, String name) {
	        String newPath = PublicTools.replaceFilename(image.getMediapath(), name);
	        boolean exist = false;

	        Cursor c = getCursor();
	        synchronized (c) {
	            try {
	                c.moveToFirst();
	                while (!c.isAfterLast()) {
	                    String oldpath = c.getString(INDEX_DATA);
	                    if (PublicTools.getBucketId(newPath) == PublicTools.getBucketId(oldpath)) {
	                        exist = true;
	                        break;
	                    }
	                    c.moveToNext();
	                }
	            } catch (Exception ex) {
	            }
	        }

	        return exist;
	    }

	    private boolean isIdExist(long id) {
	        boolean exist = false;
	        Cursor c = getCursor();
	        synchronized (c) {
	            try {
	                c.moveToFirst();
	                while (!c.isAfterLast()) {
	                    long idFromCursor = c.getLong(INDEX_ID);

	                    if (idFromCursor == id) {
	                        exist = true;
	                        break;
	                    }
	                    c.moveToNext();
	                }
	            } catch (Exception ex) {
	            }
	        }

	        return exist;
	    }

	    private static int indexOf(String[] array, String s) {
	        for (int i = 0; i < array.length; i++) {
	            if (array[i].equals(s)) {
	                return i;
	            }
	        }
	        return -1;
	    }

	    private String sortOrder() {
	        return MediaColumns.DATE_ADDED + " DESC ";
	    }

	    public void onDestory() {
	        try {
	            if (mMiniThumbData != null)
	                mMiniThumbData.close();
	            if (null != this.mCursor) {
	                this.mCursor.close();
	                this.mCursor = null;
	            }
	        } catch (IOException ex) {
	        }
	    }
}
