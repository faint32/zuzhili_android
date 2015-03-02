package com.zuzhili.model.multipart;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ImageUpload implements Serializable{
	String filepath;
	String newfilename;
	String desc;
	String fileidentity;
	String type;//0 item,1 add
	Bitmap src;
	int size;
	public ImageUpload copy(){
		ImageUpload it=new ImageUpload();
		it.setDesc(this.desc);
		it.setFileidentity(this.fileidentity);
		it.setFilepath(this.filepath);
		it.setNewfilename(this.newfilename);
		it.setType(this.type);
		it.setSrc(this.src);
		return it;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getFileidentity() {
		return fileidentity;
	}
	public void setFileidentity(String fileidentity) {
		this.fileidentity = fileidentity;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public Bitmap getSrc() {
		return src;
	}
	public void setSrc(Bitmap src) {
		this.src = src;
	}
	public String getNewfilename() {
		return newfilename;
	}
	public void setNewfilename(String newfilename) {
		this.newfilename = newfilename;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
}
