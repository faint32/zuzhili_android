package com.zuzhili.model.multipart;

import java.io.Serializable;

/**
 * @Title: ImageItem.java
 * @Package: com.zuzhili.multiselector.model
 * @Description: 一个图片对象
 * @author: gengxin
 * @date: 2014-2-13
 */
public class ImageItem implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -144587760487715721L;
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
    public String desc;
	public boolean isSelected = false;
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	public void setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
