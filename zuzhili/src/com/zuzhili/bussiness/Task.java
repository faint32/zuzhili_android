package com.zuzhili.bussiness;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.db.CacheDataBase;
import com.zuzhili.db.DBCache;
import com.zuzhili.db.DBHelper;
import com.zuzhili.framework.TaskApp;
import com.zuzhili.framework.http.FastJsonRequest;
import com.zuzhili.framework.http.RequestManager;
import com.zuzhili.framework.http.StringJsonRequest;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.BaseModel;
import com.zuzhili.model.Common;
import com.zuzhili.ui.activity.BaseActivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class Task {
    // 真实的地址
    public static final String API_HOST_URL =
//            "http://www.zuzhili.com/mobile/";
                "http://test.zuzhili.com/mobile/";
//            "http://192.168.40.116/zzlfront/mobile/";

    // im的地址
    public static final String API_CHAT_URL =
            "http://chat.zuzhili.com/chat/";
//            "http://192.168.40.107/zzlfront/mobile/";

    public static final String API_SANWEI_HOST = "http://121.42.53.110:8080/zhengcfw/";

    /**
     * 发送通知类型*
     */
    public static final int NOTIFY_SEND = 0;        //发送
    public static final int NOTIFY_CANCEL = 1;        //取消

    /**
     * 登陆
     */
    public static final String ACTION_LOGIN = "0";

    /**
     * 注册获取验证码
     */
    public static final String ACTION_PHONECHECK_REGISTER = "1";

    /**
     * 注册
     */
    public static final String ACTION_REGSITER = "2";

    /**
     * 获取动态信息流
     */
    public static final String ACITON_GET_FEED = "3";

    /**
     * 获取关于动态的评论
     */
    public static final String ACITON_GET_FEED_COMMENT = "4";

    /**
     * 获取社区所有成员
     */
    public static final String ACITON_GET_ALL_MEMBERS = "5";

    /**
     * 获取我关注的成员
     */
    public static final String ACITON_GET_FOCUS_MEMBERS = "6";

    /**
     * 搜索社区
     */
    public static final String ACTION_SEARCH_SOCIAL = "7";

    /**
     * 获取@ 我的内容
     */
    public static final String ACTION_GET_AT_ME_CONTENT_INFO = "8";

    /**
     * 获取@ 我的评论
     */
    public static final String ACTION_GET_AT_ME_COMMENT_INFO = "9";

    /**
     * 获取某一条动态
     */
    public static final String ACTION_GET_SPECIFIC_TREND = "10";

    /**
     * 收藏一条动态
     */
    public static final String ACTION_ADD_COLLECTION = "11";

    /**
     * 取消收藏一条动态
     */
    public static final String ACTION_CANCEL_COLLECTION = "12";

    /**
     * 收到的评论
     */
    public static final String ACTION_GET_RECEIVED_COMOMENTS = "13";

    /**
     * 发出的评论
     */
    public static final String ACTION_GET_SENDED_COMOMENTS = "14";

    /**
     * 删除一条评论
     */
    public static final String ACTION_DELETE_COMOMENT = "15";

    /**
     * 获取特定用户的动态
     */
    public static final String ACTION_GET_SPECIFIC_USER_TRENDS = "16";

    /**
     * 检测版本信息
     */
    public static final String ACTION_CHECK_NEW_VERSION = "17";

    /**
     * 发布文字*
     */
    public static final String ACTION_PUBLISH_WRITE = "20";
    /**
     * 发布图片*
     */
    public static final String ACTION_PUBLISH_PIC = "21";
    /**
     * 新建图片册*
     */
    public static final String ACTION_CREATE_ALBUM = "23";
    /**
     * 图片册列表*
     */
    public static final String ACTION_GET_ALBUM_LIST = "24";
    /**
     * 音频列表*
     */
    public static final String ACTION_GET_MUSIC_FOLDER_LIST = "25";
    /**
     * 视频列表*
     */
    public static final String ACTION_GET_VEDIO_FOLDER_LIST = "26";
    /**
     * 创建音频文件夹*
     */
    public static final String ACTION_CREATE_MUSIC_FOLDER = "27";
    /**
     * 创建视频文件夹*
     */
    public static final String ACTION_CREATE_VEDIO_FOLDER = "28";
    /**
     * 创建普通文件夹*
     */
    public static final String ACTION_CREATE_FILE_FOLDER = "29";
    /**
     * 发布音频*
     */
    public static final String ACTION_PUBLISH_MUSIC = "30";
    /**
     * 发布视频*
     */
    public static final String ACTION_PUBLISH_VEDIO = "31";
    /**
     * 文件夹列表*
     */
    public static final String ACTION_FOLDER_LIST = "32";
    /**
     * 发布文件*
     */
    public static final String ACTION_PUBLISH_FILE = "33";
    /**
     * 私信相关
     */
    public static final String ACTION_MSG_LIST = "41";

    /*
     * 私信删除
	 */
    public static final String ACTION_MSG_DEL = "42";

    /*私信添加
     */
    public static final String ACTION_MSG_ADD = "43";

    /*
     *获取私信详细列表
     */
    public static final String ACTION_MSG_DETAILLIST = "44";
    /*
     * 验证手机号接口
	 */
    public static final String ACTION_CODECHECK = "45";

    /*
     * 发表评论
     */
    public static final String ACTION_PUBLISHCOMMENT = "46";

    /*
     * 转发
     */
    public static final String ACTION_PUBLISHTEEND = "47";

    //申请加入社区
    public static final String ACTION_APPLYSOCIA = "48";

    //发送反馈
    public static final String ACTION_PUB_FEEDBACK = "49";

    //修改密码
    public static final String ACTION_MODIFYPAS = "50";

    //获取文章详情
    public static final String ACTION_ARTICLEDETAIL = "51";

    //我收到的申请
    public static final String ACTION_APPROVALS = "52";

    /**
     * 获取所有群组
     */
    public static final String ACTION_GET_GROUPS = "53";

    /**
     * 获取特定用户的动态
     */
    public static final String ACTION_INVITE = "54";

    /**
     * 找回密码获取验证码
     */
    public static final String ACTION_PHONECHECK_FIND_PSW = "55";

    /**
     * 获取聊天所有用户
     */
    public static final String ACTION_GET_ALL_USERS = "56";

    /**
     * 获取我所在的所有社区
     */
    public static final String ACTION_GET_MY_SOCIALS = "57";

    /**
     * 创建群组
     */
    public static final String ACTION_CREATE_GROUP = "58";

    /**
     * 邀请成员
     */
    public static final String ACTION_INVITE_JOIN = "59";

    /**
     * 获取单个群组
     */
    public static final String ACTION_GET_GROUP = "60";

    /**
     * 删除群组用户
     */
    public static final String ACTION_DELETE_GROUP_MEMBWE = "61";

    /**
     * 修改群组信息
     */
    public static final String ACTION_MODIFY_GROUP = "62";

    /**
     * 删除群组
     */
    public static final String ACTION_DELETE_GROUP = "63";

    /**
     * 用户退出群组
     */
    public static final String ACTION_TO_OUT_GROUP = "64";

    /**
     * 查询群组成员
     */
    public static final String ACTION_QUERY_MEMBER = "65";

    /**
     * 用户设置群消息接受规则
     */
    public static final String ACTION_SET_GROUPMSG_USER = "66";

    /**
     * 获取云通讯帐号相关信息
     */
    public static final String ACTION_GET_YTX_ACCOUNT = "67";

    /**
     * 非组织力社区注册
     */
    public static final String ACTION_REGSITER_NON_ZUZHILI = "68";

    /**
     * 登陆非组织力社区
     */
    public static final String ACTION_LOGIN_NON_ZUZHILI = "69";


    /**
     * 根据关键字查询群组、用户
     */
    public static final String ACTION_QUERY_GROUPS_USERS_BY_KEY = "70";

    /**
     * 查询聊天记录
     */
    public static final String ACTION_HISTORY_MSG = "71";

    /**
     * 查询聊天记录
     */
    public static final String ACTION_QUERY_ALL_GROUPS_USERS_BY_KEY = "72";

    /**
     * 查询用户空间简介
     */
    public static final String ACTION_QUERY_USER_SUMMARY = "73";

    /**
     * 查询群组空间简介
     */
    public static final String ACTION_QUERY_GROUP_SUMMARY = "74";

    /**
     * 查询我关注的人
     */
    public static final String ACTION_QUERY_MY_LOVERS = "75";

    /**
     * 查询关注我的人
     */
    public static final String ACTION_QUERY_PEOPLE_LOVE_ME = "76";

    /**
     * 查询群组成员
     */
    public static final String ACTION_QUERY_GROUP_MEMBERS = "77";

    /**
     * 查询指定群组动态
     */
    public static final String ACTION_GET_SPECIFIC_GROUP_TRENDS = "78";

    /**
     * 添加或者取消关注
     */
    public static final String ACTION_ADD_FOLLOW = "79";

    /**
     * 请求加入空间
     */
    public static final String ACTION_REQUEST_JOIN_SPACE = "80";

    /**
     * 请求退出空间
     */
    public static final String ACTION_REQUEST_QUIT_SPACE = "81";

    /**
     * 查询收藏列表
     */
    public static final String ACTION_QUERY_COLLECTION = "82";

    /**
     * 查询空间管理员
     */
    public static final String ACTION_QUERY_SPACE_ADMINS = "83";

    /**
     * 获取相册照片
     */
    public static final String ACTION_GET_ALBUM_PHOTO = "100";

    /** 获取图片评论 */
    public static final String ACTION_GET_PHOTO_COMMENT = "101";

    /** 发表图片评论*/
    public static final String ACTION_PUBLISH_PIC_COMMENT = "102";

    /** 回复图片评论*/
    public static final String ACTION_PUBLISH_PIC_ADDCOMMENT = "103";

    /** 删除图片评论*/
    public static final String ACTION_DELETE_PIC_COMMENT = "104";

    public static final String ACTION_GET_UNREAD_MSG_COUNT = "150";

    /**是否有权限查看*/
    public static final String ACTION_QUERY_HAVE_Auth = "151";

    /*
     * 加载所有的地址
     */
    public static Map<String, String> API_URLS = new HashMap<String, String>();


    // 初始化所有的地址
    public static void init() {
        API_URLS.put(ACTION_LOGIN, "user_login.json");
        API_URLS.put(ACTION_LOGIN_NON_ZUZHILI, "user_login_san.json");
        API_URLS.put(ACTION_PHONECHECK_REGISTER, "user_registerPhoneConfirm.json");
        API_URLS.put(ACTION_PHONECHECK_FIND_PSW, "user_checkPhoneConfirm.json");
        API_URLS.put(ACTION_REGSITER, "user_register.json");
        API_URLS.put(ACTION_REGSITER_NON_ZUZHILI, "user_register_listid.json");
        API_URLS.put(ACITON_GET_FEED, "abs_queryabsbyids.json");
        API_URLS.put(ACITON_GET_ALL_MEMBERS, "identity_queryUsersInNetWithFocusFlag.json");
        API_URLS.put(ACITON_GET_FOCUS_MEMBERS, "identity_getMyLovers.json");
        API_URLS.put(ACTION_CODECHECK, "user_toCheckPhoneConfirm.json");
        API_URLS.put(ACTION_PUBLISH_WRITE, "publish_text.json");
        API_URLS.put(ACTION_PUBLISH_PIC, "publish_pic.json");
        API_URLS.put(ACTION_MSG_LIST, "pl_getMsgUserList.json");
        API_URLS.put(ACTION_CREATE_ALBUM, "photo_createAlbum.json");
        API_URLS.put(ACTION_GET_ALBUM_LIST, "photo_getAlbumeList.json");
        API_URLS.put(ACTION_MSG_DEL, "pl_deleteMsgUser.json");
        API_URLS.put(ACTION_MSG_ADD, "pl_sendMsg.json");
        API_URLS.put(ACTION_MSG_DETAILLIST, "pl_getNewestMsgTalkList.json");
        API_URLS.put(ACTION_GET_MUSIC_FOLDER_LIST, "music_getMusicFolders.json");
        API_URLS.put(ACTION_GET_VEDIO_FOLDER_LIST, "music_getVideoFolders.json");
        API_URLS.put(ACTION_CREATE_MUSIC_FOLDER, "music_createMusicFolder.json");
        API_URLS.put(ACTION_CREATE_VEDIO_FOLDER, "music_createVideoFolder.json");
        API_URLS.put(ACTION_CREATE_FILE_FOLDER, "folder_createFolder.json");
        API_URLS.put(ACTION_PUBLISH_MUSIC, "publish_music.json");
        API_URLS.put(ACITON_GET_FEED_COMMENT, "comment_queryAllCommentsByAbsid.json");
        API_URLS.put(ACTION_PUBLISH_VEDIO, "publish_video.json");
        API_URLS.put(ACTION_FOLDER_LIST, "folder_getFolderList.json");
        API_URLS.put(ACTION_PUBLISH_FILE, "publish_file.json");
        API_URLS.put(ACTION_PUBLISHCOMMENT, "comment_addcomment.json");
        API_URLS.put(ACTION_PUBLISHTEEND, "abs_addforward.json");
        API_URLS.put(ACTION_SEARCH_SOCIAL, "list_searchListsWithRelation.json");
        API_URLS.put(ACTION_APPLYSOCIA, "user_joinlist.json");
        API_URLS.put(ACTION_GET_AT_ME_CONTENT_INFO, "abs_getAtAbs.json");
        API_URLS.put(ACTION_GET_AT_ME_COMMENT_INFO, "comment_getatMyComments.json");
        API_URLS.put(ACTION_GET_SPECIFIC_TREND, "abs_queryabsbyabsid.json");
        API_URLS.put(ACTION_ADD_COLLECTION, "collection_addcollection.json");
        API_URLS.put(ACTION_CANCEL_COLLECTION, "collection_deleteCollectionByAbsidAndIds.json");
        API_URLS.put(ACTION_PUB_FEEDBACK, "user_feedback.json");
        API_URLS.put(ACTION_MODIFYPAS, "user_modifyPassword.json");
        API_URLS.put(ACTION_GET_RECEIVED_COMOMENTS, "comment_getMyRecivedComments.json");
        API_URLS.put(ACTION_GET_SENDED_COMOMENTS, "comment_getFromComments.json");
        API_URLS.put(ACTION_DELETE_COMOMENT, "comment_deleteCommentById.json");
        API_URLS.put(ACTION_ARTICLEDETAIL, "abs_queryarticlebyabsid.json");
        API_URLS.put(ACTION_APPROVALS, "approval_getApprovals.json");
        API_URLS.put(ACTION_GET_SPECIFIC_USER_TRENDS, "queryAbs_Personal.json");
//        API_URLS.put(ACTION_GET_SPECIFIC_USER_TRENDS, "abs_queryuserabsbyids.json");
        API_URLS.put(ACTION_CHECK_NEW_VERSION, "detectNewApkVersion.json");
        API_URLS.put(ACTION_GET_GROUPS, "getGroups.json");
        API_URLS.put(ACTION_INVITE, "inviteFriendsByPhone.json");
        API_URLS.put(ACTION_GET_ALL_USERS, "getAllUser.json");
        API_URLS.put(ACTION_GET_MY_SOCIALS, "list_getMySocial.json");
        API_URLS.put(ACTION_CREATE_GROUP, "createGroup.json");
        API_URLS.put(ACTION_INVITE_JOIN, "inviteJoinGroup.json");
        API_URLS.put(ACTION_GET_GROUP, "getGroup.json");
        API_URLS.put(ACTION_DELETE_GROUP_MEMBWE, "deleteGroupMember.json");
        API_URLS.put(ACTION_MODIFY_GROUP, "modifyGroup.json");
        API_URLS.put(ACTION_DELETE_GROUP, "deleteGroup.json");
        API_URLS.put(ACTION_TO_OUT_GROUP, "toOutGroup.json");
        API_URLS.put(ACTION_QUERY_MEMBER, "queryMember.json");
        API_URLS.put(ACTION_SET_GROUPMSG_USER, "setGroupMsg_user.json");
        API_URLS.put(ACTION_GET_YTX_ACCOUNT, "getLoginUser.json");
        API_URLS.put(ACTION_QUERY_GROUPS_USERS_BY_KEY, "getGroupsUsers_byKey.json");
        API_URLS.put(ACTION_QUERY_ALL_GROUPS_USERS_BY_KEY, "getZGroupsByParentid.json");
        API_URLS.put(ACTION_HISTORY_MSG, "getMsgs_curDate.json");
        API_URLS.put(ACTION_QUERY_USER_SUMMARY, "getCountsArray_Personal.json");
        API_URLS.put(ACTION_QUERY_GROUP_SUMMARY, "getCountsArray_Space.json");
        API_URLS.put(ACTION_QUERY_MY_LOVERS, "identity_getMyLovers.json");
        API_URLS.put(ACTION_QUERY_PEOPLE_LOVE_ME, "identity_getLoveMeUser.json");
        API_URLS.put(ACTION_QUERY_GROUP_MEMBERS, "space_getMembers.json");
        API_URLS.put(ACTION_GET_SPECIFIC_GROUP_TRENDS, "queryAbs_Space.json");
		API_URLS.put(ACTION_GET_ALBUM_PHOTO, "photo_albumPhoto.json");
		API_URLS.put(ACTION_GET_PHOTO_COMMENT, "photo_photoGetCommentList.json");
		API_URLS.put(ACTION_PUBLISH_PIC_COMMENT, "photo_photoAddComment.json");
		API_URLS.put(ACTION_PUBLISH_PIC_ADDCOMMENT, "comment_addcomment.json");
		API_URLS.put(ACTION_DELETE_PIC_COMMENT, "comment_deleteCommentById.json");
        API_URLS.put(ACTION_ADD_FOLLOW, "identity_UserFocusOp.json");
        API_URLS.put(ACTION_GET_UNREAD_MSG_COUNT, "list_getRefreshInfo.json");
        API_URLS.put(ACTION_REQUEST_JOIN_SPACE, "space_request_addspace.json");
        API_URLS.put(ACTION_REQUEST_QUIT_SPACE, "space_request_quitspace.json");
        API_URLS.put(ACTION_QUERY_COLLECTION, "abs_getCollectionAbs.json");
        API_URLS.put(ACTION_QUERY_SPACE_ADMINS, "getAdmin_Space.json");
        API_URLS.put(ACTION_QUERY_HAVE_Auth, "abs_queryHaveAuth.json");
    }

    public static final void querySpaceAdmins(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_QUERY_SPACE_ADMINS), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_QUERY_SPACE_ADMINS), params);
        RequestManager.getRequestQueue().add(request);
    }

    public static final void queryUserCollection(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_QUERY_COLLECTION), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_QUERY_COLLECTION), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 申请加入空间
     *
     * @param params
     * @param listener
     * @param errorListener
     */
    public static void requestJoinSpace(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_REQUEST_JOIN_SPACE), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_REQUEST_JOIN_SPACE), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 申请退出空间
     *
     * @param params
     * @param listener
     * @param errorListener
     */
    public static void requestQuitSpace(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_REQUEST_QUIT_SPACE), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_REQUEST_QUIT_SPACE), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 用户 添加关注或者取消关注
     *
     * @param params
     * @param listener
     * @param errorListener
     */
    public static void changeFollowState(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_ADD_FOLLOW), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_ADD_FOLLOW), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 查询群组成员
     *
     * @param params
     * @param listener
     * @param errorListener
     */
    public static void queryGroupMembers(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_QUERY_GROUP_MEMBERS), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_QUERY_GROUP_MEMBERS), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * Query people who were followed by me.
     *
     * @param params
     * @param listener
     * @param errorListener
     */
    public static void queryMyLovers(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_QUERY_MY_LOVERS), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_QUERY_MY_LOVERS), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * Query people who follow me.
     *
     * @param params
     * @param listener
     * @param errorListener
     */
    public static void queryMyFans(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_QUERY_PEOPLE_LOVE_ME), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_QUERY_PEOPLE_LOVE_ME), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * Query user space summary
     *
     * @param params
     * @param listener
     * @param errorListener
     */
    public static void queryUserSpaceSummary(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_QUERY_USER_SUMMARY), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_QUERY_USER_SUMMARY), params);
        RequestManager.getRequestQueue().add(request);
    }


    /**
     * Query group space summary
     *
     * @param params
     * @param listener
     * @param errorListener
     */
    public static void queryGroupSpaceSummary(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_QUERY_GROUP_SUMMARY), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_QUERY_GROUP_SUMMARY), params);
        RequestManager.getRequestQueue().add(request);
    }

    public static void getApprovals(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_APPROVALS), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_APPROVALS), params);
        RequestManager.getRequestQueue().add(request);
    }

    public static void getArticleDetail(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_ARTICLEDETAIL), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_ARTICLEDETAIL), params);
        RequestManager.getRequestQueue().add(request);
    }

    //修改密码
    public static void modifyPass(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_MODIFYPAS), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_MODIFYPAS), params);
        RequestManager.getRequestQueue().add(request);
    }

    //发送反馈
    public static void sendFeedBack(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_PUB_FEEDBACK), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_PUB_FEEDBACK), params);
        RequestManager.getRequestQueue().add(request);
    }

    public static void applySocial(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_APPLYSOCIA), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_APPLYSOCIA), params);
        RequestManager.getRequestQueue().add(request);
    }

    //获取私信列表
    public static void getMsgList(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_MSG_LIST), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_MSG_LIST), params);
        RequestManager.getRequestQueue().add(request);
    }

    //发布私信
    public static void addMsg(HashMap<String, String> params, Listener<Common> listener, ErrorListener errorListener) {
        FastJsonRequest<Common> request = new FastJsonRequest<Common>(Method.POST, API_HOST_URL + API_URLS.get(ACTION_MSG_ADD), params, Common.class, listener, errorListener);
        Utils.I(request.toString());
        RequestManager.getRequestQueue().add(request);
    }

    //获取私信对话详情
    public static void getDetailMsgList(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_MSG_DETAILLIST), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_MSG_DETAILLIST), params);
        RequestManager.getRequestQueue().add(request);
    }

    //删除私信通过id
    public static void delMsgById(HashMap<String, String> params, Listener<Common> listener, ErrorListener errorListener) {
        FastJsonRequest<Common> request = new FastJsonRequest<Common>(Method.POST, API_HOST_URL + API_URLS.get(ACTION_MSG_DEL), params, Common.class, listener, errorListener);
        Utils.I(request.toString());
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 检测新版本
     */
    public static void checkNewVersion(Context context, Listener<String> listener, ErrorListener errorListener) {
        final HashMap<String, String> params = new HashMap<String, String>(2);
        params.put("current_version_code", String.valueOf(Utils.getVersionCode(context)));
        params.put("from", Constants.DEVICE_TYPE_ANDROID);
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_CHECK_NEW_VERSION), params, listener, errorListener);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 登陆相关接口
     */
    public static void login(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_LOGIN), params, listener, errorListener);
        Utils.I(request.toString());
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_LOGIN), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 登陆非组织力社区
     */
    public static void loginNonZuzhili(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_LOGIN_NON_ZUZHILI), params, listener, errorListener);
        Utils.I(request.toString());
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_LOGIN_NON_ZUZHILI), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 获取注册验证码
     */
    public static void getRegiterVerificationCode(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_PHONECHECK_REGISTER), params, listener, errorListener);
        Utils.I(request.toString());
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 获取重设密码验证码
     */
    public static void getResetPasswordVerificationCode(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_PHONECHECK_FIND_PSW), params, listener, errorListener);
        Utils.I(request.toString());
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 注册
     */
    public static void register(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_REGSITER), params, listener, errorListener);
        Utils.I(request.toString());
        RequestManager.getRequestQueue().add(request);
    }


    /**
     * 非组织力社区注册
     */
    public static void registerNonZuzhii(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_REGSITER_NON_ZUZHILI), params, listener, errorListener);
        Utils.I(request.toString());
        RequestManager.getRequestQueue().add(request);
    }

    /*
     * 验证手机号和验证码是否正确
     */
    public static void validatePhoneCode(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_CODECHECK), params, listener, errorListener);
        Utils.I(request.toString());
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 发布文字*
     */
    public static void publishWrite(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        showParamsInLog(API_HOST_URL + API_URLS.get(ACTION_PUBLISH_WRITE), params);
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_PUBLISH_WRITE), params, listener, errorListener);
        Utils.I(request.toString());
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 发布评论*
     */
    public static void publishComment(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        showParamsInLog(API_HOST_URL + API_URLS.get(ACTION_PUBLISHCOMMENT), params);
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_PUBLISHCOMMENT), params, listener, errorListener);
        Utils.I(request.toString());
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 回复图片评论
     */
    public static void publishAddComment(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        showParamsInLog(API_HOST_URL + API_URLS.get(ACTION_PUBLISH_PIC_ADDCOMMENT), params);
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_PUBLISH_PIC_ADDCOMMENT), params, listener, errorListener);
        Utils.I(request.toString());
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 删除自己对图片评论
     */
    public static void deletePicComment(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        showParamsInLog(API_HOST_URL + API_URLS.get(ACTION_DELETE_PIC_COMMENT), params);
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_DELETE_PIC_COMMENT), params, listener, errorListener);
        Utils.I(request.toString());
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 发布图片评论*
     */
    public static void publishPicComment(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        showParamsInLog(API_HOST_URL + API_URLS.get(ACTION_PUBLISH_PIC_COMMENT), params);
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_PUBLISH_PIC_COMMENT), params, listener, errorListener);
        Utils.I(request.toString());
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 发布评论
     */
    public static void publishTrend(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        showParamsInLog(API_HOST_URL + API_URLS.get(ACTION_PUBLISHTEEND), params);
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_PUBLISHTEEND), params, listener, errorListener);
        Utils.I(request.toString());
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 创建图片册*
     */
    public static void createNewAlbum(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        showParamsInLog(API_HOST_URL + API_URLS.get(ACTION_CREATE_ALBUM), params);
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_CREATE_ALBUM), params, listener, errorListener);
        RequestManager.getRequestQueue().add(request);

    }

    /**
     * 图片册列表*
     */
    public static void getAlbumList(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        showParamsInLog(API_HOST_URL + API_URLS.get(ACTION_GET_ALBUM_LIST), params);
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_GET_ALBUM_LIST), params, listener, errorListener);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 音频文件夹列表*
     */
    public static void getMusicFolderList(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        showParamsInLog(API_HOST_URL + API_URLS.get(ACTION_GET_MUSIC_FOLDER_LIST), params);
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_GET_MUSIC_FOLDER_LIST), params, listener, errorListener);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 视频文件夹列表*
     */
    public static void getVedioFolderList(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        showParamsInLog(API_HOST_URL + API_URLS.get(ACTION_GET_VEDIO_FOLDER_LIST), params);
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_GET_VEDIO_FOLDER_LIST), params, listener, errorListener);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 文件夹列表*
     */
    public static void getFileFolderList(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        showParamsInLog(API_HOST_URL + API_URLS.get(ACTION_FOLDER_LIST), params);
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_FOLDER_LIST), params, listener, errorListener);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 创建文件夹*
     */
    public static void createNewFolder(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener, String folderType) {
        String url = null;
        LogUtils.e(folderType);
        if (folderType.equals(Constants.MULTI_FOLDER_TYPE_MUSIC)) {
            url = API_HOST_URL + API_URLS.get(ACTION_CREATE_MUSIC_FOLDER);
        } else if (folderType.equals(Constants.MULTI_FOLDER_TYPE_VEDIO)) {
            url = API_HOST_URL + API_URLS.get(ACTION_CREATE_VEDIO_FOLDER);
        } else if (folderType.equals(Constants.FOLDER_TYPE_FILE)) {
            url = API_HOST_URL + API_URLS.get(ACTION_CREATE_FILE_FOLDER);
        }
        showParamsInLog(url, params);
        StringJsonRequest request = new StringJsonRequest(Method.POST, url, params, listener, errorListener);
        RequestManager.getRequestQueue().add(request);

    }

    /**
     * 返回缓存中的数据(可扩展)
     *
     * @param cacheData
     * @param listener
     * @param clazz
     */
    private static void setCacheResponse(DBCache cacheData, Listener listener, Class clazz) {
        BaseModel response = (BaseModel) JSON.parseObject(cacheData.getJsondata(), clazz);
        response.setCache(true);
        listener.onResponse(response);
    }

    /**
     * 获取数据库缓存数据
     *
     * @param context
     * @param cachetype
     * @param identify
     * @return
     */
    private static DBCache getCacheData(Context context, String cachetype, String identify) {
        DBHelper helper = ((TaskApp) ((BaseActivity) context).getApplication()).getDbHelper();
        CacheDataBase cacheDB = helper.getCacheDB();
        DBCache cacheData = cacheDB.getCacheData(cachetype, identify);
        return cacheData;
    }

    /**
     * 获取缓存
     */
    public static void getCache(Context context, HashMap<String, String> params,
                                Listener<String> listener, ErrorListener errorListener,
                                String cachetype, String identify) {
        DBCache cacheData = getCacheData(context, cachetype, identify);
        if (cacheData != null) {
            listener.onResponse(cacheData.getJsondata());
        } else {
            listener.onResponse(null);
        }
    }

    /**
     * 获取信息流（动态）
     */
    public static void getFeed(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACITON_GET_FEED), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACITON_GET_FEED), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 获取关于动态的评论
     */
    public static void getFeedComment(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACITON_GET_FEED_COMMENT), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACITON_GET_FEED_COMMENT), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 获取社区所有成员、或关注的成员
     */
    public static void getMembers(HashMap<String, String> params,
                                  Listener<String> listener, ErrorListener errorListener, String memberType) {
        String apiUrl;
        if (memberType.equals(Constants.TYPE_ALL_MEMBERS)) {
            apiUrl = API_URLS.get(ACITON_GET_ALL_MEMBERS);
        } else if (memberType.equals(Constants.TYPE_FOCUS_MEMBERS)) {
            apiUrl = API_URLS.get(ACITON_GET_FOCUS_MEMBERS);
        } else {
            return;
        }
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + apiUrl, params, listener, errorListener);
        printRequestUri(API_HOST_URL + apiUrl, params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 搜索社区
     */
    public static void getSocial(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_SEARCH_SOCIAL), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_SEARCH_SOCIAL), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 获取@ 我的内容
     */
    public static void getAtMeContentInfo(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_GET_AT_ME_CONTENT_INFO), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_GET_AT_ME_CONTENT_INFO), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 获取@ 我的评论
     */
    public static void getAtMeCommentInfo(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_GET_AT_ME_COMMENT_INFO), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_GET_AT_ME_COMMENT_INFO), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 根据某一条动态的id获取该动态的内容
     */
    public static void getSpecificTrend(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_GET_SPECIFIC_TREND), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_GET_SPECIFIC_TREND), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 收藏一条动态
     */
    public static void addCollection(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_ADD_COLLECTION), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_ADD_COLLECTION), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 取消收藏一条动态
     */
    public static void cancelCollection(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_CANCEL_COLLECTION), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_CANCEL_COLLECTION), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 收到的评论
     */
    public static void getReceivedComments(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_GET_RECEIVED_COMOMENTS), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_GET_RECEIVED_COMOMENTS), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 发出的评论
     */
    public static void getSendedComments(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_GET_SENDED_COMOMENTS), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_GET_SENDED_COMOMENTS), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 删除一条评论
     */
    public static void deleteComment(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_DELETE_COMOMENT), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_DELETE_COMOMENT), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 获取特定用户的动态
     */
    public static void getSpecificUserTrends(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_GET_SPECIFIC_USER_TRENDS), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_GET_SPECIFIC_USER_TRENDS), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 获取特定群组的动态
     *
     * @param params
     * @param listener
     * @param errorListener
     */
    public static void getSpecificGroupTrends(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_GET_SPECIFIC_GROUP_TRENDS), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_GET_SPECIFIC_GROUP_TRENDS), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 获取全部群组
     */
    public static void getGroups(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_CHAT_URL + API_URLS.get(ACTION_GET_GROUPS), params, listener, errorListener);
        printRequestUri(API_CHAT_URL + API_URLS.get(ACTION_GET_GROUPS), params);
        RequestManager.getRequestQueue().add(request);
    }

    /*根据关键字查询我加入的群组和用户*/
    public static void getGroupsUsersByKey(HashMap<String, String> params, Listener<String> listener, ErrorListener errorLisenter) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_CHAT_URL + API_URLS.get(ACTION_QUERY_GROUPS_USERS_BY_KEY), params, listener, errorLisenter);
        printRequestUri(API_CHAT_URL + API_URLS.get(ACTION_QUERY_GROUPS_USERS_BY_KEY), params);
        RequestManager.getRequestQueue().add(request);
    }

    /*根据关键字查询所有的群组和用户*/
    public static void getAllGroupsUsersByKey(HashMap<String, String> params, Listener<String> listener, ErrorListener errorLisenter) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_CHAT_URL + API_URLS.get(ACTION_QUERY_ALL_GROUPS_USERS_BY_KEY), params, listener, errorLisenter);
        printRequestUri(API_CHAT_URL + API_URLS.get(ACTION_QUERY_ALL_GROUPS_USERS_BY_KEY), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 获取单个群组
     */
    public static void getGroup(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_CHAT_URL + API_URLS.get(ACTION_GET_GROUP), params, listener, errorListener);
        printRequestUri(API_CHAT_URL + API_URLS.get(ACTION_GET_GROUP), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 获取特定用户的动态
     */
    public static void invite(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_INVITE), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_INVITE), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 获取聊天所有用户
     */
    public static void getAllUser(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_CHAT_URL + API_URLS.get(ACTION_GET_ALL_USERS), params, listener, errorListener);
        printRequestUri(API_CHAT_URL + API_URLS.get(ACTION_GET_ALL_USERS), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 创建群聊
     */
    public static void getCreateGroup(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_CHAT_URL + API_URLS.get(ACTION_CREATE_GROUP), params, listener, errorListener);
        printRequestUri(API_CHAT_URL + API_URLS.get(ACTION_CREATE_GROUP), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 邀请加入群聊
     */
    public static void inviteJoinGroup(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_CHAT_URL + API_URLS.get(ACTION_INVITE_JOIN), params, listener, errorListener);
        printRequestUri(API_CHAT_URL + API_URLS.get(ACTION_INVITE_JOIN), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 获取我所在的所有社区
     */
    public static void getMySocials(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_GET_MY_SOCIALS), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_GET_MY_SOCIALS), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 删除群组成员
     */
    public static void deleteGroupMember(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_CHAT_URL + API_URLS.get(ACTION_DELETE_GROUP_MEMBWE), params, listener, errorListener);
        printRequestUri(API_CHAT_URL + API_URLS.get(ACTION_DELETE_GROUP_MEMBWE), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 修改群组信息
     */
    public static void modifyGroup(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_CHAT_URL + API_URLS.get(ACTION_MODIFY_GROUP), params, listener, errorListener);
        printRequestUri(API_CHAT_URL + API_URLS.get(ACTION_MODIFY_GROUP), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 删除群组
     */
    public static void deleteGroup(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_CHAT_URL + API_URLS.get(ACTION_DELETE_GROUP), params, listener, errorListener);
        printRequestUri(API_CHAT_URL + API_URLS.get(ACTION_DELETE_GROUP), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 用户退出群组
     */
    public static void toOutGroup(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_CHAT_URL + API_URLS.get(ACTION_TO_OUT_GROUP), params, listener, errorListener);
        printRequestUri(API_CHAT_URL + API_URLS.get(ACTION_TO_OUT_GROUP), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 查询群组成员
     */
    public static void queryMember(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_CHAT_URL + API_URLS.get(ACTION_QUERY_MEMBER), params, listener, errorListener);
        printRequestUri(API_CHAT_URL + API_URLS.get(ACTION_QUERY_MEMBER), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 用户设置群消息接受规则
     */
    public static void setGroupMsg_user(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_CHAT_URL + API_URLS.get(ACTION_SET_GROUPMSG_USER), params, listener, errorListener);
        printRequestUri(API_CHAT_URL + API_URLS.get(ACTION_SET_GROUPMSG_USER), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 获取用户云通讯账号相关信息
     */
    public static void getYTXAccount(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_CHAT_URL + API_URLS.get(ACTION_GET_YTX_ACCOUNT), params, listener, errorListener);
        printRequestUri(API_CHAT_URL + API_URLS.get(ACTION_GET_YTX_ACCOUNT), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 获取用户聊天记录
     */
    public static void getHistoryMsgs(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_CHAT_URL + API_URLS.get(ACTION_HISTORY_MSG), params, listener, errorListener);
        printRequestUri(API_CHAT_URL + API_URLS.get(ACTION_HISTORY_MSG), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 获取相册照片
     */
    public static void getAlbumPhoto(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_GET_ALBUM_PHOTO), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_GET_ALBUM_PHOTO), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 获取照片评论列表
     */
    public static void getPhotoComment(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_GET_PHOTO_COMMENT), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_GET_PHOTO_COMMENT), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 是否可以查看文章详情(公共空间发布仅成员可见)
     */
    public static void canArticleDetail(HashMap<String, String> params, Listener<String> listener, ErrorListener errorListener) {
        StringJsonRequest request = new StringJsonRequest(Method.POST, API_HOST_URL + API_URLS.get(ACTION_QUERY_HAVE_Auth), params, listener, errorListener);
        printRequestUri(API_HOST_URL + API_URLS.get(ACTION_QUERY_HAVE_Auth), params);
        RequestManager.getRequestQueue().add(request);
    }

    /**
     * 上传文件相关（文件、音视频、图片）
     *
     * @param activity
     * @param params
     * @return
     */
    public static void upload(Activity activity, RequestParams params) {
        Handler handler = getNotificationHandler(activity);
        Message msg = new Message();
        msg.obj = params;
        msg.arg1 = NOTIFY_SEND;
        handler.sendMessage(msg);
    }


    /**
     * 上传到服务器
     *
     * @param activity
     * @param params
     * @return
     */
    private static void uploadToServer(final Activity activity, final RequestParams params) {
        HttpUtils util = new HttpUtils();
        util.send(HttpMethod.POST, API_HOST_URL + params.getTask(), params, new RequestCallBack<String>(200) {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                LogUtils.d(responseInfo.result);
                if (responseInfo.result != null && !responseInfo.result.equals("")) {
                    JSONObject object = JSON.parseObject(responseInfo.result);
                    if (object != null) {
                        String errmsg = object.getString("errmsg");
                        params.setResultJson(responseInfo.result);
                        if (errmsg != null && errmsg.equals("ok")) {
                            ((BaseActivity) activity).showSuccessNotify(params.getContent());
                            params.listener.OnNetSuccess(params);
                        } else {
                            ((BaseActivity) activity).showFailtureNotify(params.getContent());
                            params.listener.OnNetFailure(params);
                        }
                    }
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                ((BaseActivity) activity).callback.onException(error);
                params.listener.OnNetFailure(params);
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                params.listener.onLoading(total, current, isUploading);
            }
        });
    }

    /**
     * 处理消息
     */
    private static Handler getNotificationHandler(final Activity activity) {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.arg1) {
                    case NOTIFY_SEND:
                        RequestParams params = (RequestParams) msg.obj;
                        ((BaseActivity) activity).showSendingNotify(params.getContent());
                        uploadToServer(activity, params);
                        break;
                    case NOTIFY_CANCEL:
                        ((BaseActivity) activity).cancelNotify();
                        break;
                }
            }
        };
        return handler;
    }

    /**
     * 打印request
     *
     * @param url
     * @param params
     */
    private static void printRequestUri(String url, HashMap<String, String> params) {
        StringBuilder builder = new StringBuilder(url + "?");
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> next = iterator.next();
            String key = next.getKey();
            String value = next.getValue();
            builder.append(key).append("=").append(value).append("&");
        }
        LogUtils.i("request uri: " + builder.substring(0, builder.length() - 1));
    }

    /**
     * log输出请求参数
     *
     * @param url
     * @param params
     */
    private static void showParamsInLog(String url, HashMap<String, String> params) {
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        StringBuffer sb = new StringBuffer();
        while (iterator.hasNext()) {
            Entry<String, String> entry = (Entry<String, String>) iterator.next();
            String key = (String) entry.getKey();
            String val = (String) entry.getValue();
            sb.append(key + "=" + val + "&");
        }
        if (sb.lastIndexOf("&") != -1) {
            String log = url + "?" + sb.substring(0, sb.lastIndexOf("&")).toString();
            LogUtils.d(url + "?" + sb.substring(0, sb.lastIndexOf("&")).toString());
        }
    }

}
