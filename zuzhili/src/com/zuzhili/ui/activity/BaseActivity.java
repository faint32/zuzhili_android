package com.zuzhili.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hisun.phone.core.voice.CCPCall;
import com.hisun.phone.core.voice.Device;
import com.hisun.phone.core.voice.util.Log4Util;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.helper.CCPHelper;
import com.zuzhili.bussiness.utility.AnimType;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.db.DBHelper;
import com.zuzhili.exception.NetCallback;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.TaskApp;
import com.zuzhili.framework.im.CCPIntentUtils;
import com.zuzhili.framework.im.ITask;
import com.zuzhili.framework.im.TaskKey;
import com.zuzhili.framework.im.ThreadPoolManager;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.Member;
import com.zuzhili.ui.actionbar.ActionBarHelper;
import com.zuzhili.ui.fragment.SampleFrg;
import com.zuzhili.ui.fragment.SocialFrg;
import com.zuzhili.ui.fragment.SocialSearchFrg;
import com.zuzhili.ui.fragment.WebViewFragment;
import com.zuzhili.ui.fragment.approval.ApprovalContainerFrg;
import com.zuzhili.ui.fragment.atme.AtMeContainerFrg;
import com.zuzhili.ui.fragment.comment.CommentContaierFrg;
import com.zuzhili.ui.fragment.member.MemberContainerFrg;
import com.zuzhili.ui.fragment.more.MoreFrg;
import com.zuzhili.ui.fragment.space.PersonalSpaceFrg;
import com.zuzhili.ui.views.LoadingDialog;

import net.simonvt.menudrawer.MenuDrawer;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;

/**
 * 所有Activity都基础该基类
 * Created by liutao on 14-1-21.
 */
public abstract class BaseActivity extends ActionBarActivity implements OnClickListener, ThreadPoolManager.OnTaskDoingLinstener, CCPHelper.RegistCallBack {

    // 应用session
    public Session mSession;
    private Dialog loadingDialog;
    protected Handler exph = new Handler();

	public NetCallback callback = new DefaultCallBack(exph);
	public ActionBar mActionBar;

	private ImageButton leftImgBtn;
	private ImageButton rightImgBtn;
	private Button rightBtn;
	private TextView titleTxtV;
	private View titleRight;
	private TextView leftTxtV;
    private TextView rightTxtV;
	private NotificationManager  notificationMagager;

	private OnClickListener onClickLeftListener;	// 左侧点击监听
	private OnClickListener onClickMiddleListener;	// 中部标题点击监听
	private OnClickListener onClickRightListener;	// 右侧点击监听
    protected LayoutInflater mLayoutInflater;
    protected ProgressDialog progressDialog;
    protected TimeToShowActionBarCallback timeToShowActionBarCallback;
    protected HandleChatConnectionCallback handleChatConnectionCallback;
    protected DBHelper dbHelper;

    protected FragmentManager mFragmentManager;
    protected FragmentTransaction mFragmentTransaction;


    /**----------------------------------------------*/
    InternalReceiver internalReceiver = null;

    public static final int WHAT_SHOW_PROGRESS = 0x101A;
    public static final int WHAT_CLOSE_PROGRESS = 0x101B;

    private AudioManager mAudioManager = null;

    private Object mToneGeneratorLock = new Object();

    private ToneGenerator mToneGenerator;

    private static final int STREAM_TYPE 												= AudioManager.STREAM_MUSIC;

    private static final float TONE_RELATIVE_VOLUME 									= 100.0F;

    public static final int TONE_LENGTH_MS 												= 200;

    private Vibrator mVibrator;						// Vibration (haptic feedback) for dialer key presses.

    private BroadcastReceiver imReceiver;

    /**
     * 显示actionBar回调，通常所有BaseActivity的子类都需要实现该接口，
     * 并实现接口里的方法。在接口方法中调用initActionBar方法，显示actionBar.
     * 如果需要隐藏actionBar,则不需要实现该接口。在onCreate方法中调用mActionBar.hide()方法就哦了
     */
    public interface TimeToShowActionBarCallback {
        public boolean showCustomActionBar();
    }

    /**
     * 控制progressBar显示和隐藏的回调函数
     */
    public interface HandleProgressBarVisibilityCallback {
        public void setProgressBarVisibility(int visibility);
    }

