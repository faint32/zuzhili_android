/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.cloopen.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.zuzhili.bussiness.helper;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.hisun.phone.core.voice.CCPCall;
import com.hisun.phone.core.voice.Device;
import com.hisun.phone.core.voice.Device.Codec;
import com.hisun.phone.core.voice.DeviceListener;
import com.hisun.phone.core.voice.listener.OnChatroomListener;
import com.hisun.phone.core.voice.listener.OnIMListener;
import com.hisun.phone.core.voice.listener.OnInterphoneListener;
import com.hisun.phone.core.voice.listener.OnVoIPListener;
import com.hisun.phone.core.voice.model.CloopenReason;
import com.hisun.phone.core.voice.model.DownloadInfo;
import com.hisun.phone.core.voice.model.chatroom.Chatroom;
import com.hisun.phone.core.voice.model.chatroom.ChatroomMember;
import com.hisun.phone.core.voice.model.chatroom.ChatroomMsg;
import com.hisun.phone.core.voice.model.im.IMAttachedMsg;
import com.hisun.phone.core.voice.model.im.IMDismissGroupMsg;
import com.hisun.phone.core.voice.model.im.IMInviterMsg;
import com.hisun.phone.core.voice.model.im.IMJoinGroupMsg;
import com.hisun.phone.core.voice.model.im.IMProposerMsg;
import com.hisun.phone.core.voice.model.im.IMQuitGroupMsg;
import com.hisun.phone.core.voice.model.im.IMRemoveMemeberMsg;
import com.hisun.phone.core.voice.model.im.IMReplyJoinGroupMsg;
import com.hisun.phone.core.voice.model.im.IMTextMsg;
import com.hisun.phone.core.voice.model.im.InstanceMsg;
import com.hisun.phone.core.voice.model.interphone.InterphoneMember;
import com.hisun.phone.core.voice.model.interphone.InterphoneMsg;
import com.hisun.phone.core.voice.model.setup.UserAgentConfig;
import com.hisun.phone.core.voice.util.Log4Util;
import com.hisun.phone.core.voice.util.SdkErrorCode;
import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.GroupInfo;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.Constants;

import com.zuzhili.bussiness.utility.IMParseUtil;
import com.zuzhili.db.DBHelper;
import com.zuzhili.db.IMGroupInfoTable;
import com.zuzhili.db.IMMessageTable;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.TaskApp;
import com.zuzhili.framework.im.CCPConfig;
import com.zuzhili.framework.im.CCPIntentUtils;
import com.zuzhili.framework.im.CCPVibrateUtil;
import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.framework.utils.VolleyImageUtils;
import com.zuzhili.model.im.IMChatMessageDetail;
import com.zuzhili.service.SyncUtils;
import com.zuzhili.ui.activity.HomeTabActivity;
import com.zuzhili.ui.activity.im.GroupChatActivity;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * VOIP Helper for Activity, it already has been did something important jobs
 * that activity just show state of ui by handler.
 * 
 * Before running the demo, you should be become a developer by CCP web site so that 
 * you can get the main account and token, otherwise also see test info.
 * 
 * @version 1.0.0
 */
