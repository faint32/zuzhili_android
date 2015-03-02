package com.zuzhili.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.bussiness.socket.model.UserInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liutao on 14-8-6.
 */
public class IMGroupUserTable extends Table {

    public static final String TABLES_NAME_IM_GROUP_USER 			= "im_group_user";

    /**
     * group user information
     */
    public class IMGroupUserColumn extends BaseColumn {
        public static final String _ID                              = "_id";
        public static final String GROUP_ID 						= "group_id";                    // 对应组织力平台上的群组id
        public static final String GROUP_YUN_ID 			    	= "group_y_id";                  // 对应云通讯群组id
        public static final String U_ID 						    = "u_id"; 						 // user id
        public static final String U_IDS    	 					= "u_ids"; 						 // user ids
        public static final String U_NAME    						= "u_name"; 				     // user name
        public static final String U_ICON        					= "u_icon"; 					 // user icon
        public static final String U_LIST_ID         				= "u_listid"; 				     // user listid
        public static final String U_LAST_SAY       				= "u_lastSa"; 				     // user last say
        public static final String Y_SUB_ID          				= "y_subid";
        public static final String Y_SUB_PASS                       = "y_subpass";
        public static final String Y_VOIP                           = "y_voip";
        public static final String Y_VOIP_PASS                      = "y_voippass";
        public static final String IDENTITY                         = "identity";
    }