    /**
     * 与云通讯服务器连接或断开回调接口
     */
    public interface HandleChatConnectionCallback {
        public void handleChatConnection();
    }

	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);
		final Context context = getApplicationContext();
        configCustomActionBar();
        mLayoutInflater = LayoutInflater.from(this);
		mSession = Session.get(context);
        mSession.setCurrentActivityName(this.getLocalClassName());
        LogUtils.e("currentActivityName: " + this.getLocalClassName());
        dbHelper = ((TaskApp) getApplication()).getDbHelper();
		notificationMagager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mFragmentManager = getSupportFragmentManager();
        ((TaskApp) getApplication()).addActivity(this);
        initScreenStates();
        registerInternalReceiver();
	}

	@Override
	protected void onResume() {
		super.onResume();
		final Context context = getApplicationContext();
        mSession = Session.get(context);
	}

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void doSDKRegist() {
        ITask iTask = new ITask(TaskKey.KEY_SDK_REGIST);
        addTask(iTask);
    }

    protected void doSDKUnregist() {
        ITask iTask = new ITask(TaskKey.KEY_SDK_UNREGIST);
        addTask(iTask);
    }

    /**
     * 显示自定义actionBar,一般情况下子类不需要复写
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if(timeToShowActionBarCallback != null) {
            timeToShowActionBarCallback.showCustomActionBar();
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterInternalReceiver();
    }

    @Override
	protected void onDestroy() {

        mAudioManager = null;
        synchronized(mToneGeneratorLock) {
            if (mToneGenerator != null) {
                mToneGenerator.release();
                mToneGenerator = null;
            }
        }
        if(imReceiver != null){
            unregisterReceiver(imReceiver);
        }
        imReceiver = null;
        ((TaskApp) getApplication()).finishActivity(this);

        super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
        if (intent.getBooleanExtra(Constants.EXTRA_ANIM_REVERSE, false)) {
            overridePendingTransition(R.anim.enter_scale_anim, R.anim.exit_trans_anim);
        }
        if (!intent.getBooleanExtra(Constants.EXTRA_ANIM_DEFAULT, false)) {
            overridePendingTransition(R.anim.enter_trans_anim, R.anim.exit_scale_anim);
        } else {
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
        }
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.enter_trans_anim, R.anim.exit_scale_anim);
	}
	@Override
	public void finish() {
		super.finish();
//		overridePendingTransition(R.anim.enter_scale_anim, R.anim.exit_trans_anim);
	}

    protected String getAtString(Member member) {
        StringBuilder builder = new StringBuilder();
        builder.append("@")
                .append(member.getName())
                .append("(")
                .append(member.getId())
                .append(")")
                .append("  ");
        return builder.toString();
    }

    /**
     * 把fragment重新添加到当前的 view hierarchy并显示
     * @param layout
     * @param f
     * @param tag
     */
    public void attachFragment(int layout, Fragment f, String tag) {
        if (f != null) {
            if (f.isDetached()) {
                ensureTransaction();
                mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                mFragmentTransaction.attach(f);
            } else if (!f.isAdded()) {
                ensureTransaction();
                mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                mFragmentTransaction.add(layout, f, tag);
            }
            commitTransactions();
        }
    }

    /**
     * 把fragment重新添加到当前的 view hierarchy并显示
     * @param layout
     * @param f
     * @param tag
     */
    public void attachFragment(int layout, Fragment f, String tag, AnimType animType) {

        if (animType == AnimType.STANDARD) {
            attachFragment(layout, f, tag);
            return;
        } else if (animType == AnimType.SLIDE_DOWN_UP) {
            if (f != null) {
                if (f.isDetached()) {
                    ensureTransaction();
                    mFragmentTransaction.setCustomAnimations(R.anim.slide_down_anim, R.anim.slide_up_anim);
                    mFragmentTransaction.attach(f);
                } else if (!f.isAdded()) {
                    ensureTransaction();
                    mFragmentTransaction.setCustomAnimations(R.anim.slide_down_anim, R.anim.slide_up_anim);
                    mFragmentTransaction.add(layout, f, tag);
                }
                commitTransactions();
            }
        }
    }

    /**
     * 从当前的UI中分离fragment，fragment的视图结构会被破
     * @param f
     */
    public void detachFragment(Fragment f) {
        if (f != null && !f.isDetached()) {
            ensureTransaction();
            mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            mFragmentTransaction.detach(f);
            commitTransactions();
        }
    }

    /**
     * 从当前的UI中分离fragment，fragment的视图结构会被破
     * @param f
     * @param animType 过渡动画类型
     */
    public void detachFragment(Fragment f, AnimType animType) {
        if (animType == AnimType.STANDARD) {
            detachFragment(f);
            return;
        } else if (animType == AnimType.SLIDE_DOWN_UP) {
            if (f != null && !f.isDetached()) {
                ensureTransaction();
                mFragmentTransaction.setCustomAnimations(R.anim.slide_down_anim, R.anim.slide_up_anim);
                mFragmentTransaction.detach(f);
                commitTransactions();
            }
        }
    }

    public void replaceFragment(int layout, Fragment f, String tag) {
        if (f != null) {
            ensureTransaction();
            mFragmentTransaction.replace(layout, f, tag);
            commitTransactions();
        }
    }

    /**
     * 如果container存在fragment，从container中移除fragment
     * @param f
     */
    public void removeFragment(Fragment f) {
        if (f != null && !f.isDetached()) {
            ensureTransaction();
            mFragmentTransaction.remove(f);
            commitTransactions();
        }
    }

    public FragmentTransaction ensureTransaction() {
        if (mFragmentTransaction == null) {
            mFragmentTransaction = mFragmentManager.beginTransaction();
        }

        return mFragmentTransaction;
    }

    public void commitTransactions() {
        if (mFragmentTransaction != null && !mFragmentTransaction.isEmpty()) {
            mFragmentTransaction.commit();
            mFragmentTransaction = null;
        }
    }

    /**
     * 根据tag找到相应的fragment
     * @param tag
     * @return
     */
    public Fragment getFragment(String tag) {
        Fragment f = mFragmentManager.findFragmentByTag(tag);

        if (f == null) {
            if (tag.equals(Constants.TAG_MEMBERS)) {
                f = MemberContainerFrg.newInstance(Constants.EXTRA_FROM_HOME);
            } else if(tag.equals(Constants.TAG_MORE)){
                f = new MoreFrg();
            } else if (tag.equals(Constants.TAG_SOCIALS)) {
                f = new SocialFrg();
            } else if (tag.equals(Constants.TAG_SOCIAL_SEARCH)) {
                f = new SocialSearchFrg();
            } else if (tag.equals(Constants.TAG_AT_ME)) {
                f = new AtMeContainerFrg();
            } else if (tag.equals(Constants.TAG_COMMENT)) {
                f = new CommentContaierFrg();
            } else if(tag.equals(Constants.TAG_APPROVAL)){
            	f=new ApprovalContainerFrg();
            }
            else {
                f = SampleFrg.newInstance(tag);
            }
        }
        return f;
    }

    public Fragment getFragment(Bundle bundle) {
        String tag = bundle.getString(Constants.EXTRA_FRAGMENT_TAG);
        Fragment f = mFragmentManager.findFragmentByTag(tag);

        if (f == null) {
            if (tag.equals(Constants.TAG_MEMBERS)) {
                String from = bundle.getString(Constants.EXTRA_FROM_WHICH_PAGE);
                f = MemberContainerFrg.newInstance(from);
            } else if(tag.equals(Constants.TAG_MORE)){
                f = new MoreFrg();
            } else if (tag.equals(Constants.TAG_SOCIALS)) {
                f = new SocialFrg();
            } else if (tag.equals(Constants.TAG_SOCIAL_SEARCH)) {
                f = new SocialSearchFrg();
            } else if (tag.equals(Constants.TAG_PERSONAL_SPACE)) {
                Member member = (Member) bundle.getSerializable(Constants.EXTRA_MEMBER);
                f = PersonalSpaceFrg.newInstance(member);
            } else if (tag.equals(Constants.TAG_VOTE) || tag.equals(Constants.TAG_REGISTER)) {
                String url = bundle.getString(Constants.EXTRA_URL);
                f = WebViewFragment.newInstance(url, tag);
            }
            else {
                f = SampleFrg.newInstance(tag);
            }
        }
        return f;
    }

	final ActionBarHelper mActionBarHelper = ActionBarHelper.createInstance(this);

    /**
     * Returns the {@link com.zuzhili.ui.actionbar.ActionBarHelper} for this activity.
     */
    protected ActionBarHelper getActionBarHelper() {
        return mActionBarHelper;
    }

    /**{@inheritDoc}*/
    @Override
    public MenuInflater getMenuInflater() {
        return mActionBarHelper.getMenuInflater(super.getMenuInflater());
    }

    /**{@inheritDoc}*/
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarHelper.onPostCreate(savedInstanceState);
    }

    /**
     * 配置actionBar
     */
    private void configCustomActionBar() {
        mActionBar = getSupportActionBar();
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        View view = LayoutInflater.from(this).inflate(R.layout.actionbar, null);
        mActionBar.setCustomView(view, params);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setDisplayShowCustomEnabled(true);
        mActionBar.show();

        leftImgBtn = (ImageButton) this.findViewById(R.id.imgBtn_left);
        rightImgBtn = (ImageButton) this.findViewById(R.id.imgBtn_right);
        rightBtn = (Button) this.findViewById(R.id.btn_right);
        titleTxtV = (TextView) this.findViewById(R.id.txt_title);
        titleRight = this.findViewById(R.id.view_holder);
        leftTxtV = (TextView) this.findViewById(R.id.txt_left);
        rightTxtV = (TextView) this.findViewById(R.id.txt_right);
    }

    /**
     * 在子类相关生命周期方法中（onCreate,onResume,etc...）设置callback.
     * @param timeToShowActionBarCallback
     */
    public void setCustomActionBarCallback(TimeToShowActionBarCallback timeToShowActionBarCallback) {
        this.timeToShowActionBarCallback = timeToShowActionBarCallback;
    }

    public void setChatConnectionCallback(HandleChatConnectionCallback handleChatConnectionCallback) {
        this.handleChatConnectionCallback = handleChatConnectionCallback;
    }

    /**{@inheritDoc}*/
    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        mActionBarHelper.onTitleChanged(title, color);
        super.onTitleChanged(title, color);
    }

    /**
     * 初始化title bar, 传资源文件id
     * @param rightImgResId	右侧按钮资源id
     * @param isTitleClickable	标题是否可点击
     */
    public void initActionBar(int leftImgResId, int rightImgResId, String title, boolean isTitleClickable) {
    	initImgBtn(leftImgResId, leftImgBtn, leftTxtV);
    	initImgBtn(rightImgResId, rightImgBtn, rightTxtV);
    	initTitleTxtV(title, titleTxtV, isTitleClickable);
    }

    public View getTitleTxtxV(){
        return  titleRight;
    }


    /**
     * 初始化title bar, 传资源文件id
     * @param id	背景颜色按钮资源id
     */
    public void initActionBar(int leftImgResId, int id, String title) {
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        titleTxtV.setTextColor(Color.WHITE);
        initImgBtn(leftImgResId, leftImgBtn, leftTxtV);
        initTitleTxtV(title, titleTxtV, false);
    }

    /**
     * 初始化title bar
     * @param leftTxt	左侧为文字
     * @param rightTxt	右侧为文字
     * @param title	中间标题为文字
     * @param isTitleClickable	标题是否可点击
     */
    public void initActionBar(String leftTxt, String rightTxt, String title, boolean isTitleClickable) {
    	initNonTitleTxtV(leftTxt, leftTxtV, leftImgBtn);
    	initNonTitleTxtV(rightTxt, rightTxtV, rightImgBtn);
    	initTitleTxtV(title, titleTxtV, isTitleClickable);
    }

    /**
     * 初始化title bar
     * @param leftTxt	左侧为文字
     * @param title	中间标题为文字
     * @param isTitleClickable	标题是否可点击
     */
    public void initActionBar(String leftTxt, int rightImgResId, String title, boolean isTitleClickable) {
    	initNonTitleTxtV(leftTxt, leftTxtV, leftImgBtn);
    	initImgBtn(rightImgResId, rightImgBtn, rightTxtV);
    	initTitleTxtV(title, titleTxtV, isTitleClickable);
    }

    /**
     * 初始化title bar, 传资源文件id
     * @param rightTxt	右侧按钮文字
     */
    public void initActionBar(int leftImgResId, String rightTxt, String title) {
        initImgBtn(leftImgResId, leftImgBtn, leftTxtV);
        initBtn(rightTxt, rightBtn, rightTxtV);
        initTitleTxtV(title, titleTxtV, false);
    }

    /**
     * 初始化title bar
     * @param rightTxt	右侧为文字
     * @param title	中间标题为文字
     * @param isTitleClickable	标题是否可点击
     */
    public void initActionBar(int leftImgResId, String rightTxt, String title, boolean isTitleClickable) {
    	initImgBtn(leftImgResId, leftImgBtn, leftTxtV);
    	initNonTitleTxtV(rightTxt, rightTxtV, rightImgBtn);
    	initTitleTxtV(title, titleTxtV, isTitleClickable);
    }

    /**
     * 设置actionBar标题文字
     */
    public void setTitle(String title, boolean isClickable) {
        initTitleTxtV(title, titleTxtV, isClickable);
    }

    /**
     * 隐藏actionBar左侧icon或文字
     */
    public void setLeftRegionInvisible() {
        if (leftImgBtn != null && leftImgBtn.getVisibility() == View.VISIBLE) {
            leftImgBtn.setVisibility(View.INVISIBLE);
        }
        if (leftTxtV != null && leftTxtV.getVisibility() == View.VISIBLE) {
            leftTxtV.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置actionbar左边按钮触摸事件
     * @param listener
     */
    public void setLeftImgBtnOnTouchListener(View.OnTouchListener listener) {
        if (leftImgBtn != null && leftImgBtn.getVisibility() == View.VISIBLE) {
            leftImgBtn.setOnTouchListener(listener);
        }
    }
    /**
     * 设置actionbar右边按钮触摸事件
     * @param listener
     */
    public void setRightBtnOnTouchListener(View.OnTouchListener listener) {
        if (rightBtn != null ) {
            rightBtn.setOnTouchListener(listener);
        }
    }

    /**
     * 显示actionBar左侧icon或文字
     */
    public void setLeftRegionVisible() {
        if (leftImgBtn != null && leftImgBtn.getVisibility() != View.VISIBLE) {
            leftImgBtn.setVisibility(View.VISIBLE);
        }
        if (leftTxtV != null && leftTxtV.getVisibility() != View.VISIBLE) {
            leftTxtV.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏title
     */
    public void setTitleInvisible() {
        if (titleTxtV != null && titleTxtV.getVisibility() == View.VISIBLE) {
            titleTxtV.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 显示title
     */
    public void setTitleVisiable() {
        if (titleTxtV != null && titleTxtV.getVisibility() != View.VISIBLE) {
            titleTxtV.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置标题右侧图标
     */
    public void setCompoundDrawableVisible() {
        if (titleTxtV != null) {
            titleTxtV.setVisibility(View.VISIBLE);
            titleTxtV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pull_down_indicater, 0);
        }
    }

    /**
     * 设置标题右侧内容
     */
    public void setCompoundTextVisible(String s) {
        if (rightTxtV != null) {
            rightTxtV.setVisibility(View.VISIBLE);
            rightTxtV.setText(s);
        }
    }

    /**
     * 不显示标题右侧图标
     */
    public void setCompoundDrawableGone() {
        if (titleTxtV != null) {
            titleTxtV.setVisibility(View.VISIBLE);
            titleTxtV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    public OnClickListener getOnClickLeftListener() {
		return onClickLeftListener;
	}

	public void setOnClickLeftListener(OnClickListener onClickLeftListener) {
		this.onClickLeftListener = onClickLeftListener;
	}

	public OnClickListener getOnClickMiddleListener() {
		return onClickMiddleListener;
	}

	public void setOnClickMiddleListener(OnClickListener onClickMiddleListener) {
		this.onClickMiddleListener = onClickMiddleListener;
	}

	public OnClickListener getOnClickRightListener() {
		return onClickRightListener;
	}

	public void setOnClickRightListener(OnClickListener onClickRightListener) {
		this.onClickRightListener = onClickRightListener;
	}

	@Override
    public void onClick(View v) {
    	switch (v.getId()) {
		case R.id.imgBtn_left:
		case R.id.txt_left:
			if(onClickLeftListener != null) {
				performClickOnLeft(onClickLeftListener);
			} else {
				performClickOnLeft();
			}
			break;
		case R.id.imgBtn_right:
		case R.id.txt_right:
        case R.id.btn_right:
			if(onClickRightListener != null) {
				performClickOnRight(onClickRightListener);
			} else {
				performClickOnRight();
			}
			break;
		case R.id.txt_title:
			if(onClickMiddleListener != null) {
				performClickOnMiddle(onClickMiddleListener);
			} else {
				performClickOnMiddle();
			}
			break;

		default:
			break;
		}
    }

    /** 子类可选择复写该方法，处理左侧点击事件 */
    public boolean performClickOnLeft() {
    	return true;
    }

    /** 子类可选择复写该方法，处理中间标题文字点击事件 */
    public boolean performClickOnMiddle() {
    	return true;
    }

    /** 子类可选择复写该方法，处理右侧点击事件 */
    public boolean performClickOnRight() {
    	return true;
    }

    /** 用于activity添加fragment时，能监听action bar左侧按钮的变化 */
    public boolean performClickOnLeft(OnClickListener clickListener) {
    	if(leftImgBtn.getVisibility() == View.VISIBLE) {
            clickListener.onClick(leftImgBtn);
        } else if(leftTxtV.getVisibility() == View.VISIBLE && leftTxtV.isClickable()) {
            clickListener.onClick(leftTxtV);
        }
        return true;
    }

    /** 用于activity添加fragment时，能监听action bar标题的变化 */
    public boolean performClickOnMiddle(OnClickListener clickListener) {
    	if(titleTxtV.getVisibility() == View.VISIBLE && titleTxtV.isClickable()) {
    		clickListener.onClick(leftImgBtn);
    	}
    	return true;
    }

    /** 用于activity添加fragment时，能监听action bar右侧按钮的变化 */
    public boolean performClickOnRight(OnClickListener clickListener) {
    	if(rightImgBtn.getVisibility() == View.VISIBLE) {
    		clickListener.onClick(rightImgBtn);
    	} else if(rightTxtV.getVisibility() == View.VISIBLE && rightTxtV.isClickable()) {
    		clickListener.onClick(leftTxtV);
    	}
    	return true;
    }

    public MenuDrawer getMenuDrawer() {
    	return null;
    }

    /**
     * 获取Session对象
     * @return
     */
    public Session getSession() {
    	return mSession;
    }

    public LayoutInflater getmLayoutInflater() {
        return mLayoutInflater;
    }

    public DBHelper getDbHelper() {
        return dbHelper;
    }

	public void showLoading(OnDismissListener listener) {
		if (loadingDialog != null && loadingDialog.isShowing())
			try {
				loadingDialog.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
		loadingDialog = LoadingDialog.getLoadingDialog(this);
		loadingDialog.setOnDismissListener(listener);
		loadingDialog.show();
	}

	public void removeLoading() {
		if (loadingDialog != null && loadingDialog.isShowing()) {
			loadingDialog.dismiss();
		}
	}

	public HttpHandler<String> requestJson( final RequestParams params) {
		HttpUtils util = new HttpUtils();
		HttpHandler<String> httpHandler = util.send(HttpMethod.POST, Task.API_HOST_URL + params.getTask(), params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				LogUtils.d(responseInfo.result);
				if(responseInfo.result != null && !responseInfo.result.equals("")){
					JSONObject object = JSON.parseObject(responseInfo.result);
					if(object != null){
						String errmsg = object.getString("errmsg");
						params.setResultJson(responseInfo.result);
						if(errmsg != null && errmsg.equals("ok")){
							params.listener.OnNetSuccess(params);
						} else {
							params.listener.OnNetFailure(params);
						}
					}
				}
			}
			@Override
			public void onFailure(HttpException error, String msg) {
				callback.onException(error);
			}
		});
		return httpHandler;
	}

	public class DefaultCallBack implements NetCallback {
		private Handler expH;

		public DefaultCallBack(Handler expH) {
			super();
			this.expH = expH;
		}

		@Override
		public void onException(Exception e) {
			if (e instanceof ConnectTimeoutException) {
				expH.post(new Runnable() {

					@Override
					public void run() {
						Utils.makeEventToast(getApplicationContext(), "网络异常，请检查网络", false);
					}
				});
			} else if (e instanceof java.net.SocketException) {
				expH.post(new Runnable() {

					@Override
					public void run() {
						Utils.makeEventToast(getApplicationContext(), "网络异常，请检查网络", false);
					}
				});
			} else if (e instanceof IOException) {
				expH.post(new Runnable() {

					@Override
					public void run() {
						Utils.makeEventToast(getApplicationContext(), "网络异常，请检查网络", false);
					}
				});
			} else if(e instanceof com.android.volley.NoConnectionError){
				expH.post(new Runnable() {

					@Override
					public void run() {
						Utils.makeEventToast(getApplicationContext(), "网络链接异常，请检查网络", false);
					}
				});
			} else if(e instanceof com.android.volley.TimeoutError){
				expH.post(new Runnable() {

					@Override
					public void run() {
						Utils.makeEventToast(getApplicationContext(), "网络超时，请检查网络", false);
					}
				});
			} else if(e instanceof com.android.volley.ServerError){
				expH.post(new Runnable() {

					@Override
					public void run() {
						Utils.makeEventToast(getApplicationContext(), "服务器异常，请稍后重试", false);
					}
				});
			} else if(e instanceof com.android.volley.ParseError){
				expH.post(new Runnable() {

					@Override
					public void run() {
						Utils.makeEventToast(getApplicationContext(), "数据解析异常，请稍后重试", false);
					}
				});
			} else if(e instanceof com.android.volley.NetworkError){
				expH.post(new Runnable() {

					@Override
					public void run() {
						Utils.makeEventToast(getApplicationContext(), "网络异常，请检查网络", false);
					}
				});
			} else if(e instanceof DbException){
			expH.post(new Runnable() {

				@Override
				public void run() {
					Utils.makeEventToast(getApplicationContext(), "数据库异常", false);
				}
			});
		}

			if (loadingDialog != null && loadingDialog.isShowing()) {
				loadingDialog.dismiss();
			}
		}
	}

	/**
	 * 加载失败时,设置网络
	 */
	public void setWifi() {
		Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
		startActivity(intent);
	}

	/**
	 * 通知栏显示正在发送的通知
	 * @param content
	 */
	public void showSendingNotify(String content){
		Notification notification = initNotifyConfig("发送中...");
		Intent intent = new Intent();
		PendingIntent pending = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		if(pending != null){
			notification.setLatestEventInfo(this, "发送中...", content, pending);
			notificationMagager.notify(1, notification);
		}

	}

	/**
	 * 通知栏显示发送成功的通知
	 * @param content
	 */
	public void showSuccessNotify(String content){
		Notification notification = initNotifyConfig("发送成功");
		Intent intent = new Intent();
		PendingIntent pending = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		if(pending != null){
			notification.setLatestEventInfo(this, "发送成功", content, pending);
			notificationMagager.notify(1, notification);
		}
	}

	/**
	 * 发送失败、保存到草稿箱
	 * @param content
	 */
	public void showFailtureNotify(String content){
		Notification notification = initNotifyConfig("发送失败");
		Intent intent = new Intent();
		PendingIntent pending = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		if(pending != null){
			notification.setLatestEventInfo(this, "发送失败", content, pending);
			notificationMagager.notify(1, notification);
		}
	}


	/**
	 * 初始化notification的config
	 * @param string
	 */
	private Notification initNotifyConfig(String string) {
		Notification notification = new Notification();
		notification.icon = R.drawable.notify;
		notification.tickerText = string;
		notification.defaults = 0;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		return notification;
	}

	/**
	 * 取消通知
	 */
	public void cancelNotify(){
		notificationMagager.cancel(1);
	}

	/**
	 * 初始化imageButton,控制重叠按钮的显示和隐藏
	 * @param resId
	 * @param imgBtn
	 * @param txtV
	 */
	private void initImgBtn(int resId, ImageButton imgBtn, TextView txtV) {
        if(txtV!=null&&imgBtn!=null) {
            if (txtV.getVisibility() == View.VISIBLE) {
                txtV.setVisibility(View.GONE);
            }

            if(resId != 0) {
                imgBtn.setImageResource(resId);
                imgBtn.setVisibility(View.VISIBLE);
                imgBtn.setOnClickListener(this);
            } else {
                imgBtn.setVisibility(View.GONE);
            }
        }
	}


    /**
     * 初始化Button,控制重叠按钮的显示和隐藏
     * @param btn
     * @param txtV
     */
    private void initBtn(String text, Button btn, TextView txtV) {
        if(txtV!=null&&btn!=null) {
            if (txtV.getVisibility() == View.VISIBLE) {
                txtV.setVisibility(View.GONE);
            }

            if(!TextUtils.isEmpty(text)) {
                if(getString(R.string.send).equals(text) || "确认(0)".equals(text)){
                    btn.setTextColor(getResources().getColor(R.color.normal_but_text));
                    btn.setEnabled(false);
                }else {
                    btn.setTextColor(Color.WHITE);
                    btn.setEnabled(true);
                }
                btn.setVisibility(View.VISIBLE);
                btn.setText(text);
                btn.setOnClickListener(this);
            } else {
                btn.setVisibility(View.GONE);
            }
        }
    }

	/**
	 * 初始化action bar 标题文字
	 * @param text
	 * @param txtV
	 * @param isClickable
	 */
	private void initTitleTxtV(String text, TextView txtV, boolean isClickable) {
		if(text != null && txtV!=null) {
			txtV.setVisibility(View.VISIBLE);
			txtV.setText(text);
    		if(isClickable) {
    			txtV.setOnClickListener(this);
                setCompoundDrawableVisible();
    		} else {
    			txtV.setClickable(false);
                setCompoundDrawableGone();
    		}
    	} else {
            if(txtV!=null) {
                txtV.setVisibility(View.GONE);
            }
    	}

	}

	/**
	 * 初始化textView,控制重叠文
     * 字和按钮的显示和隐藏
	 * @param text
	 * @param txtV
	 * @param imgBtn
	 */
	private void initNonTitleTxtV(String text, TextView txtV, ImageButton imgBtn) {
		if(imgBtn.getVisibility() == View.VISIBLE) {
			imgBtn.setVisibility(View.GONE);
		}
		if(text != null) {
			txtV.setVisibility(View.VISIBLE);
			txtV.setText(text);
    		txtV.setOnClickListener(this);
    	} else {
    		txtV.setVisibility(View.GONE);
    	}
	}

    /**
     * 右侧显示searchView
     * @param imgBtn
     */
    private void initSearchView(SearchView searchView, ImageButton imgBtn, TextView txtV) {
        if(imgBtn.getVisibility() == View.VISIBLE) {
            imgBtn.setVisibility(View.GONE);
        }
        if(txtV.getVisibility() == View.VISIBLE) {
            txtV.setVisibility(View.GONE);
        }
        searchView.setVisibility(View.VISIBLE);
        setSearchViewStyle(searchView);
    }

    /**
     * 设置searchView的style
     * @param searchView
     */
    private void setSearchViewStyle(final SearchView searchView) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

//        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
//        searchAutoComplete.setHintTextColor(Color.WHITE);
//        searchAutoComplete.setTextColor(Color.WHITE);

//        View searchplate = (View)searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
//        searchplate.setBackgroundResource(R.drawable.texfield_searchview_holo_light);

//        ImageView searchCloseIcon = (ImageView)searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
//        searchCloseIcon.setImageResource(R.drawable.icon_action_search);
//
//        ImageView voiceIcon = (ImageView)searchView.findViewById(android.support.v7.appcompat.R.id.search_voice_btn);
//        voiceIcon.setImageResource(R.drawable.abc_ic_voice_search);

//        ImageView searchMagIcon = (ImageView)searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
//        searchIcon.setImageResource(R.drawable.icon_action_search);

        ImageView searchBtn = (ImageView)searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        searchBtn.setImageResource(R.drawable.icon_action_search);
//        searchBtn.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (searchView.isIconified()) {
//                    searchView.setIconified(false);
//                } else {
//                    searchView.setIconified(true);
//                }
//            }
//        });
    }

    protected final void registerInternalReceiver() {
        IntentFilter intentfilter = new IntentFilter(CCPIntentUtils.INTENT_CONNECT_CCP);
        intentfilter.addAction(CCPIntentUtils.INTENT_DISCONNECT_CCP);
        intentfilter.addAction(CCPIntentUtils.INTENT_KICKEDOFF);
        if (internalReceiver == null) {
            internalReceiver = new InternalReceiver();
        }
        registerReceiver(internalReceiver, intentfilter);
    }

    protected final void unregisterInternalReceiver() {
        if (internalReceiver != null) {
            unregisterReceiver(internalReceiver);
            internalReceiver = null;
        }
    }

    protected final void registerIMReceiver(String[] actionArray) {
        if (actionArray == null) {
            return;
        }

        IntentFilter imIntentfilter = new IntentFilter();
        for (String action : actionArray) {
            imIntentfilter.addAction(action);
        }

        if (imReceiver == null) {
            imReceiver=new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    onReceiveBroadcast(intent);
                }
            };
        }

        registerReceiver(imReceiver, imIntentfilter);
    }

    protected void unregisterIMReceiver() {
        if (imReceiver != null) {
            unregisterReceiver(imReceiver);
            imReceiver = null;
        }
    }

    class InternalReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null && CCPIntentUtils.INTENT_KICKEDOFF.equals(intent.getAction())) {

                if (handleChatConnectionCallback != null) {
                    handleChatConnectionCallback.handleChatConnection();
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
                builder.setTitle(R.string.account_offline_notify);
                builder.setMessage(getString(R.string.hint_account_login_somewhere));

                builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doSDKUnregist();
                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doSDKRegist();
                            }
                        }, 1000);

                        //TODO  修改名称
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                if(intent == null || TextUtils.isEmpty(intent.getAction())) {
                    return;
                }

                /**
                 * version 3.5 for listener SDcard status
                 */
                if(Intent.ACTION_MEDIA_REMOVED.equalsIgnoreCase(intent.getAction())
                        || Intent.ACTION_MEDIA_MOUNTED.equalsIgnoreCase(intent.getAction())) {

                    updateExternalStorageState();
                    return ;
                }
                onReceiveBroadcast(intent);
            }
        }
    }


    @Override
    public void onRegistResult(final int reason , final String msg) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (reason == CCPHelper.WHAT_ON_CONNECT) {
                        LogUtils.i("YES , it's ok, connected");
                        if (handleChatConnectionCallback != null) {
                            handleChatConnectionCallback.handleChatConnection();
                        }
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                    } else if (reason == CCPHelper.WHAT_ON_DISCONNECT || reason == CCPHelper.WHAT_INIT_ERROR) {
                        // do nothing ...
                        Log4Util.d(CCPHelper.DEMO_TAG, "Sorry , cWHAT_ON_DISCONNECT WHAT_INIT_ERROR" + msg);
                        if (handleChatConnectionCallback != null) {
                            handleChatConnectionCallback.handleChatConnection();
                        }
                        Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT).show();
                    } else {
                        Log4Util.d(CCPHelper.DEMO_TAG, "Sorry , can't handle a message " + msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CCPHelper.getInstance().setRegistCallback(null);
            }
        });
    }

    protected void onReceiveBroadcast(Intent intent) {

    }

    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;

    void updateExternalStorageState(){
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        handleExternalStorageState(mExternalStorageAvailable,
                mExternalStorageWriteable);
    }

    void handleExternalStorageState(boolean available, boolean writeable) {

        if(!available || !writeable) {

            Toast.makeText(getApplicationContext(), R.string.media_ejected, Toast.LENGTH_LONG).show();
        } else {

            TaskApp.getInstance().getVoiceStore();
        }

    }

    public boolean checkeDeviceHelper() {
        Device device = CCPHelper.getInstance().getDevice();
        if(device == null) {
            return false;
        }
        return true;
    }


    public void addTask(ITask iTask){
        ThreadPoolManager.getInstance().setOnTaskDoingLinstener(this);
        ThreadPoolManager.getInstance().addTask(iTask);
    }

    @Override
    public void doTaskBackGround(ITask iTask) {
        handleTaskBackGround(iTask);
    }

    protected void handleTaskBackGround(ITask iTask) {
        if(iTask.getKey() == TaskKey.KEY_SDK_REGIST) {
            CCPHelper.getInstance().registerCCP(this);
        } else if (iTask.getKey() == TaskKey.KEY_SDK_UNREGIST) {    // 与云通讯断开连接
            CCPHelper.getInstance().release();
            CCPCall.shutdown();
        }
    }

    private ProgressDialog pVideoDialog = null;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what == WHAT_SHOW_PROGRESS) {
                pVideoDialog = new ProgressDialog(BaseActivity.this);
                //pVideoDialog.setTitle(R.string.str_dialog_title);
                //pVideoDialog.setMessage(getString(R.string.str_dialog_message_default));
                pVideoDialog.setCanceledOnTouchOutside(false);
                String message = (String) msg.obj;
                if(!TextUtils.isEmpty(message))
                    pVideoDialog.setMessage(message);
                pVideoDialog.show();
            } else if (msg.what == WHAT_CLOSE_PROGRESS) {
                if(pVideoDialog != null ) {
                    pVideoDialog.dismiss();
                    pVideoDialog = null;
                }
            } else {
                switch (msg.what) {
//                    case R.layout.ads_tops_view:
//                        if(msg.obj != null && msg.obj instanceof View) {
//                            removeNotificatoinView((View)msg.obj);
//                        }
//                        break;

                    default:
                        handleNotifyMessage(msg);
                        break;
                }
            }
        };
    };


    @SuppressLint("HandlerLeak")
    public Handler getBaseHandle() {
        return handler;
    }

    protected void handleNotifyMessage(Message msg) {

    }



    /**
     * Plays the specified tone for TONE_LENGTH_MS milliseconds.
     *
     * The tone is played locally, using the audio stream for phone calls.
     * Tones are played only if the "Audible touch tones" user preference
     * is checked, and are NOT played if the device is in silent mode.
     *
     * @param tone a tone code from {@link android.media.ToneGenerator}
     */
    public void playTone(int tone ,int durationMs) {

        // Also do nothing if the phone is in silent mode.
        // We need to re-check the ringer mode for *every* )
        // call, rather than keeping a local flag that's updated in
        // onResume(), since it's possible to toggle silent mode without
        // leaving the current activity (via the ENDCALL-longpress menu.)
        int ringerMode = mAudioManager.getRingerMode();
        if ((ringerMode == AudioManager.RINGER_MODE_SILENT)
                || (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
            return;
        }

        synchronized(mToneGeneratorLock) {
            if (mToneGenerator == null) {
                Log4Util.d("playTone: mToneGenerator == null, tone: " + tone);
                return;
            }

            // Start the new tone (will stop any playing tone)
            mToneGenerator.startTone(tone, durationMs);
        }
    }

    public void stopTone() {
        if(mToneGenerator != null)
            mToneGenerator.stopTone();
    }


    // Access to the audio manager and vibration manager
    // Initialize the manager parameters, is initial
    private void initScreenStates() {
        if(mAudioManager == null) {
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        }
//        if (mWindowManager == null) {
//            mWindowManager = ((WindowManager) getSystemService(Context.WINDOW_SERVICE));
//            mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        }
        synchronized (mToneGeneratorLock) {
            if (mToneGenerator == null) {
                try {
                    int streamVolume = mAudioManager.getStreamVolume(STREAM_TYPE);
                    int streamMaxVolume = mAudioManager.getStreamMaxVolume(STREAM_TYPE);
                    int volume = (int) (TONE_RELATIVE_VOLUME * (streamVolume / streamMaxVolume));
                    mToneGenerator = new ToneGenerator(STREAM_TYPE,
                            volume);

                } catch (RuntimeException e) {
                    Log4Util.d("Exception caught while creating local tone generator: "
                            + e);
                    mToneGenerator = null;
                }
            }
        }
        //mWakeLock = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
        //        | PowerManager.ACQUIRE_CAUSES_WAKEUP, CCPHelper.DEMO_TAG);
    }



    /**
     * Triggers haptic feedback
     * Can also be based on the system settings to enable touch feedback
     */
    public synchronized void vibrate(long milliseconds) {
        if (mVibrator == null) {
            mVibrator = (Vibrator) /*new Vibrator();*/getSystemService(Context.VIBRATOR_SERVICE);
        }
        mVibrator.vibrate(milliseconds);
    }
}
