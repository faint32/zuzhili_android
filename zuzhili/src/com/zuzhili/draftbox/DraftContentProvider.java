package com.zuzhili.draftbox;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fanruikang on 14-9-23.
 */
public class DraftContentProvider extends ContentProvider {

    private DraftDBHelper draftDBHelper;
    private SQLiteDatabase db;
    private static final UriMatcher URI_MATCHER;

    public static final int DRAFTS = 1;
    public static final int DRAFT_ID = 2;

    private static Map<String, String> projectionMap;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(DraftContract.AUTHORITY, "drafts", DRAFTS);
        URI_MATCHER.addURI(DraftContract.AUTHORITY, "drafts/#", DRAFT_ID);

        projectionMap = new HashMap<String, String>();
        projectionMap.put(DraftContract.Draft._ID, DraftContract.Draft._ID);
        projectionMap.put(DraftContract.Draft.COLUMN_NAME_LIST_ID, DraftContract.Draft.COLUMN_NAME_LIST_ID);
        projectionMap.put(DraftContract.Draft.COLUMN_NAME_IDS, DraftContract.Draft.COLUMN_NAME_IDS);
        projectionMap.put(DraftContract.Draft.COLUMN_NAME_SPACE_ID, DraftContract.Draft.COLUMN_NAME_SPACE_ID);
        projectionMap.put(DraftContract.Draft.COLUMN_NAME_CONTENT_TYPE, DraftContract.Draft.COLUMN_NAME_CONTENT_TYPE);
        projectionMap.put(DraftContract.Draft.COLUMN_NAME_CONTENT, DraftContract.Draft.COLUMN_NAME_CONTENT);
        projectionMap.put(DraftContract.Draft.COLUMN_NAME_CREATE_TIME, DraftContract.Draft.COLUMN_NAME_CREATE_TIME);
        projectionMap.put(DraftContract.Draft.COLUMN_NAME_UPDATE_TIME, DraftContract.Draft.COLUMN_NAME_UPDATE_TIME);
    }

    @Override
    public boolean onCreate() {
        draftDBHelper = new DraftDBHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(DraftContract.Draft.TABLE_NAME);

        switch (URI_MATCHER.match(uri)) {
            case DRAFTS:
                builder.setProjectionMap(projectionMap);
                break;

            case DRAFT_ID:
                builder.setProjectionMap(projectionMap);
                builder.appendWhere(DraftContract.Draft._ID + "=" + uri.getPathSegments().get(DraftContract.Draft.DRAFT_ID_PATH_POSITION));
                break;

            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = DraftContract.Draft.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        SQLiteDatabase db = draftDBHelper.getReadableDatabase();

        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, orderBy);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        if (URI_MATCHER.match(uri) != DRAFTS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues contentValues;
        if (values != null) {
            contentValues = new ContentValues(values);
        } else {
            contentValues = new ContentValues();
        }

        Long now = System.currentTimeMillis();

        if (!contentValues.containsKey(DraftContract.Draft.COLUMN_NAME_CREATE_TIME)) {
            contentValues.put(DraftContract.Draft.COLUMN_NAME_CREATE_TIME, now);
        }

        if (!contentValues.containsKey(DraftContract.Draft.COLUMN_NAME_UPDATE_TIME)) {
            contentValues.put(DraftContract.Draft.COLUMN_NAME_UPDATE_TIME, now);
        }

        SQLiteDatabase db = draftDBHelper.getWritableDatabase();

        long rowId = db.insert(DraftContract.Draft.TABLE_NAME, null, contentValues);

        if (rowId > 0) {
            Uri draftUri = ContentUris.withAppendedId(DraftContract.Draft.CONTENT_ID_URI_BASE, rowId);
            getContext().getContentResolver().notifyChange(draftUri, null);
            return draftUri;
        }

        throw new android.database.SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = draftDBHelper.getWritableDatabase();
        String finalWhere;
        int count = 0;

        switch (URI_MATCHER.match(uri)) {
            case DRAFT_ID:
                finalWhere = DraftContract.Draft._ID + "=" + uri.getPathSegments().get(DraftContract.Draft.DRAFT_ID_PATH_POSITION);

                if (selection != null) {
                    finalWhere = finalWhere + " AND " + selection;
                }

                count = db.delete(DraftContract.Draft.TABLE_NAME, finalWhere, selectionArgs);
                break;

            case DRAFTS://delete all
                count = db.delete(DraftContract.Draft.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown Uri " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase db = draftDBHelper.getWritableDatabase();
        int count;
        String where;

        Long now = System.currentTimeMillis();
        values.put(DraftContract.Draft.COLUMN_NAME_UPDATE_TIME, now);

        switch (URI_MATCHER.match(uri)) {
            case DRAFTS:
                count = db.update(DraftContract.Draft.TABLE_NAME, values, selection, selectionArgs);
                break;

            case DRAFT_ID:
                String draftId = uri.getPathSegments().get(DraftContract.Draft.DRAFT_ID_PATH_POSITION);
                where = DraftContract.Draft._ID + "=" + draftId;
                if (selection != null) {
                    where = where + " AND " + selection;
                }

                count = db.update(DraftContract.Draft.TABLE_NAME, values, where, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown Uri " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }
}
