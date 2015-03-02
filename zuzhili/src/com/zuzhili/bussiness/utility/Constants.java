package com.zuzhili.bussiness.utility;

/**
 * 
 * @Title: Constants.java
 * @Package: com.zuzhili.bussiness.utility
 * @Description: 常量集
 * @author: gengxin
 * @date: 2014-1-16
 */
public class Constants {

	/**--------bundle---------**/
	public static final String ACTIVITY_FROM_BUNDLE = "activity_from_bundle";					// 页面跳转通用页面源头标识
	public static final String ACTIVITY_FROM_BUNDLE_SPACEID = "activity_from_bundle_spaceid";	// 来自公共空间:spaceid
    public static final String ACTIVITY_FROM_BUNDLE_USERID = "activity_from_bundle_userid";

	/** page size */
	public static final int PAGE_SIZE = 20;													    // 请求页面大小
	
	/**来自android客户端**/
	public static final String APP_FROM_ANDROID = "1";


    /**---------发布-----------**/
    public static final String EXTRA_IMAGE_LIST = "imagelist";
    public static final int IMAGE_COUNT_MAX = 5;
    public static final String IMAGE_CHOOSED_LIST = "image_choosed_list";
    public static final String IMAGE_CHOOSED_COUNT = "image_choosed_count";
    public static final String IMAGE_BUCKET_NAME = "image_bucket_name";
    public static final String TEMP_IMAGE_PATH = "temp_image_path";
    public static final String ACTION = "action";
    public static final String ACTION_RECEIVE_IM_MSG = "action.receive.im.msg";
    public static final String ACTION_RECEIVE_CREATE_CONTACT_CHAT = "action.receive.im.create.contact.chat";
    public static final String ACTION_REFRESH_LATEST_CONTACT_LIST = "action.refresh.latest.contact.list";
    public static final String ACTION_RECEIVE_REMOVE_GROUP = "action.receive.remove.group";
    public static final String ACTION_CEMARA = "action.cemara";
    public static final String ACTION_COMMENT = "action.edit";
    public static final String ACTION_PIC_COMMENT = "action.pic.edit";
    public static final String ACTION_REPOST = "action.repost";
    public static final String ACTION_EDIT = "action_edit";
    public static final String IMAGE_ITEM = "image_item";
    public static final String IMAGE_EDIT_POSITION = "image_edit_position";
    public static final String IMAGE_DELETE = "image_delete";
    public static final String ALBUM_SELECTED = "album_selected";
    public static final String ALBUM_SELECTED_ID = "album_selected_id";
    public static final String ALBUM_SELECTED_NAME = "album_selected_name";
    public static final String MULTI_FOLDER_TYPE_MUSIC = "multi_folder_type_music";
    public static final String MULTI_FOLDER_TYPE_VEDIO = "multi_folder_type_vedio";
    public static final String FOLDER_TYPE_FILE = "folder_type_file";
    public static final String MULTI_FOLDER_TYPE = "multi_folder_type";
    public static final String MULTI_FOLDER_ITEM = "multi_folder_item";
    public static final String MUSIC_CHOOSED_COUNT = "music_choosed_count";
    public static final String MUSIC_CHOOSED_LIST = "music_choosed_list";
    public static final String FILE_TYPE = "file_type";
    public static final String FILE_TYPE_FILE = "file_type_file";
    public static final String FILE_TYPE_MUSIC = "file_type_music";
    public static final String FILE_TYPE_VEDIO = "file_type_vedio";
    public static final String FILE_NAME = "file_name";
    public static final String FILE_DESC = "file_desc";
    public static final String MUSIC_EDIT_POSITION = "music_edit_position";
    public static final String FILE_IS_DELETED = "file_is_deleted";
    public static final String VEDIO_LIST = "vedio_list";
    public static final String VEDIO_ITEM = "vedio_item";
    public static final String VEDIO_PATH = "vedio_path";
    public static final String VEDIO_NAME = "vedio_name";
    public static final String VEDIO_COVER = "vedio_cover";
    public static final String SELECT_TYPE = "select_type";
    public static final int SELECT_TYPE_FOLDER = 0;
    public static final int SELECT_TYPE_FILE = 1;

    /** 动态 */
    public static final String EXTRA_TREND_ITEM = "extra.trend.item";

