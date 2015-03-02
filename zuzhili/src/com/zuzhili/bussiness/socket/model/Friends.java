package com.zuzhili.bussiness.socket.model;

import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.bussiness.utility.pinyin.CharacterParser;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.utils.Utils;

import java.util.ArrayList;

/**
 * Created by liutao on 14-4-12.
 */
public class Friends {

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;

    private String size;

    private ArrayList<FriendInfo> friendsList;

    public String getSize() {
        return size;
    }

    public ArrayList<FriendInfo> getFriendsList() {
        return friendsList;
    }

    public Friends() {
        characterParser = CharacterParser.getInstance();
    }

    public void parse(String[] result, Session session, String commandType, boolean isFilterMyself) {
        this.size = result[1];
        if (size.equals("0")) {
            this.friendsList = new ArrayList<FriendInfo>(0);
        } else {
            this.friendsList = new ArrayList<FriendInfo>(Integer.valueOf(size));
            int index = 2;
            for (int i = 0; i < Integer.valueOf(size); i++) {
                FriendInfo item = new FriendInfo();
                item.setUid(result[index++]);
                item.setUserName(result[index++]);
                item.setUserAvatar(result[index++]);
                item.setUserStatus(result[index++]);
                item.setLastTalkTime(result[index++]);
                item.setOnLineType(result[index++]);

                if (commandType.equals(Constants.IM_CMD_GET_GROUP_USER)) {
                    item.setManagerFlag(result[index++]);
                    item.setCreatorFlag(result[index++]);
                    item.setMuteFlag(result[index++]);
                    item.setJoinTime(result[index++]);
                }

                item.setIdentity(Utils.getIdentity(session));
                updateSortKey(item);
                if (!isFilterMyself) {
                    friendsList.add(item);
                    if (item.getUid().equals(session.getIds())) {
//                        session.setMySelfInfo(item);
                    }
                } else {
                    if (!item.getUid().equals(session.getIds())) {
                        friendsList.add(item);
                    } else {
//                        session.setMySelfInfo(item);
                    }
                }
            }
        }
    }

    private void updateSortKey(FriendInfo friendInfo) {
        //汉字转换成拼音
        String pinyin = characterParser.getSelling(TextUtil.processNullString(friendInfo.getUserName()));
//        LogUtils.e("userName: " + friendInfo.getUserName() + ", pinyin: " + pinyin);
        String sortString;
        if (pinyin != null && pinyin.length() > 0) {
            sortString = pinyin.substring(0, 1).toUpperCase();
        } else {
            sortString = "#";
        }

        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            friendInfo.setSortKey(sortString.toUpperCase());
        } else {
            friendInfo.setSortKey("#");
        }
    }
}
