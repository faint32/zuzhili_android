package com.zuzhili.model.multipart;

import java.io.Serializable;
import java.util.List;

/**
 * @Title: ImageBucket.java
 * @Package: com.zuzhili.multiselector.model
 * @Description: 一个目录的相册对象
 * @author: gengxin
 * @date: 2014-2-13
 */
public class ImageBucket implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7020018486104696917L;
	
	public int count = 0;
	public String bucketName;
	public List<ImageItem> imageList;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getBucketName() {
		return bucketName;
	}
	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}
	public List<ImageItem> getImageList() {
		return imageList;
	}
	public void setImageList(List<ImageItem> imageList) {
		this.imageList = imageList;
	}
	
	

}