    public static final String EXTRA_ANIM_REVERSE = "extra.anim.reverse";
    public static final String EXTRA_TREND_ABSID = "extra.trend.absid";
    public static final String EXTRA_TREND_TOCOMMENTID = "extra.trend.tocommentid";
    public static final String EXTRA_TREND_PRIABSID = "extra.trend.priabsid";
    public static final String EXTRA_TREND_SOURCETEXT = "extra.trend.sourcetext";
    public static final String EXTRA_FROM_WHICH_PAGE = "extra.from.which.pages";
    public static final String EXTRA_FROM_HOME = "extra.from.home";
    public static final String EXTRA_FROM_COMMENT_EDIT = "extra.from.comment.edit";
    public static final String EXTRA_FROM_AT_ME_COMMENT = "extra.from.at.me.comment";
    public static final String EXTRA_FROM_TREND_DETAIL = "extra.from.trend.detail";
    public static final String EXTRA_FROM_AT_ME_CONTENT_ACTION_REPOST = "extra.from.at.me.content.action.repost";
    public static final String EXTRA_FROM_AT_ME_CONTENT_ACTION_COMMENT = "extra.from.at.me.content.action.comment";
    public static final String EXTRA_FROM_FRAGMENT_MESSAGE = "extra.from.fragment.message";
    public static final String EXTRA_FROM_CHAT_ROOM_SETTINGS = "extra.from.chat.room.settings";
    public static final String EXTRA_AT_INFO = "extra.at.info";
    public static final String EXTRA_PICCOMMENT_INFO = "extra.piccomment.info";
    public static final String EXTRA_COMMENT_INFO = "extra.comment.info";
    public static final String EXTRA_FRAGMENT_TAG = "extra.fragment.tag";
    public static final String EXTRA_URL = "extra.url";
    public static final String EXTRA_SEARCH_URL = "extra.search.url";
    public static final String EXTRA_MEMBER = "extra.member";
    public static final String EXTRA_IM_CONTACT ="extra.im.contact" ;
    public static final String EXTRA_IDS ="extra.ids";
    public static final String EXTRA_USER_NAME ="extra.user.name";
    public static final String EXTRA_PARCELABLE_CONTACTS = "extra.parcelable.contacts";
    public static final String EXTRA_PARCELABLE_GROUP_CHAT_INFO = "extra.parcelable.group.chat.info";
    public static final String EXTRA_IM_GROUPID = "extra.im.groupid";
    public static final String EXTRA_IM_YGROUPID = "extra.im.y.groupid";
    public static final String EXTRA_IM_GROUPNNAME = "extra.im.groupname";
    public static final String EXTRA_IM_NEED_GET_GROUP_USER = "extra.im.need.get.group.user";
    public static final String EXTRA_IM_GROUP_NAME = "extra.im.group.name";
    public static final String EXTRA_IM_GROUP_CLEAR = "extra.im.group.clear";
    public static final String EXTRA_GROUP_CHAT_FLAG = "extra.group.chat.flag";
    public static final String EXTRA_IM_NEED_REFRESH_CONTACT_LIST = "extra.im.need.refresh.contact.list";
    public static final String EXTRA_FINISH_ACTIVITY = "extra.finish.activity";
    public static final String EXTRA_IM_ALL_USER = "extra.im.all.user";
    public static final String EXTRA_IM_GROUP_USER_COUNT = "extra.im.group.user.count";
    public static final String EXTRA_IM_CHAT_ROOM_TYPE = "extra.im.chat.room.type";
    public static final String EXTRA_ANIM_DEFAULT = "extra.anim.default";
    public static final String EXTRA_REGISTER = "extra.register";
    public static final String EXTRA_PHONE_NUM = "extra.phone.num";
    public static final String EXTRA_IM_GROUP = "extra.im.group";
    public static final String SYMBOL_PERIOD = ".";
    public static final String SYMBOL_COMMA = ",";
    public static final String SYMBOL_COMMA_CHN = "、";

    /** member related */
    public static final String TYPE_ALL_MEMBERS = "type.all.members";
    public static final String TYPE_FOCUS_MEMBERS = "type.focus.members";
    public static final String TYPE_RECENT_CONTACT_MEMBERS = "type.recent.contact.members";
    public static final String TAG_FEED = "tag.feed";
    public static final String TAG_AT_ME = "tag.at.me";
    public static final String TAG_COMMENT = "tag.comment";
    public static final String TAG_CHAT_CONTACTS = "tag.chat.contacts";
    public static final String TAG_MEMBERS = "tag.member";

    public static final String TAG_MORE="tag.more";

    public static final String TAG_SOCIALS="tag.socials";
    public static final String TAG_SOCIAL_SEARCH = "tag.social.search";
    public static final String TAG_MESSAGE_LIST = "tag.message.list";
    public static final String TAG_APPROVAL="tag.approval";
    public static final String TAG_PERSONAL_SPACE = "tag.personal.space";
    public static final String TAG_ALL_CONTACTS = "tag.all.contacts";
    public static final String TAG_VOTE = "tag.vote";
    public static final String TAG_REGISTER = "tag.register";
    public static final String TAG_WEBVIEW = "tag.webview";
    public static final String TAG_MENU_RESOURCE = "tag.menu.resources";
    public static final String DEVICE_TYPE_ANDROID = "1";
    public static final String RESOURCE_TYPE_PICTURE = "6";
    public static final String RESOURCE_TYPE_FILE = "9";

