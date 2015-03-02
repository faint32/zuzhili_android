package com.zuzhili.bussiness.socket;

import android.os.AsyncTask;

import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.bussiness.socket.task.Task;
import com.zuzhili.bussiness.utility.Constants;

/**
 * Created by liutao on 14-4-11.
 */
public class RetrieveIMSessionTask extends AsyncTask<Task, Void, Void> {

    private MainSocket socket;

    private String ids;

    public RetrieveIMSessionTask(String ids) {
        this.ids = ids;
    }

    @Override
    protected Void doInBackground(Task... params) {
        this.socket = MainSocket.getInstance(ids);
        socket.sendCommand(params[0].getFullCommand(), true, null, false);
        LogUtils.e("---------------- Task executes! full command: " + getFullCommand(params[0].getFullCommand()));
        return null;
    }

    private String getFullCommand(String[] commands) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < commands.length; i++) {
            builder.append(commands[i]).append(Constants.BLANK);
        }
        return builder.toString();
    }
}
