package com.zuzhili.model;

public class ImageBean {
	private String topImagePath;
    private String folderName;
	private int imageCounts;
    private boolean isSelect;
	public String getTopImagePath() {
		return topImagePath;
	}
    public void setTopImagePath(String topImagePath) {
		this.topImagePath = topImagePath;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public int getImageCounts() {
		return imageCounts;
	}
	public void setImageCounts(int imageCounts) {
		this.imageCounts = imageCounts;
	}

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }
}
