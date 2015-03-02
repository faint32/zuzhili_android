package com.zuzhili;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.db.DBHelper;
import com.zuzhili.framework.Session;
import com.zuzhili.model.im.IMConversation;
import com.zuzhili.model.menu.SanweiMenu;
import com.zuzhili.provider.Contract;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Define a sync adapter for the app.
 *
 * <p>This class is instantiated in SyncService, which also binds SyncAdapter to the system.
 * SyncAdapter should only be initialized in SyncService, never anywhere else.
 *
 * <p>The system calls onPerformSync() via an RPC call through the IBinder object supplied by
 * SyncService.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";

    public static final String ACTION_UPDATE_UNREAD = "update_unread";
    public static final String ACTION_UPDATE_MENU = "update_menu";


    private String action;
    /**
     * URL to fetch content from during a sync.
     *
     * <p>This points to the Android Developers Blog. (Side note: We highly recommend reading the
     * Android Developer Blog to stay up to date on the latest Android platform developments!)
     */
    private static final String URL = Task.API_HOST_URL + Task.API_URLS.get(Task.ACTION_GET_UNREAD_MSG_COUNT);

    /**
     * Network connection timeout, in milliseconds.
     */
    private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000;  // 15 seconds

    /**
     * Network read timeout, in milliseconds.
     */
    private static final int NET_READ_TIMEOUT_MILLIS = 10000;  // 10 seconds

    /**
     * Content resolver, for performing database operations.
     */
    private final ContentResolver mContentResolver;

    /**
     * Projection for querying the content provider.
     */
    private static final String[] PROJECTION = new String[]{
            Contract.Entry._ID,
            Contract.Entry.COLUMN_NAME_LIST_ID,
            Contract.Entry.COLUMN_NAME_IDS,
            Contract.Entry.COLUMN_NAME_REFRESH_COUNT,
            Contract.Entry.COLUMN_NAME_MSG_COUNT,
            Contract.Entry.COLUMN_NAME_AT_FEED_COUNT,
            Contract.Entry.COLUMN_NAME_AT_COMMENT_COUNT,
            Contract.Entry.COLUMN_NAME_COMMENT_COUNT,
            Contract.Entry.COLUMN_NAME_NOTIFY_COUNT,
            Contract.Entry.COLUMN_NAME_APPROVAL_COUNT,
            Contract.Entry.COLUMN_NAME_APPROVAL_REPLY_COUNT,
            Contract.Entry.COLUMN_NAME_ALL_COUNT
    };

    // Column indexes. The index of a column in the Cursor is the same as its relative position in
    // the projection.
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_LIST_ID = 1;
    private static final int COLUMN_IDS = 2;
    private static final int COLUMN_REFRESH_COUNT = 3;
    private static final int COLUMN_MSG_COUNT = 4;
    private static final int COLUMN_AT_FEED_COUNT = 5;
    private static final int COLUMN_AT_COMMENT_COUNT = 6;
    private static final int COLUMN_COMMENT_COUNT = 7;
    private static final int COLUMN_NOTIFY_COUNT = 8;
    private static final int COLUMN_APPROVAL_COUNT = 9;
    private static final int COLUMN_APPROVAL_REPLY_COUNT = 10;
    private static final int COLUMN_ALL_COUNT = 11;

    private static final String[] PROJECTION_MENU = new String[] {
            Contract.Menu.MENU_COLUMN_SANWEI_USERID
    };

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    /**
     * Constructor. Obtains handle to content resolver for later use.
     */
    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    /**
     * Called by the Android system in response to a request to run the sync adapter. The work
     * required to read data from the network, parse it, and store it in the content provider is
     * done here. Extending AbstractThreadedSyncAdapter ensures that all methods within SyncAdapter
     * run on a background thread. For this reason, blocking I/O and other long-running tasks can be
     * run <em>in situ</em>, and you don't have to set up a separate thread for them.
     .
     *
     * <p>This is where we actually perform any work required to perform a sync.
     * {@link android.content.AbstractThreadedSyncAdapter} guarantees that this will be called on a non-UI thread,
     * so it is safe to peform blocking I/O here.
     *
     * <p>The syncResult argument allows you to pass information back to the method that triggered
     * the sync.
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.i(TAG, "Beginning network synchronization");
        try {
            Session session = Session.get(getContext());
            URL location;
            if (extras.getString("url") != null) {
                location = new URL(extras.getString("url"));
                action = ACTION_UPDATE_MENU;
            } else {
                location = new URL(URL + "?userid=" + session.getUid() + "&listid=" + session.getListid());
                action = ACTION_UPDATE_UNREAD;
            }
            InputStream stream = null;

            try {
                Log.i(TAG, "Streaming data from network: " + location);
//                stream = downloadUrl(location);
                stream = sync(location);
                update(stream, syncResult, action);
                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        } catch (MalformedURLException e) {
            Log.wtf(TAG, "Feed URL is malformed", e);
            syncResult.stats.numParseExceptions++;
            return;
        } catch (IOException e) {
            Log.e(TAG, "Error reading from network: " + e.toString());
            syncResult.stats.numIoExceptions++;
            return;
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Error parsing: " + e.toString());
            syncResult.stats.numParseExceptions++;
            return;
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing: " + e.toString());
            syncResult.stats.numParseExceptions++;
            return;
        } catch (RemoteException e) {
            Log.e(TAG, "Error updating database: " + e.toString());
            syncResult.databaseError = true;
            return;
        } catch (OperationApplicationException e) {
            Log.e(TAG, "Error updating database: " + e.toString());
            syncResult.databaseError = true;
            return;
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing: " + e.toString());
            syncResult.stats.numParseExceptions++;
            return;
        }
        Log.i(TAG, "Network synchronization complete");
    }

    /**
     * Read XML from an input stream, storing it into the content provider.
     */
    public void update(final InputStream stream, final SyncResult syncResult, final String action)
            throws IOException, XmlPullParserException, RemoteException,
            OperationApplicationException, ParseException {
        final ContentResolver contentResolver = getContext().getContentResolver();

        InputStream in = stream;
        InputStreamReader is = new InputStreamReader(in);
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(is);
        String read = br.readLine();

        while (read != null) {
            sb.append(read);
            read = br.readLine();

        }
        ArrayList<ContentProviderOperation> batch;
        if (action.equals(ACTION_UPDATE_UNREAD)) {
            batch = updateUnreadData(sb, contentResolver, syncResult);
        } else {
            batch = updateMenu(sb, contentResolver, syncResult);
        }

        Log.i(TAG, "Applying batch update");
        mContentResolver.applyBatch(Contract.CONTENT_AUTHORITY, batch);
        mContentResolver.notifyChange(
                Contract.Entry.CONTENT_URI, // URI where data was modified
                null,                           // No local observer
                false);
    }

    public InputStream sync(final URL url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(15, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(15, TimeUnit.SECONDS);
        Request request = new Request.Builder()
                    .url(url)
                    .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response != null && response.code() == 200) {
                String header = response.header("Set-Cookie");
                Log.d(TAG, "Set-Cookie is: " + header);
                if (header != null) {
                    Session.get(getContext()).setSessionId(header);
                }
                return response.body().byteStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets an input stream.
     */
    private InputStream downloadUrl(final URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(NET_READ_TIMEOUT_MILLIS /* milliseconds */);
        conn.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS /* milliseconds */);
        if (url.getHost().equals("121.42.53.110")) {
            conn.setRequestProperty("Host", "121.42.53.110:8080");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 5.0.1; Nexus 5 Build/LRX22C) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.93 Mobile Safari/537.36");
            conn.setRequestProperty("Cache-Control", "max-age=0");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
        }

        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        // Starts the query
        conn.connect();

//        final String COOKIES_HEADER = "Set-Cookie";
//        java.net.CookieManager msCookieManager = new java.net.CookieManager();
//
//        Map<String, List<String>> headerFields = conn.getHeaderFields();
//        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
//
//        if (cookiesHeader != null) {
//            for (String cookie : cookiesHeader) {
//                msCookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
//            }
//        }

        // Get cookie
        return conn.getInputStream();
    }

    private ArrayList<ContentProviderOperation> updateMenu(StringBuilder sb, ContentResolver contentResolver, SyncResult syncResult) {
        String vote = null;
        String hall = null;
        String username = null;
        String more = null;
        int roleId;
        String contacts = null;
        String list = null;
        int sanweiUserId;

        JSONObject jsonObject = JSON.parseObject(sb.toString());

        if (jsonObject != null && jsonObject.get("errmsg").equals("ok")) {
            vote = jsonObject.getString("toupiaoimg");
            hall = jsonObject.getString("datingimg");
            username = jsonObject.getString("username");
            more = jsonObject.getString("gengduoimg");
            roleId = jsonObject.getInteger("roleid");
            contacts = jsonObject.getString("tongxunluimg");
            list = jsonObject.getString("list");
            sanweiUserId = jsonObject.getInteger("sanweiuserid");

            ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

            updateList(list, batch, contentResolver, syncResult);

            Uri uri = Contract.Menu.CONTENT_URI;

            Cursor c = contentResolver.query(uri, PROJECTION_MENU, null, null, null);
            assert c != null;
            Log.i(TAG, "Found " + c.getCount() + " local entries. Computing updating solution...");

            if (c.moveToNext()) {
                syncResult.stats.numEntries++;
                int id = c.getInt(COLUMN_ID);
                // Check to see if the entry needs to be updated
                Uri existingUri = Contract.Menu.CONTENT_URI.buildUpon()
                        .appendPath(Integer.toString(id)).build();

                batch.add(ContentProviderOperation.newUpdate(existingUri)
                        .withValue(Contract.Menu.MENU_COLUMN_VOTE, vote)
                        .withValue(Contract.Menu.MENU_COLUMN_HALL, hall)
                        .withValue(Contract.Menu.MENU_COLUMN_USERNAME, username)
                        .withValue(Contract.Menu.MENU_COLUMN_MORE, more)
                        .withValue(Contract.Menu.MENU_COLUMN_ROLEID, roleId)
                        .withValue(Contract.Menu.MENU_COLUMN_CONTACTS, contacts)
                        .withValue(Contract.Menu.MENU_COLUMN_LIST, list)
                        .withValue(Contract.Menu.MENU_COLUMN_SANWEI_USERID, sanweiUserId)
                        .build());

                syncResult.stats.numUpdates++;

                c.close();
            } else {
                batch.add(ContentProviderOperation.newInsert(Contract.Menu.CONTENT_URI)
                        .withValue(Contract.Menu.MENU_COLUMN_VOTE, vote)
                        .withValue(Contract.Menu.MENU_COLUMN_HALL, hall)
                        .withValue(Contract.Menu.MENU_COLUMN_USERNAME, username)
                        .withValue(Contract.Menu.MENU_COLUMN_MORE, more)
                        .withValue(Contract.Menu.MENU_COLUMN_ROLEID, roleId)
                        .withValue(Contract.Menu.MENU_COLUMN_CONTACTS, contacts)
                        .withValue(Contract.Menu.MENU_COLUMN_LIST, list)
                        .withValue(Contract.Menu.MENU_COLUMN_SANWEI_USERID, sanweiUserId)
                        .build());
                syncResult.stats.numInserts++;
            }
            return batch;
        }
        return null;
    }

    private void updateList(String list, ArrayList<ContentProviderOperation> batch, ContentResolver contentResolver, SyncResult syncResult) {

        List<SanweiMenu> sanweiMenuList = JSON.parseArray(list, SanweiMenu.class);

        Uri uri = Contract.SubMenu.CONTENT_URI;

        int deleteNums = contentResolver.delete(uri, null, null);
        Log.i(TAG, "Delete " + deleteNums + " local entries");

        for (SanweiMenu submenu: sanweiMenuList) {
            syncResult.stats.numEntries++;

            batch.add(ContentProviderOperation.newInsert(Contract.SubMenu.CONTENT_URI)
                    .withValue(Contract.SubMenu.SUBMENU_COLUMN_ID, submenu.id)
                    .withValue(Contract.SubMenu.SUBMENU_COLUMN_NAME, submenu.lname)
                    .withValue(Contract.SubMenu.SUBMENU_COLUMN_LEVEL, submenu.llevel)
                    .withValue(Contract.SubMenu.SUBMENU_COLUMN_PARENTID, submenu.lparentid)
                    .withValue(Contract.SubMenu.SUBMENU_COLUMN_SEARCHURL, submenu.searchurl)
                    .withValue(Contract.SubMenu.SUBMENU_COLUMN_ICON, submenu.licon)
                    .withValue(Contract.SubMenu.SUBMENU_COLUMN_CATEGORYURL, submenu.lurl)
                    .build());

            syncResult.stats.numInserts++;
        }
    }

    private ArrayList<ContentProviderOperation> updateUnreadData(StringBuilder sb, ContentResolver contentResolver, SyncResult syncResult) {
        String listId = null;
        String ids = null;
        String unreadnewfreshcount = null;
        String unreadnewmsgcount = null;
        String unreadnewatfeed = null;
        String unreadnewatcomment = null;
        String unreadnewcomment = null;
        String unreadnewnotify = null;
        String unreadshenpi = null;
        String unreadshenpihuifu = null;
        String allcount = null;

        List<IMConversation> imConversations = null;
        try {
            DBHelper helper = DBHelper.getInstance(getContext());
            helper.getTable().setDbUtils(helper.getDbUtils());
            imConversations = helper.getTable().queryIMConversation(buildIdentity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (imConversations != null) {
            int unreadMsg = 0;
            for (IMConversation conversation : imConversations) {
                unreadMsg += Integer.valueOf(conversation.getUnReadNum());
            }
            // 本地未读聊天消息数目
            unreadnewmsgcount = String.valueOf(unreadMsg);
        }

        JSONObject jsonObject = JSON.parseObject(sb.toString());

        if (jsonObject != null && jsonObject.get("errmsg").equals("ok")) {
            JSONObject list = JSONObject.parseObject(jsonObject.getString("list"));
            listId = list.getString("id");
            ids = Session.get(getContext()).getIds();
            unreadnewfreshcount = list.getString("unreadnewfreshcount");
            unreadnewatfeed = list.getString("unreadnewatfeed");
            unreadnewatcomment = list.getString("unreadnewatcomment");
            unreadnewcomment = list.getString("unreadnewcomment");
            unreadnewnotify = list.getString("unreadnewnotify");
            unreadshenpi = list.getString("unreadshenpi");
            unreadshenpihuifu = list.getString("unreadshenpihuifu");
            allcount = list.getString("allcount") != null ? list.getString("allcount") : "0";

            ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
            Uri uri = Contract.Entry.CONTENT_URI;

            String selection = "listid = ? AND ids = ?";
            String[] selectionArgs = new String[]{listId, ids};
            Cursor c = contentResolver.query(uri, PROJECTION, selection, selectionArgs, null);
            assert c != null;
            Log.i(TAG, "Found " + c.getCount() + " local menus. Computing updating solution...");

            if (c.moveToNext()) {
                syncResult.stats.numEntries++;
                int id = c.getInt(COLUMN_ID);
                // Check to see if the entry needs to be updated
                Uri existingUri = Contract.Entry.CONTENT_URI.buildUpon()
                        .appendPath(Integer.toString(id)).build();

                batch.add(ContentProviderOperation.newUpdate(existingUri)
                        .withValue(Contract.Entry.COLUMN_NAME_LIST_ID, listId)
                        .withValue(Contract.Entry.COLUMN_NAME_IDS, ids)
                        .withValue(Contract.Entry.COLUMN_NAME_REFRESH_COUNT, unreadnewfreshcount)
                        .withValue(Contract.Entry.COLUMN_NAME_MSG_COUNT, unreadnewmsgcount)
                        .withValue(Contract.Entry.COLUMN_NAME_AT_FEED_COUNT, unreadnewatfeed)
                        .withValue(Contract.Entry.COLUMN_NAME_AT_COMMENT_COUNT, unreadnewatcomment)
                        .withValue(Contract.Entry.COLUMN_NAME_COMMENT_COUNT, unreadnewcomment)
                        .withValue(Contract.Entry.COLUMN_NAME_NOTIFY_COUNT, unreadnewnotify)
                        .withValue(Contract.Entry.COLUMN_NAME_APPROVAL_COUNT, unreadshenpi)
                        .withValue(Contract.Entry.COLUMN_NAME_APPROVAL_REPLY_COUNT, unreadshenpihuifu)
                        .withValue(Contract.Entry.COLUMN_NAME_ALL_COUNT, allcount)
                        .build());

                syncResult.stats.numUpdates++;

                c.close();
            } else {
                batch.add(ContentProviderOperation.newInsert(Contract.Entry.CONTENT_URI)
                        .withValue(Contract.Entry.COLUMN_NAME_LIST_ID, listId)
                        .withValue(Contract.Entry.COLUMN_NAME_IDS, ids)
                        .withValue(Contract.Entry.COLUMN_NAME_REFRESH_COUNT, unreadnewfreshcount)
                        .withValue(Contract.Entry.COLUMN_NAME_MSG_COUNT, unreadnewmsgcount)
                        .withValue(Contract.Entry.COLUMN_NAME_AT_FEED_COUNT, unreadnewatfeed)
                        .withValue(Contract.Entry.COLUMN_NAME_AT_COMMENT_COUNT, unreadnewatcomment)
                        .withValue(Contract.Entry.COLUMN_NAME_COMMENT_COUNT, unreadnewcomment)
                        .withValue(Contract.Entry.COLUMN_NAME_NOTIFY_COUNT, unreadnewnotify)
                        .withValue(Contract.Entry.COLUMN_NAME_APPROVAL_COUNT, unreadshenpi)
                        .withValue(Contract.Entry.COLUMN_NAME_APPROVAL_REPLY_COUNT, unreadshenpihuifu)
                        .withValue(Contract.Entry.COLUMN_NAME_ALL_COUNT, allcount)
                        .build());
                syncResult.stats.numInserts++;
            }
            return batch;
        }
        return null;
    }

    private String buildIdentity() {
        StringBuilder builder = new StringBuilder();
        Session s = Session.get(getContext());
        builder.append(s.getListid())
                .append(Constants.SYMBOL_PERIOD)
                .append(s.getIds());
        return  builder.toString();
    }
}
