package com.zuzhili.model.approval;
import java.io.Serializable;
public class FileConfig implements Serializable{
	private static final long serialVersionUID = -5479257577427085059L;
	private String name;
	private String path;
	private String url;
	private int type;
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
