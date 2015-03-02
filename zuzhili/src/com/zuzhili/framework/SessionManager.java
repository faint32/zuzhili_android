package com.zuzhili.framework;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;


/**
 * 观察者类，保存一些全局变量，数据发生变化后异步更新数据到sharedPreference文件
 * @Title: SessionManager.java
 * @author taoliuh@gmail.com 
 * @date 2013-12-19 下午8:56:44
 * @version 0.1
 */
public class SessionManager implements Observer {

    public static final String P_IS_AUTO_LOGIN = "pref.is.auto.Login";
    public static final String P_USERNAME = "pref.username";
    public static final String P_EMAIL = "pref.email";
    public static final String P_PASSWORD = "pref.rm.password";
    public static final String P_REMEMBER_PASSWORD = "pref.remember.password";
    public static final String P_UID = "pref.uid";
    public static final String P_LISTID = "pref.listid";
    public static final String P_IDS = "pref.ids";
    public static final String P_SOCIAL_NAME = "pref.social.name";
    public static final String P_SOCIAL_SHORT_NAME = "pref.social.short.name";
    public static final String P_CLEAR_CACHE = "auto_clear_cache";
    public static final String P_USER_COOKIES = "pref.cookies";

    public static final String P_UPDATE_AVAILABIE = "pref.update.available";
    public static final String P_UPDATE_DESC = "pref.update.desc";
    public static final String P_UPDATE_URI = "pref.update.uri";
    
    public static final String P_REGION = "pref.region";
    public static final String P_APP_TYPE = "pref.app.type";
    public static final String P_USER_HEAD = "pref.user.head";

    // 最新下载任务id
    public static final String P_UPDATE_ID = "pref.update.id";
    
	// version name
	public static final String P_CURRENT_VERSION = "pref.current.version";

    public static final String P_VOIP_ID = "pref.voip.id";
    public static final String P_VOIP_PASSWORD = "pref.voip.password";
    public static final String P_SUB_ACCOUNT = "pref.sub.account";
    public static final String P_SUB_TOKEN = "pref.sub.token";

    private static SessionManager mInstance;
    
    private SharedPreferences mPreference;
    private Context mContext;
	private LinkedList<Pair<String, Object>> mUpdateQueue = new LinkedList<Pair<String, Object>>();
	private Thread mCurrentUpdateThread;
    
    private SessionManager(Context context) {
        synchronized (this) {
            mContext = context;
            if (mPreference == null) {
                mPreference = PreferenceManager.getDefaultSharedPreferences(mContext);
            }
        }
    }
    
    public static SessionManager get(Context context) {
        if (mInstance == null) {
            mInstance = new SessionManager(context);
        }
        return mInstance;
    }
    
    private static final Method sApplyMethod = findApplyMethod();

    private static Method findApplyMethod() {
        try {
			Class<Editor> cls = Editor.class;
            return cls.getMethod("apply");
        } catch (NoSuchMethodException unused) {
            // fall through
        }
        return null;
    }

    /** Use this method to modify preference */
    public static void apply(Editor editor) {
        if (sApplyMethod != null) {
            try {
                sApplyMethod.invoke(editor);
                return;
            } catch (InvocationTargetException unused) {
                // fall through
            } catch (IllegalAccessException unused) {
                // fall through
            }
        }
        editor.commit();
    }
    
    
    /**
     * Release all resources
     */
    public void close() {
        mPreference = null;
        mInstance = null;
    }
    
