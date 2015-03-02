/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.cloopen.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.zuzhili.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.framework.im.EmoticonUtil;


/**
 * 
 * @ClassName: CCPTextView.java
 * @Description: TODO
 * @author Jorstin Chan 
 * @date 2014-1-3
 * @version 3.6
 */
public class FaceTextView extends TextView {

	/**
	 * @param context
	 */
	public FaceTextView(Context context) {
		super(context);
	}
	
	/**
	 * @param context
	 * @param attrs
	 */
	public FaceTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public FaceTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setEmojiText(String text) {
		setText(TextUtil.formatImage(text,getContext()));
	}
	
}
