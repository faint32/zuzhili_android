package com.zuzhili.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.zuzhili.db.DBHelper;
import com.zuzhili.framework.utils.SelectionBuilder;

/**
 * Created by liutao on 14-9-1.
 */
public class DataProvider extends ContentProvider {

    DBHelper mDatabaseHelper;

    /**
     * Content authority for this provider.
     */
    private static final String AUTHORITY = Contract.CONTENT_AUTHORITY;

    // The constants below represent individual URI routes, as IDs. Every URI pattern recognized by
    // this ContentProvider is defined using sUriMatcher.addURI(), and associated with one of these
    // IDs.
    //
    // When a incoming URI is run through sUriMatcher, it will be tested against the defined
    // URI patterns, and the corresponding route ID will be returned.
    /**
     * URI ID for route: /entries
     */
    public static final int ROUTE_ENTRIES = 1;

    /**
     * URI ID for route: /entries/{ID}
     */
    public static final int ROUTE_ENTRIES_ID = 2;

    public static final int ROUTE_MENUS = 3;
    public static final int ROUTE_MENUS_ID = 4;

    public static final int ROUTE_SUBMENUS = 5;
    public static final int ROUTE_SUBMENUS_ID = 6;

    /**
     * UriMatcher, used to decode incoming URIs.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, "entries", ROUTE_ENTRIES);
        sUriMatcher.addURI(AUTHORITY, "entries/*", ROUTE_ENTRIES_ID);

        sUriMatcher.addURI(AUTHORITY, "menus", ROUTE_MENUS);
        sUriMatcher.addURI(AUTHORITY, "menus/*", ROUTE_MENUS_ID);

        sUriMatcher.addURI(AUTHORITY, "submenus", ROUTE_SUBMENUS);
        sUriMatcher.addURI(AUTHORITY, "submenus/*", ROUTE_SUBMENUS_ID);
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = DBHelper.getInstance(getContext());
        return true;
    }

    /**
     * Determine the mime type for entries returned by a given URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ROUTE_ENTRIES:
                return Contract.Entry.CONTENT_TYPE;
            case ROUTE_ENTRIES_ID:
                return Contract.Entry.CONTENT_ITEM_TYPE;
            case ROUTE_MENUS:
                return Contract.Menu.CONTENT_TYPE;
            case ROUTE_MENUS_ID:
                return Contract.Menu.CONTENT_ITEM_TYPE;
            case ROUTE_SUBMENUS:
                return Contract.SubMenu.CONTENT_TYPE;
            case ROUTE_SUBMENUS_ID:
                return Contract.SubMenu.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDatabaseHelper.getDbUtils().getDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case ROUTE_ENTRIES_ID:
                // Return a single entry, by ID.
                String id = uri.getLastPathSegment();
                builder.where(Contract.Entry._ID + "=?", id);
            case ROUTE_ENTRIES:
                // Return all known entries.
                builder.table(Contract.Entry.TABLE_NAME)
                        .where(selection, selectionArgs);
                Cursor c = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                Context ctx = getContext();
                assert ctx != null;
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;
            case ROUTE_MENUS:
                // Return all known entries.
                builder.table(Contract.Menu.TABLE_NAME)
                        .where(selection, selectionArgs);
                c = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                ctx = getContext();
                assert ctx != null;
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;

            case ROUTE_SUBMENUS_ID:
                // Return a single entry, by ID.
                id = uri.getLastPathSegment();
                builder.where(Contract.Entry._ID + "=?", id);
            case ROUTE_SUBMENUS:
                // Return all known entries.
                builder.table(Contract.SubMenu.TABLE_NAME)
                        .where(selection, selectionArgs);
                c = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                ctx = getContext();
                assert ctx != null;
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDatabaseHelper.getDbUtils().getDatabase();
        assert db != null;
        final int match = sUriMatcher.match(uri);
        Uri result;
        switch (match) {
            case ROUTE_ENTRIES:
                long id = db.insertOrThrow(Contract.Entry.TABLE_NAME, null, values);
                result = Uri.parse(Contract.Entry.CONTENT_URI + "/" + id);
                break;
            case ROUTE_MENUS:
                id = db.insertOrThrow(Contract.Menu.TABLE_NAME, null, values);
                result = Uri.parse(Contract.Menu.CONTENT_URI + "/" + id);
                break;
            case ROUTE_SUBMENUS:
                id = db.insertOrThrow(Contract.SubMenu.TABLE_NAME, null, values);
                result = Uri.parse(Contract.SubMenu.CONTENT_URI + "/" + id);
                break;
            case ROUTE_ENTRIES_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return result;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getDbUtils().getDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case ROUTE_ENTRIES:
                count = builder.table(Contract.Entry.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_ENTRIES_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(Contract.Entry.TABLE_NAME)
                        .where(Contract.Entry._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_MENUS:
                count = builder.table(Contract.Menu.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_MENUS_ID:
                id = uri.getLastPathSegment();
                count = builder.table(Contract.Menu.TABLE_NAME)
                        .where(Contract.Menu._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;

            case ROUTE_SUBMENUS:
                count = builder.table(Contract.SubMenu.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_SUBMENUS_ID:
                id = uri.getLastPathSegment();
                count = builder.table(Contract.SubMenu.TABLE_NAME)
                        .where(Contract.SubMenu._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getDbUtils().getDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case ROUTE_ENTRIES:
                count = builder.table(Contract.Entry.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_ENTRIES_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(Contract.Entry.TABLE_NAME)
                        .where(Contract.Entry._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_MENUS:
                count = builder.table(Contract.Menu.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_MENUS_ID:
                id = uri.getLastPathSegment();
                count = builder.table(Contract.Menu.TABLE_NAME)
                        .where(Contract.Menu._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_SUBMENUS:
                count = builder.table(Contract.SubMenu.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_SUBMENUS_ID:
                id = uri.getLastPathSegment();
                count = builder.table(Contract.SubMenu.TABLE_NAME)
                        .where(Contract.SubMenu._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }
}
