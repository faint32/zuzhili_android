package com.zuzhili.db.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Id;

/**
 * Created by liutao on 14-5-9.
 */
@Deprecated
public class Speech implements Parcelable {

    @Id
    private int id;

    private long time;

    private String msgEntity;

    private String speaker;

    /** p2p chat, groupId i.e. friendId; group chat, groupId i.e. groupId in fact */
    private String groupId;

    /** a session is a p2p chat, or group chat. session id is used for the session identification to query chat history */
    private String sessionId;

    private String groupType;

    private String identity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMsgEntity() {
        return msgEntity;
    }

    public void setMsgEntity(String msgEntity) {
        this.msgEntity = msgEntity;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    @Override
    public String toString() {
        return "Speech{" +
                "id=" + id +
                ", time=" + time +
                ", msgEntity='" + msgEntity + '\'' +
                ", speaker='" + speaker + '\'' +
                ", groupId='" + groupId + '\'' +
                ", groupType='" + groupType + '\'' +
                ", identity='" + identity + '\'' +
                '}';
    }


    public static final Parcelable.Creator<Speech> CREATOR = new Creator() {

        @Override
        public Speech createFromParcel(Parcel source) {
            Speech speech = new Speech();
            speech.setId(source.readInt());
            speech.setTime(source.readLong());
            speech.setMsgEntity(source.readString());
            speech.setSpeaker(source.readString());
            speech.setGroupId(source.readString());
            speech.setSessionId(source.readString());
            speech.setGroupType(source.readString());
            speech.setIdentity(source.readString());
            return speech;
        }

        @Override
        public Speech[] newArray(int size) {
            return new Speech[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(time);
        dest.writeString(msgEntity);
        dest.writeString(speaker);
        dest.writeString(groupId);
        dest.writeString(sessionId);
        dest.writeString(groupType);
        dest.writeString(identity);
    }

}
