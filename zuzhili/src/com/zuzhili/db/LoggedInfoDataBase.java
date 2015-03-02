package com.zuzhili.db;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

/**
 * Created by liutao on 14-3-3.
 */
public class LoggedInfoDataBase {
    private DbUtils utils;

    private WhereBuilder whereBuilder;

    public void setDbUtils(DbUtils utils){
        this.utils = utils;
    }

    public DbUtils getDbUtils(){
        return utils;
    }


    public void create(DbUtils utils){
        this.utils = utils;
        try {
            utils.createTableIfNotExist(DBCache.class);
            utils.configAllowTransaction(true);
            utils.configDebug(true);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void upgrade(DbUtils db, int oldVersion, int newVersion){

    }

    public LoggedInfo getLoggedInfo(String listid, String ids){
        LoggedInfo loggedInfo = null;
        try {
            whereBuilder = WhereBuilder.b().and("listid", "=", listid).and("ids", "=", ids);
            loggedInfo = utils.findFirst(Selector.from(LoggedInfo.class).where(whereBuilder));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return loggedInfo;
    }

    public void insertLoggedInfo(LoggedInfo loggedInfo){
        try {
            if (null != getLoggedInfo(loggedInfo.getListid(), loggedInfo.getIds())) {
                utils.update(loggedInfo, whereBuilder);
            } else {
                utils.save(loggedInfo);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
