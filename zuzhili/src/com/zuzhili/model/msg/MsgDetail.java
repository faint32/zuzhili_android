package com.zuzhili.model.msg;

import com.zuzhili.model.Member;

import java.io.Serializable;
import java.util.List;
/**
 * Created by zuosl on 14-2-21.
 */
public class MsgDetail implements Serializable, Comparable<MsgDetail> {
    private String comfrom;//来源
    private String content;//内容
    private long createTime;//创建时间
    private List<Attachment> configlist;//附件列表
    private String delflag;//删除标识
    private int fromids;//来源编号
    private int hasread;//已读标识
    private int id;//身份编号
    private int ishaveattatchfile;
    private int ishavephoto;//是否有头像
    private long isuniqueId;//是否唯一值
    private String isuniqueIdinfo;
    private int letterType;
    private int listid;
    private int privateletterid;
    private int toids;
    private long updateTime;
    private String username;
    private Member totidentity;//身份对象

    public String getComfrom() {
        return comfrom;
    }

    public void setComfrom(String comfrom) {
        this.comfrom = comfrom;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getDelflag() {
        return delflag;
    }

    public void setDelflag(String delflag) {
        this.delflag = delflag;
    }

    public int getFromids() {
        return fromids;
    }

    public void setFromids(int fromids) {
        this.fromids = fromids;
    }

    public int getHasread() {
        return hasread;
    }

    public void setHasread(int hasread) {
        this.hasread = hasread;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIshaveattatchfile() {
        return ishaveattatchfile;
    }

    public void setIshaveattatchfile(int ishaveattatchfile) {
        this.ishaveattatchfile = ishaveattatchfile;
    }

    public int getIshavephoto() {
        return ishavephoto;
    }

    public void setIshavephoto(int ishavephoto) {
        this.ishavephoto = ishavephoto;
    }

    public long getIsuniqueId() {
        return isuniqueId;
    }

    public void setIsuniqueId(long isuniqueId) {
        this.isuniqueId = isuniqueId;
    }

    public String getIsuniqueIdinfo() {
        return isuniqueIdinfo;
    }

    public void setIsuniqueIdinfo(String isuniqueIdinfo) {
        this.isuniqueIdinfo = isuniqueIdinfo;
    }

    public int getLetterType() {
        return letterType;
    }

    public void setLetterType(int letterType) {
        this.letterType = letterType;
    }

    public int getListid() {
        return listid;
    }

    public void setListid(int listid) {
        this.listid = listid;
    }

    public int getPrivateletterid() {
        return privateletterid;
    }

    public void setPrivateletterid(int privateletterid) {
        this.privateletterid = privateletterid;
    }

    public int getToids() {
        return toids;
    }

    public void setToids(int toids) {
        this.toids = toids;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Member getTotidentity() {
        return totidentity;
    }

    public void setTotidentity(Member totidentity) {
        this.totidentity = totidentity;
    }

    public List<Attachment> getConfiglist() {
        return configlist;
    }

    public void setConfiglist(List<Attachment> configlist) {
        this.configlist = configlist;
    }

    @Override
    public int compareTo(MsgDetail another) {
        return (int) (this.getCreateTime() - another.getCreateTime());
    }
}
