package com.zuzhili.bussiness.socket.task;

import com.zuzhili.bussiness.utility.Constants;

/**
 * Created by liutao on 14-4-22.
 */
public class GetGroupUserTask extends Task {

    public GetGroupUserTask(String userId, String groupId) {
        super.command = Constants.IM_CMD_GET_GROUP_USER;
        super.userId = userId;
        super.groupId = groupId;
    }
    @Override
    public String[] getFullCommand() {
        return new String[] {command, userId, groupId};
    }
}
