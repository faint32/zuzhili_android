package com.zuzhili.db;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.db.table.TableUtils;
import com.lidroid.xutils.exception.DbException;
import com.zuzhili.db.model.Speech;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by liutao on 14-5-6.
 */
@Deprecated
public class IMChatHistoryTable extends Table {

    @Override
    public void upgrade(DbUtils db, Class clazz, int oldVersion, int newVersion) {
        if(oldVersion < newVersion){
            if (oldVersion < 2) {
                upgradeTable2(db);
            }
        }
    }

    private void upgradeTable2(DbUtils dbUtils) {
        try {
            if (!checkColumnExist(dbUtils.getDatabase(), TableUtils.getTableName(Speech.class), "sessionId")) {
                dbUtils.execNonQuery("ALTER TABLE " + TableUtils.getTableName(Speech.class) + " ADD COLUMN sessionId TEXT ");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    public List<Speech> get(String identity, String sessionId, String groupType, int limit, int offset){
        List<Speech> cache = null;
        try {
            whereBuilder = WhereBuilder.b().and("identity", "=", identity).and("sessionId", "=", sessionId).and("groupType", "=", groupType);
            cache = utils.findAll(Selector.from(Speech.class).where(whereBuilder).orderBy("time", true).limit(limit).offset(offset));
            Collections.sort(cache, new Comparator<Speech>() {
                @Override
                public int compare(Speech lhs, Speech rhs) {
                    return (int) (lhs.getTime() - rhs.getTime());
                }
            });
        } catch (DbException e) {
            e.printStackTrace();
        }
        return cache;
    }

    public List<Speech> getAll() {
        List<Speech> cache = null;
        try {
            cache = utils.findAll(Selector.from(Speech.class));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return cache;
    }

    public void insert(Speech speech) {
        try {
            utils.save(speech);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void insert(List<Speech> speechList) {
        try {
            utils.saveAll(speechList);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void delete(String identity, String groupId) {
        try {
            whereBuilder = WhereBuilder.b().and("identity", "=", identity).and("groupId", "=", groupId);
            utils.delete(Speech.class, whereBuilder);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
