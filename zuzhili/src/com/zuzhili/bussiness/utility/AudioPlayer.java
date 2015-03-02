package com.zuzhili.bussiness.utility;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class AudioPlayer implements OnCompletionListener{

	public interface PlayerCallBack{
		void onStop();
	}

	private MediaPlayer mMediaPlayer;

	private static AudioPlayer mInstance;

	private PlayerCallBack mPlayerCallBack;
	
	private String mPath;
	
	public static synchronized AudioPlayer getInstance() {
		if (mInstance == null) {
			mInstance = new AudioPlayer();
		}
		return mInstance;
	}

	private AudioPlayer() {
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnCompletionListener(this);
	}

	public void play(String path , PlayerCallBack back) {
		if (mMediaPlayer.isLooping() || mMediaPlayer.isPlaying()) {
			if(mPlayerCallBack != null && !mPlayerCallBack.equals(back)){
				mPlayerCallBack.onStop();
			}
			mMediaPlayer.stop();
		}
		if(path != null && path.equals(mPath)){
			mPath = null;
			return;
		}
		try {
			mPlayerCallBack = back;
			mMediaPlayer.reset();
			mPath = path;
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
		}catch (Exception e) {
			if(mPlayerCallBack != null){
				mPlayerCallBack.onStop();
			}
			mMediaPlayer.release();
			mMediaPlayer = null;
			mMediaPlayer = new MediaPlayer();
		}
	}

	public void releaseAll() {
		mMediaPlayer.release();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mPath = null;
		if(mPlayerCallBack != null){
			mPlayerCallBack.onStop();
		}
	}

	public void stopAll() {
		mPath = null;
		if (mMediaPlayer.isLooping() || mMediaPlayer.isPlaying()) {
			if(mPlayerCallBack != null){
				mPlayerCallBack.onStop();
			}
			mMediaPlayer.stop();
		}
	}

	public void stopIfequal(String path) {
		if(path != null && path.equals(mPath)){
			mPath = null;
			if (mMediaPlayer.isLooping() || mMediaPlayer.isPlaying()) {
				if(mPlayerCallBack != null){
					mPlayerCallBack.onStop();
				}
				mMediaPlayer.stop();
			}
		}
	}
}
