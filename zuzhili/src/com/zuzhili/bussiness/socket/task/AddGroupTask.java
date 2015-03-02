package com.zuzhili.bussiness.socket.task;

import com.zuzhili.bussiness.utility.Constants;

/**
 * Created by liutao on 14-4-21.
 */
public class AddGroupTask extends Task {

    public AddGroupTask(String userId, String groupName, String groupType, String listId) {
        super.command = Constants.IM_CMD_ADD_GROUP;
        super.userId = userId;
        super.groupName = groupName;
        super.groupType = groupType;
        super.listId = listId;
    }

    @Override
    public String[] getFullCommand() {
        return new String[] {command, userId, groupName, groupType, listId};
    }
}
