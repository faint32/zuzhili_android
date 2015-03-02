package com.zuzhili.draftbox;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by fanruikang on 14-9-22.
 */
@SuppressWarnings("unused")
public final class DraftContract {

    public static final String AUTHORITY = "com.zuzhili.provider.draftbox";

    public static final class Draft implements BaseColumns {

        public static final int DRAFT_ID_PATH_POSITION = 1;
        public static final String SCHEME = "content://";

        private static final String PATH_DRAFT_ID = "/drafts/";
        private static final String PATH_DRAFTS = "/drafts";

        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_DRAFT_ID);
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_DRAFTS);

        public static final String DEFAULT_SORT_ORDER = "update_time DESC";

        public static final String TABLE_NAME = "DRAFTS";
        public static final String COLUMN_NAME_LIST_ID = "list_id";
        public static final String COLUMN_NAME_IDS = "ids";
        public static final String COLUMN_NAME_SPACE_ID = "space_id";
        public static final String COLUMN_NAME_CONTENT_TYPE = "content_type";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_CREATE_TIME = "create_time";
        public static final String COLUMN_NAME_UPDATE_TIME = "update_time";

        public static final String CONTENT_TYPE_TEXT = "text";
        public static final String CONTENT_TYPE_PICTURE = "picture";
        public static final String CONTENT_TYPE_FILE = "file";
        public static final String CONTENT_TYPE_COMMENT = "comment";

    }

}
