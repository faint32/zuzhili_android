package com.zuzhili.framework;

import com.hisun.phone.core.voice.model.im.InstanceMsg;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.CCPUtil;
import com.zuzhili.db.DBHelper;
import com.zuzhili.exception.CrashHandler;
import com.zuzhili.framework.http.RequestManager;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.framework.images.ImageCacheManager.CacheType;
import com.zuzhili.model.im.DemoAccounts;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class TaskApp extends Application {

	/** Default disk image cache size */
	private static int DISK_IMAGECACHE_SIZE = 1024*1024*10;
	
	/** Default disk image cache compress format */
	private static CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = CompressFormat.PNG;
	
	/** PNG is lossless so quality is ignored but must be provided */
	private static int DISK_IMAGECACHE_QUALITY = 100;

    private static Stack<Activity> activityStack;

    private Activity activity = null;

    private Handler handler = new Handler();

	private DBHelper dbHelper;


    /**------------------------------------------------------------*/
    private static TaskApp instance;
    private DemoAccounts accounts;
    private File vStore;

    public Map<String, SoftReference<Bitmap>> mPhotoOriginalCache = new HashMap<String, SoftReference<Bitmap>>();

	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}
	
	private void init() {
		RequestManager.init(this);
        // Create the image cache. Uses Memory Cache by default. Change to Disk for a Disk based LRU implementation.
        ImageCacheManager.getInstance().init(this,
				this.getPackageCodePath()
				, DISK_IMAGECACHE_SIZE
				, DISK_IMAGECACHE_COMPRESS_FORMAT
				, DISK_IMAGECACHE_QUALITY
				, CacheType.MEMORY);
        Task.init();

//        CrashHandler.getInstance().init(getApplicationContext());
        dbHelper = DBHelper.getInstance(getApplicationContext());
        dbHelper.createDB();

        instance = this;
	}

	public DBHelper getDbHelper(){
		return dbHelper;
	}

    /**
     * add Activity 添加Activity到栈
     */
    public void addActivity(Activity activity){
        if(activityStack ==null){
            activityStack =new Stack<Activity>();
        }
        activityStack.add(activity);
    }
    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }
    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    public Activity getActivity() {
        return activity;
    }

    public Handler getUIHandler() {
        return handler;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void exitApp() {
        try {
            finishAllActivity();
        } catch (Exception e) {
        }
    }

    /**------------------------------------------------------------*/
    public static TaskApp getInstance() {
        return instance;
    }

    /**
     * User-Agent
     *
     * @return user-agent
     */
    public String getUser_Agent() {
        String ua = "Android;" + getOSVersion() + ";" + getVersion() + ";"
                + getVendor() + "-" + getDevice();

        return ua;
    }

    /**
     * @return the OS version
     */
    public String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取版本
     *
     * @return versionName
     */
    public String getVersion() {
        String version = "0.0.0";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(
                    getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }

    /**
     * device factory name, e.g: Samsung
     *
     * @return the vENDOR
     */
    public String getVendor() {
        return Build.BRAND;
    }

    /**
     * device model name, e.g: GT-I9100
     *
     * @return the user_Agent
     */
    public String getDevice() {
        return Build.MODEL;
    }

    /**
     * 存储语音
     */
    private void initFileStore() {
        if (!CCPUtil.isExistExternalStore()) {
            Toast.makeText(getApplicationContext(), R.string.media_ejected,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        File directory = new File(Environment.getExternalStorageDirectory(),
                CCPUtil.DEMO_ROOT_STORE);
        if (!directory.exists() && !directory.mkdirs()) {
//            Toast.makeText(getApplicationContext(),
//                    R.string.pathFile, Toast.LENGTH_SHORT)
//                    .show();
            return;
        }
        vStore = directory;
    }

    /**
     * 声音
     * @return
     */
    public File getVoiceStore() {
        if(vStore == null || vStore.exists()) {
            initFileStore();
        }
        return vStore;
    }

    public void putDemoAccounts(DemoAccounts demoAccounts) {
        accounts = demoAccounts;
    }

    public static HashMap<String, InstanceMsg> rMediaMsgList = new HashMap<String, InstanceMsg>();

    public InstanceMsg getMediaData(String key) {
        if (key != null) {
            return rMediaMsgList.get(key);
        } else {
            return null;
        }
    }

    /**
     * @param key
     * @paramlist
     */
    public void putMediaData(String key, InstanceMsg obj) {
        if (key != null && obj != null) {
            rMediaMsgList.put(key, obj);
        }
    }

    public void removeMediaData(String key) {
        if (key != null) {
            rMediaMsgList.remove(key);
        }
    }

}
