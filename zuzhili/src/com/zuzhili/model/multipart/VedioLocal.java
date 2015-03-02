package com.zuzhili.model.multipart;

import android.graphics.Bitmap;

import com.zuzhili.bussiness.helper.VedioHelper;
import com.zuzhili.model.BaseModel;

/**
 * Created by addison on 2/21/14.
 */
public class VedioLocal extends BaseModel{
    public VedioHelper vedio;
    public long dataModified = 0;
    public String datapath;
    public String name;
    public String duration;
    public String size;
    public int lastPos = 0;
    public Bitmap cover;

    public VedioHelper getVedio() {
        return vedio;
    }

    public void setVedio(VedioHelper vedio) {
        this.vedio = vedio;
    }

    public long getDataModified() {
        return dataModified;
    }

    public void setDataModified(long dataModified) {
        this.dataModified = dataModified;
    }

    public String getDatapath() {
        return datapath;
    }

    public void setDatapath(String datapath) {
        this.datapath = datapath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getLastPos() {
        return lastPos;
    }

    public void setLastPos(int lastPos) {
        this.lastPos = lastPos;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
    }
}
