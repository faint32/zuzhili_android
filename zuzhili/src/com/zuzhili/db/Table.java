package com.zuzhili.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.bussiness.utility.IMParseUtil;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.model.im.IMChatMessageDetail;
import com.zuzhili.model.im.IMConversation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liutao on 14-5-6.
 */
public class Table {

    protected static final String DESC = "DESC";
    protected static final String ASC = "ASC";

    protected DbUtils utils;

    protected WhereBuilder whereBuilder;

    protected String[] shouldUpdateAll = new String[0];	// 更新表中记录的所有字段

    public void setDbUtils(DbUtils utils){
        this.utils = utils;
    }

    public DbUtils getDbUtils(){
        return utils;
    }

    public void create(DbUtils utils) {
        this.utils = utils;
        createTable();
    }

    public void create(DbUtils utils, Class clazz) {
        this.utils = utils;
        try {
            utils.createTableIfNotExist(clazz);
            utils.configAllowTransaction(true);
            utils.configDebug(true);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void upgrade(DbUtils db, Class clazz, int oldVersion, int newVersion){

    }

    public void createTable() {

    }

    protected boolean checkColumnExist(SQLiteDatabase db, String tableName, String columnName) {
        boolean result = false;
        Cursor cursor = null;
        try {
            //查询一行
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0", null);
            result = cursor != null && cursor.getColumnIndex(columnName) != -1;
        } catch (Exception e) {
            LogUtils.e("checkColumnExists..." + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

    public class BaseColumn {
        public static final String UNREAD_NUM = "unread_num";
    }

    /**
     * 根据社区id查询IM信息会话列表
     * @param identity
     * @return
     * @throws Exception
     */
    public List<IMConversation> queryIMConversation(String identity) throws Exception {
        Cursor cursor = null;
        Cursor rawQuery = null;
        ArrayList<IMConversation> imConversations = null;
        try {
            String sql = "select aa.* , bb.unread_num from ((select a."
                    + IMMessageTable.IMMessageColumn.IM_LIST_ID + " , a."
                    + IMMessageTable.IMMessageColumn.IM_SESSION_ID + " ,a."
                    + IMMessageTable.IMMessageColumn.IM_CURRENT_DATE+ " ,a."
                    + IMMessageTable.IMMessageColumn.IM_MESSAGE_CONTENT + ",a."
                    + IMMessageTable.IMMessageColumn.IM_USER_DATE + " ,a."
                    + IMMessageTable.IMMessageColumn.IM_MESSAGE_TYPE +" , b."
                    + IMGroupInfoTable.IMGroupInfoColumn.GROUP_NAME + " , b."
                    + IMGroupInfoTable.IMGroupInfoColumn.GROUP_ID + " , b."
                    + IMGroupInfoTable.IMGroupInfoColumn.GROUP_MEMBER_COUNTS + " , b."
                    + IMGroupInfoTable.IMGroupInfoColumn.GROUP_OWNER + " , b."
                    + IMGroupInfoTable.IMGroupInfoColumn.GROUP_Z_TYPE
                    + " from ((select * from " + IMMessageTable.TABLES_NAME_IM_MESSAGE + " order by " + IMMessageTable.IMMessageColumn.IM_CURRENT_DATE + " asc) as a LEFT JOIN "
                    + IMGroupInfoTable.TABLES_NAME_IM_GROUP_INFO+ " as b ON a."
                    + IMMessageTable.IMMessageColumn.IM_SESSION_ID + " = b."
                    + IMGroupInfoTable.IMGroupInfoColumn.GROUP_YUN_ID + ") where " + IMMessageTable.IMMessageColumn.IM_IDENTITY_ID + " = '" + identity + "' group by "
                    + IMMessageTable.IMMessageColumn.IM_SESSION_ID + " order by "
                    + IMMessageTable.IMMessageColumn.IM_CURRENT_DATE + " desc"
                    + " )as aa LEFT JOIN (select count("+ IMMessageTable.IMMessageColumn.IM_READ_STATUS+") " + BaseColumn.UNREAD_NUM + " ,"
                    + IMMessageTable.IMMessageColumn.IM_SESSION_ID
                    + " from " + IMMessageTable.TABLES_NAME_IM_MESSAGE + " where "+ IMMessageTable.IMMessageColumn.IM_READ_STATUS+" = " + IMChatMessageDetail.STATE_UNREAD
                    + " and " + IMMessageTable.IMMessageColumn.IM_IDENTITY_ID + " = '" + identity + "'"
                    + " group by " + IMMessageTable.IMMessageColumn.IM_SESSION_ID
                    + ") as bb ON aa."
                    + IMMessageTable.IMMessageColumn.IM_SESSION_ID + " = bb."
                    + IMMessageTable.IMMessageColumn.IM_SESSION_ID + " )";

            LogUtils.d("queryIMConversion sql is :" + sql);

            cursor = utils.getDatabase().rawQuery(sql, null);

            if ((cursor != null) && (cursor.getCount() > 0)) {
                imConversations = new ArrayList<IMConversation>();
                while (cursor.moveToNext()) {
                    String contactId = cursor.getString(cursor.getColumnIndex(IMMessageTable.IMMessageColumn.IM_SESSION_ID));
                    String groupName = cursor.getString(cursor.getColumnIndex(IMGroupInfoTable.IMGroupInfoColumn.GROUP_NAME));
                    String groupType = cursor.getString(cursor.getColumnIndex(IMGroupInfoTable.IMGroupInfoColumn.GROUP_Z_TYPE));
                    String dateCreated = cursor.getString(cursor.getColumnIndex(IMMessageTable.IMMessageColumn.IM_CURRENT_DATE));
                    String userData = cursor.getString(cursor.getColumnIndex(IMMessageTable.IMMessageColumn.IM_USER_DATE));
                    String msgContent = cursor.getString(cursor.getColumnIndex(IMMessageTable.IMMessageColumn.IM_MESSAGE_CONTENT));
                    String groupId = cursor.getString(cursor.getColumnIndex(IMGroupInfoTable.IMGroupInfoColumn.GROUP_ID));
                    String groupUerCount = cursor.getString(cursor.getColumnIndex(IMGroupInfoTable.IMGroupInfoColumn.GROUP_MEMBER_COUNTS));
                    String owner = cursor.getString(cursor.getColumnIndex(IMGroupInfoTable.IMGroupInfoColumn.GROUP_OWNER));
                    int msgType = cursor.getInt(cursor.getColumnIndex(IMMessageTable.IMMessageColumn.IM_MESSAGE_TYPE));
                    int unreadNum = cursor.getInt(cursor.getColumnIndex(BaseColumn.UNREAD_NUM));

                    IMConversation session = new IMConversation();
                    session.setId(contactId); // 会话
                    session.setDateCreated(dateCreated);
                    session.setUnReadNum(unreadNum + "");
                    session.setGroupType(groupType);
                    session.setIds(IMParseUtil.getIds(userData));
                    session.setListId(IMParseUtil.getListId(userData));
                    session.setGroupId(groupId);
                    session.setGroupUserCount(groupUerCount);
                    session.setOwner(owner);
                    if (groupName != null && TextUtils.isEmpty(groupName)) {
                        session.setUserName(groupName);
                    }

                    session.setContact(contactId);
                    if(msgType == IMChatMessageDetail.TYPE_MSG_TEXT) {
                        session.setRecentMessage(msgContent);
                    } else if (msgType == IMChatMessageDetail.TYPE_MSG_FILE ) {
                        session.setRecentMessage("[文件]");
                    } else if (msgType == IMChatMessageDetail.TYPE_MSG_VOICE ) {
                        session.setRecentMessage("[语音]");
                    }else if (msgType == IMChatMessageDetail.TYPE_MSG_PIC ) {
                        session.setRecentMessage("[图片]");
                    }
                    imConversations.add(session);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("[Table] queryIMConversation: " + e.toString());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            if (rawQuery != null) {
                rawQuery.close();
                rawQuery = null;
            }
        }
        return imConversations;
    }
}
