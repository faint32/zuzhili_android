package com.zuzhili.model.space;

import java.util.List;

import com.zuzhili.model.MiniBlog;

public class UserTotalInfo {
	String userid;
	List<MiniBlog> json;
	UserInfoSummary usersummary;
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public List<MiniBlog> getJson() {
		return json;
	}
	public void setJson(List<MiniBlog> json) {
		this.json = json;
	}
	public UserInfoSummary getUsersummary() {
		return usersummary;
	}
	public void setUsersummary(UserInfoSummary usersummary) {
		this.usersummary = usersummary;
	}
}
