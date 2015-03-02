package com.zuzhili.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.table.TableUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.im.IMChatMessageDetail;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by liutao on 14-6-15.
 */
public class IMMessageTable extends Table {

    public static final String TABLES_NAME_IM_MESSAGE 				= "im_message";

    /**
     * IM message
     */
    public class IMMessageColumn extends BaseColumn {
        public static final String IM_MESSAGE_ID 					= "MSGID"; 						// message id
        public static final String IM_SESSION_ID 					= "SESSIONID";					// Identifies a dialogue
        public static final String IM_MESSAGE_TYPE 					= "MSG_TYPE";  					// The type of information (Attached, text)
        public static final String IM_MESSAGE_SENDER 				= "SENDER";						// the message sender
        public static final String IM_READ_STATUS 					= "ISREAD";    					// if message read or not
        public static final String IM_SEND_STATUS 					= "IM_STATE";   				// Send state
        public static final String IM_DATE_CREATE 					= "CREATEDATE";   				// IM message creation time (server)
        public static final String IM_CURRENT_DATE 					= "CURDATE";   					// IM news (local time to create and manage)
        public static final String IM_USER_DATE 					= "USERDATA";   				// User defined extension field
        public static final String IM_MESSAGE_CONTENT 				= "MSGCONTENT"; 				// Information content
        public static final String IM_FILE_URL 						= "FILEURL";               		// Attached url on the server.
        public static final String IM_FILE_PATH 					= "FILEPATH";               	// Local storage address (attachment sending or receiving complete)
        public static final String IM_FILE_EXT 						= "FILEEXT";               		// Attachment extension
        public static final String IM_DURATION 						= "DURATION";               	// If the attachment for the voice file, for the voice file time
        public static final String IM_LIST_ID 						= "LIST_ID";               	    // 消息所在的社区
        public static final String IM_IDENTITY_ID 					= "IDENTITY_ID";               	// Util.getIdentityId()

    }

    @Override
    public void createTable() {
        super.createTable();
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLES_NAME_IM_MESSAGE
                + " ( " //ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + IMMessageColumn.IM_MESSAGE_ID + " TEXT PRIMARY KEY , "
                + IMMessageColumn.IM_SESSION_ID + " TEXT NOT NULL, "
                + IMMessageColumn.IM_MESSAGE_TYPE + "  INTEGER NOT NULL, "
                + IMMessageColumn.IM_MESSAGE_SENDER + " TEXT ,"
                + IMMessageColumn.IM_READ_STATUS + "  INTEGER NOT NULL DEFAULT 0, "
                + IMMessageColumn.IM_SEND_STATUS + "  INTEGER NOT NULL, "
                + IMMessageColumn.IM_DATE_CREATE + " TEXT , "
                + IMMessageColumn.IM_CURRENT_DATE + " TEXT , "
                + IMMessageColumn.IM_USER_DATE + " TEXT , "
                + IMMessageColumn.IM_MESSAGE_CONTENT + " TEXT , "
                + IMMessageColumn.IM_FILE_URL + " TEXT , "
                + IMMessageColumn.IM_FILE_PATH + " TEXT , "
                + IMMessageColumn.IM_FILE_EXT + " TEXT , "
                + IMMessageColumn.IM_LIST_ID + " TEXT NOT NULL, "
                + IMMessageColumn.IM_IDENTITY_ID + " TEXT, "
                + IMMessageColumn.IM_DURATION + " INTEGER)";
        LogUtils.i("CREATE TABLE " + TABLES_NAME_IM_MESSAGE + sql);
        utils.getDatabase().execSQL(sql);
    }

