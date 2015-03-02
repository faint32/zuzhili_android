package com.zuzhili.model.msg;
import java.util.List;
import java.io.Serializable;
public class MsgDetailRec implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String content;//内容相关
    long createTime;//创建时间 
    String documents;//文档
	String fromids;//来源
	int hasread;//读取标识
	int id;
	int ishaveattatchfile;//是否有附件
	int ishavephoto;//是否有图片
	long isuniqueId;//是否唯一标识
	String isuniqueIdinfo;
	int letterType;//私信类型
	int listid;//社区编号
	int privateletterid;//私信编号
	int toids;//身份编号
	long updateTime;//更新时间
	List<Attachment> configlist;//附件列表

	public long getIsuniqueId() {
		return isuniqueId;
	}
	public void setIsuniqueId(long isuniqueId) {
		this.isuniqueId = isuniqueId;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public long getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getDocuments() {
		return documents;
	}
	public void setDocuments(String documents) {
		this.documents = documents;
	}
	public String getFromids() {
		return fromids;
	}
	public void setFromids(String fromids) {
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
	
	public List<Attachment> getConfiglist() {
		return configlist;
	}
	public void setConfiglist(List<Attachment> configlist) {
		this.configlist = configlist;
	}
	
	
	
}
