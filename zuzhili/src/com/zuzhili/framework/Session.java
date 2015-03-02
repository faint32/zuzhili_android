package com.zuzhili.framework;

import java.util.HashMap;
import java.util.List;
import java.util.Observable;

import com.zuzhili.bussiness.socket.model.GroupInfo;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.Account;
import com.zuzhili.service.GetMsgService;

import static com.zuzhili.framework.SessionManager.P_CLEAR_CACHE;
import static com.zuzhili.framework.SessionManager.P_CURRENT_VERSION;
import static com.zuzhili.framework.SessionManager.P_IS_AUTO_LOGIN;
import static com.zuzhili.framework.SessionManager.P_EMAIL;
import static com.zuzhili.framework.SessionManager.P_PASSWORD;
import static com.zuzhili.framework.SessionManager.P_SOCIAL_SHORT_NAME;
import static com.zuzhili.framework.SessionManager.P_USERNAME;
import static com.zuzhili.framework.SessionManager.P_UID;
import static com.zuzhili.framework.SessionManager.P_LISTID;
import static com.zuzhili.framework.SessionManager.P_IDS;
import static com.zuzhili.framework.SessionManager.P_SOCIAL_NAME;
import static com.zuzhili.framework.SessionManager.P_UPDATE_AVAILABIE;
import static com.zuzhili.framework.SessionManager.P_UPDATE_DESC;
import static com.zuzhili.framework.SessionManager.P_UPDATE_ID;
import static com.zuzhili.framework.SessionManager.P_UPDATE_URI;
import static com.zuzhili.framework.SessionManager.P_REGION;
import static com.zuzhili.framework.SessionManager.P_APP_TYPE;
import static com.zuzhili.framework.SessionManager.P_VOIP_ID;
import static com.zuzhili.framework.SessionManager.P_VOIP_PASSWORD;
import static com.zuzhili.framework.SessionManager.P_SUB_ACCOUNT;
import static com.zuzhili.framework.SessionManager.P_SUB_TOKEN;
import static com.zuzhili.framework.SessionManager.P_USER_HEAD;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.util.SparseBooleanArray;

import org.json.JSONObject;

/**
 * 
 * @Title: Session.java
 * @Description: client session object, extends Observable, 序列化相关信息
 * @author taoliuh@gmail.com
 * @date 2013-12-19 下午5:55:51
 * @version 0.1
 */
public class Session extends Observable {
	/** Session instance object */
	private static Session mInstance;

	/** log tag */
	private final String TAG = getClass().getSimpleName();

	/** Application Context */
	private Context mContext;

	/** The application debug mode */
	public boolean isDebug;

    /** auto login flag */
	private boolean isAutoLogin;

    /** 是否切换社区 */
    private SparseBooleanArray shouldUpdateUIArray;

	private Account account;

	/** Indicate whether auto clear cache when user exit */
	private boolean isAutoClearCache;

	/** The userid */
	private String uid;

	/** The listid */
	private String listid;

    /** 当前已登陆社区名称 */
    private String socialName;

    /** 当前已登陆社区简称 */
    private String socialShortName;

	/** 用户在每个社区唯一的ids */
	private String ids;

	/** 用户在当前社区的名称 */
	private String userName;

    /** user login account */
    private String email;

	/** The user login password */
	private String password;

	/** The Application Version Code */
	private int versionCode;

	/** The Application package name */
	private String packageName;

	/** The Application version name */
	private String versionName;

	/** The Application version name */
	private String appName;

	/** Indicate whether new version is available */
	private boolean isUpdateAvailable;

	/** The new version desc */
    private String updateVersionDesc;
    
    /** The new version update uri */
    private String updateUri;
    
    /** The new version APK download task id*/
    private long updateId;
    
    /** The current version */
    private int lastVersion;
    
    /** The Application Debug flag */
    private String debugType;
    
    /** 动态一级筛选条件, "1"表示我关注的， "2"表示全部动态 */
    private String region;

    /** 动态二级筛选条件，"0"全部 "3"文字 "6"图片 "9"文件 "16"音频 "19"视频 */
    private String appType;

    /** 筛选的json*/
    private JSONObject typeJson;

    private String userhead;

    /** spaceActivity launchMode is singleTask, this variable indicates whether activity is instantiated or not */
    private boolean isSpaceActivityInstantiated;

