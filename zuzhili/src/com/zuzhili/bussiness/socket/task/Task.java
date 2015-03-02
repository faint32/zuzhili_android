package com.zuzhili.bussiness.socket.task;

/**
 * Created by liutao on 14-4-11.
 */
public abstract class Task {

    /** socket command */
    protected String command;

    /** user id */
    protected String userId;

    /** friend id */
    protected String friendId;

    protected String password;

    protected String userName;

    /** group type, 0 indicates custom group, 1 indicates conference group */
    protected String groupType;

    protected String groupName;

    protected String reason;

    /** chat content */
    protected String content;

    /** page index */
    protected String pageIndex;

    /** page size */
    protected String pageSize;

    protected String listId;

    /** group chat user ids */
    protected String friendIds;

    /** group id */
    protected String groupId;

    protected String applyerId;

    /** get specific full commmand */
    public abstract String[] getFullCommand();

    public String getCommand() {
        return command;
    }

    public String getUserId() {
        return userId;
    }

    public String getFriendId() {
        return friendId;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public String getGroupType() {
        return groupType;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getReason() {
        return reason;
    }

    public String getContent() {
        return content;
    }

    public String getPageIndex() {
        return pageIndex;
    }

    public String getPageSize() {
        return pageSize;
    }

    public String getListId() {
        return listId;
    }

    public String getFriendIds() {
        return friendIds;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getApplyerId() {
        return applyerId;
    }
}
