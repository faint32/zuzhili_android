package com.zuzhili.model.folder;

import com.zuzhili.model.BaseModel;

import java.io.Serializable;

/**
 * Created by addison on 2/20/14.
 * 图片册
 */
public class Album extends BaseModel{

    private String id;
    private String name;
    private String coverphotopath;
    private String description;
    private String authority;
    private int photonum;

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

    public String getCoverphotopath() {
        return coverphotopath;
    }

    public void setCoverphotopath(String coverphotopath) {
        this.coverphotopath = coverphotopath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public int getPhotonum() {
        return photonum;
    }

    public void setPhotonum(int photonum) {
        this.photonum = photonum;
    }
}