    @Override
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS "
                + TABLES_NAME_IM_GROUP_USER
                + " ( "
                + IMGroupUserColumn._ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + IMGroupUserColumn.GROUP_ID + " TEXT , "
                + IMGroupUserColumn.GROUP_YUN_ID + " TEXT , "
                + IMGroupUserColumn.U_ID + " TEXT , "
                + IMGroupUserColumn.U_IDS + " TEXT , "
                + IMGroupUserColumn.U_NAME + " TEXT , "
                + IMGroupUserColumn.U_ICON + " TEXT , "
                + IMGroupUserColumn.U_LIST_ID + " TEXT , "
                + IMGroupUserColumn.U_LAST_SAY + " TEXT , "
                + IMGroupUserColumn.Y_SUB_ID + " TEXT, "
                + IMGroupUserColumn.Y_SUB_PASS + " TEXT, "
                + IMGroupUserColumn.Y_VOIP + " TEXT , "
                + IMGroupUserColumn.Y_VOIP_PASS + " TEXT , "
                + IMGroupUserColumn.IDENTITY + " TEXT)";
        LogUtils.i("CREATE TABLE " + TABLES_NAME_IM_GROUP_USER + sql);
        utils.getDatabase().execSQL(sql);
    }

    public void insertIMGroupUser(UserInfo userInfo, String groupId, String groupYId) throws SQLException {

        if (userInfo == null) {
            throw new SQLException("[insertIMGroupUser] The inserted data is empty");
        }

        ContentValues values = null;
        try {
            values = new ContentValues();
            values.put(IMGroupUserColumn.GROUP_ID, groupId);
            values.put(IMGroupUserColumn.GROUP_YUN_ID, groupYId);
            values.put(IMGroupUserColumn.U_ID, userInfo.getU_id());
            values.put(IMGroupUserColumn.U_IDS, userInfo.getU_ids());
            values.put(IMGroupUserColumn.U_NAME, userInfo.getU_name());

            values.put(IMGroupUserColumn.U_ICON, userInfo.getU_icon());
            values.put(IMGroupUserColumn.U_LIST_ID, userInfo.getU_listid());
            values.put(IMGroupUserColumn.U_LAST_SAY, userInfo.getU_lastSa());
            values.put(IMGroupUserColumn.Y_SUB_ID, userInfo.getY_subid());
            values.put(IMGroupUserColumn.Y_SUB_PASS, userInfo.getY_subpass());
            values.put(IMGroupUserColumn.Y_VOIP, userInfo.getY_voip());
            values.put(IMGroupUserColumn.Y_VOIP_PASS, userInfo.getY_voippass());
            values.put(IMGroupUserColumn.IDENTITY, userInfo.getIdentity());
            utils.getDatabase().insert(TABLES_NAME_IM_GROUP_USER, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        } finally {
            if (values != null) {
                values.clear();
                values = null;
                userInfo = null;
            }
        }
    }

    public void updateGroupUser(UserInfo userInfo, String groupId, String groupYId) throws SQLException {
        if (userInfo == null) {
            throw new SQLException("[updateGroupUser] The update data is empty");
        }

        ContentValues values = null;
        try {
            final String where = IMGroupUserColumn.GROUP_YUN_ID + " ='"
                    + groupYId + "'";
            values = new ContentValues();
            values.put(IMGroupUserColumn.GROUP_ID, groupId);
            values.put(IMGroupUserColumn.GROUP_YUN_ID, groupYId);
            values.put(IMGroupUserColumn.U_ID, userInfo.getU_id());
            values.put(IMGroupUserColumn.U_IDS, userInfo.getU_ids());
            values.put(IMGroupUserColumn.U_NAME, userInfo.getU_name());

            values.put(IMGroupUserColumn.U_ICON, userInfo.getU_icon());
            values.put(IMGroupUserColumn.U_LIST_ID, userInfo.getU_listid());
            values.put(IMGroupUserColumn.U_LAST_SAY, userInfo.getU_lastSa());
            values.put(IMGroupUserColumn.Y_SUB_ID, userInfo.getY_subid());
            values.put(IMGroupUserColumn.Y_SUB_PASS, userInfo.getY_subpass());
            values.put(IMGroupUserColumn.Y_VOIP, userInfo.getY_voip());
            values.put(IMGroupUserColumn.Y_VOIP_PASS, userInfo.getY_voippass());
            values.put(IMGroupUserColumn.IDENTITY, userInfo.getIdentity());

            utils.getDatabase().update(TABLES_NAME_IM_GROUP_USER, values,
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

    public void insertIMGroupUserList(List<UserInfo> list, String groupId, String groupYId) throws SQLException {
        if (list == null ) {
            throw new SQLException("The inserted data is empty.");
        }

        try {

            // Set the start transaction
            utils.getDatabase().beginTransaction();

            // Batch processing operation
            for(UserInfo item : list){
                try {
                    if (isExistsUserIds(item.getU_ids()) != null) {
                        updateGroupUser(item, groupId, groupYId);
                    } else {
                        insertIMGroupUser(item, groupId, groupYId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Set transaction successful, do not set automatically
            // rolls back not submitted.
            utils.getDatabase().setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        } finally {
            utils.getDatabase().endTransaction();
        }
    }

    public String isExistsUserIds(String ids) throws SQLException {
        if (TextUtils.isEmpty(ids)) {
            return null;
        }
        Cursor cursor = null;
        try {
            String where = IMGroupUserColumn.U_IDS + " ='" + ids + "'";
            cursor = utils.getDatabase().query(TABLES_NAME_IM_GROUP_USER, new String[]{IMGroupUserColumn.U_IDS}, where, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                if(cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndex(IMGroupUserColumn.U_IDS));
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

    public List<UserInfo> queryGroupUsers(String groupYId, String identity) throws Exception {
        if (groupYId == null || TextUtils.isEmpty(groupYId)) {
            throw new IllegalArgumentException("[queryGroupUsers] the groupYId is empty " );
        }
        List<UserInfo> result = null;
        Cursor cursor = null;
        try {

            String sql = "SELECT * FROM " + TABLES_NAME_IM_GROUP_USER + " WHERE " + IMGroupUserColumn.GROUP_YUN_ID + " = ? AND " + IMGroupUserColumn.IDENTITY + " = ?";
            cursor = utils.getDatabase().rawQuery(sql, new String[]{groupYId, identity});
            if (cursor != null && cursor.getCount() > 0) {
                result = new ArrayList<UserInfo>();
                while (cursor.moveToNext()) {
                    String userId = cursor.getString(cursor.getColumnIndex(IMGroupUserColumn.U_ID));
                    String ids = cursor.getString(cursor.getColumnIndex(IMGroupUserColumn.U_IDS));

                    String name = cursor.getString(cursor.getColumnIndex(IMGroupUserColumn.U_NAME));
                    String icon = cursor.getString(cursor.getColumnIndex(IMGroupUserColumn.U_ICON));
                    String listId = cursor.getString(cursor.getColumnIndex(IMGroupUserColumn.U_LIST_ID));
                    String lastSay = cursor.getString(cursor.getColumnIndex(IMGroupUserColumn.U_LAST_SAY));
                    String subId = cursor.getString(cursor.getColumnIndex(IMGroupUserColumn.Y_SUB_ID));
                    String subPass = cursor.getString(cursor.getColumnIndex(IMGroupUserColumn.Y_SUB_PASS));
                    String voip = cursor.getString(cursor.getColumnIndex(IMGroupUserColumn.Y_VOIP));
                    String voipPass = cursor.getString(cursor.getColumnIndex(IMGroupUserColumn.Y_VOIP_PASS));

                    UserInfo user = new UserInfo();
                    user.setU_id(userId);
                    user.setU_ids(ids);
                    user.setU_name(name);
                    user.setU_icon(icon);
                    user.setU_listid(listId);
                    user.setU_lastSa(lastSay);
                    user.setY_subid(subId);
                    user.setY_subpass(subPass);
                    user.setY_voip(voip);
                    user.setY_voippass(voipPass);
                    user.setIdentity(identity);
                    result.add(user);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return result;
    }

}
