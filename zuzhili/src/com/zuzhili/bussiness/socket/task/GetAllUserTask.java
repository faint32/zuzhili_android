package com.zuzhili.bussiness.socket.task;

import com.zuzhili.bussiness.utility.Constants;

/**
 * Created by liutao on 14-4-12.
 * retrive my friends command
 */
public class GetAllUserTask extends Task {

    public GetAllUserTask(String listId) {
        super.command = Constants.IM_CMD_GET_ALL_USER;
        super.listId = listId;
    }

    @Override
    public String[] getFullCommand() {
        return new String[] {command, listId};
    }
}
