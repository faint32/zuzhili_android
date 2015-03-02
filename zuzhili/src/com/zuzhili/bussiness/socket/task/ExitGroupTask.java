package com.zuzhili.bussiness.socket.task;

import com.zuzhili.bussiness.utility.Constants;

/**
 * Created by liutao on 14-4-27.
 */
public class ExitGroupTask extends Task {

    public ExitGroupTask(String userId, String applyerId, String groupId) {
        super.command = Constants.IM_CMD_EXIT_GROUP;
        super.userId = userId;
        super.applyerId = applyerId;
        super.groupId = groupId;
    }

    @Override
    public String[] getFullCommand() {
        return new String[] {command, userId, applyerId, groupId};
    }
}
