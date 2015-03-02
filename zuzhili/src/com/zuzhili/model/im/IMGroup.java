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

import com.hisun.phone.core.voice.model.Response;

public class IMGroup extends Response {
	
	public static final int MODEL_GROUP_PUBLIC = 0x0;
	public static final int MODEL_GROUP_AUTHENTICATION = 0x1;
	public static final int MODEL_GROUP_PRIVATE = 0x2;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1317673848969906965L;

    private String id;          // 群组id
	private String groupId;     // 对应云通讯群组id
    private String name;        // 群组名字
    private String type;        // 群组类型 0：临时组 1：普通组 2：VIP组
    private String declared;    // 群组公告
    private String createdDate; // 该群组的创建时间
    private String permission;  // 申请加入模式 0：默认直接加入1：需要身份验证
    private String owner;       // 群创建者
    private String count;       // 群组人数
    private String listId;      // 群组所在社区id
    private String lastMessage; // 群组最后的发言

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDeclared() {
        return declared;
    }

    public void setDeclared(String declared) {
        this.declared = declared;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public IMGroup() {
    }

    public IMGroup(String id, String groupId, String name, String type, String declared, String createdDate, String permission, String owner, String count, String listId, String lastMessage) {
        this.id = id;
        this.groupId = groupId;
        this.name = name;
        this.type = type;
        this.declared = declared;
        this.createdDate = createdDate;
        this.permission = permission;
        this.owner = owner;
        this.count = count;
        this.listId = listId;
        this.lastMessage = lastMessage;
    }
}
