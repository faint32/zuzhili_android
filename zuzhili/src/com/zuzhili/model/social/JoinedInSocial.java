package com.zuzhili.model.social;

import com.zuzhili.model.Member;

import java.io.Serializable;

public class JoinedInSocial implements Serializable{
	
	/**
	 * 社区相关信息
	 */
	private static final long serialVersionUID = 1L;
	private int id;//社区编号
    private String createTime;//创建时间 
    private String updateTime;//更新时间
    private String delflag;//删除标识
    private String listname;//社区名称
    private String shortname;//社区简称
    private String listdesc;//社区描述
    private String listtype;//社区类型
    private int parentlistid;//父社区编号
    private String url;//url
    private String  loginlogo;//社区logo
    private String logo;//logo
    private String istop;//是否top
    private int creatorid;//创建人编号
    private int mainlistid;//主社区编号
    private int orderInParent;//父类编号
    private String viewidInTtree;//
    private int unreadnewfreshcount;
    private int unreadnewatfeed;
    private int unreadnewatcomment;
    private int unreadnewmsgcount;
    private int unreadnewcomment;
    private int unreadnewnotify;
    private String nickname;//昵称
    private String userdepartment;//部门
    private String userposition;//职位
    private String userhead;//头像
    private String creatorname;//创建人名字
    private int countUser;//用户数量
    private boolean focusFlag;//关注标识
    private boolean applyauth;//应用权限
    private boolean canApplyFlag;//应用标志
    private boolean isnickorname;//是否昵称
    private boolean isactive;//是否激活
    private boolean ids;//身份编号
    private String sex;//性别
    private String isep;//
    private String epmail;//
    private boolean isdefnew;//
    private int starttime;//开始时间
    private int finishtime;//完成时间
    private int state;//状态
    private String percent;//百分比
    private String curIndexInParent;//当前缩印百分比
    private String nodes;//
    private String subOrder;
    private String children;
    private String parentlists;
    private boolean ispublish;//是否发布
    private boolean isexit;//是否退出
    private int maxPeople;//最大数目人数
    private int yptMembercount;//成员数量
    private Member identity;//身份信息
    
