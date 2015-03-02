package com.zuzhili.db;

import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.provider.Contract;

/**
 * Created by liutao on 14-9-1.
 */
public class UnreadMsgCountTable extends Table {

    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String COMMA_SEP = ",";
    /** SQL statement to create "entry" table. */
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + Contract.Entry.TABLE_NAME + " (" +
                    Contract.Entry._ID + " INTEGER PRIMARY KEY," +
                    Contract.Entry.COLUMN_NAME_LIST_ID + TYPE_TEXT + COMMA_SEP +
                    Contract.Entry.COLUMN_NAME_IDS    + TYPE_TEXT + COMMA_SEP +
                    Contract.Entry.COLUMN_NAME_REFRESH_COUNT + TYPE_TEXT + COMMA_SEP +
                    Contract.Entry.COLUMN_NAME_MSG_COUNT + TYPE_TEXT + COMMA_SEP +
                    Contract.Entry.COLUMN_NAME_AT_FEED_COUNT + TYPE_TEXT + COMMA_SEP +
                    Contract.Entry.COLUMN_NAME_AT_COMMENT_COUNT + TYPE_TEXT + COMMA_SEP +
                    Contract.Entry.COLUMN_NAME_COMMENT_COUNT + TYPE_TEXT + COMMA_SEP +
                    Contract.Entry.COLUMN_NAME_NOTIFY_COUNT + TYPE_TEXT + COMMA_SEP +
                    Contract.Entry.COLUMN_NAME_APPROVAL_COUNT + TYPE_TEXT + COMMA_SEP +
                    Contract.Entry.COLUMN_NAME_APPROVAL_REPLY_COUNT + TYPE_TEXT + COMMA_SEP +
                    Contract.Entry.COLUMN_NAME_ALL_COUNT + TYPE_TEXT + ")";

    @Override
    public void createTable() {
        super.createTable();
        LogUtils.i("CREATE TABLE " + Contract.Entry.TABLE_NAME + SQL_CREATE_ENTRIES);
        utils.getDatabase().execSQL(SQL_CREATE_ENTRIES);
    }
}
