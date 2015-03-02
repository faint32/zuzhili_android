package com.zuzhili.db;

import com.lidroid.xutils.util.LogUtils;

/**
 * Created by liutao on 14-6-15.
 */
public class IMGroupNoticeTable extends Table {

    public static final String TABLES_NAME_IM_GROUP_NOTICE 		= "im_group_notice";

    /**
     * Group notification
     */
    public class IMGroupNoticeColumn extends BaseColumn{
        public static final String NOTICE_ID 						= "ID";							// notice message id
        public static final String NOTICE_VERIFYMSG 				= "VERIFY_MSG"; 				// i.e. Apply or invite additional reason
        public static final String NOTICE_TYPE 						= "MSGTYPE"; 					// The system notification message type (i.e.  invitation or apply)
        public static final String NOTICE_OPERATION_STATE 			= "STATE"; 						// The system message operation (i.e. rejected or by)
        public static final String NOTICE_GROUPID 					= "GROUPID"; 					// Group ID message belongs to
        public static final String NOTICE_WHO					 	= "WHO"; 						// Participants in the system message
        public static final String NOTICE_READ_STATUS 				= "ISREAD"; 					// The status of the message (read or unread)
        public static final String NOTICE_DATECREATED 				= "CURDATE"; 					// date
    }

    @Override
    public void createTable() {
        super.createTable();
        String sql = "CREATE TABLE IF NOT EXISTS "
                + TABLES_NAME_IM_GROUP_NOTICE
                + " ("+IMGroupNoticeColumn.NOTICE_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + IMGroupNoticeColumn.NOTICE_VERIFYMSG + " TEXT , "
                + IMGroupNoticeColumn.NOTICE_OPERATION_STATE + " INTEGER , "
                + IMGroupNoticeColumn.NOTICE_TYPE + " INTEGER , "
                + IMGroupNoticeColumn.NOTICE_GROUPID + " TEXT , "
                + IMGroupNoticeColumn.NOTICE_DATECREATED + " TEXT , "
                + IMGroupNoticeColumn.NOTICE_WHO + " TEXT , "
                + IMGroupNoticeColumn.NOTICE_READ_STATUS + " INTEGER)";
        LogUtils.i("CREATE TABLE " + TABLES_NAME_IM_GROUP_NOTICE + sql);
        utils.getDatabase().execSQL(sql);
    }
}
