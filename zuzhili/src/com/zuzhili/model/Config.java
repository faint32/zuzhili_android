package com.zuzhili.model;

import java.io.Serializable;

public class Config implements Serializable{
	
	private static final long serialVersionUID = 3556437211091674206L;
	private String id;
	private String nid;
	private String url;
	private String name;
	private String path;
	private String path150;
	private String sourcepath;
	private String type;
	private String typeid;
	private String desc;
	
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getPath150() {
		return path150;
	}
	public void setPath150(String path_150) {
		this.path150 = path_150;
	}
	public String getSourcepath() {
		return sourcepath;
	}
	public void setSourcepath(String path_src) {
		this.sourcepath = path_src;
	}
	public String getNid() {
		return nid;
	}
	public void setNid(String nid) {
		this.nid = nid;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getTypeid() {
		return typeid;
	}
	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}

}
