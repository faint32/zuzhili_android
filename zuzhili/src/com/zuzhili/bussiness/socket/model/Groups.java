package com.zuzhili.bussiness.socket.model;

import java.util.ArrayList;

/**
 * Created by liutao on 14-4-11.
 */
public class Groups {

    /** indicates group count which i joined */
    private String size;

    private ArrayList<GroupInfo> groupList;

    public String getSize() {
        return size;
    }

    public ArrayList<GroupInfo> getGroupList() {
        return groupList;
    }

    public void parse(String[] result) {
        this.size = result[1];
        if (size.equals("0")) {
            this.groupList = new ArrayList<GroupInfo>(0);
        } else {
            this.groupList = new ArrayList<GroupInfo>(Integer.valueOf(size));
            int index = 2;
            for (int i = 0; i < Integer.valueOf(size); i++) {
                GroupInfo item = new GroupInfo();
                item.setId(result[index++]);
                item.setY_gid(result[index++]);
                item.setCreatorid(result[index++]);
                item.setG_name(result[index++]);
                item.setG_declared(result[index++]);
                item.setG_type(result[index++]);
                item.setG_permisson(result[index++]);
                item.setG_ucount(result[index++]);
                item.setU_listid(result[index++]);
                item.setG_lastSay(result[index++]);
                //if (item.getG_type() != null && !item.getG_type().equals("1")) {
                    groupList.add(item);
                //}
            }
        }
    }
}
