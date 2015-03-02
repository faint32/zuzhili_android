package com.zuzhili.bussiness.utility;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class FolderClickableSpan extends ClickableSpan {

	String type = null;
	String id = null;
	String name = null;
	String userid = null;
	
	public FolderClickableSpan(String folderType, String userId) {
		this.type = folderType;
		this.userid = userId;
	}
	
	public void setFolderType(String folderType) {
		this.type = folderType;
	}
	
	public String getFolderType() {
		return type;
	}
	
	public void setFolderId(String id) {
		this.id = id;
	}
	
	public String getFolderId() {
		return id;
	}

	public void setFolderName(String name) {
		this.name = name;
	}
	
	public String getFolderName() {
		return name;
	}

	public void setUserId(String userId) {
		this.userid = userId;
	}
	
	public String getUserId() {
		return userid;
	}
	
	public FolderClickableSpan getSelf() {
		return new FolderClickableSpan(this.type, this.userid);
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		super.updateDrawState(ds);
		ds.setUnderlineText(false);
//		ds.setColor(getResources().getColor(R.color.weibo_title));
	}

	@Override
	public void onClick(View widget) {
//		misAtClick = true;
//		Intent intent = new Intent();
//		intent.putExtra(Commstr.USERID, getUserId());
//		intent.putExtra(Commstr.FOLDER_ID, getFolderId());
//		intent.putExtra(Commstr.FOLDER_NAME, getFolderName());
//		intent.putExtra(Commstr.ACTIVIY_FROM, Commstr.ACTIVIY_FROM_USERINFO);
//		intent.putExtra(Commstr.NEWS_TYPE, type);
//		if(type.equals("9")) {
//			intent.setClass(NewsDetailActivity.this, SpaceFileListActivity.class);
//			startActivity(intent);
//		} else if(type.equals("16") || type.equals("18")) {
//			intent.setClass(NewsDetailActivity.this, MediaListActivity.class);
//			if(type.equals("16")) {
//				intent.putExtra(Commstr.NEWS_TYPE, Commstr.NEWS_TYPE_MUSIC);
//			} else {
//				intent.putExtra(Commstr.NEWS_TYPE, Commstr.NEWS_TYPE_VEDIO);
//			}
//			startActivity(intent);
//		}
	}

}
