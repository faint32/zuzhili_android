/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zuzhili.framework.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.ServerError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import java.io.UnsupportedEncodingException;
import java.util.Map;
/**
 * A request for retrieving a {@link JSONArray} response body at a given URL.
 */
public class JsonArrayRequest extends JsonRequest<JSONArray> {
	
	/**
	 * Method POST request params map
	 */
	private final Map<String, String> mParams;
	
    /**
     * Creates a new request.
     * @param url URL to fetch the JSON from
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public JsonArrayRequest(String url
    		, Map<String, String> params
    		, Listener<JSONArray> listener
    		, ErrorListener errorListener) {
    	super(Method.POST, url, null, listener, errorListener);
    	this.mParams = params;
    }

    
    public JsonArrayRequest(int method
    		, String url
    		, Map<String, String> params
    		, Listener<JSONArray> listener
    		, ErrorListener errorListener) {
    	super(method, url, null, listener, errorListener);
    	this.mParams = params;
    }

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        try {
            String json =
                new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            if(isDataValid(json)) {
            		return Response.success(getJsonArray(json),
            				HttpHeaderParser.parseCacheHeaders(response));
            } else {
            	return Response.error(new ServerError());
            }
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
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
	
	private JSONArray getJsonArray(String json) {
		JSONObject jsonObj = JSON.parseObject(json);
		if(jsonObj.containsKey("json")) {
			return jsonObj.getJSONArray("json");
		} else {
			return new JSONArray();
		}
	}
}
