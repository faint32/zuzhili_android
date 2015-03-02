package com.zuzhili.bussiness.socket.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liutao on 14-4-17.
 */
public class Messages {

    private String size;

    private ArrayList<ChatMessage> chatMessages;

    private Map<String, String> userAvatars = new HashMap<String, String>();

    public ArrayList<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    public String getSize() {
        return size;
    }

    public void parse(String[] result, String userAvatar) {
        this.size = result[1];
        if (size.equals("0")) {
            this.chatMessages = new ArrayList<ChatMessage>(0);
        } else {
            this.chatMessages = new ArrayList<ChatMessage>(Integer.valueOf(size));
            int index = 2;
            for (int i = 0; i < (result.length - 2) / 3; i++) {
                TextChatMessage item = new TextChatMessage();
                item.setFriendId(result[index++]);
                item.setLastTalkTime(result[index++]);
                item.setTextChat(new TextChat().parse(result[index++]));

                item.setType(item.getTextChat().getType());
                item.setUserAvatar(userAvatar);

                chatMessages.add(0, item);
            }
            Collections.sort(chatMessages, new Comparator<ChatMessage>() {
                @Override
                public int compare(ChatMessage lhs, ChatMessage rhs) {
                    return (int) (Long.valueOf(((TextChatMessage)lhs).getLastTalkTime()) - Long.valueOf(((TextChatMessage)rhs).getLastTalkTime()));
                }
            });
        }
    }

//    private String getUserAvatar(DBHelper dbHelper, Session session, String friendId) {
//        if (userAvatars.get(friendId) == null) {
//            if (friendId.equals(session.getIds())) {
//                userAvatars.put(friendId, session.getUserhead());
//            } else {
//                FriendInfo friendInfo = dbHelper.getImContactTable().get(friendId, Utils.getIdentity(session));
//                if (friendInfo != null) {
//                    userAvatars.put(friendId, TextUtil.processNullString(friendInfo.getUserAvatar()));
//                }
//            }
//        }
//        return userAvatars.get(friendId);
//    }
}
