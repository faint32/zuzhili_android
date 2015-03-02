package com.zuzhili.db.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Unique;

/**
 * Created by liutao on 14-4-22.
 */
public class GroupChatInfo implements Parcelable {

    private int id;

    private long time;

    private String lastTalkJson;

    private int chatRoomNum;

    private String name;

    /** p2p chat, groupId i.e. friendId; group chat, groupId i.e. groupId in fact */
    @Unique
    private String groupId;

    /** 组用户 */
    private String contactJson;

    /** 发言人 */
    private String speakerJson;

    private String groupType;

    private String icon;

    private String identity;

    private int unreadMsgCount;

    /** 0, custom group; 2, conference */
    private String chatRoomType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getLastTalkJson() {
        return lastTalkJson;
    }

    public void setLastTalkJson(String lastTalkJson) {
        this.lastTalkJson = lastTalkJson;
    }

    public int getChatRoomNum() {
        return chatRoomNum;
    }

    public void setChatRoomNum(int chatRoomNum) {
        this.chatRoomNum = chatRoomNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getContactJson() {
        return contactJson;
    }

    public void setContactJson(String contactJson) {
        this.contactJson = contactJson;
    }

    public String getSpeakerJson() {
        return speakerJson;
    }

    public void setSpeakerJson(String speakerJson) {
        this.speakerJson = speakerJson;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public int getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(int unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }

    public String getChatRoomType() {
        return chatRoomType;
    }

    public void setChatRoomType(String chatRoomType) {
        this.chatRoomType = chatRoomType;
    }

    @Override
    public String toString() {
        return "GroupChatInfo{" +
                "id=" + id +
                ", time=" + time +
                ", lastTalkJson='" + lastTalkJson + '\'' +
                ", chatRoomNum=" + chatRoomNum +
                ", name='" + name + '\'' +
                ", groupId='" + groupId + '\'' +
                ", contactJson='" + contactJson + '\'' +
                ", speakerJson='" + speakerJson + '\'' +
                ", groupType='" + groupType + '\'' +
                ", icon='" + icon + '\'' +
                ", identity='" + identity + '\'' +
                ", unreadMsgCount=" + unreadMsgCount +
                ", chatRoomType='" + chatRoomType + '\'' +
                '}';
    }

    public static final Parcelable.Creator<GroupChatInfo> CREATOR = new Creator() {

        @Override
        public GroupChatInfo createFromParcel(Parcel source) {
            GroupChatInfo groupChatInfo = new GroupChatInfo();
            groupChatInfo.setId(source.readInt());
            groupChatInfo.setTime(source.readLong());
            groupChatInfo.setLastTalkJson(source.readString());
            groupChatInfo.setChatRoomNum(source.readInt());
            groupChatInfo.setName(source.readString());
            groupChatInfo.setGroupId(source.readString());
            groupChatInfo.setContactJson(source.readString());
            groupChatInfo.setSpeakerJson(source.readString());
            groupChatInfo.setGroupType(source.readString());
            groupChatInfo.setIcon(source.readString());
            groupChatInfo.setIdentity(source.readString());
            groupChatInfo.setUnreadMsgCount(source.readInt());
            groupChatInfo.setChatRoomType(source.readString());
            return groupChatInfo;
        }

        @Override
        public GroupChatInfo[] newArray(int size) {
            return new GroupChatInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(time);
        dest.writeString(lastTalkJson);
        dest.writeInt(chatRoomNum);
        dest.writeString(name);
        dest.writeString(groupId);
        dest.writeString(contactJson);
        dest.writeString(speakerJson);
        dest.writeString(groupType);
        dest.writeString(icon);
        dest.writeString(identity);
        dest.writeInt(unreadMsgCount);
        dest.writeString(chatRoomType);
    }

}