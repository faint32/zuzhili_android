package com.zuzhili.db;

/**
 * Created by liutao on 14-3-3.
 */
public class LoggedInfo {
    String id;
    String listid;
    String ids;
    /** 用户已登录过该社区 */
    boolean loggedin;
    String updatetime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getListid() {
        return listid;
    }

    public void setListid(String listid) {
        this.listid = listid;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public boolean isLoggedin() {
        return loggedin;
    }

    public void setLoggedin(boolean loggedin) {
        this.loggedin = loggedin;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }
}
