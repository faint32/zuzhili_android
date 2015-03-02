package com.zuzhili.model;

public class ContactRec extends SortModel {
	String name;
	String phone;
	boolean bisselect;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public boolean isBisselect() {
		return bisselect;
	}
	public void setBisselect(boolean bisselect) {
		this.bisselect = bisselect;
	}
}
