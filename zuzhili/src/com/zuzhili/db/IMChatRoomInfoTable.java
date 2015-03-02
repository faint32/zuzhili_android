package com.zuzhili.db;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.db.table.TableUtils;
import com.lidroid.xutils.exception.DbException;
import com.zuzhili.db.model.GroupChatInfo;

import java.util.List;

/**
 * Created by liutao on 14-4-22.
 * latest contacts table, this table only
 */
@Deprecated
public class IMChatRoomInfoTable extends Table {

    @Override
    public void upgrade(DbUtils db, Class clazz, int oldVersion, int newVersion){
        if(oldVersion < newVersion) {
            if (oldVersion < 2) {
                updateTable2(db);
            }
        }
    }

    private void updateTable2(DbUtils utils) {
        try {
            if (!checkColumnExist(utils.getDatabase(), TableUtils.getTableName(GroupChatInfo.class), "chatRoomType")) {
                utils.execNonQuery("ALTER TABLE " + TableUtils.getTableName(GroupChatInfo.class) + " ADD COLUMN chatRoomType TEXT ");
            }

            if (!checkColumnExist(utils.getDatabase(), TableUtils.getTableName(GroupChatInfo.class), "speakerJson")) {
                utils.execNonQuery("ALTER TABLE " + TableUtils.getTableName(GroupChatInfo.class) + " ADD COLUMN speakerJson TEXT ");
            }
            if (!checkColumnExist(utils.getDatabase(), TableUtils.getTableName(GroupChatInfo.class), "title")) {
                utils.execNonQuery("ALTER TABLE " + TableUtils.getTableName(GroupChatInfo.class) + " ADD COLUMN title TEXT ");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所有最近参与的聊天室信息
     * @param identity
     * @return
     */
    public List<GroupChatInfo> get(String identity){
        List<GroupChatInfo> cache = null;
        try {
            whereBuilder = WhereBuilder.b().and("identity", "=", identity);
            cache = utils.findAll(Selector.from(GroupChatInfo.class).where(whereBuilder));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return cache;
    }

    public void insert(GroupChatInfo groupChatInfo, String groupId, String identity) {
        try {
            whereBuilder = WhereBuilder.b().and("identity", "=", identity).and("groupId", "=", groupId);
            GroupChatInfo first = utils.findFirst(Selector.from(GroupChatInfo.class).where(whereBuilder));
            if (null != first) {
                utils.update(groupChatInfo, whereBuilder, shouldUpdateAll);
            } else {
                utils.save(groupChatInfo);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void update(List<GroupChatInfo> groupChatInfoList) {
        try {
            utils.replaceAll(groupChatInfoList);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void delete(String groupId, String identity) {
        try {
            whereBuilder = WhereBuilder.b().and("identity", "=", identity).and("groupId", "=", groupId);
            utils.delete(GroupChatInfo.class, whereBuilder);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
