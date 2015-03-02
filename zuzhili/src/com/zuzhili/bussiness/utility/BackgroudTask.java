package com.zuzhili.bussiness.utility;

import android.os.AsyncTask;

/**
 * Created by liutao on 14-4-23.
 */
public class BackgroudTask extends AsyncTask<Runnable , Void, Void> {
    @Override
    protected Void doInBackground(Runnable... params) {
        new Thread(params[0]).start();
        return null;
    }
}
