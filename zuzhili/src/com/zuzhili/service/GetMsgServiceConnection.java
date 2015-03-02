package com.zuzhili.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.zuzhili.framework.Session;

/**
 * Created by liutao on 14-4-24.
 */
public class GetMsgServiceConnection implements ServiceConnection {

    private GetMsgService.ServiceBinder mBinder;

    private Session session;

    public GetMsgServiceConnection(Session session) {
        this.session = session;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mBinder = (GetMsgService.ServiceBinder) service;
        session.setBinder(mBinder);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        mBinder = null;
        session.setBinder(mBinder);
    }

    public GetMsgService.ServiceBinder getmBinder() {
        return mBinder;
    }
}
