package com.zuzhili.db;

public class DBCache {
	private String _id;
	private String cachetype;           //可自定义常量集CacheType 区分cachetype;
	private String jsondata;
	private String time;
	private String identify;            //可用标识字符串拼接方式区分，如：list+ids+folderid的方式(63.170.1234)

    public String get_Id() {
        return _id;
    }

    public void setId(String id) {
        this._id = id;
    }

    public String getCachetype() {
		return cachetype;
	}
	public void setCachetype(String cachetype) {
		this.cachetype = cachetype;
	}
	public String getJsondata() {
		return jsondata;
	}
	public void setJsondata(String jsondata) {
		this.jsondata = jsondata;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getIdentify() {
		return identify;
	}
	public void setIdentify(String identify) {
		this.identify = identify;
	}
	
	
}
