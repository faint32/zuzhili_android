package com.zuzhili.ui.activity.multiselect;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.helper.VedioHelper;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.PublicTools;
import com.zuzhili.bussiness.utility.VedioListContainer;
import com.zuzhili.bussiness.utility.VedioMiniThumb;
import com.zuzhili.controller.VedioListAdapter;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.multipart.VedioLocal;
import com.zuzhili.ui.activity.BaseActivity;

/**
 * Created by addison on 2/21/14.
 */
public class VedioListActivity extends BaseActivity implements OnItemClickListener, OnScrollListener, OnClickListener, BaseActivity.TimeToShowActionBarCallback {

    @ViewInject(R.id.vedio_list_lv)
    private ListView vedioListLV;

    private static final String CALLER_VIDEOPLAYER = "VIDEOPLAYER";
    private static final String CALLER_CAMERA = "CAMERA";
    private static final int PROCESS_DIALOG_START_KEY = 0;
    private static final int PROCESS_MEDIA_SCANNING_KEY = 1;

    private static final String CAMERAFOLDER_SDCARD_PATH = "/mnt/sdcard/Camera/Videos";

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, 0, getString(R.string.title_select_vedio_hint), false);
        return true;
    }

    @Override
    public boolean performClickOnLeft() {
        finish();
        return super.performClickOnLeft();
    }

    private enum ListEnum {
        NormalVideo, CameraVideo
    };

    private static final int LIST_STATE_IDLE = 0;
    private static final int LIST_STATE_BUSY = 1;
    private static final int LIST_STATE_REQUEST_REFRESH = 2;
    private static final int APPSTATE_FIRST_START = 0;
    private static final int APPSTATE_INITIALIZED = 1;
    private static final int APPSTATE_FINISHED = 2;

    public class ListLastPosition {
        public int normalVideo = 0;
        public int cameraVideo = 0;
    }

    private int mAppState;
    private boolean mRequest_stop_thread;
    private boolean mFinishScanning;
    private int mCurrentListState;
    private String mCaller;
    private ListLastPosition listLastPosition = new ListLastPosition();
    private VedioLocal mLastPlayedItem;
    private VedioListAdapter mListAdapter;
    private VedioListContainer mAllVedios;
    private List<VedioLocal> mAllVideoList = new ArrayList<VedioLocal>();
    private List<VedioLocal> mNormalVideoList = new ArrayList<VedioLocal>();
    private List<VedioLocal> mCameraList = new ArrayList<VedioLocal>();
    private List<VedioLocal> mActiveList;
    private ArrayList<String> mCurrentPlayList;

    private Hashtable<Integer, Bitmap> mThumbHash = new Hashtable<Integer, Bitmap>();
    private Bitmap mDefaultBitmap;

    private Thread mLoadingThread = null;

    private ImageButton backIMB;
    private ImageButton doneIMB;

    @Override
    public LayoutInflater getLayoutInflater() {
        return getWindow().getLayoutInflater();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mAppState = APPSTATE_INITIALIZED;

            if (mCaller.equals(CALLER_CAMERA)) {
                mActiveList = mCameraList;
            } else {
                mActiveList = mNormalVideoList;
                refreshLastest(false);
            }
            checkListScanning();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vedio_list);
        setCustomActionBarCallback(this);
        ViewUtils.inject(this);
        if (isSDcardEjected()) {
            mLoadingThread = createLoadingThread();
            mLoadingThread.start();
        }
        initData();
        initListener();
        // initialize();
    }

    private void initData() {
        mAppState = APPSTATE_FIRST_START;
        mCaller = CALLER_VIDEOPLAYER;
        mFinishScanning = false;
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        iFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        iFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        iFilter.addDataScheme("file");
        registerReceiver(mBroadcastReceiver, iFilter);
        mThumbHash.clear();
        mDefaultBitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.video_default_icon);
    }


    private void initListener() {
        vedioListLV.setOnItemClickListener(this);
        vedioListLV.setOnScrollListener(this);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        boolean mountState = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mAppState == APPSTATE_FINISHED) {
                return;
            }
            String action = intent.getAction();
            LogUtils.d("BroadcastReceiver action : " + action);

            if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
                if (!mountState) {
                    LogUtils.d("BroadcastReceiver sdcard ejected/mounted");
                    if (mAppState == APPSTATE_INITIALIZED) {
                        uninitialize();
                    }
                    mountState = true;
                }
            } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_STARTED)) {
                LogUtils.d("BroadcastReceiver start scan media");
            } else if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED)) {
                if (isSDcardEjected() && mAppState != APPSTATE_FINISHED) {
                    LogUtils.d("BroadcastReceiver stop scan media");
                    if (mAppState == APPSTATE_FIRST_START) {
                        showDialog(PROCESS_DIALOG_START_KEY);
                        createLoadingThread().start();
                    } else {
                        removeDialog(PROCESS_MEDIA_SCANNING_KEY);
                        refreshLastest(true);
                    }
                    mountState = false;
                    mFinishScanning = true;
                }
            }
        }
    };

    /**
     * SD卡是否存在
     *
     * @return
     */
    private boolean isSDcardEjected() {
        boolean isSdcard_ok = false;
        String status = Environment.getExternalStorageState();
        LogUtils.d("status : " + status + status.equals(Environment.MEDIA_REMOVED));

        if (status.equals(Environment.MEDIA_MOUNTED)) {
            isSdcard_ok = true;
            return true;
        }

        if (!isSdcard_ok) {
            if (status.equals(Environment.MEDIA_UNMOUNTED)) {
                Utils.makeEventToast(this, getString(R.string.sd_unmounted), false);
            } else if (status.equals(Environment.MEDIA_SHARED)) {
                Utils.makeEventToast(this, getString(R.string.sd_shared), false);
            } else if (status.equals(Environment.MEDIA_REMOVED)) {
                Utils.makeEventToast(this, getString(R.string.sd_removed), false);
            } else {
                Utils.makeEventToast(this,getString(R.string.sd_noinsert), false);
            }
        }

        return isSdcard_ok;
    }

    private Thread createLoadingThread() {
        return new Thread(new Runnable() {
            private static final int STATE_STOP = 0;
            private static final int STATE_IDLE = 1;
            private static final int STATE_TERMINATE = 2;
            private int workStatus;
            private int currentPos;
            private int maxPos;
            private Object[] items;

            @Override
            public void run() {
                LogUtils.d("LoadDataThread  run");
                mRequest_stop_thread = false;

                getVideoData();
                mHandler.sendMessage(mHandler.obtainMessage());

                init();
                loadThumbnails();
            }

            private void init() {
                mCurrentListState = LIST_STATE_IDLE;
                workStatus = STATE_STOP;

                items = mAllVideoList.toArray();
                maxPos = items.length;
                currentPos = 0;

            }

            private void loadThumbnails() {
                while (workStatus != STATE_TERMINATE) {
                    switch (workStatus) {
                        case STATE_STOP:
                            workStatus = onStop();
                            break;
                        case STATE_IDLE:
                            workStatus = onIdle();
                            break;
                        default:
                            break;
                    }
                }
            }

            private int onIdle() {

                while (true) {
                    if (mRequest_stop_thread || (currentPos == maxPos)) {
                        return STATE_TERMINATE;
                    }
                    if (mCurrentListState == LIST_STATE_REQUEST_REFRESH) {
                        mCurrentListState = LIST_STATE_IDLE;
                        return STATE_STOP;
                    }

                    PublicTools.sleep(PublicTools.LONG_INTERVAL);
                }
            }

            private int onStop() {
                if (mRequest_stop_thread) {
                    return STATE_TERMINATE;
                }
                if (mActiveList == null || vedioListLV == null) {
                    PublicTools.sleep(PublicTools.SHORT_INTERVAL);
                    return STATE_STOP;
                }
                if (mActiveList.isEmpty()) {
                    return STATE_IDLE;
                }
                if (-1 == vedioListLV.getLastVisiblePosition()) {
                    PublicTools.sleep(PublicTools.SHORT_INTERVAL);
                    return STATE_STOP;
                }


                Object[] viewHolders = mListAdapter.getHolderObjects();
                int count = viewHolders.length;
                for (int i = 0; i < count; i++) {
                    if (mCurrentListState == LIST_STATE_BUSY) {
                        return STATE_IDLE;
                    } else if (mCurrentListState == LIST_STATE_REQUEST_REFRESH) {
                        mCurrentListState = LIST_STATE_IDLE;
                        return STATE_STOP;
                    }
                    RefreshThumbnail((VedioListAdapter.ViewHolder) viewHolders[i]);
                    PublicTools.sleep(PublicTools.MINI_INTERVAL);
                }

                PublicTools.sleep(PublicTools.MIDDLE_INTERVAL);

                if (count < mListAdapter.getHolderObjects().length) {
                    return STATE_STOP;
                }
                if (mCurrentListState == LIST_STATE_IDLE) {
                    return STATE_IDLE;
                } else {
                    mCurrentListState = LIST_STATE_IDLE;
                    return STATE_STOP;
                }
            }

            private void RefreshThumbnail(VedioListAdapter.ViewHolder holder) {
                if (holder == null) {
                    return;
                }
                if (!holder.mUseDefault|| holder.mItem == null|| PublicTools.THUMBNAIL_CORRUPTED == holder.mItem.vedio.getThumbnailState()) {
                    return;
                }
                holder.mBitmap = holder.mItem.vedio.miniThumbBitmap(false,mThumbHash, mDefaultBitmap);
                if (PublicTools.THUMBNAIL_PREPARED == holder.mItem.vedio.getThumbnailState()) {
                    mListAdapter.sendRefreshMessage(holder);
                    holder.mUseDefault = false;
                } else {
                    holder.mUseDefault = true;
                }
            }
        });
    }

    public void refreshLastest(boolean isRefreshData) {
        if (isRefreshData) {
            getVideoData();
        }
        if (mActiveList == mNormalVideoList) {
            refreshList(ListEnum.NormalVideo);
        } else if (mActiveList == mCameraList) {
            refreshList(ListEnum.CameraVideo);
        }
        if (isRefreshData) {
            Toast.makeText(this, getString(R.string.list_refresh), 0).show();
        }
    }

    private void refreshList(ListEnum list) {
        int lastPos = vedioListLV.getFirstVisiblePosition();

        if (mActiveList == mNormalVideoList) {
            listLastPosition.normalVideo = lastPos;
        } else if (mActiveList == mCameraList) {
            listLastPosition.cameraVideo = lastPos;
        }
        if (list.equals(ListEnum.NormalVideo)) {
            mActiveList = mNormalVideoList;
            lastPos = listLastPosition.normalVideo;
        } else if (list.equals(ListEnum.CameraVideo)) {
            mActiveList = mCameraList;
            lastPos = listLastPosition.cameraVideo;
        }

        mListAdapter = new VedioListAdapter(this);
        mListAdapter.setThumbHashtable(mThumbHash, mDefaultBitmap);
        mListAdapter.setListItems(mActiveList);

        vedioListLV.setAdapter(mListAdapter);
        vedioListLV.setSelection(lastPos);

        mCurrentListState = LIST_STATE_REQUEST_REFRESH;
    }


    public void checkListScanning() {
        if (PublicTools.isMediaScannerScanning(getContentResolver())&& !mFinishScanning) {
            showDialog(PROCESS_MEDIA_SCANNING_KEY);
        }
    }


    public void getVideoData() {

        mAllVideoList.clear();
        mNormalVideoList.clear();
        mCameraList.clear();

        mAllVedios = allVedios(); // Video List

        if (mAllVedios != null) {
            int totalNum = mAllVedios.getCount();
            for (int i = 0; i < totalNum; i++) {
                VedioHelper vedio = mAllVedios.getImageAt(i);

                VedioLocal item = new VedioLocal();
                item.vedio = vedio;
                item.name = vedio.getTitle();
                item.duration = vedio.getDuration();
                item.size = vedio.getSize();
                item.datapath = vedio.getMediapath();
                LogUtils.d(item.datapath + "");
//				item.cover = vedio.miniThumbBitmap(false, mThumbHash, mDefaultBitmap);

                long bucketId = vedio.getBucketId();

                if (PublicTools.getBucketId(CAMERAFOLDER_SDCARD_PATH) == bucketId) {
                    item.dataModified = vedio.getDateModified();
                    mCameraList.add(item);
                } else {
                    mNormalVideoList.add(item);
                }

                mAllVideoList.add(item);
            }

        }
    }

    private void uninitialize() {
        Toast.makeText(this, getString(R.string.sd_shared), 0).show();
        if (mAllVedios != null) {
            mAllVedios.onDestory();
        }
        listLastPosition.cameraVideo = 0;
        listLastPosition.normalVideo = 0;
        mAllVedios = null;
        mAllVideoList.clear();
        mNormalVideoList.clear();
        mCameraList.clear();
        if (mCurrentPlayList != null)
            mCurrentPlayList.clear();
        if (mLastPlayedItem != null) {
            mLastPlayedItem.vedio = null;
            mLastPlayedItem.lastPos = 0;
        }
        refreshLastest(false);
    }


    private VedioListContainer allVedios() {
        mAllVedios = null;
        return VedioMiniThumb.instance().allVedios(VedioListActivity.this,getContentResolver(), VedioMiniThumb.INCLUDE_VIDEOS, VedioMiniThumb.SORT_ASCENDING);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        VedioLocal vedioItem = (VedioLocal) parent.getItemAtPosition(position);
        if(vedioItem.vedio.getThumbnailState()==PublicTools.THUMBNAIL_PREPARED){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString(Constants.VEDIO_NAME, vedioItem.getName());
            bundle.putString(Constants.VEDIO_PATH, vedioItem.getDatapath());
            bundle.putParcelable(Constants.VEDIO_COVER, vedioItem.getCover());
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }else{
            Utils.makeEventToast(this, "加载中...", false);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_IDLE:
                mCurrentListState = LIST_STATE_REQUEST_REFRESH;
                break;
            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
            case OnScrollListener.SCROLL_STATE_FLING:
                mCurrentListState = LIST_STATE_BUSY;
                break;
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // TODO Auto-generated method stub
    }


    @Override
    protected void onDestroy() {
        LogUtils.d("call onDestroy");
        mRequest_stop_thread = true;
        mAppState = APPSTATE_FINISHED;

        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }
        if (mAllVedios != null) {
            mAllVedios.onDestory();
        }
        Enumeration<Bitmap> e = mThumbHash.elements();
        while (e.hasMoreElements()) {
            Bitmap tmp = e.nextElement();
            if (!tmp.isRecycled()) {
                tmp.recycle();
            }
        }
        mThumbHash.clear();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
