package com.zuzhili.bussiness.socket.task;

import com.zuzhili.bussiness.utility.Constants;

/**
 * Created by liutao on 14-4-21.
 */
public class GroupChatTask extends Task {

    public GroupChatTask(String userId, String groupId, String content, String listId) {
        super.command = Constants.IM_CMD_TALK_ALL;
        super.userId = userId;
        super.groupId = groupId;
        super.content = content;
        super.listId = listId;
    }

    @Override
    public String[] getFullCommand() {
        return new String[] {command, userId, groupId, content, listId};
    }
}
