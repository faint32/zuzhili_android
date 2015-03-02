package com.zuzhili.bussiness.socket.task;

import com.zuzhili.bussiness.utility.Constants;

/**
 * Created by liutao on 14-4-25.
 */
public class GetUserInfoTask extends Task {

    public GetUserInfoTask(String userId) {
        super.command = Constants.IM_CMD_GET_USER_INFO;
        super.userId = userId;
    }

    @Override
    public String[] getFullCommand() {
        return new String[] {command, userId};
    }
}
