package com.zuzhili.model.approval;
import java.io.Serializable;
import java.util.List;
import com.zuzhili.model.Member;
//审批对象
public class Approval implements Serializable{
	private static final long serialVersionUID = 2792692665242003594L;
	private String title;//标题
	private String content;//内容
	private long time;//时间
	private int id;//审批主键
	private int status;//状态
	private int from;//来源
	private Member identity;//发布人
	private List<Member> identities;//审批人列表 
	private List<FileConfig> configlist;//附件
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getFrom() {
		return from;
	}
	public void setFrom(int from) {
		this.from = from;
	}
	public Member getIdentity() {
		return identity;
	}
	public void setIdentity(Member identity) {
		this.identity = identity;
	}
	public List<Member> getIdentities() {
		return identities;
	}
	public void setIdentities(List<Member> identities) {
		this.identities = identities;
	}
	public List<FileConfig> getConfiglist() {
		return configlist;
	}
	public void setConfiglist(List<FileConfig> configlist) {
		this.configlist = configlist;
	}
	

}
