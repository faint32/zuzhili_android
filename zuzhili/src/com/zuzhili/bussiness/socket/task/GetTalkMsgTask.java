package com.zuzhili.bussiness.socket.task;

import com.zuzhili.bussiness.utility.Constants;

/**
 * Created by liutao on 14-4-17.
 */
public class GetTalkMsgTask extends Task {

    public GetTalkMsgTask(String friendId, int pageIndex, int pageSize) {
        super.command = Constants.IM_CMD_GET_TALK_MSG;
        super.friendId = friendId;
        super.pageIndex = String.valueOf(pageIndex);
        super.pageSize = String.valueOf(pageSize);
    }

    @Override
    public String[] getFullCommand() {
        return new String[] {command, friendId, pageIndex, pageSize, null, null, null};
    }
}
