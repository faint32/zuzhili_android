package com.zuzhili.bussiness.utility;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class PhotoClickableSpan extends ClickableSpan {

	String id = null;
	String name = null;

	public void setAlbumeID(String id) {
		this.id = id;
	}

	public void setAlbumeName(String name) {
		this.name = name;
	}

	public PhotoClickableSpan getSelf() {
		return new PhotoClickableSpan();
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
//		Intent it = new Intent(NewsDetailActivity.this, AlbumeViewer.class);
//		it.putExtra("albumeid", id);
//		it.putExtra("albumename", name);
//		NewsDetailActivity.this.startActivity(it);
	}

}
