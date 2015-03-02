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

import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class StringRequest extends Request<String> {
	
	private final Map<String, String> mParams;
	
    private final Listener<String> mListener;

    /**
     * Creates a new request with the given method.
     *
     * @param method the request {@link Method} to use
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public StringRequest(int method, String url, Map<String, String> params, Listener<String> listener,
            ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
        mParams = params;
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }
    
    @Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mParams;
	}

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
    	String parsed;
        try {
            parsed = new String(response.data, "utf-8");
            if(isDataValid(parsed)) {
				return Response.success(getJsonString(parsed),
						HttpHeaderParser.parseCacheHeaders(response));
			}  else {
				return Response.error(new ServerError());
			}
        } catch (UnsupportedEncodingException e) {
        	return Response.error(new ParseError(e));
        } catch (JSONException e) {
			return Response.error(new ParseError(e));
		}
    }
    
    /**
	 * errmsg with "ok" indicates correct response, or else indicates an server error
	 * @param json
	 * @return
	 */
	private boolean isDataValid(String json) {
		JSONObject jsonObj = JSON.parseObject(json);
		String title = jsonObj.getString("result");
		if(title != null && title.equals("true"))
			return true;
		return false;
	}
	
	private String getJsonString(String json) {
		JSONObject jsonObj = JSON.parseObject(json);
		if (jsonObj.containsKey("json")) {
			return jsonObj.getString("json");
		}
        if (jsonObj.containsKey("list")) {
			return jsonObj.getString("list");
		}
        if (jsonObj.containsKey("commentlist")) {
            return jsonObj.getString("commentlist");
        }
		return "";
	}

}
