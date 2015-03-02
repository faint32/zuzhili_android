package com.zuzhili.draftbox;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fanruikang on 14-9-22.
 */
@SuppressWarnings("unused")
public class DraftDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DraftBox.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DraftContract.Draft.TABLE_NAME + " (" +
                    DraftContract.Draft._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    DraftContract.Draft.COLUMN_NAME_LIST_ID + TEXT_TYPE + COMMA_SEP +
                    DraftContract.Draft.COLUMN_NAME_IDS + TEXT_TYPE + COMMA_SEP +
                    DraftContract.Draft.COLUMN_NAME_SPACE_ID + TEXT_TYPE + COMMA_SEP +
                    DraftContract.Draft.COLUMN_NAME_CONTENT_TYPE + TEXT_TYPE + COMMA_SEP +
                    DraftContract.Draft.COLUMN_NAME_CONTENT + TEXT_TYPE + COMMA_SEP +
                    DraftContract.Draft.COLUMN_NAME_CREATE_TIME + TEXT_TYPE + COMMA_SEP +
                    DraftContract.Draft.COLUMN_NAME_UPDATE_TIME + TEXT_TYPE +
                    " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DraftContract.Draft.TABLE_NAME;

    public DraftDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
}
