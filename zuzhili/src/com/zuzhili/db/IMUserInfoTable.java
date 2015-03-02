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
 * Created by liutao on 14-6-29.
 */
public class IMUserInfoTable extends Table {

    public void upgrade(DbUtils db, Class clazz, int oldVersion, int newVersion) {
        if(oldVersion < newVersion){
            if (oldVersion < 5) {
                upgradeTable5(db);
            }
        }
    }

    private void upgradeTable5(DbUtils dbUtils) {
        try {
            if (!checkColumnExist(dbUtils.getDatabase(), TableUtils.getTableName(UserInfo.class), "u_ids")) {
                dbUtils.execNonQuery("ALTER TABLE " + TableUtils.getTableName(UserInfo.class) + " ADD COLUMN u_ids TEXT ");
            }
            if (!checkColumnExist(dbUtils.getDatabase(), TableUtils.getTableName(UserInfo.class), "u_phone")) {
                dbUtils.execNonQuery("ALTER TABLE " + TableUtils.getTableName(UserInfo.class) + " ADD COLUMN u_phone TEXT ");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取所有联系人信息
     * @return
     */
    public List<UserInfo> get(String listId, String identity) {
        List<UserInfo> cache = null;
        try {
            whereBuilder = WhereBuilder.b().and("u_listid", "=", listId).and("identity", "=", identity);
            cache = utils.findAll(Selector.from(UserInfo.class).where(whereBuilder));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return cache;
    }

    public List<UserInfo> get(String listId) {
        List<UserInfo> cache = null;
        try {
            whereBuilder = WhereBuilder.b().and("u_listid", "=", listId);
            cache = utils.findAll(Selector.from(UserInfo.class).where(whereBuilder));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return cache;
    }

    /**
     * 获取某一个联系人
     * @return
     */
    public UserInfo get(String y_voip, String listId, String identity) {
        UserInfo cache = null;
        try {
            whereBuilder = WhereBuilder.b().and("y_voip", "=", y_voip).and("u_listid", "=", listId).and("identity", "=", identity);
            cache = utils.findFirst(Selector.from(UserInfo.class).where(whereBuilder));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return cache;
    }

    /**
     * 获取某一个联系人
     * @return
     */
    public UserInfo getUserByUid(String u_id, String listId) {
        UserInfo cache = null;
        try {
            whereBuilder = WhereBuilder.b().and("u_id", "=", u_id).and("u_listid", "=", listId);
            cache = utils.findFirst(Selector.from(UserInfo.class).where(whereBuilder));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return cache;
    }

    public UserInfo getUserByIds(String ids, String listId) {
        UserInfo cache = null;
        try {
            whereBuilder = WhereBuilder.b().and("u_ids", "=", ids).and("u_listid", "=", listId);
            cache = utils.findFirst(Selector.from(UserInfo.class).where(whereBuilder));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return cache;
    }

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
            deleteAll(identity);
            utils.saveAll(userInfoList);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void deleteAll(String identity) {
        try {
            whereBuilder = WhereBuilder.b().and("identity", "=", identity);
            utils.delete(UserInfo.class, whereBuilder);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
