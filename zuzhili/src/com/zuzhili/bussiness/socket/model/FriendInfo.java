package com.zuzhili.bussiness.socket.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Transient;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.bussiness.utility.pinyin.CharacterParser;
import com.zuzhili.model.SortModel;

import java.io.Serializable;

/**
 * Created by liutao on 14-4-12.
 */
public class FriendInfo extends SortModel {

    public static final String OFFLINE = "0";

    public static final String ONLINE = "1";

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser = CharacterParser.getInstance();

    public int _id;

    /** user id */
    private String uid;

    private String userName;

    /** user avatar */
    private String userAvatar;

    /** online status, 0 indicates offline, 1 indicates online */
    private String userStatus;

    private String lastTalkTime;

    /** online type, 0 web , 1 android, 2 ios */
    private String onLineType;

    private String identity;

    /** manager flag, 0 false, 1 true */
    private String managerFlag;

    /** creator flag, 0 false, 1 true */
    private String creatorFlag;

    /** mute flag, 0 false, 1 true */
    private String muteFlag;

    /** join time */
    private String joinTime;

    /** sex */
    private String sex;

    /** groups which user joined, separated with comma, return 0 if user not join any group */
    private String inGroups;

    private String sortKey;

    /** 根据此标志位判断是否是是聊天用户的头像、或者加人、或者踢人；0 表示头像， 1 表示家人， 2 表示踢人 */
    @Transient
    private int userFlag;

    @Transient
    private boolean showLeftTopRemoveUserIcon;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getLastTalkTime() {
        return lastTalkTime;
    }

    public void setLastTalkTime(String lastTalkTime) {
        this.lastTalkTime = lastTalkTime;
    }

    public String getOnLineType() {
        return onLineType;
    }

    public void setOnLineType(String onLineType) {
        this.onLineType = onLineType;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getManagerFlag() {
        return managerFlag;
    }

    public void setManagerFlag(String managerFlag) {
        this.managerFlag = managerFlag;
    }

    public String getCreatorFlag() {
        return creatorFlag;
    }

    public void setCreatorFlag(String creatorFlag) {
        this.creatorFlag = creatorFlag;
    }

    public String getMuteFlag() {
        return muteFlag;
    }

    public void setMuteFlag(String muteFlag) {
        this.muteFlag = muteFlag;
    }

    public String getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(String joinTime) {
        this.joinTime = joinTime;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getInGroups() {
        return inGroups;
    }

    public void setInGroups(String inGroups) {
        this.inGroups = inGroups;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public int getUserFlag() {
        return userFlag;
    }

    public void setUserFlag(int userFlag) {
        this.userFlag = userFlag;
    }

    public boolean isShowLeftTopRemoveUserIcon() {
        return showLeftTopRemoveUserIcon;
    }

    public void setShowLeftTopRemoveUserIcon(boolean showLeftTopRemoveUserIcon) {
        this.showLeftTopRemoveUserIcon = showLeftTopRemoveUserIcon;
    }

    public static final Parcelable.Creator<FriendInfo> CREATOR = new Creator() {

        @Override
        public FriendInfo createFromParcel(Parcel source) {
            FriendInfo friendInfo = new FriendInfo();
            friendInfo.setSortKey(source.readString());
            friendInfo.set_id(source.readInt());
            friendInfo.setUid(source.readString());
            friendInfo.setUserName(source.readString());
            friendInfo.setUserAvatar(source.readString());
            friendInfo.setUserStatus(source.readString());
            friendInfo.setLastTalkTime(source.readString());
            friendInfo.setOnLineType(source.readString());
            friendInfo.setIdentity(source.readString());
            friendInfo.setUserFlag(source.readInt());
            return friendInfo;
        }

        @Override
        public FriendInfo[] newArray(int size) {
            return new FriendInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sortKey);
        dest.writeInt(_id);
        dest.writeString(uid);
        dest.writeString(userName);
        dest.writeString(userAvatar);
        dest.writeString(userStatus);
        dest.writeString(lastTalkTime);
        dest.writeString(onLineType);
        dest.writeString(identity);
        dest.writeInt(userFlag);
    }

    public FriendInfo parse(String[] result) {
        int index = 1;
        this.setUid(result[index++]);
        this.setUserName(result[index++]);
        this.setUserAvatar(result[index++]);
        this.setSex(result[index++]);
        this.setOnLineType(result[index++]);
        this.setUserStatus(result[index++]);
        this.setInGroups(result[index++]);
        this.setUserFlag(0);
        updateSortKey(this);
        return this;
    }

    private void updateSortKey(FriendInfo friendInfo) {
        //汉字转换成拼音
        String pinyin = characterParser.getSelling(TextUtil.processNullString(friendInfo.getUserName()));
        String sortString;
        if (pinyin != null && pinyin.length() > 0) {
            sortString = pinyin.substring(0, 1).toUpperCase();
        } else {
            sortString = "#";
        }

        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            friendInfo.setSortKey(sortString.toUpperCase());
        } else {
            friendInfo.setSortKey("#");
        }
    }
}
