package com.zuzhili.framework.http;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.zuzhili.db.DBCache;
import com.zuzhili.db.DBHelper;
import com.zuzhili.exception.BusinessError;
import com.zuzhili.model.Account;

public class FastJsonRequest<T> extends Request<T>{
	/**
	 * Class type for the response
	 */
	private final Class<T> mClass;
	
	/**
	 * Method POST request params map
	 */
	private final Map<String, String> mParams;
	
	/**
	 * Callback for response delivery 
	 */
	private final Listener<T> mListener;
	private boolean isCache = false;
	private DBHelper dbHelper;
	private String cachetype;
	private String identify;
	
	
	
	
	public boolean isCache() {
		return isCache;
	}

	public void setCache(boolean isCache) {
		this.isCache = isCache;
	}

	public DBHelper getDbHelper() {
		return dbHelper;
	}

	public void setDbHelper(DBHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	public String getCachetype() {
		return cachetype;
	}

	public void setCachetype(String cachetype) {
		this.cachetype = cachetype;
	}

	public String getIdentify() {
		return identify;
	}

	public void setIdentify(String identify) {
		this.identify = identify;
	}

	/**
	 * @param url
	 * 		path for the requests
	 * @param objectClass
	 * 		expected class type for the response. Used by fastjson for serialization.
	 * @param listener
	 * 		handler for the response
	 * @param errorListener
	 * 		handler for errors
	 */
	public FastJsonRequest(String url
						, Map<String, String> params
						, Class<T> objectClass
						, Listener<T> listener
						, ErrorListener errorListener) {
		super(Method.POST, url, errorListener);
		this.mParams = params;
		this.mClass = objectClass;
		this.mListener = listener;
	}
	
	/**
	 * 
	 * @param url
	 * @param params
	 * @param objectClass
	 * @param listener
	 * @param errorListener
	 * @param isCache		//是否是缓存数据
	 * @param dbHelper		//DB
	 * @param cachetype		//缓存类型
	 * @param identify		//缓存唯一标识
	 */
	public FastJsonRequest(String url
			, Map<String, String> params
			, Class<T> objectClass
			, Listener<T> listener
			, ErrorListener errorListener
			, boolean isCache
			, DBHelper dbHelper
			, String cachetype
			, String identify) {
		super(Method.POST, url, errorListener);
		this.mParams = params;
		this.mClass = objectClass;
		this.mListener = listener;
		this.dbHelper = dbHelper;
		this.isCache = isCache;
		this.cachetype = cachetype;
		this.identify = identify;
	}
	
	
	
	/**
	 * 
	 * @param method
	 * 			Request type.. Method.GET,etc.
	 * @param url
	 * 			path for the requests
	 * @param params
	 * 			request params
	 * @param objectClass
	 * 			expected class type for the response. Used by gson for serialization.
	 * @param listener
	 * 			handler for the response
	 * @param errorListener
	 * 			handler for errors
	 */
	public FastJsonRequest(int method
						, String url
						, Map<String, String> params
						, Class<T> objectClass
						, Listener<T> listener
						, ErrorListener errorListener) {
		super(method, url, errorListener);
		this.mParams = params;
		this.mClass = objectClass;
		this.mListener = listener;
	}
	
	public FastJsonRequest(int method, String url,
			HashMap<String, String> params, Class<T> objectClass,
			Listener<T> listener, ErrorListener errorListener, boolean isCache,
			String cachetype, String identify, DBHelper db) {
		super(Method.POST, url, errorListener);
		this.mParams = params;
		this.mClass = objectClass;
		this.mListener = listener;
		this.isCache = isCache;
		this.dbHelper = db;
		this.identify = identify;
		this.cachetype = cachetype;
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
			if(isDataValid(json)) {
				if(isCache){
					DBCache cache = new DBCache();
					cache.setCachetype(cachetype);
					cache.setIdentify(identify);
					cache.setJsondata(json);
					dbHelper.getCacheDB().insertCacheData(cache);
				}
				return Response.success(JSON.parseObject(json, mClass), HttpHeaderParser.parseCacheHeaders(response));
			}  else {
                return Response.error(new BusinessError(json));
			}
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		} catch (JSONException e) {
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}

	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mParams;
	}
	
	/**
	 * errmsg with "ok" indicates correct response, or else indicates an server error
	 * @param json
	 * @return
	 */
	private boolean isDataValid(String json) {
		JSONObject jsonObj = JSON.parseObject(json);
		String title = jsonObj.getString("errmsg");
		if(title != null && title.equals("ok"))
			return true;
		return false;
	}
}
