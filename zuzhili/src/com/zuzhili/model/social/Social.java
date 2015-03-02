package com.zuzhili.model.social;

import java.io.Serializable;

/**
 * Created by liutao on 14-3-11.
 */
public class Social implements Serializable {
    private String listname;
    private String listdesc;
    private String logo;
    private int creatorid;
    private int id;
    private long createTime;
    private int optUserId;
    private String nickname;
    private boolean canApplyFlag;
    private int countUser;
    private int ids;
    private boolean isactive;
    private String shortname;
    private int opflag;

    public String getListname() {
        return listname;
    }

    public void setListname(String listname) {
        this.listname = listname;
    }

    public String getListdesc() {
        return listdesc;
    }

    public void setListdesc(String listdesc) {
        this.listdesc = listdesc;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public int getCreatorid() {
        return creatorid;
    }

    public void setCreatorid(int creatorid) {
        this.creatorid = creatorid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getOptUserId() {
        return optUserId;
    }

    public void setOptUserId(int optUserId) {
        this.optUserId = optUserId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isCanApplyFlag() {
        return canApplyFlag;
    }

    public void setCanApplyFlag(boolean canApplyFlag) {
        this.canApplyFlag = canApplyFlag;
    }

    public int getCountUser() {
        return countUser;
    }

    public void setCountUser(int countUser) {
        this.countUser = countUser;
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public boolean isIsactive() {
        return isactive;
    }

    public void setIsactive(boolean isactive) {
        this.isactive = isactive;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public int getOpflag() {
        return opflag;
    }

    public void setOpflag(int opflag) {
        this.opflag = opflag;
    }
}
