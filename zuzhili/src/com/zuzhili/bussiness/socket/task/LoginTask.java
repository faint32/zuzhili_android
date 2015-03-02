package com.zuzhili.bussiness.socket.task;

import com.zuzhili.bussiness.utility.Constants;

/**
 * Created by liutao on 14-4-26.
 */
public class LoginTask extends Task {

    public LoginTask(String userId) {
        super.command = Constants.IM_CMD_LOGIN;
        super.userId = userId;
    }

    @Override
    public String[] getFullCommand() {
        return new String[] {command, userId, "1", "1", "1"};
    }
}
