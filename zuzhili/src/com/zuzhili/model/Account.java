package com.zuzhili.model;

import com.zuzhili.model.social.JoinedInSocial;

import java.util.ArrayList;
import java.util.List;

public class Account extends BaseModel{
	public String userid;//用户编号
	public String ishaveiostoken;
	public int mCurrent = 0;//当前用户
	public List<JoinedInSocial> list=new  ArrayList<JoinedInSocial>();
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	public String getIshaveiostoken() {
		return ishaveiostoken;
	}
	public void setIshaveiostoken(String ishaveiostoken) {
		this.ishaveiostoken = ishaveiostoken;
	}
	public int getmCurrent() {
		return mCurrent;
	}
	public void setmCurrent(int mCurrent) {
		this.mCurrent = mCurrent;
	}
	public List<JoinedInSocial> getList() {
		return list;
	}
	public void setList(List<JoinedInSocial> list) {
		this.list = list;
	}

    public String getIdsViaListId(String listId) {
        if (listId == null) {
            return "0";
        }
        if (list != null && list.size() > 0) {
            for (JoinedInSocial s : list) {
                if (s.getIdentity() != null && s.getIdentity().getListid() != null) {
                    if (s.getIdentity().getListid().equals(listId)) {
                        return s.getIdentity().getId();
                    }
                } else {
                    return "0";
                }
            }
        }
        return "0";
    }
}
