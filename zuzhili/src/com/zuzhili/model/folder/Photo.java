package com.zuzhili.model.folder;

import com.zuzhili.model.BaseModel;

/**
 * Created by kj on 2014/8/18.
 */
public class Photo extends BaseModel {
    private String id;
    private String createTime;
    private String updateTime;
    private String delflag;
    private String optids;
    private String optuserid;
    private String description;
    private String originalname;
    private String savename;
    private String url_small;
    private String url_source;
    private String url_big;
    private String savepath;
    private String relsavepah;
    private String ishavelable;
    private String albumid;
    private String phototype;
    private String netid;
    private String creatorid;
    private String isnetown;
    private String listid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getDelflag() {
        return delflag;
    }

    public void setDelflag(String delflag) {
        this.delflag = delflag;
    }

    public String getOptids() {
        return optids;
    }

    public void setOptids(String optids) {
        this.optids = optids;
    }

    public String getOptuserid() {
        return optuserid;
    }

    public void setOptuserid(String optuserid) {
        this.optuserid = optuserid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOriginalname() {
        return originalname;
    }

    public void setOriginalname(String originalname) {
        this.originalname = originalname;
    }

    public String getSavename() {
        return savename;
    }

    public void setSavename(String savename) {
        this.savename = savename;
    }

    public String getUrl_small() {
        return url_small;
    }

    public void setUrl_small(String url_small) {
        this.url_small = url_small;
    }

    public String getUrl_source() {
        return url_source;
    }

    public void setUrl_source(String url_source) {
        this.url_source = url_source;
    }

    public String getUrl_big() {
        return url_big;
    }

    public void setUrl_big(String url_big) {
        this.url_big = url_big;
    }

    public String getSavepath() {
        return savepath;
    }

    public void setSavepath(String savepath) {
        this.savepath = savepath;
    }

    public String getRelsavepah() {
        return relsavepah;
    }

    public void setRelsavepah(String relsavepah) {
        this.relsavepah = relsavepah;
    }

    public String getIshavelable() {
        return ishavelable;
    }

    public void setIshavelable(String ishavelable) {
        this.ishavelable = ishavelable;
    }

    public String getAlbumid() {
        return albumid;
    }

    public void setAlbumid(String albumid) {
        this.albumid = albumid;
    }

    public String getPhototype() {
        return phototype;
    }

    public void setPhototype(String phototype) {
        this.phototype = phototype;
    }

    public String getNetid() {
        return netid;
    }

    public void setNetid(String netid) {
        this.netid = netid;
    }

    public String getCreatorid() {
        return creatorid;
    }

    public void setCreatorid(String creatorid) {
        this.creatorid = creatorid;
    }

    public String getIsnetown() {
        return isnetown;
    }

    public void setIsnetown(String isnetown) {
        this.isnetown = isnetown;
    }

    public String getListid() {
        return listid;
    }

    public void setListid(String listid) {
        this.listid = listid;
    }

    public Photo() {
    }

    public Photo( String id, String url_source,String description) {
        this.description = description;
        this.id = id;
        this.url_source = url_source;
    }
}
