package com.zuzhili.bussiness.socket.task;

import com.zuzhili.bussiness.utility.Constants;

/**
 * Created by liutao on 14-4-21.
 */
public class AddPersonToGroupTask extends Task {

    public AddPersonToGroupTask(String userId, String userIds, String groupId) {
        super.command = Constants.IM_CMD_ADD_PERSON_TO_GROUP;
        super.userId = userId;
        super.friendIds = userIds;
        super.groupId = groupId;
    }

    @Override
    public String[] getFullCommand() {
        return new String[] {command, userId, friendIds, groupId};
    }
}
