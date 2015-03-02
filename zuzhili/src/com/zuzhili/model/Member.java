package com.zuzhili.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Member extends SortModel implements Serializable, Parcelable {
	
    private String id;

    private String name;

    private String sex;

    private String phone;

    private String summary;

    private Long birthday;

    private String mail;

    private String department;

    private String responsibility;

    private String listid;
    
    private String userhead;

    private String userhead150;

    private String iscontact;

    private String type;
    
    private int userid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }

    public String getListid() {
        return listid;
    }

    public void setListid(String listid) {
        this.listid = listid;
    }

    public String getUserhead() {
        return userhead;
    }

    public void setUserhead(String userhead) {
        this.userhead = userhead;
    }

    public String getUserhead150() {
        return userhead150;
    }

    public void setUserhead150(String userhead150) {
        this.userhead150 = userhead150;
    }

    public String getIscontact() {
        return iscontact;
    }

    public void setIscontact(String iscontact) {
        this.iscontact = iscontact;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public static final Parcelable.Creator<Member> CREATOR = new Creator() {

        @Override
        public Member createFromParcel(Parcel source) {
            Member member = new Member();
            member.setId(source.readString());
            member.setName(source.readString());
            member.setSex(source.readString());
            member.setPhone(source.readString());
            member.setSummary(source.readString());
            member.setMail(source.readString());
            member.setDepartment(source.readString());
            member.setResponsibility(source.readString());
            member.setListid(source.readString());
            member.setUserhead(source.readString());
            member.setUserhead150(source.readString());
            member.setIscontact(source.readString());
            member.setType(source.readString());
            member.setUserid(source.readInt());
            return member;
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(sex);
        dest.writeString(phone);
        dest.writeString(summary);
        dest.writeString(mail);
        dest.writeString(department);
        dest.writeString(responsibility);
        dest.writeString(listid);
        dest.writeString(userhead);
        dest.writeString(userhead150);
        dest.writeString(iscontact);
        dest.writeString(type);
        dest.writeInt(userid);
    }
}