public class CCPHelper implements CCPCall.InitListener
        , DeviceListener
		, OnVoIPListener
		, OnIMListener
		, OnChatroomListener
		, OnInterphoneListener
        , Response.Listener<String>
        , Response.ErrorListener{

	public static final String DEMO_TAG = "CCP_Demo";

    public static final int SDK_NOTIFICATION		    = 99;
    public static final int ICON_LEVEL_ORANGE		    = 0;
    public static final int ICON_LEVEL_GREEN		    = 1;
    public static final int ICON_LEVEL_RED			    = 2;
    public static final int ICON_LEVEL_BLACK			= 3;

	// our suggestion this context should be ApplicationContext
	private Context context;

	// invoked after created it
	private Device device;
	
	private RegistCallBack mCallback;

    private LinkedList<String> newGroupMsgUserDataList = new LinkedList<String>();
	
	private static CCPHelper sInstance;

    public Session mSession;
    public IMMessageTable messageTable;
    public IMGroupInfoTable groupTable;
    protected DBHelper dbHelper;
    private ActivityManager am;
	/**
	 * <p>Title: getInstance</p>
	 * <p>Description: </p>
	 * @seecom.voice.demo.ui.CCPHelper#init(android.content.Context, android.os.Handler)
	 * @return
	 */
	public static CCPHelper getInstance() {
		if(sInstance == null) {
			sInstance = new CCPHelper(TaskApp.getInstance());
		}
		return sInstance;
	}
	
	
	/**
	 * Constructs a new {@code VoiceHelper} instance.
	 * 
	 * @param context
	 */
	private CCPHelper(Context context, RegistCallBack rcb) {
		this.context = context;
		this.mCallback = rcb;
		
	}
	
	/**
	 * Constructs a new {@code VoiceHelper} instance.
	 * 
	 * @param context
	 */
	private CCPHelper(Context context) {
		this(context, null);
	}
	
	/**
	 * 
	 * @param rcb
	 */
	public void registerCCP(RegistCallBack rcb) {
		setRegistCallback(rcb);

		Log4Util.init(true);
        /**DK初始化过程依赖 由于CCP SDK 整个生命周期都会持有上下文Context引用，建议此处传入ApplicaionContext。*/
		CCPCall.init(context, this);
		Log4Util.d(DEMO_TAG, "[VoiceHelper] CCPCallService init");
		
	}

	/**
	 * Callback this method when SDK init success.
	 * 
	 * Please note: you must write info that those remark start.
	 * 
	 * SDK init just once, but device can create more.
	 * 
	 * @see #onInitialized()
     * SDK初始化回调
	 */
	@Override
	public void onInitialized() {
		try {
            mSession = Session.get(context);
            dbHelper = TaskApp.getInstance().getDbHelper();
            messageTable = dbHelper.getMessageTable();
            groupTable = dbHelper.getGroupInfoTable();
            // Activity管理,还可以管理service
            am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            createDevice();
		} catch (Exception e) {
			e.printStackTrace();
			//throw new RuntimeException(e);
			onError(e);
		}
	}

	/**
	 * 
	* <p>Title: createDevice</p>
	* <p>Description: modify by version 3.5</p>
	* @throws Exception
	 */
	private void  createDevice() throws Exception {
		// 封装参数
		Map<String, String> params = new HashMap<String, String>();
		// * REST服务器地址
		params.put(UserAgentConfig.KEY_IP, CCPConfig.REST_SERVER_ADDRESS);
        // * REST服务器端口
        params.put(UserAgentConfig.KEY_PORT, CCPConfig.REST_SERVER_PORT);
        // * VOIP账号 , 可以填入CCP网站Demo管理中的测试VOIP账号信息
        params.put(UserAgentConfig.KEY_SID, mSession.getVoipId());
        // * VOIP账号密码, 可以填入CCP网站Demo管理中的测试VOIP账号密码
        params.put(UserAgentConfig.KEY_PWD, mSession.getVoipPassword());
        // * 子账号, 可以填入CCP网站Demo管理中的测试子账号信息
        params.put(UserAgentConfig.KEY_SUBID, mSession.getSubAccount());
        // * 子账号密码, 可以填入CCP网站Demo管理中的测试子账号密码
        params.put(UserAgentConfig.KEY_SUBPWD, mSession.getSubToken());
		// User-Agent
		params.put(UserAgentConfig.KEY_UA, TaskApp.getInstance().getUser_Agent());

		//创建Device
        //SDK需要启动Android Service处理通话,当服务启动后,需要创建Device并使用来调用SDK所提供的接口。
        //SDK接口回调监听类
        //参数:DeviceListener,服务器参数及账户信息
		device = CCPCall.createDevice(this /* DeviceListener */, params);
		// 设置当呼入请求到达时, 唤起的界面
		//Intent intent = new Intent(context, CallInActivity.class);
		//PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		//device.setIncomingIntent(pendingIntent);
		
		device.setCodecEnabled(Codec.Codec_iLBC, false);
		device.setCodecEnabled(Codec.Codec_SILK8K, false);
		device.setCodecEnabled(Codec.Codec_G729, true);
		//device.setSrtpEnabled("");
		
		// set Listener ...
		// In a later version of SDK 3.5, SDK will implement the Interphone, VOIP, voice Chatroom, 
		// completely separate from the IM instant messaging function, if the need to use a function, 
		// only need to set the listener, do not need to ignore 
		// for SDK version 3.5 above
		device.setOnVoIPListener(this);
        /**当SDK收到IM消息或者执行完IM消息发送后会回调该接口通知*/
		device.setOnIMListener(this);
        /**实时对讲监听*/
		device.setOnInterphoneListener(this);
		device.setOnChatroomListener(this);

		Log4Util.d(DEMO_TAG, "[onInitialized] sdk init success. done");
	}
	
	/**
	 * Callback this method when sdk init errors.
	 * 
	 * @param exception
	 *            SDK init execption
	 * @seecom.voice.demo.ui.CCPHelper#(android.content.Context, android.os.Handler)
	 */
	@Override
	public void onError(Exception exception) {
		Log4Util.d(DEMO_TAG, "[onError] " + "SDK init error , " + exception.getMessage());
		if(mCallback != null ) {
			mCallback.onRegistResult(WHAT_INIT_ERROR ,"SDK初始化错误, " +  exception.getMessage());
		}
		
		// If not null, you will not be able to execute the init method
		//sInstance = null;0.
		CCPCall.shutdown();
	}

	/**
	 * handler 转换消息id
	 */
	public static final int WHAT_ON_CONNECT = 0x2000;
	public static final int WHAT_ON_DISCONNECT = 0x2001;
	public static final int WHAT_INIT_ERROR = 0x200A;
	public static final int WHAT_ON_CALL_ALERTING = 0x2002;
	public static final int WHAT_ON_CALL_ANSWERED = 0x2003;
	public static final int WHAT_ON_CALL_PAUSED = 0x2004;
	public static final int WHAT_ON_CALL_PAUSED_REMOTE = 0x2005;
	public static final int WHAT_ON_CALL_RELEASED = 0x2006;
	public static final int WHAT_ON_CALL_PROCEEDING = 0x2007;
	public static final int WHAT_ON_CALL_TRANSFERED = 0x2008;
	public static final int WHAT_ON_CALL_MAKECALL_FAILED = 0x2009;
	public static final int WHAT_ON_CALL_BACKING = 0x201B;
	
	//2013.3.11
	public static final int WHAT_ON_NEW_VOICE = 0x201C;
	public static final int WHAT_ON_AMPLITUDE = 0x201D;
	public static final int WHAT_ON_RECODE_TIMEOUT = 0x202A;
	public static final int WHAT_ON_UPLOAD_VOICE_RES = 0x202B;
	public static final int WHAT_ON_PLAY_VOICE_FINSHING = 0x202C;
	
	public static final int WHAT_ON_INTERPHONE = 0x203A;
	public static final int WHAT_ON_CONTROL_MIC = 0x203B;
	public static final int WHAT_ON_RELEASE_MIC = 0x203C;
	public static final int WHAT_ON_INTERPHONE_MEMBERS = 0x203D;
	public static final int WHAT_ON_INTERPHONE_SIP_MESSAGE = 0x203E;
	public static final int WHAT_ON_DIMISS_DIALOG = 0x204A;;
	
	public static final int WHAT_ON_REQUEST_MIC_CONTROL = 0x204C;
	public static final int WHAT_ON_RELESE_MIC_CONTROL = 0x204D;
	public static final int WHAT_ON_PLAY_MUSIC = 0x204E;
	public static final int WHAT_ON_STOP_MUSIC = 0x204F;

	
	public static final int WHAT_ON_VERIFY_CODE_SUCCESS = 0x205A;
	public static final int WHAT_ON_VERIFY_CODE_FAILED = 0x205B;
	
	// Chatroom
	public static final int WHAT_ON_CHATROOM_SIP_MESSAGE = 0x205C;
	public static final int WHAT_ON_CHATROOM_MEMBERS = 0x205D;
	public static final int WHAT_ON_CHATROOM_LIST = 0x205E;
	public static final int WHAT_ON_CHATROOM = 0x206A;
	public static final int WHAT_ON_CHATROOM_INVITE = 0x206B;
	public static final int WHAT_ON_MIKE_ANIM = 0x206C;
	public static final int WHAT_ON_CNETER_ANIM = 0x206D;
	public static final int WHAT_ON_VERIFY_CODE = 0x206E;
	public static final int WHAT_ON_CHATROOMING = 0x207A;
	public static final int WHAT_ON_CHATROOM_KICKMEMBER = 0x207B;
	
	
	// IM
	public static final int WHAT_ON_SEND_MEDIAMSG_RES = 0x208A;
	public static final int WHAT_ON_NEW_MEDIAMSG = 0x208B;
	public static final int WHAT_ON_RECEIVE_SYSTEM_EVENTS = 0x208C;

	/**
	 * handler for update activity
	 */
	private Handler handler;

	/**
	 * set handler.
	 * 
	 * @param handler
	 *            activity handler
	 */
	public void setHandler(final Handler handler) {
		this.handler = handler;
	}

	/**
	 * get the device.
	 * 
	 * @return the device
	 */
	public Device getDevice() {
		return device;
	}

	long t = 0;

	/**
	 * send object to activity by handler.
	 * 
	 * @param what
	 *            message id of handler
	 * @param obj
	 *            message of handler
	 */
	private void sendTarget(int what, Object obj) {
		t = System.currentTimeMillis();
		// for kinds of mobile phones
		while (handler == null && (System.currentTimeMillis() - t < 3200)) {
			Log4Util.d(DEMO_TAG, "[VoiceHelper] handler is null, activity maybe destory, wait...");
			try {
				Thread.sleep(80L);
			} catch (InterruptedException e) {
			}
		}

		if (handler == null) {
			Log4Util.d(DEMO_TAG, "[VoiceHelper] handler is null, need adapter it.");
			return;
		}

		Message msg = Message.obtain(handler);
		msg.what = what;
		msg.obj = obj;
		msg.sendToTarget();
	}

	/***********************************************************************************
	 *                                                                                 *
	 *            Following are DeviceListener Callback Methods                        *
	 *                                                                                 *
	 ************************************************************************************/

	/**
	 * Callback this method when register successful, developer can show
	 * notification to user.
	 */
	@Override
	public void onConnected() {
		LogUtils.i("[CCPHelper - onConnected]Connected on the cloud communication platform success..");
        // 设备登陆云通讯
        mSession.setChatLogin(true);
        if(mCallback != null) {
            mCallback.onRegistResult(WHAT_ON_CONNECT, "Connected on the cloud communication platform success");
            return ;
        }

        //发送广播
		this.context.sendBroadcast(new Intent(CCPIntentUtils.INTENT_CONNECT_CCP));
	}

	/**
	 * Callback this method when register failed, developer can show
	 * hint to user.
	 * 
	 * @param reason
	 *            register failed reason
	 */
	@Override
	public void onDisconnect(Reason reason) {
		
		if(mCallback != null) {
			mCallback.onRegistResult(WHAT_ON_DISCONNECT , reason.toString());
			return;
		}
		
		if(reason == Reason.KICKEDOFF) {
			Log4Util.d(DEMO_TAG, "Login account in other places.");

            // 设备被踢
            mSession.setChatLogin(false);
 			this.context.sendBroadcast(new Intent(CCPIntentUtils.INTENT_KICKEDOFF));
			return ;
		} 

		this.context.sendBroadcast(new Intent(CCPIntentUtils.INTENT_DISCONNECT_CCP));
		Log4Util.d(DEMO_TAG, "[VoiceHelper - onDisconnect]Can't connect the cloud communication platform" +
                ", please check whether the network connection,");
	}

	/**
	 * Callback this method when call arrived in remote.
	 * 
	 * @param callid
	 */
	@Override
	public void onCallAlerting(String callid) {
		sendTarget(WHAT_ON_CALL_ALERTING, callid);
	}

	/**
	 * Callback this method when remote answered.
	 * 
	 * @param callid
	 *           calling id
	 */
	@Override
	public void onCallAnswered(String callid) {
		sendTarget(WHAT_ON_CALL_ANSWERED, callid);
	}

	/**
	 * Callback this method when call arrived in soft-switch platform.
	 * 
	 * @param callid
	 *            calling id
	 */
	@Override
	public void onCallProceeding(String callid) {
		sendTarget(WHAT_ON_CALL_PROCEEDING, callid);
	}

	/**
	 * Callback this method when remote hangup call.
	 * 
	 * @param callid
	 *            calling id
	 */
	@Override
	public void onCallReleased(String callid) {
		sendTarget(WHAT_ON_CALL_RELEASED, callid);
	}

	/**
	 * Callback this method when make call failed.
	 * 
	 * @param callid
	 *            calling id
	 * @param destionation
	 *            destionation account
	 */
	@Override
	public void onCallTransfered(String callid, String destionation) {
		Bundle b = new Bundle();
		b.putString(Device.CALLID, callid);
		b.putString(Device.DESTIONATION, destionation);
		sendTarget(WHAT_ON_CALL_TRANSFERED, b);
	}

	/**
	 * Callback this method when make call failed.
	 * 打电话失败的回调
	 * @param callid
	 *            calling id
	 * @param reason
	 *            failed reason  见sdk错误码
	 */
	@Override
	public void onMakeCallFailed(String callid, Reason reason) {
		Bundle b = new Bundle();
		b.putString(Device.CALLID, callid);
		b.putSerializable(Device.REASON, reason);
		sendTarget(WHAT_ON_CALL_MAKECALL_FAILED, b);
	}


	/**
	 * Callback this method when dial-call success.
	 * 
	 * @param status
	 *            dial-call state
	 * @param self
	 *            Self phone number
	 * @param dest
	 *            Dest phone number
	 */
	@Override
	public void onCallback(int status, String self, String dest) {
		Bundle b = new Bundle();
		b.putInt(Device.CBSTATE, status);
		b.putString(Device.SELFPHONE, self);
		b.putString(Device.DESTPHONE, dest);
		sendTarget(WHAT_ON_CALL_BACKING, b);
	}

	/**
	 * Callback this method when localize pause current call.
	 * 
	 * @param callid
	 *            calling id
	 */
	@Override
	public void onCallPaused(String callid) {

	}

	/**
	 * Callback this method when Remote pause current call.
	 * 
	 * @param callid
	 *            calling id
	 */
	@Override
	public void onCallPausedByRemote(String callid) {

	}

	public void release() {
		this.context = null;
		this.device = null;
		this.handler = null;
		
		sInstance = null;
	}
	

	
	/**********************************************************************
	 *                     voice message                                  *
	 **********************************************************************/


    /**
     * 播放语音留言结束回调，当调用播放录音接口播放完成语音留言，SDK回调该函数通知应用层。
     */
	@Override
	public void onFinishedPlaying() {
		Log4Util.d(DEMO_TAG, "[onFinishedPlaying ] MediaPlayManager play is stop ..");
		Bundle b = new Bundle();
		sendTarget(WHAT_ON_PLAY_VOICE_FINSHING, b);
		
		context.sendBroadcast(new Intent(CCPIntentUtils.INTENT_VOICE_PALY_COMPLETE));
	}


    /**
     * 录音振幅回调。当调用录音接口开始录制语音时，SDK会回调该方法返回当前麦克风的音量数字大小，即振幅。
     * @param amplitude  音量大小分贝值。
     */
	@Override
	public void onRecordingAmplitude(double amplitude) {
		Bundle b = new Bundle();
		b.putDouble(Device.VOICE_AMPLITUDE, amplitude);
		sendTarget(WHAT_ON_AMPLITUDE, b);
		
	}

    /**
     * 录音超时回调
     * 录音超时回调，当录制时间超过最大时长60s，SDK自动停止录制并回调返回。
     * @param mills
     */
	@Override
	public void onRecordingTimeOut(long mills) {
        LogUtils.e("录音超时");
		Bundle b = new Bundle();
		b.putLong("mills", mills);
		sendTarget(WHAT_ON_RECODE_TIMEOUT, b);
		
	}

    /**
     * 主动加入或者被邀请加入对讲回调。连接云通讯平台成功后，就能够收到加入实时对讲或者要求加入实时对讲回调。
     * @param reason 状态值 参考SDK错误码
     * @param confNo 对讲场景id
     */
    @Override
    public void onInterphoneState(CloopenReason reason, String confNo) {
        Log4Util.d(DEMO_TAG , "[onInterphoneState ] oninter phone state  , reason  " +reason + " , and confNo " + confNo);
//        if(!reason.isError()){
//            if(TaskApp.interphoneIds.indexOf(confNo)<0){
//                TaskApp.interphoneIds.add(confNo);
//                Intent intent = new Intent(CCPIntentUtils.INTENT_RECIVE_INTER_PHONE);
//                context.sendBroadcast(intent);
//            }
//        } else {
//            showToastMessage(reason);
//        }
//        Bundle b = new Bundle();
//        b.putInt(Device.REASON, reason.getReasonCode());
//        b.putString(Device.CONFNO, confNo);
//        sendTarget(WHAT_ON_INTERPHONE, b);
    }


    /**
     * 。
     * @param reason
     * @param speaker speaker 控麦者的VoIP账号
     */
	@Override
	public void onControlMicState(CloopenReason reason, String speaker) {
		Log4Util.d(DEMO_TAG, "[onControlMicState ] control mic return  , reason " + reason + " , and speaker " + speaker);
		//showToastMessage(reason);
		Bundle b = new Bundle();
		b.putInt(Device.REASON, reason.getReasonCode());
		b.putString(Device.SPEAKER, speaker);
		sendTarget(WHAT_ON_CONTROL_MIC, b);
	}

    /**
     * 释放，麦回调。连接云通讯平台成功后，当发起释放麦请求后，会收到释放麦结果回调。
     * @param reason
     */
	@Override
	public void onReleaseMicState(CloopenReason reason) {
		Log4Util.d(DEMO_TAG, "[onReleaseMicState ] on release mic return reason  .. " + reason);
		//showToastMessage(reason);
		Bundle b = new Bundle();
		b.putInt(Device.REASON, reason.getReasonCode());
		sendTarget(WHAT_ON_RELEASE_MIC, b);
	}

    /**
     * 查询实时对讲成员回调。连接云通讯平台成功后，当发起查询实时对讲成员请求后，会收到查询结果回调。
     * @param reason
     * @param member 实时对讲成员（InterphoneMember）信息数组
     */
	@Override
	public void onInterphoneMembers(CloopenReason reason, List<InterphoneMember> member) {
		Log4Util.d(DEMO_TAG, "[onInterphoneMembers ] on inter phone members that .. " + member);
		//showToastMessage(reason);
		Bundle b = new Bundle();
		b.putInt(Device.REASON, reason.getReasonCode());
		b.putSerializable(Device.MEMBERS, (ArrayList<InterphoneMember>) member);
		sendTarget(WHAT_ON_INTERPHONE_MEMBERS, b);
	}

    /**
     * 连接云通讯平台成功后,能够收到SDK推送消息。
     * 通知是否有人加入、退出、以及抢麦释放麦等。该API是业务层面消息回调函数，根据业务类型，
     * 来传递不同的子类业务对象，从而达到通过该函数控制不同通知的目的。
     * @param body
     */
    @Override
    public void onReceiveInterphoneMsg(InterphoneMsg body) {
        Log4Util.d(DEMO_TAG , "[onReceiveInterphoneMsg ] Receive inter phone message  , id :" + body.interphoneId);
//        if(body instanceof InterphoneOverMsg){
//            TaskApp.interphoneIds.remove(body.interphoneId);
//            Intent intent = new Intent(CCPIntentUtils.INTENT_RECIVE_INTER_PHONE);
//            context.sendBroadcast(intent);
//        } else if (body instanceof InterphoneInviteMsg) {
//            if(TaskApp.interphoneIds.indexOf(body.interphoneId)<0){
//                TaskApp.interphoneIds.add(body.interphoneId);
//            }
//            Intent intent = new Intent(CCPIntentUtils.INTENT_RECIVE_INTER_PHONE);
//            try {
//                CCPNotificationManager.showNewInterPhoneNoti(context, body.interphoneId);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            context.sendBroadcast(intent);
//        }
//        Bundle b = new Bundle();
//        b.putSerializable(Device.INTERPHONEMSG, body);
//        sendTarget(WHAT_ON_INTERPHONE_SIP_MESSAGE, b);
    }


    /**
     *创建或者加入聊天室的状态。
     * @param reason
     * @param confNo 聊天室房间号
     */
	@Override
	public void onChatroomState(CloopenReason reason, String confNo) {
		Log4Util.d(DEMO_TAG, "[onChatRoomState ] reason " + reason + " , confNo " + confNo);
		//showToastMessage(reason);
		Bundle b = new Bundle();
		b.putInt(Device.REASON, reason.getReasonCode());
		b.putString(Device.CONFNO, confNo);
		sendTarget(WHAT_ON_CHATROOM, b);
	}


    /**
     * 连接云通讯平台成功后，通知是否有人加入、退出、被踢出和解散等。
     * 该API是业务层面消息回调函数，根据业务类型，来传递不同的子类业务对象，
     * 从而达到通过该函数控制不同通知的目的
     * @param msg 基类对象，具体消息可以派生子类
     */
	@Override
	public void onReceiveChatroomMsg(ChatroomMsg msg) {
		Log4Util.d(DEMO_TAG, "[onReceiveChatRoomMsg ] Receive Chat Room message  , id :" + msg.getRoomNo());
		Bundle b = new Bundle();
		b.putSerializable(Device.CHATROOM_MSG, msg);
		sendTarget(WHAT_ON_CHATROOM_SIP_MESSAGE, b);
	}

    /**
     *获取聊天室成员
     * @param reason
     * @param member 聊天室成员信息数组
     */
	@Override
	public void onChatroomMembers(CloopenReason reason, List<ChatroomMember> member) {
		Log4Util.d(DEMO_TAG, "[onChatRoomMembers ] on Chat Room  members that .. " + member);
		//showToastMessage(reason);
		Bundle b = new Bundle();
		b.putSerializable(Device.CHATROOM_MEMBERS, (ArrayList<ChatroomMember>) member);
		sendTarget(WHAT_ON_CHATROOM_MEMBERS, b);
	}

    /**
     * 邀请加入聊天室状态
     * @param reason
     * @param confNo 邀请加入的房间号
     */
	@Override
	public void onChatroomInviteMembers(CloopenReason reason, String confNo) {
		Log4Util.d(DEMO_TAG, "[onChatRoomInvite ] reason " + reason + " , confNo " + confNo);
		//showToastMessage(reason);
		Bundle b = new Bundle();
		b.putInt(Device.REASON, reason.getReasonCode());
		b.putString(Device.CONFNO, confNo);
		sendTarget(WHAT_ON_CHATROOM_INVITE, b);
	}

    /**
     * 获取聊天室列表
     * @param reason
     * @param chatRoomList
     */
	@Override
	public void onChatrooms(CloopenReason reason, List<Chatroom> chatRoomList) {
		Log4Util.d(DEMO_TAG, "[onChatrooms ] on Chat Room  chatrooms that .. " + chatRoomList);
		//showToastMessage(reason);
		Bundle b = new Bundle();
		b.putSerializable(Device.CHATROOM_LIST, (ArrayList<Chatroom>) chatRoomList);
		sendTarget(WHAT_ON_CHATROOM_LIST, b);
		
	}

    /**
     * 发送IM消息回调
     * @param reason  状态值 参考SDK错误码
     * @param
     * //data发送IM消息的实例，
     * 可以是附件消息也可以是文本消息如果是附件信息则该附件信息之msgId保存的即为调用发送接口所返回的id，
     * serverId为该条消息为服务器上保存的Id，参考SDK类定义
     *
     */
	@Override
	public void onSendInstanceMessage(CloopenReason reason, InstanceMsg data) {
		LogUtils.e("[onSendInstanceMessage ] on send Instance Message that reason .. " + reason);
		//showToastMessage(reason);
		if(data == null) {
			return;
		}
		try {
			// If the current activity is not in the chat interface, 
			// so need here to update the database
			// If you are in a chat interface, then because here has to update the database,
			// when the chat interface to update the database will not update message state 
			// Because this message state isn't IMChatMessageDetail.STATE_IM_SENDING
			int msgType = -1;
			if(!reason.isError()) {
				msgType = IMChatMessageDetail.STATE_IM_SEND_SUCCESS;
			} else {
				if(reason.getReasonCode() != 230007) {
					msgType = IMChatMessageDetail.STATE_IM_SEND_FAILED;
				}
			}
			if(msgType != -1) {
				String messageId = null;;
				if(data instanceof IMTextMsg) {
					messageId = ((IMTextMsg)data).getMsgId();
				} else if (data instanceof IMAttachedMsg) {
					messageId = ((IMAttachedMsg)data).getMsgId();
				}
                messageTable.updateIMMessageSendStatusByMessageId(messageId, msgType);
			}
		} catch (Exception e) {
			// 
		}
		Bundle b = new Bundle();
		b.putInt(Device.REASON, reason.getReasonCode());
		b.putSerializable(Device.MEDIA_MESSAGE, data);
		sendTarget(WHAT_ON_SEND_MEDIAMSG_RES, b);
	}


    /**
     * 下载IM附件的结果回调函数
     * @param reason 状态值 参考SDK错误码
     * @param fileName 下载附件的本地路径
     */
	@Override
	public void onDownloadAttached(CloopenReason reason, String fileName) {
		Log4Util.d(DEMO_TAG , "[onDownloadAttachmentFiles ]  reason " + reason.getReasonCode() +  " , fileName= " + fileName);
		final IMChatMessageDetail rMediaInfo = (IMChatMessageDetail)TaskApp.getInstance().getMediaData(fileName);
		try {
            //success
			if(!reason.isError()) {
				if(rMediaInfo != null ) {
					String msgid[] = {rMediaInfo.getMessageId()};
					//TaskApp.getInstance().getDbHelper().getMessageTable().updateIMMessageDate(rMediaInfo.getMessageId(), CCPUtil.getDateCreate());
                    /**
                     *确认已成功下载文件消息
                     */
                    getDevice().confirmIntanceMessage(msgid);

                    if("jpg".equals(rMediaInfo.getFileExt()) ||"jpeg".equals(rMediaInfo.getFileExt()) || "png".equals(rMediaInfo.getFileExt())){
                        File file=new File(fileName);
                        VolleyImageUtils.compress(file, DensityUtil.getScreenWidth(context), DensityUtil.getScreenHeight(context));
                    }

//					Intent intent = new Intent(CCPIntentUtils.INTENT_IM_RECIVE);
//					intent.putExtra(GroupChatActivity.KEY_GROUP_ID, rMediaInfo.getSessionId());
//					intent.putExtra(GroupChatActivity.KEY_MESSAGE_ID, rMediaInfo.getMessageId());
//                  intent.putExtra(GroupChatActivity.KEY_IMMESSAGE_DETAIL, rMediaInfo);
//                  context.sendBroadcast(intent);
                    needSendMsg(rMediaInfo,null);
					CCPVibrateUtil.getInstace().doVibrate();
				}
			} else if (reason.getReasonCode() == SdkErrorCode.SDK_AMR_CANCLE || reason.getReasonCode() == SdkErrorCode.SDK_FILE_NOTEXIST) {
				// delete this message in database.
				if(rMediaInfo == null) {
					return;
				}
                messageTable.deleteIMMessage(rMediaInfo.getMessageId(),mSession.getListid());
			} else {
				// do download again ...

			}
			TaskApp.getInstance().removeMediaData(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


    public static int MSG_NUM = 0;
    public static ArrayList<String> senderList = new ArrayList<String>();

    /**
     * 接收所有的InstanceMessage消息，包括文本、附件和组管理等与InstanceMessage有关的消息
     * @param msg
     */
	@Override
	public void onReceiveInstanceMessage(InstanceMsg msg) {
        SyncUtils.TriggerRefresh();
		LogUtils.i("[onReceiveInstanceMessage ] " + msg);
		try {
			boolean isNewMessageVibrate = true;
			if(msg != null) {
				if(msg instanceof IMAttachedMsg || msg instanceof IMTextMsg){
					if(msg instanceof IMAttachedMsg) {
                        LogUtils.i("msg instanceof IMAttachedMsgmsg instanceof IMAttachedMsg");
						// if this message is attache , then push thread download.
						isNewMessageVibrate = false;
						IMAttachedMsg aMsg = (IMAttachedMsg)msg;
						if (aMsg.getSender() != null	&& aMsg.getSender().equals(CCPConfig.VoIP_ID)) {
							return;
						}
						
						String receiver = aMsg.getReceiver();
						String sender = aMsg.getSender();
						if(TextUtils.isEmpty(receiver)){
							return;
						}
						
						String contactId = "";
						
						if(mSession.getVoipId().equals(receiver)) {
							// not group message 
							contactId = sender;
						} else {
							// group 
							contactId = receiver;
						}
						
						
						int index = aMsg.getFileUrl().indexOf("fileName=");
						String fileName = aMsg.getFileUrl().substring(index+9, aMsg.getFileUrl().length());
						String localPath = new File(TaskApp.getInstance().getVoiceStore(), fileName).getAbsolutePath();
						
						String msgContent = aMsg.getFileUrl().substring(index+9, aMsg.getFileUrl().length());
						
						IMChatMessageDetail chatMessageDetail = null;
						if("amr".equals(aMsg.getExt())) {
                            //语音
							chatMessageDetail = IMChatMessageDetail.getGroupItemMessageReceived(aMsg.getMsgId(), IMChatMessageDetail.TYPE_MSG_VOICE
                                    , contactId, sender);
						} else if("jpg".equals(aMsg.getExt()) ||"jpeg".equals(aMsg.getExt())|| "png".equals(aMsg.getExt())){
                            chatMessageDetail = IMChatMessageDetail.getGroupItemMessageReceived(aMsg.getMsgId(), IMChatMessageDetail.TYPE_MSG_PIC
                                    , contactId, sender);
                        } else{
							chatMessageDetail = IMChatMessageDetail.getGroupItemMessageReceived(aMsg.getMsgId(), IMChatMessageDetail.TYPE_MSG_FILE,
                                    contactId, sender) ;
							chatMessageDetail.setMessageContent(msgContent);
						}
						chatMessageDetail.setFileExt(aMsg.getExt());
						
						// file path in server
						// Voice file or multimedia attachments on the file server URL 
						// Save this address. You can download it again in the download failed 
						chatMessageDetail.setFileUrl(aMsg.getFileUrl()); 
						
						// local save path 
						// File in the local store path of SDCARD.
						chatMessageDetail.setFilePath(localPath);              
						chatMessageDetail.setUserData(aMsg.getUserData());
						chatMessageDetail.setDateCreated(aMsg.getDateCreated()); //file dateCreate in server ..
                        chatMessageDetail.setListId(IMParseUtil.getListId(aMsg.getUserData()));
                        chatMessageDetail.setCurDate(String.valueOf((new Date()).getTime()));
                        chatMessageDetail.setIdentityId(IMParseUtil.getInstance(mSession).buildIdentity(chatMessageDetail.getUserData()));
                        messageTable.insertIMMessage(chatMessageDetail);
                        insertNewGroup(receiver, aMsg.getUserData());
                        TaskApp.getInstance().putMediaData(localPath, chatMessageDetail);

						// download ..
						ArrayList<DownloadInfo> dLoadList = new ArrayList<DownloadInfo>();
						dLoadList.add(new DownloadInfo(aMsg.getFileUrl(), localPath , aMsg.isChunked()));
                        /**
                         * 下载附件
                         */
						getDevice().downloadAttached(dLoadList);

						//文本消息
					} else {
                        if (msg instanceof IMTextMsg) {
                            IMTextMsg aMsg = (IMTextMsg) msg;
                            String sender = aMsg.getSender();
                            String message = aMsg.getMessage();
                            String receiver = aMsg.getReceiver();

                            if (TextUtils.isEmpty(sender) || TextUtils.isEmpty(message) || TextUtils.isEmpty(receiver)) {
                                return;
                            }

                            if (CCPConfig.VoIP_ID.equals(sender)) {
                                return;
                            }

                            String contactId = "";
                            if (mSession.getVoipId().equals(receiver)) {
                                // not group message
                                contactId = sender;
                            } else {
                                // group
                                contactId = receiver;
                            }

                            IMChatMessageDetail chatMessageDetail = IMChatMessageDetail.getGroupItemMessageReceived(aMsg.getMsgId(), IMChatMessageDetail.TYPE_MSG_TEXT
                                    , contactId, sender);
                            chatMessageDetail.setMessageContent(message);
                            chatMessageDetail.setDateCreated(aMsg.getDateCreated());
                            chatMessageDetail.setUserData(aMsg.getUserData());
                            chatMessageDetail.setListId(IMParseUtil.getListId(aMsg.getUserData()));
                            chatMessageDetail.setCurDate(String.valueOf((new Date()).getTime()));
                            chatMessageDetail.setIdentityId(IMParseUtil.getInstance(mSession).buildIdentity(chatMessageDetail.getUserData()));
                            messageTable.insertIMMessage(chatMessageDetail);

                            insertNewGroup(receiver, aMsg.getUserData());

                            needSendMsg(chatMessageDetail,contactId);
                        }
                    }
					
					
				} else if (msg instanceof IMInviterMsg) {
                    LogUtils.e("msg instanceof IMInviterMsg instanceof IMInviterMsg instanceof IMInviterMsg");
					// Received the invitation to join the group 邀请成员加入
					IMInviterMsg imInviterMsg = (IMInviterMsg) msg;
					Log4Util.d(DEMO_TAG, "[VoiceHelper - onReceiveInstanceMessage ] Receive invitation to join the group ,that amdin " +
                            imInviterMsg.getAdmin() + " , and group id :" + imInviterMsg.getGroupId());
                    //todo
                    //CCPSqliteManager.getInstance().insertNoticeMessage(msg, IMSystemMessage.SYSTEM_TYPE_INVITE_JOIN);
					Intent intent = new Intent(CCPIntentUtils.INTENT_RECEIVE_SYSTEM_MESSAGE);
					context.sendBroadcast(intent);
				} else if (msg instanceof IMJoinGroupMsg) {
					
					// The receipt of the application to join the group
					IMJoinGroupMsg imJoinMsg = (IMJoinGroupMsg) msg;
					Log4Util.d(DEMO_TAG, "[VoiceHelper - onReceiveInstanceMessage ] Receive join message that Joiner " +
                            imJoinMsg.getProposer() + " , and group id :" + imJoinMsg.getGroupId());
                    //todo
                    //CCPSqliteManager.getInstance().insertNoticeMessage(msg, IMSystemMessage.SYSTEM_TYPE_APPLY_JOIN_UNVALIDATION);
					Intent intent = new Intent(CCPIntentUtils.INTENT_RECEIVE_SYSTEM_MESSAGE);
					context.sendBroadcast(intent);
					
					
					// BUG ..
				} else if (msg instanceof IMProposerMsg) {
					
					// The receipt of the application to join the group
					IMProposerMsg imProposerMsg = (IMProposerMsg) msg;
					Log4Util.d(DEMO_TAG, "[VoiceHelper - onReceiveInstanceMessage ] Receive proposer message that Proposer " +
                            imProposerMsg.getProposer() + " , and group id :" + imProposerMsg.getGroupId());
                    //todo
                    //CCPSqliteManager.getInstance().insertNoticeMessage(msg, IMSystemMessage.SYSTEM_TYPE_APPLY_JOIN);
					Intent intent = new Intent(CCPIntentUtils.INTENT_RECEIVE_SYSTEM_MESSAGE);
					context.sendBroadcast(intent);
				} else if (msg instanceof IMRemoveMemeberMsg) {
					// Remove group received system information
					IMRemoveMemeberMsg imrMemeberMsg = (IMRemoveMemeberMsg) msg;
					Log4Util.d(DEMO_TAG, "[VoiceHelper - onReceiveInstanceMessage ] Received system information that " +
                            "remove from group  id " + imrMemeberMsg.getGroupId());
                    //todo    !被群组删除
                    //CCPSqliteManager.getInstance().insertNoticeMessage(msg, IMSystemMessage.SYSTEM_TYPE_REMOVE);
                    messageTable.deleteIMMessage(imrMemeberMsg.getGroupId(), Session.get(context).getListid());
                    groupTable.deleteGroupByYTXGroupId(imrMemeberMsg.getGroupId());
					Intent intent = new Intent(CCPIntentUtils.INTENT_REMOVE_FROM_GROUP);
					intent.putExtra(GroupChatActivity.KEY_GROUP_ID, imrMemeberMsg.getGroupId());
					//context.sendBroadcast(intent);
				} else if (msg instanceof IMReplyJoinGroupMsg) {
					// Remove group received system information  确认加入群组
					IMReplyJoinGroupMsg imAcceptRejectMsg = (IMReplyJoinGroupMsg) msg;
					Log4Util.d(DEMO_TAG, "[VoiceHelper - onReceiveInstanceMessage ] Received system information that " +
                            "reject or accept from group  id " + imAcceptRejectMsg.getGroupId());
                    //todo
                    //CCPSqliteManager.getInstance().insertNoticeMessage(msg, IMSystemMessage.SYSTEM_TYPE_ACCEPT_OR_REJECT_JOIN);
					Intent intent = null;
					if("0".equals(imAcceptRejectMsg.getConfirm())){
						intent = new Intent(CCPIntentUtils.INTENT_JOIN_GROUP_SUCCESS);
						intent.putExtra(GroupChatActivity.KEY_GROUP_ID, imAcceptRejectMsg.getGroupId());
					} else {
						intent = new Intent(CCPIntentUtils.INTENT_RECEIVE_SYSTEM_MESSAGE);
					}
					context.sendBroadcast(intent);
				} else if (msg instanceof IMDismissGroupMsg) {
					// The group manager dismiss this group.. 管理员解散群组
					IMDismissGroupMsg imDismissGroupMsg = (IMDismissGroupMsg) msg;
					Log4Util.d(DEMO_TAG, "[VoiceHelper - onReceiveInstanceMessage ] Received system information that " +
                            "group manager dismiss this group  id " + imDismissGroupMsg.getGroupId());
                    messageTable.deleteIMMessage(imDismissGroupMsg.getGroupId(), Session.get(context).getListid());
                    groupTable.deleteGroupByYTXGroupId(imDismissGroupMsg.getGroupId());
					Intent intent = null;

					//intent = new Intent(CCPIntentUtils.INTENT_REMOVE_FROM_GROUP);
					//intent.putExtra(GroupChatActivity.KEY_GROUP_ID, imDismissGroupMsg.getGroupId());
					//context.sendBroadcast(intent);

					intent = new Intent(CCPIntentUtils.INTENT_DISMISS_GROUP);
                    intent.putExtra(GroupChatActivity.KEY_GROUP_ID, imDismissGroupMsg.getGroupId());
                    context.sendBroadcast(intent);

				} else if (msg instanceof IMQuitGroupMsg) {
					// The group member dismiss this group.. 成员退出群组
					IMQuitGroupMsg imQuitGroupMsg = (IMQuitGroupMsg) msg;
					Log4Util.d(DEMO_TAG, "[VoiceHelper - onReceiveInstanceMessage ] Received system information that " +
                            "Members quit from a group id " + imQuitGroupMsg.getGroupId());
                    //todo
                    //CCPSqliteManager.getInstance().insertNoticeMessage(msg, IMSystemMessage.SYSTEM_TYPE_GROUP_MEMBER_QUIT);
					Intent intent = null;
					intent = new Intent(CCPIntentUtils.INTENT_RECEIVE_SYSTEM_MESSAGE);
					intent.putExtra(GroupChatActivity.KEY_GROUP_ID, imQuitGroupMsg.getGroupId());
					context.sendBroadcast(intent);
				}
				
				if(isNewMessageVibrate) {
					CCPVibrateUtil.getInstace().doVibrate();
				}
			}		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

    private Map<String,String> senderNames=new HashMap<String, String>();

    /**
     * 是否需要发送通知,并发送广播
     * type 1:text type 2:voice
     */
    private void needSendMsg(IMChatMessageDetail chatMessageDetail,String contactId){
        //判断是否在当前应用
        ActivityManager.RunningTaskInfo runtask = am.getRunningTasks(1).get(0);
        // 获取当前用户可见的activity 的名字
        String activityName = runtask.topActivity.getShortClassName();
//        String fragmentTag =null;
//        runtask.
//        if(context.getString(R.string.home_activity).equals(classname)){
//            fragmentTag = ((HomeTabActivity) runtask.topActivity).getCurrentFragmentTag();
//        }
        if (!context.getString(R.string.chat_activity).equals(activityName) ||
                //&&!context.getString(R.string.home_activity).equals(classname)
                //!Constants.TAG_MESSAGE_LIST.equals(fragmentTag) ||
                !mSession.getY_groupId().equals(chatMessageDetail.getSessionId()) ||
                !mSession.getListid().equals(chatMessageDetail.getListId())){
            if (senderList.size() == 0) {
                senderList.add(chatMessageDetail.getGroupSender());
                MSG_NUM = 1;
            } else {
                if (senderList.contains(chatMessageDetail.getGroupSender())) {
                    MSG_NUM += 1;
                } else {
                    senderList.add(chatMessageDetail.getGroupSender());
                    MSG_NUM += 1;
                }
            }

            String listId=null;
            if(!mSession.getListid().equals(chatMessageDetail.getListId())){
                listId=chatMessageDetail.getListId();
            }
            String content = context.getString(
                    R.string.notification_content_new_missing_letter_count,
                    senderList.size(), MSG_NUM);
            String senderName=null;
            String senderId=chatMessageDetail.getSessionId();
            if(senderNames.size()>0 && senderNames.containsKey(senderId)){
                senderName=senderNames.get(senderId);
                if(senderName.equals(context.getString(R.string.new_group)) || senderName.equals(context.getString(R.string.new_user)) ){
                    senderName=queryName(senderId,chatMessageDetail);
                }
            }else {
                senderName=queryName(senderId,chatMessageDetail);
            }

            if(IMChatMessageDetail.TYPE_MSG_TEXT==chatMessageDetail.getMessageType()) {
                showMsgNotification(context, content, context.getString(R.string.receive_text_msg,senderName), listId);
            }else if(IMChatMessageDetail.TYPE_MSG_VOICE==chatMessageDetail.getMessageType()){
                showMsgNotification(context, content, context.getString(R.string.receive_voice_msg,senderName), listId);
            }else {
                showMsgNotification(context, content, context.getString(R.string.receive_image_msg,senderName), listId);
            }

            recieveSendBroadcast(chatMessageDetail,contactId);
        }else{
            recieveSendBroadcast(chatMessageDetail,contactId);
        }
    }

    private String queryName(String senderId,IMChatMessageDetail chatMessageDetail){
        String senderName=null;
        if(senderId.startsWith("g")){
            try {
                GroupInfo groupInfo = dbHelper.getGroupInfoTable().queryGroup(senderId);
                if(groupInfo!=null){
                    senderName=groupInfo.getG_name();
                }else {
                    senderName=context.getString(R.string.new_group);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            UserInfo sender=dbHelper.getUserInfoTable().get(senderId, chatMessageDetail.getListId(),Utils.getIdentity(mSession));
            if(sender!=null){
                senderName=sender.getU_name();
            }else {
                senderName=context.getString(R.string.new_user);
            }
        }

        senderNames.put(senderId,senderName);
        return  senderName;
    }



    private void recieveSendBroadcast(IMChatMessageDetail chatMessageDetail,String contactId){
        if(IMChatMessageDetail.TYPE_MSG_TEXT==chatMessageDetail.getMessageType()) {
            Intent intent = new Intent(CCPIntentUtils.INTENT_IM_RECIVE);
            intent.putExtra(GroupChatActivity.KEY_GROUP_ID, contactId);
            intent.putExtra(GroupChatActivity.KEY_IMMESSAGE_DETAIL, chatMessageDetail);
            context.sendBroadcast(intent);
        }else {
            Intent intent = new Intent(CCPIntentUtils.INTENT_IM_RECIVE);
            intent.putExtra(GroupChatActivity.KEY_GROUP_ID, chatMessageDetail.getSessionId());
            intent.putExtra(GroupChatActivity.KEY_MESSAGE_ID, chatMessageDetail.getMessageId());
            intent.putExtra(GroupChatActivity.KEY_IMMESSAGE_DETAIL, chatMessageDetail);
            context.sendBroadcast(intent);
        }
    }

    /**
     * 在状态栏显示消息通知
     * content  通知栏内容
     */
    private void showMsgNotification(Context context,String content,String ticker,String listId){
        // 创建一个NotificationManager的引用
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        // 设置通知的事件消息
        Intent notificationIntent = new Intent(context, HomeTabActivity.class); // 点击该通知后要跳转的Activity
        notificationIntent.putExtra(Constants.TO_GROUPSLISTFRG,"ok");
        notificationIntent.putExtra(Constants.CHANGE_SOCIAL, listId);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//Intent.FLAG_ACTIVITY_SINGLE_TOP|
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.setData(Uri.parse("custom://" + System.currentTimeMillis()));
        PendingIntent contentItent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 定义Notification的各种属性
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.new_msg_notification_title))
                .setContentText(content)
                .setSmallIcon(R.drawable.notify)
                .setTicker(ticker)
                .setContentIntent(contentItent)
                .setWhen(System.currentTimeMillis())
                .build();

        //FLAG_AUTO_CANCEL   该通知能被状态栏的清除按钮给清除掉
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        //DEFAULT_VIBRATE 使用默认手机震动，需加上<uses-permission android:name="android.permission.VIBRATE" />权限
        notification.defaults = Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.ledARGB = Color.BLUE;
        notification.ledOnMS =5000; //闪光时间，毫秒

        // 取消上一个 把Notification传递给NotificationManager
        notificationManager.cancel(0);
        notificationManager.notify(0, notification);
    }

	@Override
	public void onCallMediaUpdateRequest(String callid, int reason) {
		Log4Util.d(DEMO_TAG, "[onCallMediaUpdateRequest ]  callid=" + callid + ", reason=" + reason);
	}

	@Override
	public void onCallMediaUpdateResponse(String callid, int reason) {
		Log4Util.d(DEMO_TAG, "[onCallMediaUpdateResponse ]  callid=" + callid + ", reason=" + reason);
	}

	@Override
	public void onCallVideoRatioChanged(String callid, String resolution) {
		Log4Util.d(DEMO_TAG, "[onCallVideoRatioChanged ]  callid=" + callid + ", resolution=" + resolution);
	}

	@Override
	public void onCallMediaInitFailed(String callid, int reason) {
		Log4Util.d(DEMO_TAG, "[onCallMediaInitFailed ]  callid=" + callid + ", reason=" + reason);
	}

    /**
     * 确认已成功下载IM附件接口的回调函数，修改成功reason返回0表示成功；
     * @param reason 状态值
     */
	@Override
	public void onConfirmIntanceMessage(CloopenReason reason) {
		showToastMessage(reason);
	}

    /**
     * 解散聊天室的回调
     * @param reason
     * @param roomNo
     */
	@Override
	public void onChatroomDismiss(CloopenReason reason, String roomNo) {
		Intent intent = new Intent(CCPIntentUtils.INTENT_CHAT_ROOM_DISMISS);
		showToastMessage(reason);
		intent.putExtra("roomNo", roomNo);
		context.sendBroadcast(intent);
	}

    /**
     * 提出成员
     * @param reason
     * @param member
     */
	@Override
	public void onChatroomRemoveMember(CloopenReason reason, String member) {
		showToastMessage(reason);
		Bundle b = new Bundle();
		b.putInt(Device.REASON, reason.getReasonCode());
		b.putString("kick_member", member);
		sendTarget(WHAT_ON_CHATROOM_KICKMEMBER, b);
	}

	@Override
	public void onFirewallPolicyEnabled() {
		Intent intent = new Intent(CCPIntentUtils.INTENT_P2P_ENABLED);
		context.sendBroadcast(intent);
	}

	/**
	 * Callback this method when networks changed.
	 * 
	 * @paramapn
	 *            mobile access point name
	 * @paramns
	 *            mobile network state
	 *            
	 * @version 3.5
	 */
	@Override
	public void onReceiveEvents(CCPEvents events/*, APN network, NetworkState ns*/) {
		Log4Util.d(DEMO_TAG, "Receive CCP events , " + events)  ;
		if(events == CCPEvents.SYSCallComing) {
			Bundle b = new Bundle();
			sendTarget(WHAT_ON_RECEIVE_SYSTEM_EVENTS, b);
		}
	}

	public void showToastMessage(CloopenReason reason) {
		if(reason != null && reason.isError()) {
			if(reason.getReasonCode() == 230007) {
				//Toast.makeText(context, "语音发送被取消[" + reason.getReasonCode() + "]", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(reason.getReasonCode() == SdkErrorCode.SDK_FILE_NOTEXIST
					|| reason.getReasonCode() == SdkErrorCode.SDK_AMR_CANCLE) {
				return;
			}
			//Toast.makeText(context, reason.getMessage() + "[" + reason.getReasonCode() + "]", Toast.LENGTH_SHORT).show();
		}
	}
	
	protected void finalize () {
		
		if(device != null) {
			device.release();
			device = null;
			sInstance = null;
		}
	}
	
	/**
	 * 
	 * @param rcb
	 */
	public void setRegistCallback(RegistCallBack rcb) {
		this.mCallback = rcb;
	}
	
	/**
	 * 
	 * @ClassName: RegistCallBack.java
	 * @Description: TODO
	 * @author Jorstin Chan 
	 * @date 2013-12-12
	 * @version 3.6
	 */
	public interface RegistCallBack {
		/**WHAT_INIT_ERROR
		 * call back when regist over.
		 * @param reason {@linkWHAT_INIT_ERROR} {@linkWHAT_ON_CONNECT} {@linkWHAT_ON_DISCONNECT}
		 * @param msg regist failed message
		 * 
		 * @seecom.voice.demo.ui.CCPHelper#WHAT_ON_CONNECT
		 * @seecom.voice.demo.ui.CCPHelper#WHAT_INIT_ERROR
		 * @seecom.voice.demo.ui.CCPHelper#WHAT_ON_DISCONNECT
		 */
		void onRegistResult(int reason, String msg);
	}

    /**
     * 如果为群组，根据群组id查询该群组是否在本地存在，若不存在，表示这是一个新创建的群组，需从网络上加载该群组信息并存在本地
     */
    private void insertNewGroup(String y_groupId, String userData) {
        try {
            if (y_groupId == null || !y_groupId.startsWith("g"))
                return;
            if (groupTable.isExistsGroupId(y_groupId) == null) {
                newGroupMsgUserDataList.addFirst(userData);
                Task.getGroup(buildRequestGroupParams(y_groupId, userData), this, this);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> buildRequestGroupParams(String y_groupId, String userData) {
        final HashMap<String, String> params = new HashMap<String, String>();
        if(Session.get(context) != null) {
            params.put("u_id", Session.get(context).getUid());
            params.put("u_listid", IMParseUtil.getListId(userData));
            params.put("y_gid", y_groupId);
        }
        return params;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }

    @Override
    public void onResponse(String s) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(s);
            if (jsonObject != null && jsonObject.getString("group") != null) {
                JSONObject group = jsonObject.getJSONObject("group");
                if (group == null || group.size() == 0) {
                    return;
                }
                GroupInfo groupInfo = new GroupInfo();
                groupInfo.setCreatorid(group.getString("creatorid"));
                groupInfo.setG_ucount(group.getString("g_ucount"));
                groupInfo.setId(group.getString("id"));
                groupInfo.setG_name(group.getString("g_name"));
                groupInfo.setG_type(group.getString("g_type"));
                groupInfo.setG_permisson(group.getString("g_permisson"));
                groupInfo.setU_listid(group.getString("u_listid"));
                groupInfo.setG_capacity(group.getString("g_capacity"));
                groupInfo.setG_declared(group.getString("g_declared"));
                groupInfo.setY_gid(group.getString("y_gid"));
                groupInfo.setZ_gid(group.getString("z_gid"));
                groupInfo.setZ_type(group.getString("z_type"));
                groupInfo.setIdentityId(IMParseUtil.getInstance(Session.get(context)).buildIdentity(newGroupMsgUserDataList.poll()));

                if (dbHelper.getGroupInfoTable().isExistsGroupId(groupInfo.getY_gid()) != null) {
                    dbHelper.getGroupInfoTable().updateGroupInfo(groupInfo);
                } else {
                    dbHelper.getGroupInfoTable().insertIMGroupInfo(groupInfo);
                }
            }
        } catch (JSONException e) {

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
