package com.zuzhili.db;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;

public class CacheDataBase extends Table {

	public void upgrade(DbUtils db, int oldVersion, int newVersion){

	}
	
	public DBCache getCacheData(String cacheType, String identify){
		DBCache cache = null;
		try {
            whereBuilder = WhereBuilder.b().and("cachetype", "=", cacheType).and("identify", "=", identify);
			cache = utils.findFirst(Selector.from(DBCache.class).where(whereBuilder));
		} catch (DbException e) {
			e.printStackTrace();
		}
		return cache;
	}
	
	public void insertCacheData(DBCache cache){
		try {
            if (null != getCacheData(cache.getCachetype(), cache.getIdentify())) {
                utils.update(cache, whereBuilder, shouldUpdateAll);
                LogUtils.e("update json: " + getCacheData(cache.getCachetype(), cache.getIdentify()).getJsondata());
            } else {
                utils.save(cache);
                LogUtils.e("save json: " + getCacheData(cache.getCachetype(), cache.getIdentify()).getJsondata());
            }
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
}
