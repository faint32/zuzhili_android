package com.zuzhili.model.multipart;

import android.graphics.Bitmap;

import com.zuzhili.model.BaseModel;

/**
 * Created by addison on 2/21/14.
 * 音视频
 */
public class MultiUpload extends BaseModel{

    private String filepath;
    private String desc;
    private Bitmap src;
    private String newfilename;
    private String fileidentity;
    private String size;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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

    public String getFileidentity() {
        return fileidentity;
    }

    public void setFileidentity(String fileidentity) {
        this.fileidentity = fileidentity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
