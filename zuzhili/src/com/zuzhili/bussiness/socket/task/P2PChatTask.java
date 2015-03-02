package com.zuzhili.bussiness.socket.task;

import com.zuzhili.bussiness.utility.Constants;

/**
 * Created by liutao on 14-4-14.
 */
public class P2PChatTask extends Task {

    public P2PChatTask(String friendId, String content, String listId) {
        super.command = Constants.IM_CMD_TALK;
        super.friendId = friendId;
        super.content = content;
        super.listId = listId;
    }
    @Override
    public String[] getFullCommand() {
        return new String[] {command, friendId, content, listId};
    }
}