    private boolean isPreferenceNull() {
        if(mPreference == null) 
            return true;
        return false;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void update(Observable observable, Object data) {
    	if(data instanceof Pair) {
    		synchronized (mUpdateQueue) {
                if (data != null) {
                    mUpdateQueue.add((Pair<String, Object>) data);
                }
            }
    		writePreferenceSlowly();
    	}
    };

	/*
	 * Do Hibernation slowly
	 */
	public void writePreferenceSlowly() {
		if (mCurrentUpdateThread != null) {
			if (mCurrentUpdateThread.isAlive()) {
				// the update thread is still running, 
				// so no need to start a new one
				return;
			}
		}
		
		// update the seesion value back to preference
		// ATTENTION: some more value will be add to the queue while current task is running
		mCurrentUpdateThread = new Thread() {
			
			@Override
			public void run() {
				
				try {
                    // sleep 10secs to wait some concurrent task be
                    // inserted into the task queue
                    sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
				
				writePreference();
			}
			
		};
		mCurrentUpdateThread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		mCurrentUpdateThread.start();
	}
	
	/*
	 * Do Hibernation immediately
	 */
	public void writePreferenceQuickly() {
		
		// update the seesion value back to preference
		// ATTENTION: some more value will be add to the queue while current task is running
		mCurrentUpdateThread = new Thread() {
			
			@Override
			public void run() {
				writePreference();
			}
			
		};
		mCurrentUpdateThread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		mCurrentUpdateThread.start();
	}
	
	/**
	 * Write session value back to preference
	 */
	private void writePreference() {
		
		Editor editor = mPreference.edit();

		synchronized (mUpdateQueue) {
			while (!mUpdateQueue.isEmpty()) {

				// remove already unused reference from the task queue
				Pair<String, Object> updateItem = mUpdateQueue.remove();

				// the preference key
				final String key = (String) updateItem.first;

                //TODO
				if (P_UID.equals(key) || P_USERNAME.equals(key)
                        || P_EMAIL.equals(key)
						|| P_PASSWORD.equals(key)
                        || P_LISTID.equals(key)
						|| P_IDS.equals(key)
                        || P_SOCIAL_NAME.equals(key)
                        || P_SOCIAL_SHORT_NAME.equals(key)
						|| P_REGION.equals(key)
						|| P_APP_TYPE.equals(key)
                        || P_VOIP_ID.equals(key)
                        || P_VOIP_PASSWORD.equals(key)
                        || P_SUB_ACCOUNT.equals(key)
                        || P_USER_HEAD.equals(key)
                        || P_SUB_TOKEN.equals(key)) {
					editor.putString(key,
                            String.valueOf(updateItem.second));
				}if (P_IS_AUTO_LOGIN.equals(key)
						|| P_UPDATE_AVAILABIE.equals(key)) {
					editor.putBoolean(key, (Boolean) updateItem.second);
				}
			}
		}
		// update the preference
		apply(editor);
	}
	
	public HashMap<String, Object> readPreference() {
		
		if (isPreferenceNull()) {
            return null;
        }

        // userid, username, email, password
		HashMap<String, Object> data = new HashMap<String, Object>();
		String uid = mPreference.getString(P_UID, "");
		data.put(P_UID, uid);
		data.put(P_IS_AUTO_LOGIN, mPreference.getBoolean(P_IS_AUTO_LOGIN, false));
		String username = mPreference.getString(P_USERNAME, "");
		data.put(P_USERNAME, username);
        data.put(P_EMAIL, mPreference.getString(P_EMAIL, ""));
		String password = mPreference.getString(P_PASSWORD, "");
		data.put(P_PASSWORD, password);
		data.put(P_CLEAR_CACHE, mPreference.getBoolean(P_CLEAR_CACHE, false));
        
        // listid, ids, social name
        data.put(P_LISTID, mPreference.getString(P_LISTID, ""));
        data.put(P_IDS, mPreference.getString(P_IDS, ""));
        data.put(P_SOCIAL_NAME, mPreference.getString(P_SOCIAL_NAME, ""));
        data.put(P_SOCIAL_SHORT_NAME, mPreference.getString(P_SOCIAL_SHORT_NAME, ""));

		// update info
		data.put(P_UPDATE_AVAILABIE, mPreference.getBoolean(P_UPDATE_AVAILABIE, false));
		data.put(P_UPDATE_DESC, mPreference.getString(P_UPDATE_DESC, ""));
		data.put(P_UPDATE_URI, mPreference.getString(P_UPDATE_URI, ""));
		data.put(P_UPDATE_ID, mPreference.getLong(P_UPDATE_ID, -1));
		
		// current version
		data.put(P_CURRENT_VERSION, mPreference.getInt(P_CURRENT_VERSION, -1));

        // news region and app type
        data.put(P_REGION, mPreference.getString(P_REGION, "1")); // 默认筛选我关注的
        data.put(P_APP_TYPE, mPreference.getString(P_APP_TYPE, "0")); // 默认筛选全部动态

        data.put(P_VOIP_ID, mPreference.getString(P_VOIP_ID, "0"));
        data.put(P_VOIP_PASSWORD, mPreference.getString(P_VOIP_PASSWORD, "0"));
        data.put(P_SUB_ACCOUNT, mPreference.getString(P_SUB_ACCOUNT, "0"));
        data.put(P_SUB_TOKEN, mPreference.getString(P_SUB_TOKEN, "0"));
        data.put(P_USER_HEAD, mPreference.getString(P_USER_HEAD, ""));
        return data;
	}
}
