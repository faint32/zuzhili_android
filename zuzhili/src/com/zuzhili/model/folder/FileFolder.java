package com.zuzhili.model.folder;

import com.zuzhili.model.BaseModel;

/**
 * Created by addison on 2/25/14.
 */
public class FileFolder extends BaseModel {
    private String id;
    private String foldername;
    private String reservedint1;
    private String reservedchar2;    // 文件描述
    private String authority;
    private String aclstatus;

    public String getAclstatus() {
        return aclstatus;
    }

    public void setAclstatus(String aclstatus) {
        this.aclstatus = aclstatus;
    }

    public String getReservedint1() {
        return reservedint1;
    }

    public void setReservedint1(String reservedint1) {
        this.reservedint1 = reservedint1;
    }

    public String getReservedchar2() {
        return reservedchar2;
    }

    public void setReservedchar2(String reservedchar2) {
        this.reservedchar2 = reservedchar2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFoldername() {
        return foldername;
    }

    public void setFoldername(String foldername) {
        this.foldername = foldername;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

}
