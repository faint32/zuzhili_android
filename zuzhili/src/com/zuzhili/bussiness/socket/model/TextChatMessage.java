package com.zuzhili.bussiness.socket.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zuzhili.bussiness.utility.Constants;

/**
 * Created by liutao on 14-4-15.
 */
public class TextChatMessage extends ChatMessage {

    private TextChat textChat;

    private String lastTalkTime;

    public TextChat getTextChat() {
        return textChat;
    }

    public void setTextChat(TextChat textChat) {
        this.textChat = textChat;
    }

    public String getLastTalkTime() {
        return lastTalkTime;
    }

    public void setLastTalkTime(String lastTalkTime) {
        this.lastTalkTime = lastTalkTime;
    }

    public TextChatMessage parse(String[] result, String userAvatar) {
        JSONObject jsonObject = null;
        if (result.length == 5) {
            // talk -300 Listid userid content
            this.listId = result[2];
            this.friendId = result[3];
            this.groupType = Constants.IM_TYPE_P2P_CHAT;
            jsonObject = JSON.parseObject(result[4]);
        } else if (result.length == 6) {
            // talkAll -300 Listid Gid Userid Content
            this.listId = result[2];
            this.groupId = result[3];
            this.friendId = result[4];
            this.groupType = Constants.IM_TYPE_GROUP_CHAT;
            jsonObject = JSON.parseObject(result[5]);
        }
        TextChat textChat = new TextChat();
        setTextChat(textChat);
        if (jsonObject != null) {
            this.textChat.setType(jsonObject.getString("type"));
            this.textChat.setBody(jsonObject.getString("body").replaceAll("□■", Constants.BLANK));
            this.type = jsonObject.getString("type");
        }
        this.userAvatar = userAvatar;
        this.lastTalkTime = String.valueOf(System.currentTimeMillis());
        return this;
    }

}
