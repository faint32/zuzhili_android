package com.zuzhili.bussiness.helper;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.bussiness.utility.PublicTools;
import com.zuzhili.bussiness.utility.VedioListContainer;

import java.text.NumberFormat;
import java.util.Formatter;
import java.util.Hashtable;
import java.util.Locale;

/**
 * Created by addison on 2/21/14.
 */
public class VedioHelper {
    protected ContentResolver mContentResolver;
    public int mCursorRow;
    public long mId, mMiniThumbMagic;
    public VedioListContainer mContainer;
    private int mThumbnailState;
    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    java.util.Random mRandom = new java.util.Random(System.currentTimeMillis());

    public VedioHelper(long id, ContentResolver cr, VedioListContainer container, int row) {
        mId = id;
        mContentResolver = cr;
        mContainer = container;
        mCursorRow = row;
        mThumbnailState = PublicTools.THUMBNAIL_EMPTY;
        mMiniThumbMagic = makeThumbMagic();
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
    }

    public String getTitle() {
        String name = null;
        Cursor c = getCursor();
        synchronized (c) {
            if (c.moveToPosition(getRow())) {
                name = c.getString(mContainer.INDEX_TITLE);
            }
        }
        return (name != null && name.length() > 0) ? name : String.valueOf(mId);
    }
    public String getMediapath() {
        String path = null;
        Cursor c = getCursor();
        synchronized (c) {
            if (c.moveToPosition(getRow())) {
                path = c.getString(mContainer.INDEX_DATA);
            }
        }
        return path;
    }
    public long getBucketId() {
        long bucket_id = 0;
        Cursor c = getCursor();
        synchronized (c) {
            if (c.moveToPosition(getRow())) {
                bucket_id = c.getLong(mContainer.INDEX_BUCKET_ID);
            }
        }
        return bucket_id;
    }
    public long getDateModified() {
        long dateModified = 0;
        Cursor c = getCursor();
        synchronized (c) {
            if (c.moveToPosition(getRow())) {
                dateModified = c.getLong(mContainer.INDEX_DATE_MODIFIED);
            }
        }
        return dateModified;
    }
    public String getDuration() {
        long duration = 0;
        Cursor c = getCursor();
        synchronized (c) {
            if (c.moveToPosition(getRow())) {
                duration = c.getLong(mContainer.INDEX_DURATION);
            }
        }

        int totalSeconds = (int) (duration / 1000);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60);
        int hours = (minutes / 60);

//	        Date date = new Date(duration);
//			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
//			return sdf.format(date);

        mFormatBuilder.setLength(0);
        if(hours > 0) {
            minutes = minutes - (hours * 60);
            return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
        }
        if (hours <= 0 && minutes > 0) {
            hours = 0;
            return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
//	            return mFormatter.format("%d:%02d", minutes, seconds).toString();
        } else {
            if (seconds == 0)
                seconds = 1;
            hours = minutes = 0;
            return mFormatter.format("%02d:%02d:%02d", hours, minutes, seconds).toString();
//	            return mFormatter.format("%02d", seconds).toString();
        }
    }
    public int getMediaId() {
        int id = 0;
        Cursor c = getCursor();
        synchronized (c) {
            if (c.moveToPosition(getRow())) {
                id = c.getInt(mContainer.INDEX_ID);
            }
        }

        return id;
    }
    public String getSize() {
        long size = 0;
        Cursor c = getCursor();
        synchronized (c) {
            if (c.moveToPosition(getRow())) {
                size = c.getLong(mContainer.INDEX_SIZE);
            }
        }

        double mSize = (size / 1024.0);
        String flag;

        LogUtils.e("size" + mSize);
        if (mSize > 1024.0) {
            mSize = mSize / 1024.0;
            flag = "MB";
        } else {
            flag = "KB";
        }

        NumberFormat formater = NumberFormat.getInstance();
        formater.setMaximumFractionDigits(1);
        return formater.format(mSize) + flag;
    }

    public Bitmap miniThumbBitmap(boolean decodeOnly, Hashtable<Integer, Bitmap> ht, Bitmap defaultBitmap) {
        Bitmap mThumbnail = null;
        if(ht != null) {
            mThumbnail = ht.get(new Integer(getMediaId()));
        }

        if(mThumbnail == null) {
            mThumbnail = MediaStore.Video.Thumbnails.getThumbnail(mContentResolver, getMediaId(), MediaStore.Video.Thumbnails.MICRO_KIND, null);

            if(mThumbnail == null) {
                mThumbnail = defaultBitmap;
            }
            if(ht != null) {
                ht.put(new Integer(getMediaId()), mThumbnail);
            }
        }

        if(mThumbnail != null) {
            mThumbnailState = PublicTools.THUMBNAIL_PREPARED;
        } else {
            mThumbnailState = PublicTools.THUMBNAIL_CORRUPTED;
        }
        return mThumbnail;
    }

    // help functions
    private long makeThumbMagic() {
        Cursor c = getCursor();
        synchronized (c) {
            String path = c.getString(mContainer.INDEX_DATA);
            long lastModify = c.getLong(mContainer.INDEX_DATE_MODIFIED);
            if (lastModify == 0) {
                lastModify = mRandom.nextLong();
            }
            return (path.hashCode() + lastModify);
        }
    }

    public int getRow() {
        return mCursorRow;
    }
    Cursor getCursor() {
        return mContainer.getCursor();
    }
    public VedioListContainer getContainer() {
        return mContainer;
    }
    public long fullSizeImageId() {
        return mId;
    }
    public Uri fullSizeImageUri() {
        return mContainer.contentUri(mId);
    }
    public void onRemove() {
        mContainer.mCache.remove(mId);
    }
    public int getThumbnailState() {
        return mThumbnailState;
    }

}
