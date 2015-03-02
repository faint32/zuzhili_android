package com.zuzhili.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.table.TableUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.bussiness.socket.model.GroupInfo;
import com.zuzhili.db.model.Speech;
import com.zuzhili.model.im.IMGroup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liutao on 14-6-15.
 */
public class IMGroupInfoTable extends Table {

    public static final String TABLES_NAME_IM_GROUP_INFO 			= "im_group_info";

    /**
     *Group information
     */
    public class IMGroupInfoColumn extends BaseColumn {
        public static final String GROUP_ID 						= "GROUPID";                    // 对应组织力平台上的群组id
        public static final String GROUP_YUN_ID 			    	= "GROUP_YUN_ID";               // 对应云通讯群组id
        public static final String GROUP_NAME 						= "NAME"; 						// group name
        public static final String GROUP_OWNER	 					= "OWNER"; 						// Group creator information, 创建者的uid
        public static final String GROUP_TYPE 						= "TYPE"; 						// Group property type (whether it is normal group, VIP group.)
        public static final String GROUP_DECLARED 					= "DECLARED"; 					// The group information bulletin
        public static final String GROUP_DATE_CREATED 				= "CREATE_DATE"; 				// Group creation time
        public static final String GROUP_MEMBER_COUNTS				= "COUNT"; 						// The number of group members
        public static final String GROUP_PERMISSION 				= "PERMISSION"; 				// Join the required permission
        public static final String GROUP_LIST_ID                    = "LISTID";                     // list id indicates the group in that social
        public static final String GROUP_CAPACITY                   = "CAPACITY";                   // 组织架构群组人数
        public static final String GROUP_LAST_MESSAGE               = "LAST_MESSAGE";               // last message
        public static final String GROUP_Z_GID                      = "Z_GID";                      // 会议室群组所在公共空间
        public static final String GROUP_Z_TYPE                     = "Z_TYPE";                     // 群组分类，0用户自建，1:机构 2:项目组 3:群组 4:活动
        public static final String GROUP_IDENTITY_ID                = "IDENTITYID";                // IDENTITY ID
    }

