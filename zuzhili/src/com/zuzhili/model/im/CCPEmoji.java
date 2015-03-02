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
 */package com.zuzhili.model.im;

/**
* <p>Title: CCPEmoji</p>
* <p>Description: </p>
* <p>Company: http://www.cloopen.com/</p>
* @author  Jorstin Chan
* @version 3.6
* @date 2013-12-26
 */
public class CCPEmoji {

    /**
     * Expression corresponding resource picture ID
     */
    private int id;

    /**
     * Expression resources corresponding text description
     */
    private String EmojiDesc;

    /**
     * File name expression resources
     */
    private String EmojiName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id=id;
    }

	public String getEmojiDesc() {
		return EmojiDesc;
	}

	public void setEmojiDesc(String emojiDesc) {
		EmojiDesc = emojiDesc;
	}

	public String getEmojiName() {
		return EmojiName;
	}

	public void setEmojiName(String emojiName) {
		EmojiName = emojiName;
	}

    
}
