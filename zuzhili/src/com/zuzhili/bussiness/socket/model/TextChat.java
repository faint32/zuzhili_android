package com.zuzhili.bussiness.socket.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;

/**
 * Created by liutao on 14-4-14.
 */
public class TextChat {

    /** chat message type, "text" */
    private String type;

    /** message body */
    private String body;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public TextChat parse(String[] result) {
        JSONObject jsonObject = JSON.parseObject(result[2]);
        this.body = jsonObject.getString("body");
        this.type = jsonObject.getString("type");
        return this;
    }

    public TextChat parse(String content) {
        JSONObject jsonObject = JSON.parseObject(content);
        this.body = jsonObject.getString("body");
        this.type = jsonObject.getString("type");
        return this;
    }
}
