package com.zuzhili.bussiness.utility;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.zuzhili.framework.Session;

/**
 * Created by liutao on 14-7-22.
 */
public class IMParseUtil {

    private static IMParseUtil instance;

    private static Session mSession;

    private IMParseUtil() {

    }

    public static IMParseUtil getInstance(Session session) {
        if (instance == null) {
            instance = new IMParseUtil();
            mSession = session;
        }
        return instance;
    }

    public static JSONObject parseUserData(String userData) throws IllegalArgumentException {
        if (userData == null) {
            throw new IllegalArgumentException("userData is null");
        }
        return JSONObject.parseObject(userData);
    }

    public static String getListId(String userData) {
        try {
            JSONObject jsonObject = parseUserData(userData);
            if (jsonObject.containsKey("id")) {
                return jsonObject.getString("id");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "0";
    }

    public static String getSenderAvatar(String userData) {
        try {
            JSONObject jsonObject = parseUserData(userData);
            if (jsonObject.containsKey("h")) {
                return jsonObject.getString("h");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getSenderUserName(String userData) {
        try {
            JSONObject jsonObject = parseUserData(userData);
            if (jsonObject.containsKey("n")) {
                return jsonObject.getString("n");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getIds(String userData) {
        try {
            JSONObject jsonObject = parseUserData(userData);
            if (jsonObject.containsKey("ids")) {
                return jsonObject.getString("ids");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "0";
    }

    public static String getYunGroupId(String userData) {
        try {
            JSONObject jsonObject = parseUserData(userData);
            if (jsonObject.containsKey("y")) {
                return jsonObject.getString("y");
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "0";
    }

    public String buildIdentity(String userData) {
        StringBuilder builder = new StringBuilder();
        builder.append(getListId(userData))
                .append(Constants.SYMBOL_PERIOD)
                .append(mSession.getAccount().getIdsViaListId(getListId(userData)));
        return  builder.toString();
    }

}
