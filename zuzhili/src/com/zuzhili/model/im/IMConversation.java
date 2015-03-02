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
package com.zuzhili.model.im;

/**
 * IM message session list (IM point to point, group, system validation message)
 * @version Time: 2013-7-22
 */
public class IMConversation {

	public static final int CONVER_TYPE_MESSAGE = 0x1;
	public static final int CONVER_TYPE_SYSTEM = 0x2;
	
	private String id;                      // 可以是联系人的云通讯id或者云通讯群组id
	private String contact;                 // 联系人云通讯id
	private String dateCreated;             // 创建日期
	private String unReadNum;               // 未读消息数目
	private String recentMessage;           // 最后一条消息
	private int type;                       // 会话类型 CONVER_TYPE_MESSAGE, CONVER_TYPE_SYSTEM
    private String groupUserCount;          // 群组用户人数
    private String userAvatar;              // 头像
    private String ids;                     // 用户ids
    private String userName;                // 用户名
    private String listId;                  // 社区id
    private String groupId;                 // 本地群组id
    private String owner;                   // 群组创建者voip id
    private String groupType;               // 群组类型

	public String getId() {
        return id;
    }

	public void setId(String id) {
		this.id = id;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getUnReadNum() {
		return unReadNum;
	}

	public void setUnReadNum(String unReadNum) {
		this.unReadNum = unReadNum;
	}

	public String getRecentMessage() {
		return recentMessage;
	}

	public void setRecentMessage(String recentMessage) {
		this.recentMessage = recentMessage;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

    public String getGroupUserCount() {
        return groupUserCount;
    }

    public void setGroupUserCount(String groupUserCount) {
        this.groupUserCount = groupUserCount;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public IMConversation(String id, String contact, String dateCreated,
                          String unReadNum, String recentMessage, int type,
                          String groupUserCount, String userAvatar, String ids,
                          String userName, String listId, String groupId, String owner, String groupType) {
		super();
		this.id = id;
		this.contact = contact;
		this.dateCreated = dateCreated;
		this.unReadNum = unReadNum;
		this.recentMessage = recentMessage;
		this.type = type;
        this.groupUserCount = groupUserCount;
        this.userAvatar = userAvatar;
        this.ids = ids;
        this.userName = userName;
        this.listId = listId;
        this.groupId = groupId;
        this.owner = owner;
        this.groupType = groupType;
	}

	public IMConversation() {
		super();
	}

}
