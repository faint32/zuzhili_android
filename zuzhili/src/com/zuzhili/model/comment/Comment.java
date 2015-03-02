package com.zuzhili.model.comment;

import com.zuzhili.model.Member;

import java.io.Serializable;

/**
 * Created by liutao on 14-2-26.
 */
public class Comment implements Serializable {

    private int id;
    private long createTime;
    private String content;
    private int ids;
    private int absid;
    private int tocommentid;
    private int curnetid;
    private int ownerid;
    private int touserid;
    private String absmini;
    private String name;
    private String headimage;
    private int type;
    private Member identity = new Member();

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public int getAbsid() {
        return absid;
    }

    public void setAbsid(int absid) {
        this.absid = absid;
    }

    public int getTocommentid() {
        return tocommentid;
    }

    public void setTocommentid(int tocommentid) {
        this.tocommentid = tocommentid;
    }

    public int getCurnetid() {
        return curnetid;
    }

    public void setCurnetid(int curnetid) {
        this.curnetid = curnetid;
    }

    public int getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(int ownerid) {
        this.ownerid = ownerid;
    }

    public int getTouserid() {
        return touserid;
    }

    public void setTouserid(int touserid) {
        this.touserid = touserid;
    }

    public String getAbsmini() {
        return absmini;
    }

    public void setAbsmini(String absmini) {
        this.absmini = absmini;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadimage() {
        return headimage;
    }

    public void setHeadimage(String headimage) {
        this.headimage = headimage;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Member getIdentity() {
        return identity;
    }

    public void setIdentity(Member identity) {
        this.identity = identity;
    }

}
