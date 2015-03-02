package com.zuzhili.db;

import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.provider.Contract;

/**
 * Created by liutao on 14-9-1.
 */
public class MenuTable extends Table {

    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String COMMA_SEP = ",";
    /** SQL statement to create "entry" table. */
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + Contract.Menu.TABLE_NAME + " (" +
                    Contract.Menu._ID + " INTEGER PRIMARY KEY," +
                    Contract.Menu.MENU_COLUMN_VOTE + TYPE_TEXT + COMMA_SEP +
                    Contract.Menu.MENU_COLUMN_HALL    + TYPE_TEXT + COMMA_SEP +
                    Contract.Menu.MENU_COLUMN_USERNAME + TYPE_TEXT + COMMA_SEP +
                    Contract.Menu.MENU_COLUMN_MORE + TYPE_TEXT + COMMA_SEP +
                    Contract.Menu.MENU_COLUMN_ROLEID + TYPE_INTEGER + COMMA_SEP +
                    Contract.Menu.MENU_COLUMN_CONTACTS + TYPE_TEXT + COMMA_SEP +
                    Contract.Menu.MENU_COLUMN_LIST + TYPE_TEXT + COMMA_SEP +
                    Contract.Menu.MENU_COLUMN_SANWEI_USERID + TYPE_INTEGER + ")";

    @Override
    public void createTable() {
        super.createTable();
        LogUtils.i("CREATE TABLE " + Contract.Menu.TABLE_NAME + SQL_CREATE_ENTRIES);
        utils.getDatabase().execSQL(SQL_CREATE_ENTRIES);
    }
}