    private GetMsgService.ServiceBinder binder;

    private String currentActivityName;

    private UserInfo mySelfInfo;

    private List<GroupInfo> groupInfoList;

    /** 云通讯voip id */
    private String voipId;

    /** 云通讯 voip password */
    private String voipPassword;

    /** 子帐号 */
    private String subAccount;

    /** 子帐号 token */
    private String subToken;

	/** Session Manager */
    private SessionManager mSessionManager;

    /** 是否已登陆云通讯 */
    private boolean isChatLogin;

    /** 当前会话ID */
    private String y_groupId;

    private String sessionId;

    private Session(Context context) {
    	this.mContext = context;
        shouldUpdateUIArray = new SparseBooleanArray();
        shouldUpdateUIArray.put(Constants.PAGE_TREND, false);
        shouldUpdateUIArray.put(Constants.PAGE_AT_CONTENT, false);
        shouldUpdateUIArray.put(Constants.PAGE_AT_COMMENT, false);
        shouldUpdateUIArray.put(Constants.PAGE_COMMENT_RECEIVE, false);
        shouldUpdateUIArray.put(Constants.PAGE_COMMENT_SEND, false);
        shouldUpdateUIArray.put(Constants.PAGE_CHAT, false);
        shouldUpdateUIArray.put(Constants.PAGE_MEMBERS_ALL, false);
        shouldUpdateUIArray.put(Constants.PAGE_MEMBERS_FOCUS, false);
        shouldUpdateUIArray.put(Constants.PAGE_MEMBERS_CONTACT, false);
        shouldUpdateUIArray.put(Constants.PAGE_APPROVAL_SEND, false);
        shouldUpdateUIArray.put(Constants.PAGE_APPROVAL_RECEIVE, false);
        shouldUpdateUIArray.put(Constants.PAGE_PERSONAL_SPACE, false);
    	readSettings();
    }
    
    /**
     * get instance of Session Object
     * @param context
     * @return
     */
    public static Session get(Context context) {
        if (mInstance == null) {
            mInstance = new Session(context);
        }
        return mInstance;
    }
    
    /**
     * 获取应用包相关信息
     */
    private void getApplicationInfo() {
        
        final PackageManager pm = (PackageManager) mContext.getPackageManager();
        try {
            final PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), 0);
            versionName = pi.versionName;
            versionCode = pi.versionCode;
            
            final ApplicationInfo ai = pm.getApplicationInfo(mContext.getPackageName(),
                                                             PackageManager.GET_META_DATA);
            debugType = ai.metaData.get("debug").toString();
            
            if ("1".equals(debugType)) {
                // developer mode
                isDebug = true;
            } else if ("0".equals(debugType)) {
                // release mode
                isDebug = false;
            }
            Utils.sDebug = isDebug;
            
