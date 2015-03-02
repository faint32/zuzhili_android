package com.zuzhili.bussiness.socket.task;

import com.zuzhili.bussiness.utility.Constants;

/**
 * Created by liutao on 14-5-10.
 * 获得聊天室的详细信息
 */
public class GetGroupInfoTask extends Task {

    public GetGroupInfoTask(String userId, String groupId) {
        super.command = Constants.IM_CMD_GET_GROUP_INFO;
        super.userId = userId;
        super.groupId = groupId;
    }

    @Override
    public String[] getFullCommand() {
        return new String[] {command, userId, groupId};
    }
}
