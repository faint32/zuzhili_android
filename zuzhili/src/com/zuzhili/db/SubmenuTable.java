package com.zuzhili.db;

import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.provider.Contract;

/**
 * Created by liutao on 15-1-3.
 */
public class SubmenuTable extends Table {

    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String COMMA_SEP = ",";
    /** SQL statement to create "entry" table. */
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + Contract.SubMenu.TABLE_NAME + " (" +
                    Contract.SubMenu._ID + " INTEGER PRIMARY KEY," +
                    Contract.SubMenu.SUBMENU_COLUMN_ID + TYPE_INTEGER + COMMA_SEP +
                    Contract.SubMenu.SUBMENU_COLUMN_NAME + TYPE_TEXT + COMMA_SEP +
                    Contract.SubMenu.SUBMENU_COLUMN_LEVEL    + TYPE_INTEGER + COMMA_SEP +
                    Contract.SubMenu.SUBMENU_COLUMN_PARENTID + TYPE_INTEGER + COMMA_SEP +
                    Contract.SubMenu.SUBMENU_COLUMN_SEARCHURL + TYPE_TEXT + COMMA_SEP +
                    Contract.SubMenu.SUBMENU_COLUMN_ICON + TYPE_INTEGER + COMMA_SEP +
                    Contract.SubMenu.SUBMENU_COLUMN_CATEGORYURL + TYPE_INTEGER + ")";

    @Override
    public void createTable() {
        super.createTable();
        LogUtils.i("CREATE TABLE " + Contract.SubMenu.TABLE_NAME + SQL_CREATE_ENTRIES);
        utils.getDatabase().execSQL(SQL_CREATE_ENTRIES);
    }
}
