package com.zuzhili.db;

/**
 * @Title: CacheType.java
 * @Package: com.zuzhili.db
 * @Description: 缓存类型
 * @author: gengxin
 * @date: 2014-1-18
 */
public class CacheType {
    /**
     * 登陆
     */
    public static final String CACHE_LOGIN = "1";
    /**
     * 动态
     */
    public static final String CACHE_GET_FEED = "2";
    /**
     * 所有成员
     */
    public static final String CACHE_GET_ALL_MEMBERS = "3";
    /**
     * 关注的成员
     */
    public static final String CACHE_GET_FOCUS_MEMBERS = "4";
    /**
     * 最近联系的成员
     */
    public static final String CACHE_GET_RECENT_CONTACT_MEMBERS = "5";
    /**
     * 社区列表
     */
    public static final String CACHE_ALL_SOCIALS = "6";
    /**
     * @ 我的内容列表
     */
    public static final String CACHE_GET_AT_ME_CONTENT_INFO = "7";
    /**
     * @ 我的评论列表
     */
    public static final String CACHE_GET_AT_ME_COMMENT_INFO = "8";
    /**
     * 收到的评论
     */
    public static final String CACHE_GET_RECEIVED_COMMENTS = "9";
    /**
     * 发出的评论
     */
    public static final String CACHE_GET_SENDED_COMMENTS = "10";
    /**
     * 获取特定用户的动态
     */
    public static final String CACHE_GET_SPECIFIC_USER_TRENDS = "11";
    /**
     * 获取特定群组的动态
     */
    public static final String CACHE_GET_SPECIFIC_GROUP_TRENDS = "12";

    /**
     * 获取用户收藏列表
     */
    public static final String CACHE_GET_USER_COLLECTION = "13";
}