    @Override
    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS "
                + TABLES_NAME_IM_GROUP_INFO
                + " ( " //ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + IMGroupInfoColumn.GROUP_ID + " TEXT PRIMARY KEY , "
                + IMGroupInfoColumn.GROUP_YUN_ID + " TEXT , "
                + IMGroupInfoColumn.GROUP_NAME + " TEXT , "
                + IMGroupInfoColumn.GROUP_DATE_CREATED + " TEXT , "
                + IMGroupInfoColumn.GROUP_DECLARED + " TEXT , "
                + IMGroupInfoColumn.GROUP_OWNER + " TEXT , "
                + IMGroupInfoColumn.GROUP_MEMBER_COUNTS + " INTEGER , "
                + IMGroupInfoColumn.GROUP_PERMISSION + " INTEGER , "
                + IMGroupInfoColumn.GROUP_TYPE + " INTEGER, "
                + IMGroupInfoColumn.GROUP_LIST_ID + " TEXT NOT NULL, "
                + IMGroupInfoColumn.GROUP_Z_GID + " TEXT , "
                + IMGroupInfoColumn.GROUP_Z_TYPE + " TEXT , "
                + IMGroupInfoColumn.GROUP_CAPACITY + " TEXT , "
                + IMGroupInfoColumn.GROUP_IDENTITY_ID + " TEXT , "
                + IMGroupInfoColumn.GROUP_LAST_MESSAGE + " TEXT)";
        LogUtils.i("CREATE TABLE " + TABLES_NAME_IM_GROUP_INFO + sql);
        utils.getDatabase().execSQL(sql);
    }

    @Override
    public void upgrade(DbUtils db, Class clazz, int oldVersion, int newVersion) {
        try {
            db.execNonQuery("ALTER TABLE " + TABLES_NAME_IM_GROUP_INFO + " ADD COLUMN " + IMGroupInfoColumn.GROUP_IDENTITY_ID + " TEXT ");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 插入一条群组记录
     * @param imGroup
     * @throws SQLException
     */
    public void insertIMGroupInfo(GroupInfo imGroup) throws SQLException {

        if (imGroup == null || TextUtils.isEmpty(imGroup.getId())) {
            throw new SQLException("[insertIMGroupInfo] The inserted data is empty or groupId is null " + imGroup);
        }

        ContentValues values = null;
        try {

            values = new ContentValues();
            values.put(IMGroupInfoColumn.GROUP_ID, imGroup.getId());
            values.put(IMGroupInfoColumn.GROUP_YUN_ID, imGroup.getY_gid());
            values.put(IMGroupInfoColumn.GROUP_NAME, imGroup.getG_name());
            values.put(IMGroupInfoColumn.GROUP_PERMISSION, imGroup.getG_permisson());
            values.put(IMGroupInfoColumn.GROUP_TYPE, imGroup.getG_type());

            values.put(IMGroupInfoColumn.GROUP_OWNER, imGroup.getCreatorid());
            values.put(IMGroupInfoColumn.GROUP_DATE_CREATED, "");
            values.put(IMGroupInfoColumn.GROUP_DECLARED, imGroup.getG_declared());
            values.put(IMGroupInfoColumn.GROUP_MEMBER_COUNTS, imGroup.getG_ucount());
            values.put(IMGroupInfoColumn.GROUP_LIST_ID, imGroup.getU_listid());
            values.put(IMGroupInfoColumn.GROUP_CAPACITY, imGroup.getG_capacity());
            values.put(IMGroupInfoColumn.GROUP_LAST_MESSAGE, imGroup.getG_lastSay());
            values.put(IMGroupInfoColumn.GROUP_Z_GID, imGroup.getZ_gid());
            values.put(IMGroupInfoColumn.GROUP_Z_TYPE, imGroup.getZ_type());
            values.put(IMGroupInfoColumn.GROUP_IDENTITY_ID, imGroup.getIdentityId());

            utils.getDatabase().insert(TABLES_NAME_IM_GROUP_INFO, null, values);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        } finally {
            if (values != null) {
                values.clear();
                values = null;
                imGroup = null;
            }

        }

    }

    /**
     * insert a batch
     * @param imGroups
     * @throws SQLException
     */
    public void insertIMGroupInfos(List<GroupInfo> imGroups) throws SQLException {
        if (imGroups == null ) {
            throw new SQLException("The inserted data is empty.");
        }

        try {

            // Set the start transaction
            utils.getDatabase().beginTransaction();

            if (imGroups.size() > 0) {
                deleteGroups(imGroups.get(0).getIdentityId());
            }
            // Batch processing operation
            for(GroupInfo imGroup : imGroups){
                try {
                    if (isExistsGroupId(imGroup.getY_gid()) != null) {
                        updateGroupInfo(imGroup);
                    } else {
                        insertIMGroupInfo(imGroup);
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

    /**
     * Query the existence of the group ID
     * @param groupId 云通讯群组id
     * @return
     * @throws java.sql.SQLException
     */
    public String isExistsGroupId(String groupId) throws SQLException {
        if (TextUtils.isEmpty(groupId)) {
            return null;
        }
        Cursor cursor = null;
        try {
            String where = IMGroupInfoColumn.GROUP_YUN_ID + " ='" + groupId + "'";
            cursor = utils.getDatabase().query(TABLES_NAME_IM_GROUP_INFO, new String[]{IMGroupInfoColumn.GROUP_YUN_ID}, where, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                if(cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_YUN_ID));
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

    public boolean deleteGroupByGroupId(String groupId) throws SQLException {
        if (TextUtils.isEmpty(groupId)) {
            return false;
        }
        try {
            String sql = "DELETE FROM " + TABLES_NAME_IM_GROUP_INFO + " WHERE " + IMGroupInfoColumn.GROUP_ID + " ='" + groupId + "'";
            utils.getDatabase().execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
        return true;
    }

    public boolean deleteGroupByYTXGroupId(String y_groupId) throws SQLException {
        if (TextUtils.isEmpty(y_groupId)) {
            return false;
        }
        try {
            String sql = "DELETE FROM " + TABLES_NAME_IM_GROUP_INFO + " WHERE " + IMGroupInfoColumn.GROUP_YUN_ID + " ='" + y_groupId + "'";
            utils.getDatabase().execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
        return true;
    }

    /**
     * 根据groupId 更新一条已有的记录
     * @param imGroup
     * @throws SQLException
     */
    public void updateGroupInfo(GroupInfo imGroup) throws SQLException {
        if (imGroup == null || TextUtils.isEmpty(imGroup.getY_gid())) {
            throw new SQLException("[updateGroupInfo] The update data is empty imGroup : " + imGroup);
        }

        ContentValues values = null;
        try {
            final String where = IMGroupInfoColumn.GROUP_YUN_ID + " ='"
                    + imGroup.getY_gid() + "'";
            values = new ContentValues();
            values.put(IMGroupInfoColumn.GROUP_ID, imGroup.getId());
            values.put(IMGroupInfoColumn.GROUP_YUN_ID, imGroup.getY_gid());
            values.put(IMGroupInfoColumn.GROUP_NAME, imGroup.getG_name());
            values.put(IMGroupInfoColumn.GROUP_PERMISSION, imGroup.getG_permisson());
            values.put(IMGroupInfoColumn.GROUP_TYPE, imGroup.getG_type());

            values.put(IMGroupInfoColumn.GROUP_OWNER, imGroup.getCreatorid());
            values.put(IMGroupInfoColumn.GROUP_DATE_CREATED, "");
            values.put(IMGroupInfoColumn.GROUP_DECLARED, imGroup.getG_declared());
            values.put(IMGroupInfoColumn.GROUP_MEMBER_COUNTS, imGroup.getG_ucount());
            values.put(IMGroupInfoColumn.GROUP_LAST_MESSAGE, imGroup.getG_lastSay());
            values.put(IMGroupInfoColumn.GROUP_LIST_ID, imGroup.getU_listid());
            values.put(IMGroupInfoColumn.GROUP_CAPACITY, imGroup.getG_capacity());
            values.put(IMGroupInfoColumn.GROUP_LAST_MESSAGE, imGroup.getG_lastSay());
            values.put(IMGroupInfoColumn.GROUP_Z_GID, imGroup.getZ_gid());
            values.put(IMGroupInfoColumn.GROUP_Z_TYPE, imGroup.getZ_type());

            utils.getDatabase().update(TABLES_NAME_IM_GROUP_INFO, values,
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
     * 查询一个群组信息
     * @param groupId
     * @return
     * @throws Exception
     */
    public GroupInfo queryGroup(String groupId) throws Exception {
        if (groupId == null || TextUtils.isEmpty(groupId)) {
            throw new IllegalArgumentException("[queryGroup] the groupId is empty " );
        }
        GroupInfo result = null;
        Cursor cursor = null;
        try {

            String sql = "SELECT * FROM " + TABLES_NAME_IM_GROUP_INFO + " WHERE " + IMGroupInfoColumn.GROUP_YUN_ID + " = ?";
            cursor = utils.getDatabase().rawQuery(sql, new String[]{groupId});
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_NAME));
                    String declared = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_DECLARED));
                    String id = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_ID));
                    String count = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_MEMBER_COUNTS));
                    String owner = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_OWNER));
                    String permission = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_PERMISSION));
                    String type = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_TYPE));
                    String listId = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_LIST_ID));
                    String capacity = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_CAPACITY));
                    String lastMessage = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_LAST_MESSAGE));
                    String z_gid = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_Z_GID));
                    String z_type = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_Z_TYPE));
                    String identityId = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_IDENTITY_ID));

                    result = new GroupInfo(id
                            , groupId
                            , owner
                            , name
                            , declared
                            , type
                            , permission
                            , count
                            , listId, capacity
                            , lastMessage
                            , z_gid, z_type);

                    result.setIdentityId(identityId);

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

    /**
     * 查询一个社区下的所有群组
     * @param listId
     * @return
     * @throws Exception
     */
    public List<GroupInfo> queryGroups(String listId) throws Exception {
        if (listId == null || TextUtils.isEmpty(listId)) {
            throw new IllegalArgumentException("[queryGroup] the listId is empty " );
        }
        List<GroupInfo> result = null;
        Cursor cursor = null;
        try {

            String sql = "SELECT * FROM " + TABLES_NAME_IM_GROUP_INFO + " WHERE " + IMGroupInfoColumn.GROUP_LIST_ID + " = ?";
            cursor = utils.getDatabase().rawQuery(sql, new String[]{listId});
            if (cursor != null && cursor.getCount() > 0) {
                result = new ArrayList<GroupInfo>();
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_NAME));
                    String declared = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_DECLARED));
                    String id = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_ID));
                    String count = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_MEMBER_COUNTS));
                    String owner = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_OWNER));
                    String permission = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_PERMISSION));
                    String type = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_TYPE));
                    String lastMessage = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_LAST_MESSAGE));
                    String groupId = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_LIST_ID));
                    String z_gid = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_Z_GID));
                    String z_type = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_Z_TYPE));
                    String capacity = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_CAPACITY));

                    GroupInfo group = new GroupInfo(id
                            , groupId
                            , owner
                            , name
                            , declared
                            , type
                            , permission
                            , count
                            , listId, capacity
                            , lastMessage, z_gid, z_type);
                    result.add(group);
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

    /** 群组分类，0用户自建，1:机构 2:项目组 3:群组 4:活动 */
    public List<GroupInfo> queryGroups(String listId, String z_type) throws Exception {
        if (listId == null || TextUtils.isEmpty(listId)) {
            throw new IllegalArgumentException("[queryGroup] the listId is empty " );
        }
        List<GroupInfo> result = null;
        Cursor cursor = null;
        try {

            String sql = "SELECT * FROM " + TABLES_NAME_IM_GROUP_INFO + " WHERE " + IMGroupInfoColumn.GROUP_LIST_ID + " = ? AND " + IMGroupInfoColumn.GROUP_Z_TYPE + " = ?";
            cursor = utils.getDatabase().rawQuery(sql, new String[]{listId, z_type});
            if (cursor != null && cursor.getCount() > 0) {
                result = new ArrayList<GroupInfo>();
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_NAME));
                    String declared = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_DECLARED));
                    String id = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_ID));
                    String yid = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_YUN_ID));
                    String count = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_MEMBER_COUNTS));
                    String owner = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_OWNER));
                    String permission = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_PERMISSION));
                    String type = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_TYPE));
                    String lastMessage = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_LAST_MESSAGE));
                    String z_gid = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_Z_GID));
                    String capacity = cursor.getString(cursor.getColumnIndex(IMGroupInfoColumn.GROUP_CAPACITY));

                    GroupInfo group = new GroupInfo(id
                            , yid
                            , owner
                            , name
                            , declared
                            , type
                            , permission
                            , count
                            , listId, capacity
                            , lastMessage, z_gid, z_type);
                    result.add(0, group);
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

    public void deleteGroups(String identity) throws SQLException {
        try {
            String sql = "DELETE FROM " + TABLES_NAME_IM_GROUP_INFO + " WHERE " + IMGroupInfoColumn.GROUP_IDENTITY_ID + " ='" + identity + "'";
            utils.getDatabase().execSQL(sql);
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

}
