package com.zuzhili.framework.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.http.HttpHost;

import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.TaskApp;


import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author taoliuh@gmail.com
 * @version 0.1
 * @Title: Utils.java
 * @Description: Common Utils for the application
 * @date 2013-12-19 下午6:09:34
 */
public class Utils {

    public static boolean sDebug;
    public static String sLogTag;

    private static final String TAG = "Utils";

    // UTF-8 encoding
    private static final String ENCODING_UTF8 = "UTF-8";

    private static WeakReference<Calendar> calendar;

    /**
     * <p>
     * Get UTF8 bytes from a string
     * </p>
     *
     * @param string String
     * @return UTF8 byte array, or null if failed to get UTF8 byte array
     */
    public static byte[] getUTF8Bytes(String string) {
        if (string == null)
            return new byte[0];

        try {
            return string.getBytes(ENCODING_UTF8);
        } catch (UnsupportedEncodingException e) {
            /*
             * If system doesn't support UTF-8, use another way
             */
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);
                dos.writeUTF(string);
                byte[] jdata = bos.toByteArray();
                bos.close();
                dos.close();
                byte[] buff = new byte[jdata.length - 2];
                System.arraycopy(jdata, 2, buff, 0, buff.length);
                return buff;
            } catch (IOException ex) {
                return new byte[0];
            }
        }
    }

    /**
     * <p>
     * Get string in UTF-8 encoding
     * </p>
     *
     * @param b byte array
     * @return string in utf-8 encoding, or empty if the byte array is not encoded with UTF-8
     */
    public static String getUTF8String(byte[] b) {
        if (b == null)
            return "";
        return getUTF8String(b, 0, b.length);
    }

    /**
     * <p>
     * Get string in UTF-8 encoding
     * </p>
     */
    public static String getUTF8String(byte[] b, int start, int length) {
        if (b == null) {
            return "";
        } else {
            try {
                return new String(b, start, length, ENCODING_UTF8);
            } catch (UnsupportedEncodingException e) {
                return "";
            }
        }
    }

    /**
     * <p>
     * Parse int value from string
     * </p>
     *
     * @param value string
     * @return int value
     */
    public static int getInt(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        }

        try {
            return Integer.parseInt(value.trim(), 10);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * <p>
     * Parse float value from string
     * </p>
     *
     * @param value string
     * @return float value
     */
    public static float getFloat(String value) {
        if (value == null)
            return 0f;

        try {
            return Float.parseFloat(value.trim());
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

    /**
     * <p>
     * Parse long value from string
     * </p>
     *
     * @param value string
     * @return long value
     */
    public static long getLong(String value) {
        if (value == null)
            return 0L;

        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    public static void V(String msg) {
        if (sDebug) {
            Log.v(sLogTag, msg);
        }
    }

    public static void V(String msg, Throwable e) {
        if (sDebug) {
            Log.v(sLogTag, msg, e);
        }
    }

    public static void D(String msg) {
        if (sDebug) {
            Log.d(sLogTag, msg);
        }
    }

    public static void D(String msg, Throwable e) {
        if (sDebug) {
            Log.d(sLogTag, msg, e);
        }
    }

    public static void I(String msg) {
        if (sDebug) {
            Log.i(sLogTag, msg);
        }
    }

    public static void I(String msg, Throwable e) {
        if (sDebug) {
            Log.i(sLogTag, msg, e);
        }
    }

    public static void W(String msg) {
        if (sDebug) {
            Log.w(sLogTag, msg);
        }
    }

    public static void W(String msg, Throwable e) {
        if (sDebug) {
            Log.w(sLogTag, msg, e);
        }
    }

    public static void E(String msg) {
        if (sDebug) {
            Log.e(sLogTag, msg);
        }
    }

    public static void E(String msg, Throwable e) {
        if (sDebug) {
            Log.e(sLogTag, msg, e);
        }
    }

    public static String formatDate(long time) {
        if (calendar == null || calendar.get() == null) {
            calendar = new WeakReference<Calendar>(Calendar.getInstance());
        }
        Calendar target = calendar.get();
        target.setTimeInMillis(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(target.getTime());
    }

    public static String getTodayDate() {
        if (calendar == null || calendar.get() == null) {
            calendar = new WeakReference<Calendar>(Calendar.getInstance());
        }
        Calendar today = calendar.get();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(today.getTime());
    }

    /**
     * Returns whether the network is available
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Log.w(TAG, "couldn't get connectivity manager");
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0, length = info.length; i < length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Returns whether the network is roaming
     */
    public static boolean isNetworkRoaming(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            // Log.w(Constants.TAG, "couldn't get connectivity manager");
        } else {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            } else {
            }
        }
        return false;
    }


    /**
     * 格式化时间（Format：yyyy-MM-dd HH:mm）
     *
     * @param timeInMillis
     * @return
     */
    public static String formatTime(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(new Date(timeInMillis));
    }


    /**
     * 文件拷贝工具类
     *
     * @param in  源文件
     * @param dst 目标文件
     * @throws IOException
     */
    public static void copyFile(InputStream in, FileOutputStream dst) throws IOException {
        byte[] buffer = new byte[8192];
        int len = 0;
        while ((len = in.read(buffer)) > 0) {
            dst.write(buffer, 0, len);
        }
        in.close();
        dst.close();
    }

    /**
     * 界面切换动画
     *
     * @return
     */
    public static LayoutAnimationController getLayoutAnimation() {
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(50);
        set.addAnimation(animation);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(100);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        return controller;
    }

    /**
     * Get MD5 Code
     */
    public static String getMD5(String text) {
        try {
            byte[] byteArray = text.getBytes("utf8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(byteArray, 0, byteArray.length);
            return convertToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Convert byte array to Hex string
     */
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    /**
     * Check whether the SD card is readable
     */
    public static boolean isSdcardReadable() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)
                || Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Check whether the SD card is writable
     */
    public static boolean isSdcardWritable() {
        final String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    /**
     * 检查默认Proxy
     */
    public static HttpHost detectProxy(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null
                && ni.isAvailable()
                && ni.getType() == ConnectivityManager.TYPE_MOBILE) {
            String proxyHost = android.net.Proxy.getDefaultHost();
            int port = android.net.Proxy.getDefaultPort();
            if (proxyHost != null) {
                return new HttpHost(proxyHost, port, "http");
            }
        }
        return null;
    }

    /**
     * Android 安装应用
     *
     * @param context Application Context
     */
    public static void installApk(Context context, File file) {
        if (file.exists()) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            ((ContextWrapper) context).startActivity(i);
        } else {
            makeEventToast(context, context.getString(R.string.install_fail_file_not_exist), false);
        }
    }

    /**
     * 删除安装包
     */
    public static boolean deleteFile(String file) {
        File realFile = new File(file);
        return realFile.delete();
    }

    /**
     * 清除缓存
     *
     * @param context
     */
    public static void clearCache(Context context) {
        File file = Environment.getDownloadCacheDirectory();
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
        file = context.getCacheDir();
        files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    /**
     * Show toast information
     *
     * @param context application context
     * @param text    the information which you want to show
     * @return show toast dialog
     */
    public static void makeEventToast(Context context, String text, boolean isLongToast) {

        Toast toast = null;
        if (isLongToast) {
            toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        } else {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        }
//        View v = LayoutInflater.from(context).inflate(R.layout.toast_view, null);
//        TextView textView = (TextView) v.findViewById(R.id.text);
//        textView.setText(text);
//        toast.setView(v);
        toast.show();
    }

    /**
     * filter chinese characters
     *
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static boolean isContainChineseCharacters(String str) throws PatternSyntaxException {
        String regEx = "^[\u300a\u300b]|[\u4e00-\u9fa5]|[\uFF00-\uFFEF]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 打印执行时间
     *
     * @param start
     * @param end
     */
    public static void printComsumeTime(long start, long end) {
        LogUtils.e("consume time : " + String.valueOf(end - start) + "ms");
    }

    /**
     * 判断string是否有效
     *
     * @param str
     * @return
     */
    public static boolean isValidString(String str) {
        return (str != null) && !TextUtils.isEmpty(str) && !str.equals("null");
    }

    /**
     * 获取文件大小
     *
     * @param f
     * @return
     * @throws FileNotFoundException
     */
    public static String getFileSize(File f) {
        if (f.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                if ((fis.available() / 1024 / 1024) > 0) {
                    return fis.available() / 1024 / 1024 + "M";
                } else if (fis.available() / 1024 > 0) {
                    return fis.available() / 1024 + "K";
                } else {
                    return fis.available() + "B";
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return "0B";
        }
        return "0B";
    }

    /**
     * 文件转化为字节数组
     *
     * @param file
     * @return
     */
    public byte[] getBytesFromFile(File file) {
        byte[] ret = null;
        try {
            if (file == null) {
                // log.error("helper:the file is null!");
                return null;
            }
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
            byte[] b = new byte[4096];
            int n;
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            in.close();
            out.close();
            ret = out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static String getIdentity(Session session) {
        StringBuilder builder = new StringBuilder();
        builder.append(session.getListid())
                .append(Constants.SYMBOL_PERIOD)
                .append(session.getIds());
        return builder.toString();

    }

    /**
     * 获取应用版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static PackageInfo getAppInfo(Context context) {

        final PackageManager pm = (PackageManager) context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi;

        } catch (PackageManager.NameNotFoundException e) {
            Log.d("application", "met some error when get application info");
        }
        return null;
    }

    // 判断程序是否在前台运行
    public static boolean isTopActivity(Context context) {
        String packageName = "com.zuzhili";
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);

        if (tasksInfo.size() > 0) {
            // 应用程序位于堆栈的顶层
            if (packageName.equals(tasksInfo.get(0).topActivity
                    .getPackageName())) {
                return true;
            }
        }
        return false;
    }

    // 判断程序是否在后台运行
    public boolean isRunningInBackground(Context context) {
        String packageName = "com.zuzhili";

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(20);
        int taskNum = tasksInfo.size();
        int i = 0;
        while (taskNum > 0) {
            if (packageName.equals(tasksInfo.get(i++).topActivity
                    .getPackageName())) {
                return true;
            }
            taskNum--;
        }
        return false;
    }

    public static boolean isDevicePort() {
        return TaskApp.getInstance().getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * 友好时间
     *
     * @param targetTime
     * @return
     */
    public final static String getFriendlyTime(long targetTime) {

        String ftime = "";

        Date date = new Date(targetTime);
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH) + 1;
        int day = ca.get(Calendar.DAY_OF_MONTH);
        int hour = ca.get(Calendar.HOUR_OF_DAY);
        int minute = ca.get(Calendar.MINUTE);

        Calendar caNow = Calendar.getInstance();
        int nowYear = caNow.get(Calendar.YEAR);
        int nowMonth = caNow.get(Calendar.MONTH) + 1;
        int nowDay = caNow.get(Calendar.DAY_OF_MONTH);
        if (year == nowYear && month == nowMonth && day == nowDay) {

            if (hour >= 0 && hour < 6) {
                ftime = String.format("凌晨 %02d:%02d", hour, minute);
            } else if (hour >= 6 && hour < 9) {
                ftime = String.format("早上 %02d:%02d", hour, minute);
            } else if (hour >= 9 && hour < 12) {
                ftime = String.format("上午 %02d:%02d", hour, minute);
            } else if (hour >= 12 && hour < 14) {
                ftime = String.format("中午 %02d:%02d", hour, minute);
            } else if (hour >= 14 && hour < 18) {
                ftime = String.format("下午 %02d:%02d", hour, minute);
            } else if (hour >= 18 && hour < 24) {
                ftime = String.format("晚上 %02d:%02d", hour, minute);
            }
        } else if (year == nowYear && month == nowMonth && day == nowDay - 1) {
            ftime = String.format("昨天 %02d:%02d", hour, minute);
        } else if (year == nowYear) {
            ftime = String.format("%d月%d日", month, day);
        } else {
            ftime = String.format("%d年%d月%d日", year, month, day);
        }
        return ftime;

    }

    /**
     * 是否为第三方打包
     * @param context
     * @return
     */
    public static boolean isThirdPackage(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            boolean thirdPackageFlag = bundle.getBoolean("third_package_flag", false);
            return thirdPackageFlag;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }
        return false;
    }
}
