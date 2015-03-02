package com.zuzhili.bussiness.socket.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Transient;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.bussiness.utility.pinyin.CharacterParser;
import com.zuzhili.model.SortModel;

/**
 * Created by kj on 2014/6/24.
 */
public class UserInfo extends SortModel {
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser = CharacterParser.getInstance();

    public int _id;

    /** user id */
    private String u_id;

    /** user ids */
    private String u_ids;

    private String u_name;

    /** user icon */
    private String u_icon;

    /** user listid */
    private String u_listid;

    /** user lastSa */
    private String u_lastSa;

    /** user subid */
    private String y_subid;

    /** user subpass */
    private String y_subpass;

    /** 云通讯 voip 帐号*/
    private String y_voip;

    private String y_voippass;

    /** 根据此标志位判断是否是是聊天用户的头像、或者加人、或者踢人；0 表示头像， 1 表示加人， 2 表示踢人 */
    @Transient
    private int userFlag=0;

    @Transient
    private boolean showLeftTopRemoveUserIcon;

    private String identity;

    private String u_phone;

    public String getU_phone() {
        return u_phone;
    }

    public void setU_phone(String u_phone) {
        this.u_phone = u_phone;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
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


    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getU_ids() {
        return u_ids;
    }

    public void setU_ids(String u_ids) {
        this.u_ids = u_ids;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getU_name() {
        return u_name;
    }

    public void setU_name(String u_name) {
        this.u_name = u_name;
    }

    public String getU_icon() {
        return u_icon;
    }

    public void setU_icon(String u_icon) {
        this.u_icon = u_icon;
    }

    public String getU_listid() {
        return u_listid;
    }

    public void setU_listid(String u_listid) {
        this.u_listid = u_listid;
    }

    public String getY_subid() {
        return y_subid;
    }

    public void setY_subid(String y_subid) {
        this.y_subid = y_subid;
    }

    public String getY_subpass() {
        return y_subpass;
    }

    public void setY_subpass(String y_subpass) {
        this.y_subpass = y_subpass;
    }

    public String getU_lastSa() {
        return u_lastSa;
    }

    public void setU_lastSa(String u_lastSa) {
        this.u_lastSa = u_lastSa;
    }

    public String getY_voip() {
        return y_voip;
    }

    public void setY_voip(String y_voip) {
        this.y_voip = y_voip;
    }

    public String getY_voippass() {
        return y_voippass;
    }

    public void setY_voippass(String y_voippass) {
        this.y_voippass = y_voippass;
    }

    public static final Parcelable.Creator<UserInfo> CREATOR = new Creator() {

        @Override
        public UserInfo createFromParcel(Parcel source) {
            UserInfo userInfo = new UserInfo();
            userInfo.setSortKey(source.readString());
            userInfo.set_id(source.readInt());
            userInfo.setU_id(source.readString());
            userInfo.setU_ids(source.readString());
            userInfo.setU_name(source.readString());
            userInfo.setU_icon(source.readString());
            userInfo.setU_listid(source.readString());
            userInfo.setU_lastSa(source.readString());
            userInfo.setY_subid(source.readString());
            userInfo.setY_subpass(source.readString());
            userInfo.setY_voip(source.readString());
            userInfo.setY_voippass(source.readString());
            userInfo.setUserFlag(source.readInt());
            userInfo.setIdentity(source.readString());
            userInfo.setY_voip(source.readString());
            userInfo.setU_phone(source.readString());
            return userInfo;
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
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
        dest.writeString(u_id);
        dest.writeString(u_ids);
        dest.writeString(u_name);
        dest.writeString(u_icon);
        dest.writeString(u_listid);
        dest.writeString(u_lastSa);
        dest.writeString(y_subid);
        dest.writeString(y_subpass);
        dest.writeString(y_voip);
        dest.writeString(y_voippass);
        dest.writeInt(userFlag);
        dest.writeString(identity);
        dest.writeString(y_voip);
        dest.writeString(u_phone);
    }

    public void updateSortKey() {
        //汉字转换成拼音
        String pinyin = characterParser.getSelling(TextUtil.processNullString(this.getU_name()));
        String sortString;
        if (pinyin != null && pinyin.length() > 0) {
            sortString = pinyin.substring(0, 1).toUpperCase();
        } else {
            sortString = "#";
        }

        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            this.setSortKey(sortString.toUpperCase());
        } else {
            this.setSortKey("#");
        }
    }
}
