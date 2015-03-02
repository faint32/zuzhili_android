package com.zuzhili.framework.utils;

import java.io.File;

import com.zuzhili.ui.activity.BaseActivity;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

public class Downloader {
	public final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
	private Context mContext;
	private DownloadManager mDownloadManager;
	private long lastDownloadId;	// 上次下载任务id
	
	public Downloader(Context context) {
		this.mContext = context;
		this.mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
	}
	
	/**
	 * 将下载任务加入到下载列表中
	 * @param url
	 */
	public void enqueue(String url, String fileName) {
		Uri uri = Uri.parse(url); 
		Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir();
		lastDownloadId = mDownloadManager.enqueue(new Request(uri)
				.setAllowedNetworkTypes(Request.NETWORK_MOBILE
				| Request.NETWORK_WIFI)
				.setAllowedOverRoaming(false)
				.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName));
		mContext.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));   
        mContext.getContentResolver().registerContentObserver(CONTENT_URI, true, new DownloadChangeObserver(null));
	}
	
	public class DownloadChangeObserver extends ContentObserver {

		public DownloadChangeObserver(Handler handler) {
			super(handler);
		}
		
		@Override
		public void onChange(boolean selfChange) {
			queryDownloadStatus();
		}
		
	}
	
	// 监听下载完成的广播
	private BroadcastReceiver receiver = new BroadcastReceiver() {     

		@Override
		public void onReceive(Context context, Intent intent) {
			//这里可以取得下载的id，这样就可以知道哪个文件下载完成了。适用与多个下载任务的监听
			Log.v("liu", ""+intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0));
			if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
				long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				if(downloadId == lastDownloadId) {
					if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
						File apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "zuzhili.apk");
						if(!apkFile.exists()) {
							return;
						} else {
							Intent installIntent = new Intent(Intent.ACTION_VIEW);
							installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							installIntent.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
							mContext.startActivity(installIntent);
							((BaseActivity) mContext).finish();
						}
					}
				}
			}
		}     
    };
    
    private void queryDownloadStatus() {     
        DownloadManager.Query query = new DownloadManager.Query();     
        query.setFilterById(lastDownloadId);     
        Cursor c = mDownloadManager.query(query);     
        if(c != null && c.moveToFirst()) {     
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));     
            int reasonIdx = c.getColumnIndex(DownloadManager.COLUMN_REASON);    
            int titleIdx = c.getColumnIndex(DownloadManager.COLUMN_TITLE);    
            int fileSizeIdx = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);        
            int bytesDLIdx = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);    
            String title = c.getString(titleIdx);
            int fileSize = c.getInt(fileSizeIdx);
            int bytesDL = c.getInt(bytesDLIdx);  
              
            // Translate the pause reason to friendly text.    
            int reason = c.getInt(reasonIdx);
            StringBuilder sb = new StringBuilder();    
            sb.append(title).append("\n");
            sb.append("Downloaded ").append(bytesDL).append(" / " ).append(fileSize);
              
            // Display the status     
            Log.d("tag", sb.toString());    
            switch(status) {
            case DownloadManager.STATUS_PAUSED:     
                Log.v("tag", "STATUS_PAUSED");    
            case DownloadManager.STATUS_PENDING:     
                Log.v("tag", "STATUS_PENDING");    
            case DownloadManager.STATUS_RUNNING:     
                //正在下载，不做任何事情    
                Log.v("tag", "STATUS_RUNNING");    
                break;     
            case DownloadManager.STATUS_SUCCESSFUL:     
                //完成    
                Log.v("tag", "下载完成");    
                break;  
            case DownloadManager.STATUS_FAILED:     
                //清除已下载的内容，重新下载    
                Log.v("tag", "STATUS_FAILED");    
                mDownloadManager.remove(lastDownloadId);     
                break;     
            }
        }    
        c.close();
    }
}