    @Override
    public void upgrade(DbUtils db, Class clazz, int oldVersion, int newVersion) {
        try {
            db.execNonQuery("ALTER TABLE " + TABLES_NAME_IM_MESSAGE + " ADD COLUMN " + IMMessageColumn.IM_IDENTITY_ID + " TEXT ");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据messageId查询消息是否存在
     * @param messageId
     * @return
     * @throws SQLException
     */
    public String isExistsIMmessageId(String messageId) throws SQLException {
        if (TextUtils.isEmpty(messageId)) {
            return null;
        }
        Cursor cursor = null;
        try {
            String where = IMMessageColumn.IM_MESSAGE_ID + " ='" + messageId + "'";
            cursor = utils.getDatabase().query(TABLES_NAME_IM_MESSAGE, new String[]{IMMessageColumn.IM_MESSAGE_ID}, where, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                if(cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_MESSAGE_ID));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return null;
    }

    /**
     * 根据messageId 查询一条消息
     * @param messageId
     * @return
     * @throws SQLException
     */
    public IMChatMessageDetail queryIMChatMessageByMessageId(String messageId) throws SQLException {
        if (TextUtils.isEmpty(messageId)) {
            return null;
        }
        Cursor cursor = null;
        IMChatMessageDetail detail = null;
        try {
            String where = IMMessageColumn.IM_MESSAGE_ID + " ='" + messageId + "'";
            cursor = utils.getDatabase().query(TABLES_NAME_IM_MESSAGE, null, where, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    String listId = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_LIST_ID));
                    String sessionId = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_SESSION_ID));
                    int messageType = cursor.getInt(cursor.getColumnIndex(IMMessageColumn.IM_MESSAGE_TYPE));
                    String groupSender = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_MESSAGE_SENDER));
                    int isRead = cursor.getInt(cursor.getColumnIndex(IMMessageColumn.IM_READ_STATUS));
                    int imState = cursor.getInt(cursor.getColumnIndex(IMMessageColumn.IM_SEND_STATUS));
                    String dateCreated = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_DATE_CREATE));
                    String curCreated = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_CURRENT_DATE));
                    String userData = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_USER_DATE));
                    String messageContent = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_MESSAGE_CONTENT));
                    String fileUrl = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_FILE_URL));
                    String filePath = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_FILE_PATH));
                    String fileExt = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_FILE_EXT));

                    detail = new IMChatMessageDetail(messageId,
                            sessionId,
                            messageType,
                            groupSender,
                            isRead,
                            imState,
                            dateCreated,
                            curCreated,
                            userData,
                            messageContent,
                            fileUrl,
                            filePath,
                            fileExt,
                            listId);


                    return detail;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return detail;
    }


    /**
     * 插入一条聊天消息
     * @param imChatMessageDetail 消息实体
     * @return 是否成功插入一条数据
     * @throws SQLException
     */
    public boolean insertIMMessage(IMChatMessageDetail imChatMessageDetail) throws SQLException {

        if (imChatMessageDetail == null) {
            throw new SQLException("[AbstractSQLManager] The inserted data is empty : " + imChatMessageDetail);
        }

        if(!TextUtils.isEmpty(isExistsIMmessageId(imChatMessageDetail.getMessageId()))){
            return false;
        }

        ContentValues values = null;
        try {
            values = new ContentValues();

            values.put(IMMessageColumn.IM_MESSAGE_ID, imChatMessageDetail.getMessageId());
            values.put(IMMessageColumn.IM_SESSION_ID, imChatMessageDetail.getSessionId());
            values.put(IMMessageColumn.IM_MESSAGE_TYPE, imChatMessageDetail.getMessageType());
            values.put(IMMessageColumn.IM_MESSAGE_SENDER, imChatMessageDetail.getGroupSender());
            values.put(IMMessageColumn.IM_READ_STATUS, imChatMessageDetail.getReadStatus());
            values.put(IMMessageColumn.IM_SEND_STATUS, imChatMessageDetail.getImState());
            values.put(IMMessageColumn.IM_DATE_CREATE, imChatMessageDetail.getDateCreated());
            values.put(IMMessageColumn.IM_CURRENT_DATE, imChatMessageDetail.getCurDate());
            values.put(IMMessageColumn.IM_USER_DATE, imChatMessageDetail.getUserData());
            values.put(IMMessageColumn.IM_MESSAGE_CONTENT, imChatMessageDetail.getMessageContent());
            values.put(IMMessageColumn.IM_FILE_URL, imChatMessageDetail.getFileUrl());
            values.put(IMMessageColumn.IM_FILE_PATH, imChatMessageDetail.getFilePath());
            values.put(IMMessageColumn.IM_FILE_EXT, imChatMessageDetail.getFileExt());
            values.put(IMMessageColumn.IM_DURATION, imChatMessageDetail.getDuration());
            values.put(IMMessageColumn.IM_LIST_ID, imChatMessageDetail.getListId());
            values.put(IMMessageColumn.IM_IDENTITY_ID, imChatMessageDetail.getIdentityId());

            long result = utils.getDatabase().insert(TABLES_NAME_IM_MESSAGE, null, values);
            if (result != -1) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        } finally {
            if (values != null) {
                values.clear();
                values = null;
            }
        }
    }

    /**
     * 根据sessionId，timeStamp, 按照时间正序查询当前会话的历史聊天记录
     * @param sessionId 会话id, 可以是群组聊天时的groupId, 也可以是点对点聊天时的发送人id
     * @param listId
     * @param timeStamp 时间戳 查询该时间戳以前的聊天历史记录
     * @param zeroTime 当天零点时间戳 查询今天聊天历史记录,进行限制查询
     * @return
     */
    public ArrayList<IMChatMessageDetail> queryIMMessages(String sessionId, String listId, String timeStamp, String zeroTime) {
        if (TextUtils.isEmpty(sessionId)) {
            throw new RuntimeException("Error , sessionId is " + sessionId);
        }
        Cursor cursor = null;
        String sql= null;
        ArrayList<IMChatMessageDetail> imChatMessageDetails = null;
        try {

            if(TextUtils.isEmpty(zeroTime)){
                sql = "SELECT * FROM " + TABLES_NAME_IM_MESSAGE + " WHERE " + IMMessageColumn.IM_SESSION_ID + " = ? AND " + IMMessageColumn.IM_LIST_ID + " = ? AND " + IMMessageColumn.IM_CURRENT_DATE + " < ? ORDER BY " + IMMessageColumn.IM_CURRENT_DATE + " DESC LIMIT 20";
                cursor = utils.getDatabase().rawQuery(sql, new String[]{sessionId, listId, timeStamp});
            }else {
                sql = "SELECT * FROM " + TABLES_NAME_IM_MESSAGE + " WHERE " + IMMessageColumn.IM_SESSION_ID + " = ? AND " + IMMessageColumn.IM_LIST_ID + " = ? AND " + IMMessageColumn.IM_CURRENT_DATE + " < ? AND " + IMMessageColumn.IM_CURRENT_DATE  + " > ? ORDER BY " + IMMessageColumn.IM_CURRENT_DATE + " DESC LIMIT 20";
                cursor = utils.getDatabase().rawQuery(sql, new String[]{sessionId, listId, timeStamp, zeroTime});
            }

            if ((cursor != null) && (cursor.getCount() > 0)) {
                imChatMessageDetails = new ArrayList<IMChatMessageDetail>();
                while (cursor.moveToNext()) {
                    String messageId = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_MESSAGE_ID));
                    int messageType = cursor.getInt(cursor.getColumnIndex(IMMessageColumn.IM_MESSAGE_TYPE));
                    String groupSender = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_MESSAGE_SENDER));
                    int isRead = cursor.getInt(cursor.getColumnIndex(IMMessageColumn.IM_READ_STATUS));
                    int imState = cursor.getInt(cursor.getColumnIndex(IMMessageColumn.IM_SEND_STATUS));
                    String dateCreated = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_DATE_CREATE));
                    String curCreated = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_CURRENT_DATE));
                    String userData = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_USER_DATE));
                    String messageContent = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_MESSAGE_CONTENT));
                    String fileUrl = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_FILE_URL));
                    String filePath = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_FILE_PATH));
                    String fileExt = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_FILE_EXT));

                    if(messageType != IMChatMessageDetail.TYPE_MSG_TEXT && (filePath == null || !new File(filePath).exists())) {
                        int x=0;
                        // if the file is not exist . then ingore.
                        continue;
                    }
                    IMChatMessageDetail detail = new IMChatMessageDetail(messageId,
                            sessionId,
                            messageType,
                            groupSender,
                            isRead,
                            imState,
                            dateCreated,
                            curCreated,
                            userData,
                            messageContent,
                            fileUrl,
                            filePath,
                            fileExt,
                            listId);

                    imChatMessageDetails.add(0, detail);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return imChatMessageDetails;
    }

    /**
     * 根据 sessionId 查询未读消息
     * @param sessionId 会话id, 可以是群组聊天时的groupId, 也可以是点对点聊天时的发送人id
     * @return 未读消息列表
     */
    public ArrayList<IMChatMessageDetail> queryNewIMMessages(String sessionId, String listId) throws IllegalArgumentException {
        if (TextUtils.isEmpty(sessionId)) {
            throw new RuntimeException("Error , messageId is " + sessionId);
        }
        Cursor cursor = null;
        ArrayList<IMChatMessageDetail> imChatMessageDetails = null;
        try {
            String sql = "SELECT * FROM " + TABLES_NAME_IM_MESSAGE + " WHERE " + IMMessageColumn.IM_SESSION_ID + " =? AND " + IMMessageColumn.IM_LIST_ID + " =? AND " + IMMessageColumn.IM_READ_STATUS + " = " + IMChatMessageDetail.STATE_UNREAD + " ORDER BY " + IMMessageColumn.IM_CURRENT_DATE + " ASC LIMIT 20";
            cursor = utils.getDatabase().rawQuery(sql, new String[]{sessionId, listId});
            if ((cursor != null) && (cursor.getCount() > 0)) {
                imChatMessageDetails = new ArrayList<IMChatMessageDetail>();
                while (cursor.moveToNext()) {

                    String messageId = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_MESSAGE_ID));
                    sessionId = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_SESSION_ID));
                    int messageType = cursor.getInt(cursor.getColumnIndex(IMMessageColumn.IM_MESSAGE_TYPE));
                    String groupSender = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_MESSAGE_SENDER));
                    int isRead = cursor.getInt(cursor.getColumnIndex(IMMessageColumn.IM_READ_STATUS));
                    int imState = cursor.getInt(cursor.getColumnIndex(IMMessageColumn.IM_SEND_STATUS));
                    String dateCreated = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_DATE_CREATE));
                    String curCreated = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_CURRENT_DATE));
                    String userData = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_USER_DATE));
                    String messageContent = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_MESSAGE_CONTENT));
                    String fileUrl = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_FILE_URL));
                    String filePath = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_FILE_PATH));
                    String fileExt = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_FILE_EXT));

                    IMChatMessageDetail detail = new IMChatMessageDetail(messageId,
                            sessionId,
                            messageType,
                            groupSender,
                            isRead,
                            imState,
                            dateCreated,
                            curCreated,
                            userData,
                            messageContent,
                            fileUrl,
                            filePath,
                            fileExt,
                            listId);
                    imChatMessageDetails.add(detail);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return imChatMessageDetails;
    }

    /**
     * Delete all IM messages
     * @throws java.sql.SQLException
     */
    public void deleteAllIMMessage() throws SQLException {
        try {
            utils.getDatabase().delete(TABLES_NAME_IM_MESSAGE, null, null);
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Delete the record only to send success or failed to send message to delete operation
     * @param sessionId
     * @throws java.sql.SQLException
     */
    public void deleteIMMessage(String sessionId, String listId) throws SQLException {
        try {
            String sql = "DELETE FROM " + TABLES_NAME_IM_MESSAGE + " WHERE " + IMMessageColumn.IM_SESSION_ID + " ='" + sessionId + "' AND " + IMMessageColumn.IM_LIST_ID + " ='" + listId + "'";
            utils.getDatabase().execSQL(sql);
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    public void deleteIMMessageByListId(String listId) throws SQLException {
        try {
            String sql = "DELETE FROM " + TABLES_NAME_IM_MESSAGE + " WHERE " + IMMessageColumn.IM_LIST_ID + " ='" + listId + "'";
            utils.getDatabase().execSQL(sql);
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * query voice or accessories are stored in local path according to the session id
     * @param sessionId
     * @return
     * @throws java.sql.SQLException
     */
    public ArrayList<String> queryIMMessageFileLocalPath(String sessionId, String listId) throws SQLException {
        if(TextUtils.isEmpty(sessionId)) {
            throw new SQLException("Sql execute error , that sessionId is " + sessionId);
        }
        Cursor cursor = null;
        ArrayList<String> filePaths = null;
        try {
            String where = IMMessageColumn.IM_SESSION_ID + "='" + sessionId + "' and "
                    + IMMessageColumn.IM_MESSAGE_TYPE + " <> " + IMChatMessageDetail.TYPE_MSG_TEXT + " and "
                    + IMMessageColumn.IM_LIST_ID + "='" + listId + "'";
            cursor = utils.getDatabase().query(TABLES_NAME_IM_MESSAGE,
                    new String[] {IMMessageColumn.IM_FILE_PATH }, where, null,
                    null, null, null);
            filePaths = new ArrayList<String>();
            if ((cursor != null) && (cursor.getCount() > 0)) {
                while (cursor.moveToNext()) {
                    String filePath = cursor.getString(cursor.getColumnIndex(IMMessageColumn.IM_FILE_PATH));
                    filePaths.add(filePath);
                }

            }
        } catch (Exception e) {
            LogUtils.e("queryIMMessageFileLocalPathBySession runs into exception: " + e.getMessage());
            throw new SQLException(e.getMessage());

        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return filePaths;
    }

    /**
     * Updates status message
     * @param messageId 消息id
     * @param status 消息的状态（0代表消息发送中，1代表消息发送成功，2代表消息发送失败，3代表收到消息）
     * @throws java.sql.SQLException
     */
    public void  updateIMMessageSendStatusByMessageId(String messageId ,int status) throws SQLException {
        if (TextUtils.isEmpty(messageId)) {
            throw new SQLException("The IM message messageId is empty ：" + messageId);
        }
        ContentValues values = null;
        try {
            String where = IMMessageColumn.IM_SEND_STATUS+ " = " + IMChatMessageDetail.STATE_IM_SENDING
                    + " and " + IMMessageColumn.IM_MESSAGE_ID + " = '" + messageId + "'";

            values = new ContentValues();
            values.put(IMMessageColumn.IM_SEND_STATUS, status);

            utils.getDatabase().update(TABLES_NAME_IM_MESSAGE, values,
                    where, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        } finally {
            if (values != null) {
                values.clear();
                values = null;
            }
        }
    }

    /**
     * All is in sending messages in the transmission failure
     * @throws java.sql.SQLException
     */
    public void  updateAllIMMessageSendFailed() throws SQLException {

        ContentValues values = null;
        try {
            String where = IMMessageColumn.IM_SEND_STATUS+ " = " + IMChatMessageDetail.STATE_IM_SENDING;

            values = new ContentValues();
            values.put(IMMessageColumn.IM_SEND_STATUS, IMChatMessageDetail.STATE_IM_SEND_FAILED);

            utils.getDatabase().update(TABLES_NAME_IM_MESSAGE, values,
                    where, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        } finally {
            if (values != null) {
                values.clear();
                values = null;
            }
        }

    }

    /**
     * Depending on the type of update database system message
     * @param sessionId
     * @param listId
     * @throws java.sql.SQLException
     */
    public void updateIMMessageUnreadStatusToRead(String sessionId, String listId ) throws SQLException {
        if (TextUtils.isEmpty(sessionId)) {
            throw new SQLException("The IM sessionId is empty ：" + sessionId);
        }

        ContentValues values = null;
        try {
            final String where = IMMessageColumn.IM_SESSION_ID + " ='" + sessionId + "' and " + IMMessageColumn.IM_LIST_ID + " = '" + listId + "' and " + IMMessageColumn.IM_READ_STATUS + " =" + IMChatMessageDetail.STATE_UNREAD;
            values = new ContentValues();
            values.put(IMMessageColumn.IM_READ_STATUS, IMChatMessageDetail.STATE_READED);
            utils.getDatabase().update(TABLES_NAME_IM_MESSAGE, values, where, null);

        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        } finally {
            if (values != null) {
                values.clear();
                values = null;
            }
        }
    }

    /**
     * Depending on the type of update database system message
     * @param imChatMessageDetail
     * @throws java.sql.SQLException
     */
    public void updateIMMessage(IMChatMessageDetail imChatMessageDetail) throws SQLException {
        if (TextUtils.isEmpty(imChatMessageDetail.getMessageId())) {
            throw new SQLException("The IM sessionId is empty ：" + imChatMessageDetail.getMessageId());
        }

        ContentValues values = null;
        try {
            final String where = IMMessageColumn.IM_MESSAGE_ID + " ='" + imChatMessageDetail.getMessageId() + "'";
            values = new ContentValues();
            values.put(IMMessageColumn.IM_READ_STATUS, IMChatMessageDetail.STATE_READED);
            values.put(IMMessageColumn.IM_MESSAGE_ID, imChatMessageDetail.getMessageId());
            values.put(IMMessageColumn.IM_SESSION_ID, imChatMessageDetail.getSessionId());
            values.put(IMMessageColumn.IM_MESSAGE_TYPE, imChatMessageDetail.getMessageType());
            values.put(IMMessageColumn.IM_MESSAGE_SENDER, imChatMessageDetail.getGroupSender());
            values.put(IMMessageColumn.IM_SEND_STATUS, imChatMessageDetail.getImState());
            values.put(IMMessageColumn.IM_DATE_CREATE, imChatMessageDetail.getDateCreated());
            values.put(IMMessageColumn.IM_CURRENT_DATE, imChatMessageDetail.getCurDate());
            values.put(IMMessageColumn.IM_USER_DATE, imChatMessageDetail.getUserData());
            values.put(IMMessageColumn.IM_MESSAGE_CONTENT, imChatMessageDetail.getMessageContent());
            values.put(IMMessageColumn.IM_FILE_URL, imChatMessageDetail.getQ_fileurl());
            values.put(IMMessageColumn.IM_FILE_PATH, imChatMessageDetail.getFilePath());
            values.put(IMMessageColumn.IM_FILE_EXT, imChatMessageDetail.getFileExt());
            values.put(IMMessageColumn.IM_DURATION, imChatMessageDetail.getDuration());
            values.put(IMMessageColumn.IM_LIST_ID, imChatMessageDetail.getListId());
            values.put(IMMessageColumn.IM_IDENTITY_ID, imChatMessageDetail.getIdentityId());

            utils.getDatabase().update(TABLES_NAME_IM_MESSAGE, values, where, null);

        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        } finally {
            if (values != null) {
                values.clear();
                values = null;
            }
        }
    }

    /**
     * Depending on the type of update database system message
     * @param newChatMessages 新收到的未读消息集合
     * @throws java.sql.SQLException
     */
    public void updateIMMessageUnreadStatusToRead(ArrayList<IMChatMessageDetail> newChatMessages) throws SQLException {
        if (newChatMessages == null) {
            return;
        }

        for(IMChatMessageDetail chatMessageDetail : newChatMessages) {
            ContentValues values = null;
            try {
                final String where = IMMessageColumn.IM_MESSAGE_ID + " ='" + chatMessageDetail.getMessageId() + "' and " + IMMessageColumn.IM_READ_STATUS + " =" + IMChatMessageDetail.STATE_UNREAD;
                values = new ContentValues();
                values.put(IMMessageColumn.IM_READ_STATUS, IMChatMessageDetail.STATE_READED);
                utils.getDatabase().update(TABLES_NAME_IM_MESSAGE, values, where, null);

            } catch (Exception e) {
                e.printStackTrace();
                throw new SQLException(e.getMessage());
            } finally {
                if (values != null) {
                    values.clear();
                    values = null;
                }
            }
        }
    }
}
