package com.zuzhili.bussiness.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.widget.EditText;

public class SmsReceiverHelper extends ContentObserver{
	//获取相应的Activity
	private Activity activity;
	//获取相应的输入框
	private EditText input_code;
	private String STR_PATTERN_AT = "([\\d]{6}).*组织力";
	//正则表达式验证格式是否正确
	private Pattern mPatternAt = Pattern.compile(STR_PATTERN_AT);
	
	private Cursor cursor = null;
	public SmsReceiverHelper(Handler handler, Activity activity, EditText view) {
		super(handler);
		this.activity = activity;
		this.input_code = view;
	}
	
	@Override
	public void onChange(boolean selfChange) {
		doReadSMS();
		super.onChange(selfChange);
	}
    //读取消息
	public void doReadSMS() {
		cursor = activity.managedQuery(Uri.parse("content://sms/inbox"),
				new String[] { "_id", "address", "body", "read" },
				"read=?", new String[]{"0"}, "date desc");
		if (cursor != null) {
			if (cursor.moveToFirst()) {

				String smsbody = cursor
						.getString(cursor.getColumnIndex("body"));
				
				Matcher m = mPatternAt.matcher(smsbody);
				boolean result = m.find();
				if (result) {
					input_code.setText(m.group(1));
										}
							}
							}
							}
	
}
