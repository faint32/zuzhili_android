package com.zuzhili.framework.http;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.content.Context;

/**
 * Manager for the queue
 * @Title: RequestManager.java
 * @author taoliuh@gmail.com 
 * @date 2013-12-22 下午11:27:09
 * @version 0.1
 */
public class RequestManager {
	
	/**
	 * the queue :-)
	 */
	private static RequestQueue mRequestQueue;

	/**
	 * Nothing to see here.
	 */
	private RequestManager() {
	 // no instances
	} 

	/**
	 * @param context
	 * 			application context
	 */
	public static void init(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
	}

	/**
	 * @return
	 * 		instance of the queue
	 * @throws
	 * 		IllegalStatException if init has not yet been called
	 */
	public static RequestQueue getRequestQueue() {
	    if (mRequestQueue != null) {
	        return mRequestQueue;
	    } else {
	        throw new IllegalStateException("Not initialized");
	    }
	}
}
