package com.zuzhili.bussiness.socket.task;

import com.zuzhili.bussiness.utility.Constants;

/**
 * Created by liutao on 14-5-8.
 */
public class LogoutTask extends Task {

    public LogoutTask(String userId) {
        super.command = Constants.IM_CMD_LOGOUT;
        super.userId = userId;
    }

    @Override
    public String[] getFullCommand() {
        return new String[] {command, userId, "1"};
    }
}
