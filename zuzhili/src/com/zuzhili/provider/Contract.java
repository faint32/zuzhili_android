package com.zuzhili.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class Contract {

    private Contract() {
    }

    /**
     * Content provider authority.
     */
    public static final String CONTENT_AUTHORITY = "com.zuzhili";

    /**
     * Base URI. (content://com.zuzhili)
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Path component for "entry"-type resources..
     */
    private static final String PATH_ENTRIES = "entries";
    private static final String PATH_MENUS = "menus";
    private static final String PATH_SUBMENUS = "submenus";

    /**
     * Columns supported by "entries" records.
     */
    public static class Entry implements BaseColumns {
        /**
         * MIME type for lists of entries.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.zuzhili.entries";
        /**
         * MIME type for individual entries.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.zuzhili.entry";

        /**
         * Fully qualified URI for "entry" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRIES).build();

        /**
         * Table name where records are stored for "entry" resources.
         */
        public static final String TABLE_NAME = "entry";

        /**
         * 社区id
         */
        public static final String COLUMN_NAME_LIST_ID = "listid";
        /**
         * 用户ids
         */
        public static final String COLUMN_NAME_IDS = "ids";
        /**
         * unknown
         */
        public static final String COLUMN_NAME_REFRESH_COUNT = "unreadnewfreshcount";
        /**
         * 新消息数目，deprecated
         */
        public static final String COLUMN_NAME_MSG_COUNT = "unreadnewmsgcount";
        /**
         * at我的内容数目
         */
        public static final String COLUMN_NAME_AT_FEED_COUNT = "unreadnewatfeed";
        /**
         * at我的评论数目
         */
        public static final String COLUMN_NAME_AT_COMMENT_COUNT = "unreadnewatcomment";
        /**
         * 评论我的动态数目
         */
        public static final String COLUMN_NAME_COMMENT_COUNT = "unreadnewcomment";
        /**
         * 通知数目
         */
        public static final String COLUMN_NAME_NOTIFY_COUNT = "unreadnewnotify";
        /**
         * 审批数目
         */
        public static final String COLUMN_NAME_APPROVAL_COUNT = "unreadshenpi";
        /**
         * 审批回复数目
         */
        public static final String COLUMN_NAME_APPROVAL_REPLY_COUNT = "unreadshenpihuifu";

        /**
         * 所有社区未读消息总数
         */
        public static final String COLUMN_NAME_ALL_COUNT = "allcount";

    }

    public static class Menu implements BaseColumns {
        /**
         * MIME type for lists of entries.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.zuzhili.menu";
        /**
         * MIME type for individual entries.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.zuzhili.menu";

        /**
         * Fully qualified URI for "menu" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MENUS).build();

        /**
         * Table name where records are stored for "entry" resources.
         */
        public static final String TABLE_NAME = "menu";

        public static final String MENU_COLUMN_VOTE = "menu_column_vote";
        public static final String MENU_COLUMN_HALL = "menu_column_hall";
        public static final String MENU_COLUMN_USERNAME = "menu_column_username";
        public static final String MENU_COLUMN_MORE = "menu_column_more";
        public static final String MENU_COLUMN_ROLEID = "menu_column_role_id";
        public static final String MENU_COLUMN_CONTACTS = "menu_column_contacts";
        public static final String MENU_COLUMN_LIST = "menu_column_list";
        public static final String MENU_COLUMN_SANWEI_USERID = "menu_column_sanwei_userid";
    }

    public static class SubMenu implements BaseColumns {
        /**
         * MIME type for lists of submenus.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.zuzhili.submenu";
        /**
         * MIME type for individual submenu.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.zuzhili.submenu";

        /**
         * Fully qualified URI for "submenu" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SUBMENUS).build();

        /**
         * Table name where records are stored for "submenu" resources.
         */
        public static final String TABLE_NAME = "submenus";

        public static final String SUBMENU_COLUMN_ID = "submenu_column_id";
        public static final String SUBMENU_COLUMN_NAME = "submenu_column_name";
        public static final String SUBMENU_COLUMN_LEVEL = "submenu_column_level";
        public static final String SUBMENU_COLUMN_PARENTID = "submenu_column_parentid";
        public static final String SUBMENU_COLUMN_SEARCHURL = "submenu_column_searchurl";
        public static final String SUBMENU_COLUMN_ICON = "submenu_column_ICON";
        public static final String SUBMENU_COLUMN_CATEGORYURL = "submenu_column_CATEGORYURL";
    }
}