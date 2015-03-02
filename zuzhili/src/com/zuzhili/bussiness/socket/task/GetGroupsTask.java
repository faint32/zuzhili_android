package com.zuzhili.bussiness.socket.task;

import com.zuzhili.bussiness.utility.Constants;

/**
 * Created by liutao on 14-4-11.
 * retrieve groups commmand
 */
public class GetGroupsTask extends Task {

    public GetGroupsTask(String userId, String listId) {
        super.command = Constants.IM_CMD_GET_GROUPS;
        super.userId = userId;
        super.listId = listId;
    }

    @Override
    public String[] getFullCommand() {
        return new String[] {command, userId, listId};
    }
}
