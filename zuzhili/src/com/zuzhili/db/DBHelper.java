package com.zuzhili.db;

import android.content.Context;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DbUpgradeListener;
import com.zuzhili.bussiness.socket.model.FriendInfo;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.db.model.GroupChatInfo;
import com.zuzhili.db.model.Speech;

public class DBHelper {

    private static final int DB_NEW_VERSON = 7;

    private DbUtils dbUtils;

    private CacheDataBase cacheDB = new CacheDataBase();
    private LoggedInfoDataBase loggedInfoDataBase = new LoggedInfoDataBase();

    private Table table = new Table();
    private IMGroupInfoTable groupInfoTable = new IMGroupInfoTable();
    private IMMessageTable messageTable = new IMMessageTable();
    private IMGroupNoticeTable groupNoticeTable = new IMGroupNoticeTable();
    private IMUserInfoTable userInfoTable = new IMUserInfoTable();
    private UnreadMsgCountTable unreadMsgCountTable = new UnreadMsgCountTable();
    private MenuTable menuTable = new MenuTable();
    private SubmenuTable submenuTable = new SubmenuTable();

    private IMContactTable imContactTable = new IMContactTable();
    private IMChatRoomInfoTable imChatRoomInfoTable = new IMChatRoomInfoTable();
    private IMChatHistoryTable imChatHistoryTable = new IMChatHistoryTable();

    private static DBHelper instance;

	private DBHelper(Context context){
        dbUtils = DbUtils.create(context, "zuzhili.db", DB_NEW_VERSON, new UpdateListener());
        dbUtils.configAllowTransaction(true);
        dbUtils.configDebug(true);
	}

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }

    public DbUtils getDbUtils() {
        return dbUtils;
    }

    public void createDB() {
		cacheDB.create(dbUtils, DBCache.class);
        loggedInfoDataBase.create(dbUtils);
        imContactTable.create(dbUtils, FriendInfo.class);
        imChatRoomInfoTable.create(dbUtils, GroupChatInfo.class);
        imChatHistoryTable.create(dbUtils, Speech.class);
        groupInfoTable.create(dbUtils);
        messageTable.create(dbUtils);
        groupNoticeTable.create(dbUtils);
        userInfoTable.create(dbUtils, UserInfo.class);
        unreadMsgCountTable.create(dbUtils);
        menuTable.create(dbUtils);
        submenuTable.create(dbUtils);
	}
	
	private class UpdateListener implements DbUpgradeListener{

		@Override
		public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {
			cacheDB.upgrade(db, DBCache.class, oldVersion, newVersion);
            loggedInfoDataBase.upgrade(db, oldVersion, newVersion);
            imContactTable.upgrade(db, FriendInfo.class, oldVersion, newVersion);
            imChatRoomInfoTable.upgrade(db, GroupChatInfo.class, oldVersion, newVersion);
            imChatHistoryTable.upgrade(db, Speech.class, oldVersion, newVersion);
            if(oldVersion < newVersion){
                if (oldVersion < 3) {
                    groupInfoTable.create(dbUtils);
                    messageTable.create(dbUtils);
                    groupNoticeTable.create(dbUtils);
                    userInfoTable.create(dbUtils, UserInfo.class);
                } else if (oldVersion < 4) {
                    messageTable.upgrade(db, null, oldVersion, newVersion);
                    groupInfoTable.upgrade(db,null,oldVersion,newVersion);
                } else if (oldVersion < 5) {
                    userInfoTable.upgrade(db, UserInfo.class, oldVersion, newVersion);
                } else if (oldVersion < 7) {
                    menuTable.create(db);
                    submenuTable.create(db);
                }
            }
		}
	}

	public CacheDataBase getCacheDB() {
		return cacheDB;
	}

    public LoggedInfoDataBase getLoggedInfoDataBase() {
        return loggedInfoDataBase;
    }

    public IMContactTable getImContactTable() {
        return imContactTable;
    }

    public IMChatRoomInfoTable getImChatRoomInfoTable() {
        return imChatRoomInfoTable;
    }

    public IMChatHistoryTable getImChatHistoryTable() {
        return imChatHistoryTable;
    }

    public IMGroupInfoTable getGroupInfoTable() {
        return groupInfoTable;
    }

    public IMMessageTable getMessageTable() {
        return messageTable;
    }

    public IMGroupNoticeTable getGroupNoticeTable() {
        return groupNoticeTable;
    }

    public Table getTable() {
        return table;
    }

    public IMUserInfoTable getUserInfoTable() {
        return userInfoTable;
    }
}
