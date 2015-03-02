package com.zuzhili.bussiness.socket.model;

import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.bussiness.utility.pinyin.CharacterParser;
import com.zuzhili.bussiness.utility.pinyin.PinyinComparator;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.utils.Utils;

import java.util.ArrayList;

/**
 * Created by liutao on 14-4-12.
 */
public class Users {

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;

    private String size;

    private ArrayList<UserInfo> usersList;

    public String getSize() {
        return size;
    }

    public ArrayList<UserInfo> getUsersList() {
        return usersList;
    }

    public Users() {
        characterParser = CharacterParser.getInstance();
    }

    public void parse(String[] result, Session session, String commandType, boolean isFilterMyself) {
        this.size = result[1];
        if (size.equals("0")) {
            this.usersList = new ArrayList<UserInfo>(0);
        } else {
            this.usersList = new ArrayList<UserInfo>(Integer.valueOf(size));
            int index = 2;
            for (int i = 0; i < Integer.valueOf(size); i++) {
                UserInfo item = new UserInfo();
                item.setU_id(result[index++]);
                item.setU_name(result[index++]);
                item.setU_icon(result[index++]);
                //item.setUserStatus(result[index++]);
                item.setU_lastSa(result[index++]);
                //item.setOnLineType(result[index++]);

                if (commandType.equals(Constants.IM_CMD_GET_GROUP_USER)) {
//                    item.setManagerFlag(result[index++]);
//                    item.setCreatorFlag(result[index++]);
//                    item.setMuteFlag(result[index++]);
//                    item.setJoinTime(result[index++]);
                }

                item.setIdentity(Utils.getIdentity(session));
                updateSortKey(item);
                if (!isFilterMyself) {
                    usersList.add(item);
                    if (item.getU_id().equals(session.getUid())) {
                        session.setMySelfInfo(item);
                    }
                } else {
                    if (!item.getU_id().equals(session.getUid())) {
                        usersList.add(item);
                    } else {
                        session.setMySelfInfo(item);
                    }
                }
            }
        }
    }

    private void updateSortKey(UserInfo userInfo) {
        //汉字转换成拼音
        String pinyin = characterParser.getSelling(TextUtil.processNullString(userInfo.getU_name()));
//        LogUtils.e("userName: " + friendInfo.getUserName() + ", pinyin: " + pinyin);
        String sortString;
        if (pinyin != null && pinyin.length() > 0) {
            sortString = pinyin.substring(0, 1).toUpperCase();
        } else {
            sortString = "#";
        }

        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            userInfo.setSortKey(sortString.toUpperCase());
        } else {
            userInfo.setSortKey("#");
        }
    }
}
