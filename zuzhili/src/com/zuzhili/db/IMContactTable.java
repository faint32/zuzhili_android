package com.zuzhili.db;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.db.table.TableUtils;
import com.lidroid.xutils.exception.DbException;
import com.zuzhili.bussiness.socket.model.FriendInfo;
import com.zuzhili.bussiness.socket.model.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liutao on 14-4-17.
 */
@Deprecated
public class IMContactTable extends Table {
    @Deprecated
    public void upgrade(DbUtils db, Class clazz, int oldVersion, int newVersion) {
        if(oldVersion < newVersion){
            if (oldVersion < 2) {
                upgradeTable2(db);
            }
        }
    }
    @Deprecated
    private void upgradeTable2(DbUtils dbUtils) {
        try {
            if (!checkColumnExist(dbUtils.getDatabase(), TableUtils.getTableName(FriendInfo.class), "sortKey")) {
                dbUtils.execNonQuery("ALTER TABLE " + TableUtils.getTableName(FriendInfo.class) + " ADD COLUMN sortKey TEXT ");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取所有联系人信息
     * @param identity
     * @return
     */
    @Deprecated
    public List<FriendInfo> get(String identity){
        List<FriendInfo> cache = null;
        try {
            whereBuilder = WhereBuilder.b().and("identity", "=", identity);
            cache = utils.findAll(Selector.from(FriendInfo.class).where(whereBuilder));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return cache;
    }

    /**
     * 获取某一个联系人
     * @param friendId
     * @param identity
     * @return
     */
    @Deprecated
    public FriendInfo get(String friendId, String identity) {
        FriendInfo cache = null;
        try {
            whereBuilder = WhereBuilder.b().and("uid", "=", friendId).and("identity", "=", identity);
            cache = utils.findFirst(Selector.from(FriendInfo.class).where(whereBuilder));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return cache;
    }

    @Deprecated
    public List<FriendInfo> get(String[] friendIds, String identity) {
        List<FriendInfo> friendInfoList = new ArrayList<FriendInfo>();
        try {
            for (int i = 0; i < friendIds.length; i++) {
                whereBuilder = WhereBuilder.b().and("uid", "=", friendIds[i]).and("identity", "=", identity);
                FriendInfo cache = utils.findFirst(Selector.from(FriendInfo.class).where(whereBuilder));
                if (cache != null) {
                    friendInfoList.add(cache);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return friendInfoList;
    }

    public void insert(List<UserInfo> userInfoList, String identity){
        try {
            whereBuilder = WhereBuilder.b().and("identity", "=", identity);
//            UserInfo first = utils.findFirst(Selector.from(UserInfo.class).where(whereBuilder));
//            if (null != first) {
//                utils.updateAll(userInfoList, whereBuilder, shouldUpdateAll);
//            } else {
//                utils.saveAll(userInfoList);
//            }
            utils.delete(UserInfo.class, whereBuilder);
            utils.saveAll(userInfoList);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
