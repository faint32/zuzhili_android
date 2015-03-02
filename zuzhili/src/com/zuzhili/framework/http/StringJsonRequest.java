package com.zuzhili.framework.http;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.exception.BusinessError;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class StringJsonRequest extends Request<String> {
    private final Listener<String> mListener;
    /**
	 * Method POST request params map
	 */
	private final Map<String, String> mParams;

    /**
     * Creates a new request with the given method.
     *
     * @param method the request {@link Method} to use
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public StringJsonRequest(int method, String url, Map<String,String> params, Listener<String> listener, ErrorListener errorListener) {
        super(method, url,errorListener);
        this.mParams = params;
        mListener = listener;
    }

    /**
     * Creates a new GET request.
     *
     * @param url URL to fetch the string at
     * @param listener Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public StringJsonRequest(String url, Map<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        this(Method.POST, url, params, listener, errorListener);
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
        try {
        	String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        	LogUtils.d(json);
        	if(isDataValid(json)) {
        		return Response.success(json, HttpHeaderParser.parseCacheHeaders(response));
        	} else {
        		return Response.error(new BusinessError(json));
        	}
        } catch (UnsupportedEncodingException e) {
        	return Response.error(new ParseError(e));
        } catch(JSONException e){
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

        String errmsg = jsonObj.getString("errmsg");
        if(errmsg != null && errmsg.equals("ok"))
            return true;

        String result = jsonObj.getString("result");
		if(result != null && result.equals("true"))
            return true;
        return false;
	}
}

