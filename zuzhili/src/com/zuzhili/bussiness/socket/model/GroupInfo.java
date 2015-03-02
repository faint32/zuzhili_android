package com.zuzhili.bussiness.socket.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Transient;

import java.io.Serializable;

/**
 * Created by kj on 14-6-24.
 */
public class GroupInfo implements Serializable, Parcelable {

    private String id;

    private String y_gid;

    /**
     * 创建人u_id
     */
    private String creatorid;

    private String g_name;

    private String g_declared;

    /**
     * 人数限制
     */
    private String g_type;

    /**
     * 申请加入模式
     */
    private String g_permisson;

    /**
     * 成员数量
     */
    private String g_ucount;

    private String u_listid;

    /**
     * 群组成员数上限
     */
    private String g_capacity;

    private String g_lastSay;

    /**
     * 平台上对应的listsid
     */
    private String z_gid;

    /**
     * 群组分类，0用户自建，1:机构 2:项目组 3:群组 4:活动
     */
    private String z_type;

    private String identityId;

    /**
     * 是否是群组成员 0,未加入; 1,已加入
     */
    @Transient
    private String ismember;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getY_gid() {
        return y_gid;
    }

    public void setY_gid(String y_gid) {
        this.y_gid = y_gid;
    }

    public String getCreatorid() {
        return creatorid;
    }

    public void setCreatorid(String creatorid) {
        this.creatorid = creatorid;
    }

    public String getG_name() {
        return g_name;
    }

    public void setG_name(String g_name) {
        this.g_name = g_name;
    }

    public String getG_declared() {
        return g_declared;
    }

    public void setG_declared(String g_declared) {
        this.g_declared = g_declared;
    }

    public String getG_type() {
        return g_type;
    }

    public void setG_type(String g_type) {
        this.g_type = g_type;
    }

    public String getG_permisson() {
        return g_permisson;
    }

    public void setG_permisson(String g_permisson) {
        this.g_permisson = g_permisson;
    }

    public String getG_ucount() {
        return g_ucount;
    }

    public void setG_ucount(String g_ucount) {
        this.g_ucount = g_ucount;
    }

    public String getU_listid() {
        return u_listid;
    }

    public void setU_listid(String u_listid) {
        this.u_listid = u_listid;
    }

    public String getG_capacity() {
        return g_capacity;
    }

    public void setG_capacity(String g_capacity) {
        this.g_capacity = g_capacity;
    }

    public String getG_lastSay() {
        return g_lastSay;
    }

    public void setG_lastSay(String g_lastSay) {
        this.g_lastSay = g_lastSay;
    }

    public String getZ_gid() {
        return z_gid;
    }

    public void setZ_gid(String z_gid) {
        this.z_gid = z_gid;
    }

    public String getZ_type() {
        return z_type;
    }

    public void setZ_type(String z_type) {
        this.z_type = z_type;
    }

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public String getIsmember() {
        return ismember;
    }

    public void setIsmember(String ismember) {
        this.ismember = ismember;
    }

    public GroupInfo(String id, String y_gid, String creatorid, String g_name, String g_declared, String g_type, String g_permisson, String g_ucount, String u_listid, String g_capacity, String g_lastSay, String z_gid, String z_type) {
        this.id = id;
        this.y_gid = y_gid;
        this.creatorid = creatorid;
        this.g_name = g_name;
        this.g_declared = g_declared;
        this.g_type = g_type;
        this.g_permisson = g_permisson;
        this.g_ucount = g_ucount;
        this.u_listid = u_listid;
        this.g_capacity = g_capacity;
        this.g_lastSay = g_lastSay;
        this.z_gid = z_gid;
        this.z_type = z_type;
        this.ismember = "1";
    }

    public GroupInfo() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(y_gid);
        dest.writeString(creatorid);
        dest.writeString(g_name);
        dest.writeString(g_declared);
        dest.writeString(g_type);
        dest.writeString(g_permisson);
        dest.writeString(g_ucount);
        dest.writeString(u_listid);
        dest.writeString(g_capacity);
        dest.writeString(g_lastSay);
        dest.writeString(z_gid);
        dest.writeString(z_type);
        dest.writeString(ismember);
    }

    public static final Creator<GroupInfo> CREATOR = new Creator<GroupInfo>() {
        @Override
        public GroupInfo createFromParcel(Parcel source) {
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setId(source.readString());
            groupInfo.setY_gid(source.readString());
            groupInfo.setCreatorid(source.readString());
            groupInfo.setG_name(source.readString());
            groupInfo.setG_declared(source.readString());
            groupInfo.setG_type(source.readString());
            groupInfo.setG_permisson(source.readString());
            groupInfo.setG_ucount(source.readString());
            groupInfo.setU_listid(source.readString());
            groupInfo.setG_capacity(source.readString());
            groupInfo.setG_lastSay(source.readString());
            groupInfo.setZ_gid(source.readString());
            groupInfo.setZ_type(source.readString());
            groupInfo.setIsmember(source.readString());
            return groupInfo;
        }

        @Override
        public GroupInfo[] newArray(int size) {
            return new GroupInfo[size];
        }
    };

}
