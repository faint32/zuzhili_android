package com.zuzhili.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.GroupInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.ResultsAdapter;
import com.zuzhili.controller.TrendAdapter;
import com.zuzhili.db.CacheType;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.framework.utils.Downloader;
import com.zuzhili.framework.utils.SDCardAccessor;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.Member;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.views.PullRefreshListView;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * 动态
 * Created by liutao on 14-1-21.
 */
public class NewsTimeLineFrg extends FixedOnActivityResultBugFragment
        implements BaseActivity.HandleProgressBarVisibilityCallback, Response.Listener<String>, Response.ErrorListener {

    public static final String CACHE_TYPE = "com.zuzhili.ui.fragment.cache_type";

    private static boolean checkVersionFlag = true;

    private final long MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;

    @ViewInject(R.id.listView)
    protected PullRefreshListView pullToRefreshListView;

    @ViewInject(R.id.progressbar)
    protected ProgressBar progressBar;

    protected ResultsAdapter timeLineAdapter;
    private String cacheType = CacheType.CACHE_GET_FEED;
    private String spaceId = null;
    private HashMap<String, String> params = null;

    public NewsTimeLineFrg() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            cacheType = getArguments().getString(CACHE_TYPE);
            params = (HashMap<String, String>) getArguments().getSerializable("params");
        }

        if (Utils.isThirdPackage(getActivity())) {
            if (checkVersionFlag) {
                File apkFile = null;
                if (SDCardAccessor.isSDCardAvailable()) {
                    apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "zuzhili.apk");
                }
                if (apkFile != null) {
                    if (apkFile.getAbsoluteFile().exists()) {
                        SDCardAccessor.deleteFile(apkFile);
                    }
                }
                // 检测新版本
                Task.checkNewVersion(activity, this, this);
                checkVersionFlag = false;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listview_layout, container, false);
        ViewUtils.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        update();
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    //TODO 动态设置adapter
    public void onFilterChanged(boolean isFilterChanged) {
        if (timeLineAdapter != null && isFilterChanged) {
            timeLineAdapter.clearList();
            timeLineAdapter = null;
        }
        if (timeLineAdapter == null) {
            timeLineAdapter = new TrendAdapter(getActivity()
                    , pullToRefreshListView
                    , ImageCacheManager.getInstance().getImageLoader()
                    , buildRequestParams(), mSession, this, CacheType.CACHE_GET_FEED, TrendAdapter.REQUEST_TYPE_GET_FEED, false);
        }
        timeLineAdapter.setListView(pullToRefreshListView);
        timeLineAdapter.setOnRefreshListener();
        pullToRefreshListView.setAdapter(timeLineAdapter);
        pullToRefreshListView.setOnItemClickListener(((TrendAdapter) timeLineAdapter).trendOnItemClickListener);
    }

    public void update() {

        if (timeLineAdapter != null && mSession.isUIShouldUpdate(Constants.PAGE_TREND)) {
            timeLineAdapter.clearList();
            timeLineAdapter = null;
            mSession.resetUIShouldUpdateFlag(Constants.PAGE_TREND);
        }

        if (timeLineAdapter == null) {
            Member member = new Member();
            if (params != null) {
                member.setId(params.get("ids"));
                member.setListid(params.get("listid"));
            }

            timeLineAdapter = new TrendAdapter(getActivity()
                    , pullToRefreshListView
                    , ImageCacheManager.getInstance().getImageLoader()
                    , buildRequestParams()
                    , mSession
                    , member
                    , this
                    , cacheType
                    , cacheType.equals(CacheType.CACHE_GET_SPECIFIC_GROUP_TRENDS) ?
                    TrendAdapter.REQUEST_TYPE_GET_SPECIFIC_GROUP_TERNDS :
                    cacheType.equals(CacheType.CACHE_GET_SPECIFIC_USER_TRENDS) ?
                            TrendAdapter.REQUEST_TYPE_GET_SPECIFIC_USER_TERNDS :
                            cacheType.equals(CacheType.CACHE_GET_USER_COLLECTION) ?
                                    TrendAdapter.REQUEST_TYPE_GET_COLLECTION :
                                    TrendAdapter.REQUEST_TYPE_GET_FEED
                    , false);
        }

        timeLineAdapter.setListView(pullToRefreshListView);
        timeLineAdapter.setOnRefreshListener();
        pullToRefreshListView.setAdapter(timeLineAdapter);
        pullToRefreshListView.setOnItemClickListener(((TrendAdapter) timeLineAdapter).trendOnItemClickListener);
    }


    private HashMap<String, String> buildRequestParams() {
        if (cacheType.equals(CacheType.CACHE_GET_SPECIFIC_GROUP_TRENDS) || cacheType.equals(CacheType.CACHE_GET_SPECIFIC_USER_TRENDS)) {
            return params;
        }

        final HashMap<String, String> params = new HashMap<String, String>();
        if (mSession != null) {
            if (mSession.getRegion().equals("1")) { // 我关注的
                params.put("listid", "0");
            } else {
                params.put("listid", mSession.getListid());
            }

            params.put("ids", mSession.getIds());
            params.put("start", "0");
            params.put("length", String.valueOf(Constants.PAGE_SIZE));
            params.put("apptype", mSession.getAppType());
            params.put("region", mSession.getRegion());
        }
        return params;
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        LogUtils.e("volleyError: " + volleyError.toString());
    }

    @Override
    public void onResponse(String response) {
        JSONObject jsonObject = JSONObject.parseObject(response);

        if (jsonObject.getString("results") != null) {
            List<GroupInfo> list = JSON.parseArray(jsonObject.getString("results"), GroupInfo.class);
            mSession.setGroupInfoList(list);
            return;
        }
        final String versonCode = jsonObject.getString("current_version_code");
        final String url = jsonObject.getString("newApkUrl");
        final String updateLog = jsonObject.getString("log");
        PackageInfo currentAppInfo = Utils.getAppInfo(activity);
        if (currentAppInfo != null && versonCode != null
                && Integer.valueOf(versonCode.trim()) > currentAppInfo.versionCode) {    // 有新版本
            LogUtils.e("currentVersion: " + currentAppInfo.versionCode);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage(updateLog)
                    .setTitle(R.string.new_version_alert);

            // Add the buttons
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    File apkFile = null;
                    if (SDCardAccessor.isSDCardAvailable()) {
                        apkFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "zuzhili.apk");
                    }
                    if (apkFile == null) {
                        Utils.makeEventToast(activity, "SD卡不可用", false);
                        return;
                    }
                    if (!apkFile.exists()) {
                        Downloader downloader = new Downloader(activity);
                        downloader.enqueue(url, "zuzhili.apk");
                        return;
                    } else {
                        if ((System.currentTimeMillis() - apkFile.lastModified()) > MILLISECONDS_PER_DAY) {

                            if (apkFile.exists()) {
                                SDCardAccessor.deleteFile(apkFile);
                                Utils.makeEventToast(activity, "应用程序安装包已删除", false);
                            }
                        }
                        Intent installIntent = new Intent(Intent.ACTION_VIEW);
                        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        installIntent.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
                        startActivity(installIntent);
                        activity.finish();
                    }
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();

            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        }
    }
}
