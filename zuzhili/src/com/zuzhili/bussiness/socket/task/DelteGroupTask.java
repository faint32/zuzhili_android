package com.zuzhili.bussiness.socket.task;

import com.zuzhili.bussiness.utility.Constants;

/**
 * Created by liutao on 14-5-22.
 */
public class DelteGroupTask extends Task {

    public DelteGroupTask(String userId, String groupId) {
        super.command = Constants.IM_CMD_DEL_GROUP;
        super.userId = userId;
        super.groupId = groupId;
    }

    @Override
    public String[] getFullCommand() {
        return new String[] {command, userId, groupId};
    }
}