            appName = String.valueOf(ai.loadLabel(pm));
            Utils.sLogTag = appName;
            packageName = mContext.getPackageName();
            
        } catch (NameNotFoundException e) {
            Log.d(TAG, "met some error when get application info");
        }
    }

	/**
	 * 读配置文件信息
	 */
	private void readSettings(){
//		new Thread() {
//			public void run() {
//
//			};
//		}.start();

        mSessionManager = SessionManager.get(mContext);
        addObserver(mSessionManager);
        HashMap<String, Object> preference = mSessionManager
                .readPreference();
        uid = (String) preference.get(P_UID);
        isAutoLogin = (Boolean) preference.get(P_IS_AUTO_LOGIN);
        isAutoClearCache = (Boolean) preference.get(P_CLEAR_CACHE);
        userName = (String) preference.get(P_USERNAME);
        email = (String) preference.get(P_EMAIL);
        password = (String) preference.get(P_PASSWORD);
        updateId = (Long) preference.get(P_UPDATE_ID);
        listid = (String) preference.get(P_LISTID);
        ids = (String) preference.get(P_IDS);

        lastVersion = (Integer) preference.get(P_CURRENT_VERSION);

        isUpdateAvailable = (Boolean) preference
                .get(P_UPDATE_AVAILABIE);
        updateVersionDesc = (String) preference.get(P_UPDATE_DESC);
        updateUri = (String) preference.get(P_UPDATE_URI);


        region = (String) preference.get(P_REGION);
        appType = (String) preference.get(P_APP_TYPE);
        
        socialName = (String) preference.get(P_SOCIAL_NAME);
        socialShortName = (String) preference.get(P_SOCIAL_SHORT_NAME);

        voipId = (String) preference.get(P_VOIP_ID);
        voipPassword = (String) preference.get(P_VOIP_PASSWORD);
        subAccount = (String) preference.get(P_SUB_ACCOUNT);
        subToken = (String) preference.get(P_SUB_TOKEN);

        getApplicationInfo();
	}

	public boolean isAutoLogin() {
		return isAutoLogin;
	}

	public void setAutoLogin(boolean isAutoLogin) {
		if (this.isAutoLogin == isAutoLogin) {
			return;
		}
		this.isAutoLogin = isAutoLogin;
		super.setChanged();
		super.notifyObservers(new Pair<String, Boolean>(P_IS_AUTO_LOGIN, isAutoLogin));
	}

    /**
     * when user instantiate a fragment of HomeTabActivity, should check the flag to decide
     * to update the ui especially when social changes.
     */
    public boolean isUIShouldUpdate(int key) {
        return shouldUpdateUIArray.get(key, false);
    }

    /**
     * ui update
     */
    public void setUIShouldUpdate(int key) {
        shouldUpdateUIArray.put(key, true);
    }

    /**
     * when social change, and the user navigates to the page, after ui
     * updates, change the flag to false.
     */
    public void resetUIShouldUpdateFlag(int key) {
        shouldUpdateUIArray.put(key, false);
    }

    /**
     * when user navigates to other social, should change all fragments of HomeTabActivity
     * flag to true
     */
    public void onSocialChanged() {
        shouldUpdateUIArray.put(Constants.PAGE_TREND, true);
        shouldUpdateUIArray.put(Constants.PAGE_AT_CONTENT, true);
        shouldUpdateUIArray.put(Constants.PAGE_AT_COMMENT, true);
        shouldUpdateUIArray.put(Constants.PAGE_COMMENT_RECEIVE, true);
        shouldUpdateUIArray.put(Constants.PAGE_COMMENT_SEND, true);
        shouldUpdateUIArray.put(Constants.PAGE_CHAT, true);
        shouldUpdateUIArray.put(Constants.PAGE_MEMBERS_ALL, true);
        shouldUpdateUIArray.put(Constants.PAGE_MEMBERS_FOCUS, true);
        shouldUpdateUIArray.put(Constants.PAGE_MEMBERS_CONTACT, true);
        shouldUpdateUIArray.put(Constants.PAGE_APPROVAL_SEND, true);
        shouldUpdateUIArray.put(Constants.PAGE_APPROVAL_RECEIVE, true);
        shouldUpdateUIArray.put(Constants.PAGE_PERSONAL_SPACE, true);
    }

    public boolean isAutoClearCache() {
		return isAutoClearCache;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
		super.setChanged();
		super.notifyObservers(new Pair<String, String>(P_UID, uid));
	}

	public String getListid() {
		return listid;
	}

	public void setListid(String listid) {
		this.listid = listid;
		super.setChanged();
		super.notifyObservers(new Pair<String, String>(P_LISTID, listid));
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
		super.setChanged();
		super.notifyObservers(new Pair<String, String>(P_IDS, ids));
	}
    public String getSocialName() {
        return socialName;
    }

    public void setSocialName(String socialName) {
        this.socialName = socialName;
        super.setChanged();
        super.notifyObservers(new Pair<String, String>(P_SOCIAL_NAME, socialName));
    }

    public String getSocialShortName() {
        return socialShortName;
    }

    public void setSocialShortName(String socialShortName) {
        this.socialShortName = socialShortName;
        super.setChanged();
        super.notifyObservers(new Pair<String, String>(P_SOCIAL_SHORT_NAME, socialShortName));
    }

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
		super.setChanged();
		super.notifyObservers(new Pair<String, String>(P_USERNAME, userName));
	}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        super.setChanged();
        super.notifyObservers(new Pair<String, String>(P_EMAIL, email));
    }

    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
		super.setChanged();
		super.notifyObservers(new Pair<String, String>(P_PASSWORD, password));
	}

	public int getVersionCode() {
		return versionCode;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getVersionName() {
		return versionName;
	}

	public boolean isUpdateAvailable() {
		return isUpdateAvailable;
	}

	public String getUpdateVersionDesc() {
		return updateVersionDesc;
	}

	public String getUpdateUri() {
		return updateUri;
	}

	public void setUpdateInfo(String description, String url) {

		this.isUpdateAvailable = true;
		this.updateVersionDesc = description;
		this.updateUri = url;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(P_UPDATE_AVAILABIE, true));
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(P_UPDATE_DESC,
				description));
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(P_UPDATE_URI, url));
	}

	public long getUpdateId() {
		return updateId;
	}

	public void setUpdateId(long updateId) {
		if (this.updateId == updateId) {
			return;
		}

		this.updateId = updateId;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(P_UPDATE_ID, updateId));
	}

	public int getLastVersion() {
		return lastVersion;
	}

	public void setLastVersion(int lastVersion) {
		this.lastVersion = lastVersion;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
        this.account = account;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getRegion() {
		return region;
	}

    public void setRegion(String region) {
        this.region = region;
        super.setChanged();
        super.notifyObservers(new Pair<String, Object>(P_REGION, region));
    }

    public String getAppType() {
            return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
        super.setChanged();
        super.notifyObservers(new Pair<String, Object>(P_APP_TYPE, appType));
    }

    public String getUserhead() {
        return userhead;
    }

    public void setUserhead(String userhead) {
        this.userhead = userhead;
        super.setChanged();
        super.notifyObservers(new Pair<String, Object>(P_USER_HEAD, userhead));
    }

    public boolean isSpaceActivityInstantiated() {
        return isSpaceActivityInstantiated;
    }

    public void setSpaceActivityInstantiated(boolean isSpaceActivityInstantiated) {
        this.isSpaceActivityInstantiated = isSpaceActivityInstantiated;
    }

    public GetMsgService.ServiceBinder getBinder() {
        return binder;
    }

    public void setBinder(GetMsgService.ServiceBinder binder) {
        this.binder = binder;
    }

    public String getCurrentActivityName() {
        return currentActivityName;
    }

    public void setCurrentActivityName(String currentActivityName) {
        this.currentActivityName = currentActivityName;
    }

    public UserInfo getMySelfInfo() {
        return mySelfInfo;
    }

    public void setMySelfInfo(UserInfo mySelfInfo) {
        this.mySelfInfo = mySelfInfo;
    }

    public List<GroupInfo> getGroupInfoList() {
        return groupInfoList;
    }

    public void setGroupInfoList(List<GroupInfo> groupInfoList) {
        this.groupInfoList = groupInfoList;
    }

    public GroupInfo getGroupInfoById(String groupId) {
        if (groupInfoList != null) {
            for (GroupInfo item : groupInfoList) {
                if (item.getId().equals(groupId)) {
                    return item;
                }
            }
        }
        return null;
    }

    public String getVoipId() {
        return voipId;
    }

    public void setVoipId(String voipId) {
        this.voipId = voipId;
        super.setChanged();
        super.notifyObservers(new Pair<String, Object>(P_VOIP_ID, voipId));
    }

    public String getVoipPassword() {
        return voipPassword;
    }

    public void setVoipPassword(String voipPassword) {
        this.voipPassword = voipPassword;
        super.setChanged();
        super.notifyObservers(new Pair<String, Object>(P_VOIP_PASSWORD, voipId));
    }

    public String getSubAccount() {
        return subAccount;
    }

    public void setSubAccount(String subAccount) {
        this.subAccount = subAccount;
        super.setChanged();
        super.notifyObservers(new Pair<String, Object>(P_SUB_ACCOUNT, voipId));
    }

    public String getSubToken() {
        return subToken;
    }

    public void setSubToken(String subToken) {
        this.subToken = subToken;
        super.setChanged();
        super.notifyObservers(new Pair<String, Object>(P_SUB_TOKEN, voipId));
    }

    public boolean isChatLogin() {
        return isChatLogin;
    }

    public void setChatLogin(boolean isChatLogin) {
        this.isChatLogin = isChatLogin;
    }

    public String getY_groupId() {
        return y_groupId;
    }

    public void setY_groupId(String y_groupId) {
        this.y_groupId = y_groupId;
    }
}
