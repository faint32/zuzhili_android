package com.zuzhili.ui.activity.more;

import android.os.Bundle;
import android.view.Menu;

import com.zuzhili.R;
import com.zuzhili.ui.activity.BaseActivity;

public class AboutUsActivity extends BaseActivity  implements BaseActivity.TimeToShowActionBarCallback{
	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);
		super.setContentView(R.layout.activity_aboutus);
	}
	
	@Override
	public boolean performClickOnLeft() {
		finish();
		return super.performClickOnLeft();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		super.initActionBar(R.drawable.icon_back, 0, getString(R.string.aboutus), false);
		return true;
	}

	@Override
	public boolean showCustomActionBar() {
		super.initActionBar(R.drawable.icon_back, 0, getString(R.string.aboutus), false);
		return true;
	}
}