    public static final int LONG_DELAY = 600;
    public static final int SHORT_DELAY = 400;

    public static final String BLANK = " ";

    public static final String BROADCAST_RECEIVE_IM_MSG = "broad.receive.im.msg";
    public static final String BROADCAST_RECEIVE_IM_MSG_GROUP_TALK = "broad.receive.im.msg.group.talk";
    public static final String IM_MSG_TYPE_ATTACHMENT = "file";
    public static final String IM_MSG_TYPE_VOICE = "voice";
    public static final String IM_MSG_TYPE_TEXT = "text";

    public static final String IM_CMD_LOGIN = "login";
    public static final String IM_CMD_GET_ALL_USER = "getAllUser";
    public static final String IM_CMD_TALK = "talk";
    public static final String IM_CMD_GET_TALK_MSG = "getTalkMsg";
    public static final String IM_CMD_ADD_GROUP = "addGroup";
    public static final String IM_CMD_ADD_PERSON_TO_GROUP = "addPersonToGroup";
    public static final String IM_CMD_TALK_ALL = "talkAll";
    public static final String IM_CMD_GET_GROUPS = "getGroups";
    public static final String IM_CMD_GET_GROUP_USER = "getGroupUser";
    public static final String IM_CMD_SET_USER_INFO = "setuserinfo";
    public static final String IM_CMD_GET_USER_INFO = "getUserInfo";
    public static final String IM_CMD_EXIT_GROUP = "exitGroup";
    public static final String IM_CMD_LOGOUT = "logout";
    public static final String IM_CMD_GET_GROUP_INFO = "getGroupInfo";
    public static final String IM_CMD_DEL_GROUP = "delGroup";

    public static final String IM_TYPE_GROUP_CHAT = "im.type.group.chat";
    public static final String IM_TYPE_P2P_CHAT = "im.type.p2p.chat";
    public static final String IM_GROUP_CUSTOM = "0";
    public static final String IM_GROUP_CONFERENCE = "2";

    public static final String IM_STATE_SUCC = "-100";
    public static final String IM_STATE_FAIL = "-200";
    public static final String IM_STATE_REC_MSG = "-300";

    public static final String EXTRA_REPOST_TEXT = "repost.text";

    public static final String TO_GROUPSLISTFRG = "to.GroupListFrg";
    public static final String CHANGE_SOCIAL = "change.social";

    public static final String MSG_FORWORD = "msg.forword";
    public static final String MSG_TYPE = "msg.type";

    public static final int PAGE_TREND = 1;
    public static final int PAGE_AT_CONTENT = 2;
    public static final int PAGE_AT_COMMENT = 3;
    public static final int PAGE_COMMENT_RECEIVE = 4;
    public static final int PAGE_COMMENT_SEND = 5;
    public static final int PAGE_CHAT = 6;
    public static final int PAGE_MEMBERS_ALL = 7;
    public static final int PAGE_MEMBERS_FOCUS = 8;
    public static final int PAGE_MEMBERS_CONTACT = 9;
    public static final int PAGE_APPROVAL_RECEIVE = 10;
    public static final int PAGE_APPROVAL_SEND = 11;
    public static final int PAGE_PERSONAL_SPACE = 12;
    public static final int PAGE_CHECK_PHONE = 13;
    public static final String BROADCAST_ACTION = "com.zuzhili.BROADCAST";
    public static final String EXTENDED_DATA_STATUS = "com.zuzhili.STATUS";
    public static final int PULL_IM_USERS_FINISHED = 0;
    public static final int PULL_IM_GROUPS_FINISHED = 1;
    public static final int PULL_DATA_FAILED = -1;
    public static final String EXTRA_VOIP_ID = "y_voip";
    public static final String ARG_SHOW_SEARCH_VIEW = "show.search.view";
    public static final String EXTRA_IS_CHANGE = "extra.im.users";
    public static final String IMAGE_POSITION = "image.position";
    public static final String BIGIMAGE_PHOTOS = "big.image.photos";
    public static final String BIGIMAGE_PHOTO = "big.image.photo";
    public static final String FINISH_ACTIVITY = "finish.activity";
    public static final String PHOTO_ID = "photo.id";
    public static final String FROM_SPACE = "from_space";
    public static final String SPACE_ALBUM_NAME = "space.album.name";


    /* Public Space */
    public static final String EXTRA_SPACE_MODEL = "EXTRA_SPACE_MODEL";
    public static final String EXTRA_GUIDE = "extra.has.guide";
}
