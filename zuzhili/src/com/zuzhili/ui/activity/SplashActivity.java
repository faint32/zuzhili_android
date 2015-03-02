package com.zuzhili.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.hisun.phone.core.voice.CCPCall;
import com.hisun.phone.core.voice.util.Log4Util;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.helper.CCPHelper;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.im.ITask;
import com.zuzhili.framework.im.TaskKey;
import com.zuzhili.framework.im.ThreadPoolManager;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.Account;
import com.zuzhili.service.GetIMDataIntentService;
import com.zuzhili.ui.activity.loginreg.LoginActivity;
import com.zuzhili.ui.activity.social.SocialsActivity;

import java.util.HashMap;

import static com.zuzhili.framework.SessionManager.P_EMAIL;
import static com.zuzhili.framework.SessionManager.P_PASSWORD;

/**
 * Created by liutao on 14-5-7.
 */
public class SplashActivity extends Activity implements Response.Listener<String>, Response.ErrorListener, CCPHelper.RegistCallBack, ThreadPoolManager.OnTaskDoingLinstener {

    private Session mSession;

    private PullIMDataReceiver mReceiver;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_splash);
        init();
        IntentFilter statusIntentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION);

        // Sets the filter's category to DEFAULT
        statusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Instantiates a new DownloadStateReceiver
        mReceiver = new PullIMDataReceiver();

        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mReceiver,
                statusIntentFilter);
    }

    @Override
    protected void onDestroy() {
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        super.onDestroy();
    }

    private void init() {
        mSession = Session.get(getApplicationContext());
        SharedPreferences mPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean hasGuide = mPreference.getBoolean(Constants.EXTRA_GUIDE, false);
        String email = mPreference.getString(P_EMAIL, "");
        String password = mPreference.getString(P_PASSWORD, "");

        if (hasGuide) {
            if (mSession.isAutoLogin() && !email.isEmpty() && !password.isEmpty()) {
                final HashMap<String, String> params = new HashMap<String, String>();
                params.put("email", mSession.getEmail());
                params.put("password", mSession.getPassword());
                if (Utils.isThirdPackage(this)) {
                    Task.loginNonZuzhili(params, this, this);
                } else {
                    Task.login(params, this, this);
                }
            } else {
                Intent it = new Intent(this, LoginActivity.class);
                startActivity(it);
            }
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(SplashActivity.class.getSimpleName(), "step into handler.run()");
                    Intent it = new Intent(SplashActivity.this, GuideActivity.class);
                    startActivity(it);
                }
            }, 1500);
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Intent it = new Intent(this, LoginActivity.class);
        startActivity(it);
    }

    @Override
    public void onResponse(String result) {

        // if the user has login and not selected social, then forward to the social selection page.
        if (mSession.getListid() == null
                || mSession.getListid().trim().equals("")
                || mSession.getListid().trim().equals("0")) {

            Intent intent = new Intent(this, SocialsActivity.class);
            startActivity(intent);
            finish();

        } else {

            JSONObject jsonObject = JSONObject.parseObject(result);

            if (jsonObject.getString("user") != null) {
                JSONObject user = JSON.parseObject(jsonObject.getString("user"));
                if (user != null) {
                    mSession.setVoipId(user.getString("y_voip"));
                    mSession.setVoipPassword(user.getString("y_voippass"));
                    mSession.setSubAccount(user.getString("y_subid"));
                    mSession.setSubToken(user.getString("y_subpass"));
                    doSDKRegist();
                }
            } else {
                Account account = JSON.parseObject(result, Account.class);
                mSession.setAccount(account);
                mSession.setUid(account.getUserid());
                Task.getYTXAccount(buildGetYTXAccountParams(), this, this);
            }
        }
    }

    private void doSDKRegist() {
        ITask iTask = new ITask(TaskKey.KEY_SDK_REGIST);
        addTask(iTask);
    }

    private HashMap<String, String> buildGetYTXAccountParams() {
        final HashMap<String, String> params = new HashMap<String, String>();
        if (mSession != null) {
            params.put("u_listid", "0");  // no meaning, just a useless argument
            params.put("u_id", mSession.getUid());
        }
        return params;
    }

    public void addTask(ITask iTask) {
        ThreadPoolManager.getInstance().setOnTaskDoingLinstener(this);
        ThreadPoolManager.getInstance().addTask(iTask);
    }

    @Override
    public void doTaskBackGround(ITask iTask) {
        handleTaskBackGround(iTask);
    }

    protected void handleTaskBackGround(ITask iTask) {
        if (iTask.getKey() == TaskKey.KEY_SDK_REGIST) {
            CCPHelper.getInstance().registerCCP(this);
        }
    }

    @Override
    public void onRegistResult(final int reason, final String msg) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    //closeConnectionProgress();
                    if (reason == CCPHelper.WHAT_ON_CONNECT) {
                        //startAction();
                        Log4Util.d(CCPHelper.DEMO_TAG, "YES , it's ok");

                        Intent intent = new Intent(SplashActivity.this, GetIMDataIntentService.class);
                        intent.putExtra(Constants.ACTION, Task.ACTION_GET_ALL_USERS);
                        startService(intent);

                    } else if (reason == CCPHelper.WHAT_ON_DISCONNECT || reason == CCPHelper.WHAT_INIT_ERROR) {
                        // do nothing ...
                        //showInitErrToast(msg);
                        Log4Util.d(CCPHelper.DEMO_TAG, "Sorry , cWHAT_ON_DISCONNECT WHAT_INIT_ERROR" + msg);
                        CCPHelper.getInstance().release();
                        CCPCall.shutdown();
                    } else {
                        Log4Util.d(CCPHelper.DEMO_TAG, "Sorry , can't handle a message " + msg);
                    }
                    //Utils.makeEventToast(SplashActivity.this, msg, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CCPHelper.getInstance().setRegistCallback(null);
            }
        });
    }

    private class PullIMDataReceiver extends BroadcastReceiver {

        private PullIMDataReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra(Constants.EXTENDED_DATA_STATUS,
                    Constants.PULL_DATA_FAILED)) {
                case Constants.PULL_IM_USERS_FINISHED:
                    // 聊天成员拉取完毕，拉取群组信息
                    Intent it = new Intent(SplashActivity.this, GetIMDataIntentService.class);
                    it.putExtra(Constants.ACTION, Task.ACTION_GET_GROUPS);
                    startService(it);
                    break;
                case Constants.PULL_IM_GROUPS_FINISHED:
                    // 群组拉取完毕，跳转到首页
                    it = new Intent(SplashActivity.this, HomeTabActivity.class);
                    startActivity(it);
                    break;
            }
        }
    }
}
