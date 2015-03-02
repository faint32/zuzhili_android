package com.zuzhili.bussiness.utility;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;
/**
 * @Title: VedioMiniThumb.java
 * @Package com.zuzhili.mediaselect.util
 * @Description: 视频缩略图
 * @author gengxin
 * @date 2013-4-25 下午16:32:12
 */
public class VedioMiniThumb {
	private static final String TAG = "ImageManager";
	private static VedioMiniThumb sInstance = null;

	public enum DataLocation {
		NONE, INTERNAL, EXTERNAL, ALL
	};

	static final public int INCLUDE_VIDEOS = (1 << 2);
	static final public int SORT_ASCENDING = 1;
	static final public int SORT_DESCENDING = 2;
	private static Uri sVideoStorageURI = Uri.parse("content://media/external/video/media");
	// For getting image
	public static final int sBytesPerMiniThumb = 10000;
	static final public byte[] sMiniThumbData = new byte[sBytesPerMiniThumb];
	// For saving image
	private static final int MINI_THUMB_TARGET_WIDTH = 120;
	private static final int MINI_THUMB_TARGET_HEIGHT = 90;

	public static VedioMiniThumb instance() {
		if (sInstance == null) {
			sInstance = new VedioMiniThumb();
		}
		return sInstance;
	}

	// ctx = VideoPlayerActivity; cr = getContentResolver()
	public VedioListContainer allVedios(Context ctx, ContentResolver cr, int inclusion, int sort) {
		if (cr == null) {
			return null;
		} else {
			return new VedioListContainer(ctx, cr, sVideoStorageURI, sort);
		}
	}

	static public byte[] miniThumbData(Bitmap source) {
		if (source == null) {
			return null;
		}
		// Zip Bitmap to thumbnail size
		Bitmap miniThumbnail = extractMiniThumb(source, MINI_THUMB_TARGET_WIDTH, MINI_THUMB_TARGET_HEIGHT);
		java.io.ByteArrayOutputStream miniOutStream = new java.io.ByteArrayOutputStream();
		miniThumbnail.compress(Bitmap.CompressFormat.JPEG, 75, miniOutStream);
		miniThumbnail.recycle();
		try {
			miniOutStream.close();
			byte[] data = miniOutStream.toByteArray();
			return data;
		} catch (java.io.IOException ex) {
			Log.e(TAG, "got exception ex " + ex);
		}
		return null;
	}

	public static boolean isVideoMimeType(String mimeType) {
		return mimeType.startsWith("video/");
	}

	static public Bitmap extractMiniThumb(Bitmap source, int width, int height) {
		if (source == null) {
			return null;
		}
		float scale;
		if (source.getWidth() < source.getHeight()) {
			scale = width / (float) source.getWidth();
		} else {
			scale = height / (float) source.getHeight();
		}
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);
		Bitmap miniThumbnail = transform(matrix, source, width, height, false);

		if (miniThumbnail != source) {
			source.recycle();
		}
		return miniThumbnail;
	}

	public static Bitmap transform(Matrix scaler, Bitmap source,
			int targetWidth, int targetHeight, boolean scaleUp) {
		int deltaX = source.getWidth() - targetWidth;
		int deltaY = source.getHeight() - targetHeight;

		if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
			Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight,Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b2);

			int deltaXHalf = Math.max(0, deltaX / 2);
			int deltaYHalf = Math.max(0, deltaY / 2);
			Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf + Math.min(targetWidth, source.getWidth()), deltaYHalf + Math.min(targetHeight, source.getHeight()));
			int dstX = (targetWidth - src.width()) / 2;
			int dstY = (targetHeight - src.height()) / 2;
			Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight - dstY);

			c.drawBitmap(source, src, dst, null);
			return b2;
		}

		float bitmapWidthF = source.getWidth();
		float bitmapHeightF = source.getHeight();

		float bitmapAspect = bitmapWidthF / bitmapHeightF;
		float viewAspect = (float) targetWidth / (float) targetHeight;

		if (bitmapAspect > viewAspect) {
			float scale = targetHeight / bitmapHeightF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			} else {
				scaler = null;
			}
		} else {
			float scale = targetWidth / bitmapWidthF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			} else {
				scaler = null;
			}
		}

		Bitmap b1;
		if (scaler != null) {
			// this is used for minithumb and crop, so we want to filter here.
			b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
					source.getHeight(), scaler, true);
		} else {
			b1 = source;
		}

		int dx1 = Math.max(0, b1.getWidth() - targetWidth);
		int dy1 = Math.max(0, b1.getHeight() - targetHeight);

		Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth,
				targetHeight);

		if (b1 != source) {
			b1.recycle();
		}

		return b2;
	}
}
