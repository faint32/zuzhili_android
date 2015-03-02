package com.zuzhili.model;

import java.io.Serializable;

/**
 * 
 * @Title: BaseModel.java
 * @Package: com.zuzhili.model
 * @Description: 
 * @author: gengxin
 * @date: 2014-1-20
 */
public class BaseModel implements Serializable {
	public boolean isCache;			//是否是缓存数据

	public boolean isCache() {
		return isCache;
	}

	public void setCache(boolean isCache) {
		this.isCache = isCache;
	}
	
}