	public Member getIdentity() {
		return identity;
	}
	public void setIdentity(Member identity) {
		this.identity = identity;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
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
	public String getDelflag() {return delflag;}
	public void setDelflag(String delflag) {
		this.delflag = delflag;
	}
	public String getListname() {
		return listname;
	}
	public void setListname(String listname) {
		this.listname = listname;
	}
    public String getShortname() {return shortname;}
    public void setShortname(String shortname) {this.shortname = shortname;}

    public String getListdesc() {
		return listdesc;
	}
	public void setListdesc(String listdesc) {
		this.listdesc = listdesc;
	}
	public String getListtype() {
		return listtype;
	}
	public void setListtype(String listtype) {
		this.listtype = listtype;
	}
	public int getParentlistid() {
		return parentlistid;
	}
	public void setParentlistid(int parentlistid) {
		this.parentlistid = parentlistid;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getLoginlogo() {
		return loginlogo;
	}
	public void setLoginlogo(String loginlogo) {
		this.loginlogo = loginlogo;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getIstop() {
		return istop;
	}
	public void setIstop(String istop) {
		this.istop = istop;
	}
	public int getCreatorid() {
		return creatorid;
	}
	public void setCreatorid(int creatorid) {
		this.creatorid = creatorid;
	}
	public int getMainlistid() {
		return mainlistid;
	}
	public void setMainlistid(int mainlistid) {
		this.mainlistid = mainlistid;
	}
	public int getOrderInParent() {
		return orderInParent;
	}
	public void setOrderInParent(int orderInParent) {
		this.orderInParent = orderInParent;
	}
	public String getViewidInTtree() {
		return viewidInTtree;
	}
	public void setViewidInTtree(String viewidInTtree) {
		this.viewidInTtree = viewidInTtree;
	}
	public int getUnreadnewfreshcount() {
		return unreadnewfreshcount;
	}
	public void setUnreadnewfreshcount(int unreadnewfreshcount) {
		this.unreadnewfreshcount = unreadnewfreshcount;
	}
	public int getUnreadnewatfeed() {
		return unreadnewatfeed;
	}
	public void setUnreadnewatfeed(int unreadnewatfeed) {
		this.unreadnewatfeed = unreadnewatfeed;
	}
	public int getUnreadnewatcomment() {
		return unreadnewatcomment;
	}
	public void setUnreadnewatcomment(int unreadnewatcomment) {
		this.unreadnewatcomment = unreadnewatcomment;
	}
	public int getUnreadnewmsgcount() {
		return unreadnewmsgcount;
	}
	public void setUnreadnewmsgcount(int unreadnewmsgcount) {
		this.unreadnewmsgcount = unreadnewmsgcount;
	}
	public int getUnreadnewcomment() {
		return unreadnewcomment;
	}
	public void setUnreadnewcomment(int unreadnewcomment) {
		this.unreadnewcomment = unreadnewcomment;
	}
	public int getUnreadnewnotify() {
		return unreadnewnotify;
	}
	public void setUnreadnewnotify(int unreadnewnotify) {
		this.unreadnewnotify = unreadnewnotify;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getUserdepartment() {
		return userdepartment;
	}
	public void setUserdepartment(String userdepartment) {
		this.userdepartment = userdepartment;
	}
	public String getUserposition() {
		return userposition;
	}
	public void setUserposition(String userposition) {
		this.userposition = userposition;
	}
	public String getUserhead() {
		return userhead;
	}
	public void setUserhead(String userhead) {
		this.userhead = userhead;
	}
	public String getCreatorname() {
		return creatorname;
	}
	public void setCreatorname(String creatorname) {
		this.creatorname = creatorname;
	}
	public int getCountUser() {
		return countUser;
	}
	public void setCountUser(int countUser) {
		this.countUser = countUser;
	}
	public boolean isFocusFlag() {
		return focusFlag;
	}
	public void setFocusFlag(boolean focusFlag) {
		this.focusFlag = focusFlag;
	}
	public boolean isApplyauth() {
		return applyauth;
	}
	public void setApplyauth(boolean applyauth) {
		this.applyauth = applyauth;
	}
	public boolean isCanApplyFlag() {
		return canApplyFlag;
	}
	public void setCanApplyFlag(boolean canApplyFlag) {
		this.canApplyFlag = canApplyFlag;
	}
	public boolean isIsnickorname() {
		return isnickorname;
	}
	public void setIsnickorname(boolean isnickorname) {
		this.isnickorname = isnickorname;
	}
	public boolean isIsactive() {
		return isactive;
	}
	public void setIsactive(boolean isactive) {
		this.isactive = isactive;
	}
	public boolean isIds() {
		return ids;
	}
	public void setIds(boolean ids) {
		this.ids = ids;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getIsep() {
		return isep;
	}
	public void setIsep(String isep) {
		this.isep = isep;
	}
	public String getEpmail() {
		return epmail;
	}
	public void setEpmail(String epmail) {
		this.epmail = epmail;
	}
	public boolean isIsdefnew() {
		return isdefnew;
	}
	public void setIsdefnew(boolean isdefnew) {
		this.isdefnew = isdefnew;
	}
	public int getStarttime() {
		return starttime;
	}
	public void setStarttime(int starttime) {
		this.starttime = starttime;
	}
	public int getFinishtime() {
		return finishtime;
	}
	public void setFinishtime(int finishtime) {
		this.finishtime = finishtime;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getPercent() {
		return percent;
	}
	public void setPercent(String percent) {
		this.percent = percent;
	}
	public String getCurIndexInParent() {
		return curIndexInParent;
	}
	public void setCurIndexInParent(String curIndexInParent) {
		this.curIndexInParent = curIndexInParent;
	}
	public String getNodes() {
		return nodes;
	}
	public void setNodes(String nodes) {
		this.nodes = nodes;
	}
	public String getSubOrder() {
		return subOrder;
	}
	public void setSubOrder(String subOrder) {
		this.subOrder = subOrder;
	}
	public String getChildren() {
		return children;
	}
	public void setChildren(String children) {
		this.children = children;
	}
	public String getParentlists() {
		return parentlists;
	}
	public void setParentlists(String parentlists) {
		this.parentlists = parentlists;
	}
	public boolean isIspublish() {
		return ispublish;
	}
	public void setIspublish(boolean ispublish) {
		this.ispublish = ispublish;
	}
	public boolean isIsexit() {
		return isexit;
	}
	public void setIsexit(boolean isexit) {
		this.isexit = isexit;
	}
	public int getMaxPeople() {
		return maxPeople;
	}
	public void setMaxPeople(int maxPeople) {
		this.maxPeople = maxPeople;
	}
	public int getYptMembercount() {
		return yptMembercount;
	}
	public void setYptMembercount(int yptMembercount) {
		this.yptMembercount = yptMembercount;
	}
    
}
