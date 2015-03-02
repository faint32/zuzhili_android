package com.zuzhili.model.folder;

import com.zuzhili.model.BaseModel;

/**
 * Created by addison on 2/21/14.
 * 多媒体文件夹（音视频）
 */
public class MutilFolder extends BaseModel{
    private String id;
    private String name;
    private String coverphotopath;
    private String description;
    private int photonum;
    private String foldertype; // 0,music;1,vedio
    private String authority;

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

    public int getPhotonum() {
        return photonum;
    }

    public void setPhotonum(int photonum) {
        this.photonum = photonum;
    }

    public String getFoldertype() {
        return foldertype;
    }

    public void setFoldertype(String foldertype) {
        this.foldertype = foldertype;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }
}
