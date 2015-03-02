package com.zuzhili.ui.activity.im;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.hisun.phone.core.voice.Device;
import com.hisun.phone.core.voice.model.im.IMAttachedMsg;
import com.hisun.phone.core.voice.model.im.IMTextMsg;
import com.hisun.phone.core.voice.model.im.InstanceMsg;
import com.hisun.phone.core.voice.util.VoiceUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zuzhili.R;
import com.zuzhili.bussiness.helper.CCPHelper;
import com.zuzhili.bussiness.socket.model.GroupInfo;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.CCPUtil;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.IMParseUtil;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.controller.NonPagingResultsAdapter;
import com.zuzhili.controller.TalkDetailListAdapter;
import com.zuzhili.db.IMMessageTable;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.TaskApp;
import com.zuzhili.framework.im.CCPAudioManager;
import com.zuzhili.framework.im.CCPIntentUtils;
import com.zuzhili.framework.im.CCPPreferenceSettings;
import com.zuzhili.framework.im.ITask;
import com.zuzhili.framework.im.TaskKey;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.framework.utils.VolleyImageUtils;
import com.zuzhili.model.im.IMChatMessageDetail;
import com.zuzhili.model.msg.Attachment;
import com.zuzhili.model.multipart.ImageItem;
import com.zuzhili.service.SyncUtils;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.multiselect.FileBrowserActivity;
import com.zuzhili.ui.views.AppPanel;
import com.zuzhili.ui.views.CCPEditText;
import com.zuzhili.ui.views.EmojiGrid;
import com.zuzhili.ui.views.PagingListView;
import com.zuzhili.ui.views.RecordPopupWindow;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GroupChatActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback
        , OnClickListener
        , View.OnTouchListener
        , NonPagingResultsAdapter.ShowLastItemViewCallback
        , EmojiGrid.OnEmojiItemClickListener
        , BaseActivity.HandleProgressBarVisibilityCallback{

    private String username;

    private static final int ampValue[] = {
            0, 20, 30, 45, 60, 85, 100
    };

    private static final int ampIcon[] = {
            R.drawable.voice_interphone01,
            R.drawable.voice_interphone02,
            R.drawable.voice_interphone03,
            R.drawable.voice_interphone04,
            R.drawable.voice_interphone05,
            R.drawable.voice_interphone06,
    };
    //底部展示的排版
    @ViewInject(R.id.rla_hided_bottom)
    private RelativeLayout bottomView;
    //发布展示按钮
    @ViewInject(R.id.btn_expand_bottom)
    private Button publishButton;
    //表情按钮
    @ViewInject(R.id.btn_select_face)
    private Button faceButton;
    //选择按钮
    @ViewInject(R.id.btn_select_voice)
    private Button voiceButton;
    //表情
    @ViewInject(R.id.chatting_app_panel)
    private AppPanel mAppPanel;
    //需要发送的内容
    @ViewInject(R.id.edit_input_box)
    private CCPEditText inputBoxEidt;
    //内容发送按钮
    @ViewInject(R.id.btn_send_msg)
    private Button sendButton;
    //拍照
    @ViewInject(R.id.photograph)
    private TextView photograph;
    //照片选择
    @ViewInject(R.id.photo)
    private TextView photo;
    //文件选择
    @ViewInject(R.id.file)
    private TextView file;
    //语音
    @ViewInject(R.id.voice)
    private Button voice;

    @ViewInject(R.id.voice_rcd_cancel)
    private TextView rVoiceCancleText;

    @ViewInject(R.id.voice_rcd_cancle_icon)
    private ImageView mCancleIcon;

    @ViewInject(R.id.dialog_img)
    private ImageView ampImage;

    @ViewInject(R.id.listView)
    private PagingListView mListView;

    @ViewInject(R.id.progressbar)
    private ProgressBar progressBar;

    private View mVoiceShortLy;
    private View mVoiceLoading;
    private View mVoiceRecRy;

    //获取需要拍照的图片
    private static String localTempImageFileName = "";

    private static final int REQ_CODE_ACTIVITY_CHAT_ROOM_SETTINGS = 0;  // 聊天详情
    private static final int REQ_CODE_ACTIVITY_CEMERA = 1;          // 拍照
    private static final int REQ_CODE_ACTIVITY_SELECT_FILE = 2;     // 文件
    private static final int REQ_CODE_ACTIVITY_SELECT_IMAGE = 3;    // 手机选择

    //根据相关路径获取图片
    public static final String IMAGE_PATH = "zhiliren/images";
    public static final File FILE_SDCARD = Environment.getExternalStorageDirectory();
    public static final File FILE_LOCAL = new File(FILE_SDCARD, IMAGE_PATH);
    public static final File FILE_PIC_SCREENSHOT = new File(FILE_LOCAL, "zhiliren/images/screenshots");

    private TalkDetailListAdapter adapter;
    /**
     * 谈话对方身份
     */
    public UserInfo oppositeSide;
    /**
     * 群聊 聊天人列表
     */
    private List<UserInfo> groupChatFriends;
    /**
     * 对话人身份编号
     */
    private String toIds;
    /**
     * 若发送的信息带附件，保存附件信息用作造假数据之用
     */
    private List<Attachment> localAttachments;
    /**
     * 发送的消息内容
     */
    private String content;

    /**
     * 是否需要获得聊天群组所有用户的信息
     */
    private boolean isNeedGetGroupUser;
    /**
     * 群组id
     */
    private String groupId;
    /**
     * 云通讯群组id
     */
    private String y_groupId;
    /**
     * 群聊天室名称
     */
    private String groupName;

    private boolean isGroupChat = false;
    /**
     * 群组人数
     */
    private String groupUserCount;
    /**
     * 聊天室类型（自定义或会议室）
     */
    private String chatRoomType;

    private GroupInfo group;

    public static final String KEY_Message = "message";
    public static final String KEY_GROUP_ID = "groupId";
    public static final String KEY_GROUP_NAME = "groupName";
    public static final String KEY_MESSAGE_ID = "messageId";
    public static final String KEY_IMMESSAGE_DETAIL = "messageDetail";

    public static final int CHAT_MODE_IM_GROUP = 0x2;
    public static final int CHAT_MODE_IM_POINT = 0x1;

    private int chatModel = CHAT_MODE_IM_POINT;

    private float mTouchStartY = 0;
    private float mDistance = 0;

    private boolean isCancle = false;
    private boolean doReocrdAction = false;

    private IMMessageTable messageTable;

    // cancel recording sliding distance field.
    private static final int CANCLE_DANSTANCE = -60;

    public int mRecordState = 0;
    // recording of three states
    public static final int RECORD_NO = 0;
    public static final int RECORD_ING = 1;
    public static final int RECORD_ED = 2;

    private RecordPopupWindow popupWindow = null;

    private String currentRecName;
    private boolean isRecordAndSend = false;

    public static HashMap<String, Boolean> voiceMessage = new HashMap<String, Boolean>();

    private long recodeTime = 0;

    private static final int MIX_TIME = 1000;

    private static final int WHAT_ON_COMPUTATION_TIME = 10000;


    public static final int REQUEST_CODE_TAKE_PICTURE = 11;
    public static final int REQUEST_CODE_SELECT_FILE = 12;

    private int mode;

    private InputMethodManager mInputMethodManager;

    private int msgType;
    private String forwardText;
    private boolean isVoice=true;
    private SharedPreferences mPreference;

    /**
     * 发送消息
     * @param view
     */
    @OnClick(R.id.btn_send_msg)
    public void sendMsg(View view) {
        if (inputBoxEidt.getText().toString().length() > 0) {

            if (!mSession.isChatLogin()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.hint_account_login_somewhere)
                        .setTitle(R.string.login);
                builder.setPositiveButton(R.string.relogin, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        progressDialog = new ProgressDialog(GroupChatActivity.this);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("请稍后");
                        progressDialog.show();

                        doSDKUnregist();
                        Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CCPHelper.getInstance().setHandler(mIMChatHandler);
                                doSDKRegist();
                            }
                        }, 1000);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                builder.show();
                return;
            }

            content = TextUtil.filterEmoji(inputBoxEidt.getText().toString());
            inputBoxEidt.setText("");

            try {
                IMChatMessageDetail chatMessageDetail = IMChatMessageDetail.getGroupItemMessage(IMChatMessageDetail.TYPE_MSG_TEXT, IMChatMessageDetail.STATE_IM_SENDING, y_groupId, mSession.getVoipId(), buildUserData(IMChatMessageDetail.TYPE_MSG_TEXT));
                chatMessageDetail.setMessageContent(content);
                chatMessageDetail.setIdentityId(Utils.getIdentity(Session.get(getApplicationContext())));

                String uniqueID = getDeviceHelper().sendInstanceMessage(y_groupId, content, null, buildUserData(IMChatMessageDetail.TYPE_MSG_TEXT));
                if (TextUtils.isEmpty(uniqueID)) {
                    Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                    chatMessageDetail.setImState(IMChatMessageDetail.STATE_IM_SEND_FAILED);
                    return;
                }
                chatMessageDetail.setMessageId(uniqueID);
                chatMessageDetail.setListId(mSession.getListid());
                messageTable.insertIMMessage(chatMessageDetail);

                sendBroadcast(chatMessageDetail);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        super.setContentView(R.layout.activity_talkdetail);
        ViewUtils.inject(this);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mAppPanel.setOnEmojiItemClickListener(this);
        // 服务是不存在任务栈的 ,要在服务里面开启activity的话 必须添加这样一个flag
//        lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        isRecordAndSend = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean(CCPPreferenceSettings.SETTING_VOICE_ISCHUNKED
                .getId(), ((Boolean) CCPPreferenceSettings.SETTING_VOICE_ISCHUNKED
                .getDefaultValue()).booleanValue());

        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mListView.setCacheColorHint(Color.TRANSPARENT);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (inputBoxEidt.isFocused()) {
                    mInputMethodManager.hideSoftInputFromWindow(inputBoxEidt.getWindowToken(), 0);
                    inputBoxEidt.clearFocus();
                }
                if (bottomView.getVisibility() == View.VISIBLE) {
                    bottomView.setVisibility(View.GONE);
                }
                if (mAppPanel.getVisibility() == View.VISIBLE) {
                    mAppPanel.setVisibility(View.GONE);
                }
                return false;
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final IMChatMessageDetail item = adapter.getItem(position-1);
                if(item.getMessageType()==IMChatMessageDetail.TYPE_MSG_VOICE ){
                    return true;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatActivity.this);

                builder.setTitle("消息操作");
                builder.setItems(R.array.chat_action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        switch (which) {
                            case 0:
                                ClipData clip = ClipData.newPlainText("simple text", item.getMessageContent());
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(getBaseContext(), "已复制", Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                //转发
                                Intent intent = new Intent(GroupChatActivity.this, ForwardActivity.class);
                                intent.putExtra(Constants.MSG_FORWORD, item.getMessageContent());
                                intent.putExtra(Constants.MSG_TYPE, IMChatMessageDetail.TYPE_MSG_TEXT);

                                startActivity(intent);
                                finish();
                                break;
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
        progressBar.setVisibility(View.GONE);

        setCustomActionBarCallback(this);
        initData();

        CCPHelper.getInstance().setHandler(mIMChatHandler);
        messageTable = dbHelper.getMessageTable();

        //转发 文本或者图片
        if(msgType!=0 && IMChatMessageDetail.TYPE_MSG_TEXT==msgType){
            if(!TextUtils.isEmpty(forwardText)) {
                inputBoxEidt.setText(forwardText);
                sendMsg(inputBoxEidt);
            }
        }else {
            if(!TextUtils.isEmpty(forwardText)) {
                createIMFileMessage(null,forwardText);
            }
        }

        if (checkeDeviceHelper()) {
            initialize();
        }

        SyncUtils.TriggerRefresh();
    }

    private void initialize() {
        if (TextUtils.isEmpty(y_groupId)) {
            Toast.makeText(getApplicationContext(), "群组ID错误", Toast.LENGTH_SHORT).show();
        }

        if (y_groupId == null) {
            y_groupId = "0";
        }

        if (y_groupId.startsWith("g")) {
            chatModel = CHAT_MODE_IM_GROUP;
        } else {
            chatModel = CHAT_MODE_IM_POINT;
        }
        mSession.setY_groupId(y_groupId);
        adapter = new TalkDetailListAdapter(this, mListView, ImageCacheManager.getInstance().getImageLoader(), mSession, y_groupId, this);
        mListView.setAdapter(adapter);
    }


    /**
     * 选择表情弹窗
     */
    private void showFace() {
        if (bottomView.getVisibility() == View.VISIBLE) {
            bottomView.setVisibility(View.GONE);
        }
        inputBoxEidt.requestFocus();
        int mode = mAppPanel.isPanelVisible() ? 2 : 3;
        setMode(mode, false);
    }

    public int getMode() {
        return mode;
    }

    /**
     * @param mode
     * @paramb
     */
    public void setMode(int mode, boolean input) {
        this.mode = mode;
        switch (mode) {
            case 1:
                inputBoxEidt.requestFocus();
                resetChatFooter(false, false);
                break;
            case 2:
                resetChatFooter(true, false);
                break;
            case 3:
                resetChatFooter(true, true);
                break;
        }

        if (input) {
            mInputMethodManager.showSoftInput(inputBoxEidt, 0);
        } else {
            mInputMethodManager.hideSoftInputFromWindow(inputBoxEidt.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Reset chat footer tool panel
     *
     * @param isEmoji Whether to display the expression panel
     */
    private void resetChatFooter(boolean isTools, boolean isEmoji) {
        if (!isTools) {
            mAppPanel.setPanelGone();
            return;
        }
        if (isEmoji) {
            mAppPanel.swicthToPanel(AppPanel.APP_PANEL_NAME_DEFAULT);
            mAppPanel.setVisibility(View.VISIBLE);
        } else {
            mAppPanel.setPanelGone();
        }
    }

    private void initData() {
        Intent intent = super.getIntent();
        oppositeSide = (UserInfo) intent.getSerializableExtra(Constants.EXTRA_IM_CONTACT);
        groupChatFriends = intent.getParcelableArrayListExtra(Constants.EXTRA_PARCELABLE_CONTACTS);
        y_groupId = intent.getStringExtra(Constants.EXTRA_IM_YGROUPID);
        groupId = intent.getStringExtra(Constants.EXTRA_IM_GROUPID);
        groupName = intent.getStringExtra(Constants.EXTRA_IM_GROUP_NAME);
        isGroupChat = intent.getBooleanExtra(Constants.EXTRA_GROUP_CHAT_FLAG, false);
        chatRoomType = intent.getStringExtra(Constants.EXTRA_IM_CHAT_ROOM_TYPE);
        groupUserCount = intent.getStringExtra(Constants.EXTRA_IM_GROUP_USER_COUNT);
        isNeedGetGroupUser = intent.getBooleanExtra(Constants.EXTRA_IM_NEED_GET_GROUP_USER, false);
        group = (GroupInfo) intent.getSerializableExtra(Constants.EXTRA_IM_GROUP);
        forwardText = intent.getStringExtra(Constants.MSG_FORWORD);
        msgType = intent.getIntExtra(Constants.MSG_TYPE, 0);

        if (isNeedGetGroupUser) {
            // 拉取组用户
        }

        // p2p chat
        if (!isGroupChat) {
            username = oppositeSide.getU_name();
            toIds = String.valueOf(oppositeSide.getU_id());
        }

        voiceButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isVoice) {
                    voiceButton.setBackgroundResource(R.drawable.keyboard_btn_selector);
                    voice.setVisibility(View.VISIBLE);
                    inputBoxEidt.setVisibility(View.GONE);
                    faceButton.setVisibility(View.GONE);
                    sendButton.setVisibility(View.GONE);
                    mInputMethodManager.hideSoftInputFromWindow(inputBoxEidt.getWindowToken(), 0);
                    inputBoxEidt.clearFocus();
                    if (bottomView.getVisibility() == View.VISIBLE) {
                        bottomView.setVisibility(View.GONE);
                    }
                    if (mAppPanel.getVisibility() == View.VISIBLE) {
                        mAppPanel.setVisibility(View.GONE);
                    }
                    isVoice = false;
                } else {
                    voiceButton.setBackgroundResource(R.drawable.voice_btn_selector);
                    voice.setVisibility(View.GONE);
                    inputBoxEidt.setVisibility(View.VISIBLE);
                    faceButton.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.VISIBLE);
                    if (bottomView.getVisibility() == View.VISIBLE) {
                        bottomView.setVisibility(View.GONE);
                    }
                    inputBoxEidt.requestFocus();
                    mInputMethodManager.showSoftInput(inputBoxEidt, 0);
                    isVoice = true;
                }


                if (TextUtils.isEmpty(inputBoxEidt.getText().toString())) {
                    publishButton.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.GONE);
                    sendButton.setEnabled(false);
                } else {
                    sendButton.setVisibility(View.VISIBLE);
                    publishButton.setVisibility(View.GONE);
                    sendButton.setEnabled(true);
                }
                if (inputBoxEidt.getText().toString().length() == 140) {
                    Toast.makeText(getBaseContext(), R.string.msg_more, Toast.LENGTH_LONG).show();
                }
            }
        });

        sendButton.setEnabled(false);

        inputBoxEidt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(inputBoxEidt.getText().toString())) {
                    publishButton.setVisibility(View.VISIBLE);
                    sendButton.setVisibility(View.GONE);
                    sendButton.setEnabled(false);
                } else {
                    sendButton.setVisibility(View.VISIBLE);
                    publishButton.setVisibility(View.GONE);
                    sendButton.setEnabled(true);
                }
                if (inputBoxEidt.getText().toString().length() == 140) {
                    Toast.makeText(getBaseContext(), R.string.msg_more, Toast.LENGTH_LONG).show();
                }
            }
        });
        if(mPreference==null){
            mPreference = PreferenceManager.getDefaultSharedPreferences(this);
        }
        if(mPreference.contains(y_groupId)) {
            String draft = mPreference.getString(y_groupId, "");
            if (!TextUtils.isEmpty(draft)) {
                inputBoxEidt.setEmojiText(draft);
            }
        }
        inputBoxEidt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(140)});
        inputBoxEidt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (bottomView.getVisibility() == View.VISIBLE) {
                    bottomView.setVisibility(View.GONE);
                }
                if (mAppPanel.getVisibility() == View.VISIBLE) {
                    mAppPanel.setVisibility(View.GONE);
                }
                return false;
            }
        });

        //设置发布按钮
        publishButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.setSelection(mListView.getCount());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputBoxEidt.getWindowToken(), 0);
                inputBoxEidt.clearFocus();
                if (mAppPanel.getVisibility() == View.VISIBLE) {
                    mAppPanel.setVisibility(View.GONE);
                }
                controlBottomViewVisibility(100);
            }

        });
        //表情按钮点击
        faceButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showFace();
            }
        });
        //拍照
        photograph.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                controlBottomViewVisibility(0);
                goCamara();
            }
        });

        //照片
        photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(GroupChatActivity.this, ShowImageActivity.class);
                intent.putExtra(Constants.TAG_CHAT_CONTACTS,true);
                startActivityForResult(intent, REQ_CODE_ACTIVITY_SELECT_IMAGE);
            }

        });

        //选择文件
        file.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                controlBottomViewVisibility(0);
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(getApplicationContext(), R.string.sdcard_not_file_trans_disable, Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(GroupChatActivity.this, FileBrowserActivity.class);
                //intent.putExtra("to", recipient);
                startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
            }
        });
        voice.setOnTouchListener(this);
    }

    /**
     * voice
     */
    long currentTimeMillis = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        long time = System.currentTimeMillis() - currentTimeMillis;
        if (time <= 300) {
            currentTimeMillis = System.currentTimeMillis();
            return false;
        }

        if (!CCPUtil.isExistExternalStore()) {
            Toast.makeText(getBaseContext(), R.string.media_ejected, Toast.LENGTH_LONG).show();
            return false;
        }

        int[] location = new int[2];
        v.getLocationOnScreen(location);
        mTouchStartY = location[1];
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                doReocrdAction = true;
                setCancle(false);
                onRecordInit();
                voice.setBackgroundResource(R.drawable.grey_box);
                voice.setText(getString(R.string.voice_cancel_rcd_end));
                break;

            case MotionEvent.ACTION_MOVE:
                mDistance = event.getRawY() - mTouchStartY;
                //上滑取消
                if (mDistance < CANCLE_DANSTANCE) {
                    if (rVoiceCancleText != null) {
                        rVoiceCancleText.setText(R.string.voice_cancel_rcd_release);
                        mCancleIcon.setVisibility(View.VISIBLE);
                        ampImage.setVisibility(View.GONE);
                    }
                    isCancle = true;
                } else {
                    rVoiceCancleText.setText(R.string.voice_cancel_rcd);
                    mCancleIcon.setVisibility(View.GONE);
                    ampImage.setVisibility(View.VISIBLE);
                    isCancle = false;
                }

                break;
            case MotionEvent.ACTION_UP:
                if (doReocrdAction) {
                    voice.setEnabled(false);
                    voice.setBackgroundResource(R.drawable.white_box);
                    voice.setOnTouchListener(null);
                    if (isCancle) {
                        onRecordCancle();
                    } else {
                        onRecordOver();
                    }
                }
                break;
        }
        return true;
    }

    public void setCancle(boolean isCancle) {
        this.isCancle = isCancle;
    }

    public boolean isVoiceRecordCancle() {
        return isCancle;
    }

    String uniqueId = null;

    public void onRecordInit() {

        if (getRecordState() != RECORD_ING) {
            setRecordState(RECORD_ING);

            // release all playing voice file
            releaseVoiceAnim(-1);
            readyOperation();

            showVoiceDialog(findViewById(R.id.im_root).getHeight() - findViewById(R.id.rla_bottom_container).getHeight());

            // True audio data recorded immediately transmitted to the server
            // False just recording audio data, then send audio file after the completion of recording..
            // isRecordAndSend = true;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    currentRecName = +System.currentTimeMillis() + ".amr";
                    File directory = getCurrentVoicePath();
                    if (checkeDeviceHelper()) {
                        // If it is sent non't in chunked mode, only second parameters
                        try {
                            //发送语音   isRecordAndSend为false则保存在本地,然后进行上传
                            uniqueId = getDeviceHelper().startVoiceRecording(y_groupId, directory.getAbsolutePath(), isRecordAndSend, buildUserData(IMChatMessageDetail.TYPE_MSG_VOICE));
                            voiceMessage.put(uniqueId, true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }
    }

    // display dialog recordings
    public void showVoiceDialog(int height) {
        int heightDensity = Math.round(180 * getResources().getDisplayMetrics().densityDpi / 160.0F);
        int density = CCPUtil.getMetricsDensity(getBaseContext(), 50.0F);
        if (popupWindow == null) {
            View view = View.inflate(this, R.layout.voice_rec_dialog, null);
            popupWindow = new RecordPopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            ampImage = ((ImageView) popupWindow.getContentView().findViewById(R.id.dialog_img));
            mCancleIcon = ((ImageView) popupWindow.getContentView().findViewById(R.id.voice_rcd_cancle_icon));
            rVoiceCancleText = ((TextView) this.popupWindow.getContentView().findViewById(R.id.voice_rcd_cancel));
            mVoiceLoading = this.popupWindow.getContentView().findViewById(R.id.voice_rcd_hint_loading);
            mVoiceRecRy = this.popupWindow.getContentView().findViewById(R.id.voice_rcd_rl);
            mVoiceShortLy = this.popupWindow.getContentView().findViewById(R.id.voice_rcd_tooshort);
        }
        mVoiceLoading.setVisibility(View.VISIBLE);
        mVoiceShortLy.setVisibility(View.GONE);
        mVoiceRecRy.setVisibility(View.GONE);
        ampImage.setVisibility(View.VISIBLE);
        ampImage.setBackgroundResource(ampIcon[0]);
        mCancleIcon.setVisibility(View.GONE);
        popupWindow.showAtLocation(findViewById(R.id.im_root), Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, density + (height - heightDensity) / 2);
    }

    /**
     * 移除pop
     */
    public synchronized void removePopuWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            mVoiceRecRy.setVisibility(View.VISIBLE);
            ampImage.setVisibility(View.VISIBLE);
            mCancleIcon.setVisibility(View.GONE);
            mVoiceLoading.setVisibility(View.GONE);
            mVoiceShortLy.setVisibility(View.GONE);
            rVoiceCancleText.setText(R.string.voice_cancel_rcd);
        }
        voice.setText(getString(R.string.voice_send));
        mIMChatHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                voice.setEnabled(true);
                voice.setOnTouchListener(GroupChatActivity.this);
                doReocrdAction = false;
                currentTimeMillis = System.currentTimeMillis();
            }
        }, 100);
    }

    /**
     * 消息时长过短
     */
    public synchronized void tooShortPopuWindow() {
        voice.setEnabled(false);
        if (popupWindow != null) {
            mVoiceShortLy.setVisibility(View.VISIBLE);
            mVoiceLoading.setVisibility(View.GONE);
            mVoiceRecRy.setVisibility(View.GONE);
            popupWindow.update();
        }
        if (mIMChatHandler != null) {
            mIMChatHandler.removeMessages(CCPHelper.WHAT_ON_DIMISS_DIALOG);
            mIMChatHandler.sendEmptyMessageDelayed(CCPHelper.WHAT_ON_DIMISS_DIALOG, 500L);
        }
    }

    //语音存储
    private File getCurrentVoicePath() {
        File directory = new File(TaskApp.getInstance().getVoiceStore(), currentRecName);
        return directory;
    }

    public int getRecordState() {
        return mRecordState;
    }

    public void setRecordState(int state) {
        this.mRecordState = state;
    }

    private long computationTime = -1L;
    private Toast mRecordTipsToast;

    private void readyOperation() {
        computationTime = -1L;
        mRecordTipsToast = null;
        playTone(ToneGenerator.TONE_PROP_BEEP, TONE_LENGTH_MS);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                stopTone();
            }
        }, TONE_LENGTH_MS);
        vibrate(50L);
    }


    private void controlBottomViewVisibility(int delayTime) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (bottomView.getVisibility() == View.GONE) {
                    bottomView.setVisibility(View.VISIBLE);
                } else {
                    bottomView.setVisibility(View.GONE);
                }
            }
        }, delayTime);
    }

    public void onRecordCancle() {
        handleMotionEventActionUp(true);
    }

    public void onRecordOver() {
        handleMotionEventActionUp(false);
    }


    /**
     * @version 3.5
     * stop recording and performing transmission or cancel the operation
     */
    private void handleMotionEventActionUp(boolean doCancle) {

        if (getRecordState() == RECORD_ING) {

            if (checkeDeviceHelper()) {
                if (doCancle) {
                    /**
                     * 取消录音实时上传，仅chunk模式下使用
                     */
                    getDeviceHelper().cancelVoiceRecording();
                } else {
                    /**
                     * 停止录制语音
                     * 如果为chunk模式，停止录音并完成上传语音文件，如果是非chunked模式仅停止录音
                     */
                    getDeviceHelper().stopVoiceRecording();
                }
            }
            doProcesOperationRecordOver(doCancle);
        }
    }

    /**
     * @param isCancleSend
     */
    private void doProcesOperationRecordOver(boolean isCancleSend) {
        if (getRecordState() == RECORD_ING) {
            boolean isVoiceToShort = false;

            if (new File(getCurrentVoicePath().getAbsolutePath()).exists() && checkeDeviceHelper()) {
                //根据传入的语音文件名，返回毫秒级别的文件时长
                recodeTime = getDeviceHelper().getVoiceDuration(getCurrentVoicePath().getAbsolutePath());
                // if not chunked ,then the voice file duration is greater than 1000ms.
                // If it is chunked. the voice file exists that has been send out
                if (!isRecordAndSend) {
                    if (recodeTime < MIX_TIME) {
                        isVoiceToShort = true;
                    }
                }
            } else {
                isVoiceToShort = true;
            }

            setRecordState(RECORD_NO);

            if (isVoiceToShort && !isCancleSend) {
                tooShortPopuWindow();
                return;
            }

            removePopuWindow();

            if (!isCancleSend) {
                IMChatMessageDetail mVoicechatMessageDetail = IMChatMessageDetail.getGroupItemMessage(IMChatMessageDetail.TYPE_MSG_VOICE, IMChatMessageDetail.STATE_IM_SENDING, y_groupId, mSession.getVoipId(), buildUserData(IMChatMessageDetail.TYPE_MSG_VOICE));
                mVoicechatMessageDetail.setFilePath(getCurrentVoicePath().getAbsolutePath());
                if (!isRecordAndSend && checkeDeviceHelper()) {
                    // send
                    uniqueId = getDeviceHelper().sendInstanceMessage(y_groupId,
                            null, getCurrentVoicePath().getAbsolutePath(), buildUserData(IMChatMessageDetail.TYPE_MSG_VOICE));
                } else {
                    voiceMessage.remove(uniqueId);
                }
                try {
                    mVoicechatMessageDetail.setMessageId(uniqueId);
                    mVoicechatMessageDetail.setUserData(buildUserData(IMChatMessageDetail.TYPE_MSG_VOICE));
                    mVoicechatMessageDetail.setFileExt("amr");
                    mVoicechatMessageDetail.setListId(mSession.getListid());
                    mVoicechatMessageDetail.setCurDate(String.valueOf((new Date()).getTime()));
                    messageTable.insertIMMessage(mVoicechatMessageDetail);
                    sendBroadcast(mVoicechatMessageDetail);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        recodeTime = 0;
    }

    /**
     * 点用内容提供者拍照
     */
    private void goCamara() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                localTempImageFileName = "";
                localTempImageFileName = String.valueOf((new Date()).getTime()) + ".jpg";
                File filePath = FILE_PIC_SCREENSHOT;
                if (!filePath.exists()) {
                    filePath.mkdirs();
                }
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = new File(filePath, localTempImageFileName);
                Uri u = Uri.fromFile(f);
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_ACTIVITY_CHAT_ROOM_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
//                    groupChatFriends = data.getParcelableArrayListExtra(Constants.EXTRA_PARCELABLE_CONTACTS);
                    groupUserCount=data.getStringExtra(Constants.EXTRA_PARCELABLE_CONTACTS);
                    String gName = data.getStringExtra(Constants.EXTRA_IM_GROUP_NAME);
                    if (gName != null) {
                        groupName = gName;
                    }
                    String tempName=null;
                    if(groupName.length()>13){
                        tempName=groupName.substring(0,13)+"...";
                    }else {
                        tempName=groupName;
                    }
                    initActionBar(R.drawable.icon_back, R.drawable.im_add_contact, tempName, false);
                    if(data.getBooleanExtra(Constants.EXTRA_IM_GROUP_CLEAR,false)){
                        if (adapter != null) {
                            for (int i = 0; i < adapter.getCount(); i++) {
                                IMChatMessageDetail item = adapter.getItem(i);
                                if (item == null || item.getMessageType() == IMChatMessageDetail.TYPE_MSG_TEXT) {
                                    continue;
                                }
                                CCPUtil.delFile(item.getFilePath());
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.clearList();
                                }
                            });
                        }
                    }
                } else {
                    // 已解散该聊天室
                    Intent it = new Intent();
                    it.putExtra("groupId", groupId);
                    it.putExtra(Constants.EXTRA_FINISH_ACTIVITY, true);
                    setResult(RESULT_OK, it);
                    finish();
                }
            }
        }

        String fileName = null;
        String filePath = null;
        // If there's no data (because the user didn't select a file or take pic  and
        if (requestCode != REQUEST_CODE_TAKE_PICTURE || requestCode == REQ_CODE_ACTIVITY_SELECT_IMAGE || requestCode == REQUEST_CODE_SELECT_FILE) {
            if (data == null) {
                return;
            }
        } else if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            //拍照
            case REQUEST_CODE_TAKE_PICTURE: {
                File file = new File(FILE_PIC_SCREENSHOT, localTempImageFileName);
                if (file == null || !file.exists()) {
                    return;
                }
                filePath = file.getAbsolutePath();
                VolleyImageUtils.compress(file, DensityUtil.getScreenWidth(this), DensityUtil.getScreenHeight(this));
                createIMFileMessage(fileName, filePath);
                break;
            }

            case REQ_CODE_ACTIVITY_SELECT_IMAGE: {
                List<ImageItem> images = (List<ImageItem>) data.getSerializableExtra(Constants.IMAGE_CHOOSED_LIST);
                for (ImageItem item : images) {
                    filePath = item.getImagePath();
                    createIMFileMessage(fileName, filePath);
                }
                break;
            }
            //发送文件
            case REQUEST_CODE_SELECT_FILE: {

                if (data.hasExtra("file_name")) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        fileName = extras.getString("file_name");
                    }
                }

                if (data.hasExtra("file_url")) {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        filePath = extras.getString("file_url");
                    }
                }
                createIMFileMessage(fileName, filePath);
                break;
            }
        }
    }

    /**
     * 发送文件和图片消息
     */
    private void createIMFileMessage(String fileName, String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            Toast.makeText(GroupChatActivity.this, R.string.toast_file_exist, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(fileName)) {
            fileName = new File(filePath).getName();
        }

        IMChatMessageDetail chatMessageDetail = IMChatMessageDetail.getGroupItemMessage(IMChatMessageDetail.TYPE_MSG_FILE
                , IMChatMessageDetail.STATE_IM_SENDING, y_groupId, mSession.getVoipId(), buildUserData(IMChatMessageDetail.TYPE_MSG_FILE));
        chatMessageDetail.setMessageContent(fileName);
        chatMessageDetail.setFilePath(filePath);
        String extensionName = VoiceUtil.getExtensionName(fileName);
        if ("amr".equals(extensionName)) {
            chatMessageDetail.setMessageType(IMChatMessageDetail.TYPE_MSG_VOICE);
        } else if ("jpg".equals(extensionName) || "jpeg".equals(extensionName)|| "png".equals(extensionName)) {
            chatMessageDetail.setMessageType(IMChatMessageDetail.TYPE_MSG_PIC);
        }
        chatMessageDetail.setFileExt(extensionName);

        if (!checkeDeviceHelper()) {
            return;
        }

        try {
            String uniqueID = getDeviceHelper().sendInstanceMessage(y_groupId, null, filePath, buildUserData(IMChatMessageDetail.TYPE_MSG_FILE));
            chatMessageDetail.setMessageId(uniqueID);
            chatMessageDetail.setListId(mSession.getListid());
            chatMessageDetail.setCurDate(String.valueOf((new Date()).getTime()));
            messageTable.insertIMMessage(chatMessageDetail);
            chatMessageDetail.setUserData(buildUserData(IMChatMessageDetail.TYPE_MSG_FILE));
            sendBroadcast(chatMessageDetail);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean showCustomActionBar() {
        if (isGroupChat) {
            String tempName=null;
            if(groupName.length()>13){
                tempName=groupName.substring(0,13)+"...";
            }else {
                tempName=groupName;
            }
            initActionBar(R.drawable.icon_back, R.drawable.im_add_contact, tempName, false);
        } else {
            initActionBar(R.drawable.icon_back, R.drawable.empty_draft_box, username, false);
        }
        return true;
    }

    //点击左侧按钮事件
    @Override
    public boolean performClickOnLeft() {
        Intent data = new Intent();
        data .putExtra(Constants.EXTRA_PARCELABLE_CONTACTS, groupUserCount);
        data.putExtra(Constants.EXTRA_IM_GROUPID, groupId);
        data.putExtra(Constants.EXTRA_IM_GROUP_NAME, groupName);
        setResult(RESULT_OK, data);
        finish();
        return super.performClickOnLeft();
    }

    //点击右侧按钮事件
    @Override
    public boolean performClickOnRight() {
        if (isGroupChat) {
            Intent it = new Intent(this, ChatRoomSettingsActivity.class);
            it.putExtra(Constants.EXTRA_IM_CHAT_ROOM_TYPE, chatRoomType);
            if (groupChatFriends != null && groupChatFriends.size() > 0) {
                it.putParcelableArrayListExtra(Constants.EXTRA_PARCELABLE_CONTACTS, (ArrayList<UserInfo>) groupChatFriends);
                it.putExtra(Constants.EXTRA_IM_NEED_GET_GROUP_USER, false);
            } else {
                it.putExtra(Constants.EXTRA_IM_NEED_GET_GROUP_USER, true);
            }
            // 创建群组时，未获取到创建的群组信息。在这里造假数据，目的是让群组设置界面区分该群组是否是自建群组，群组管理员是否是自己
            if (group == null) {
                group = new GroupInfo();
                group.setCreatorid(mSession.getUid());
                group.setZ_type("0");   // 自建群组
                group.setG_name(groupName);
            }
            it.putExtra(Constants.EXTRA_IM_GROUP, (java.io.Serializable) group);
            it.putExtra(Constants.EXTRA_IM_GROUPID, groupId);
            it.putExtra(Constants.EXTRA_IM_YGROUPID, y_groupId);
            it.putExtra(Constants.EXTRA_IM_GROUPNNAME, groupName);
            it.putExtra(Constants.EXTRA_GROUP_CHAT_FLAG, isGroupChat);
            startActivityForResult(it, REQ_CODE_ACTIVITY_CHAT_ROOM_SETTINGS);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("确认删除聊天记录吗?");
            builder.setPositiveButton(R.string.clear, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ITask iTask = new ITask(TaskKey.TASK_KEY_DEL_MESSAGE);
                    addTask(iTask);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        return super.performClickOnRight();
    }

    @Override
    protected void handleTaskBackGround(ITask iTask) {
        super.handleTaskBackGround(iTask);
        int key = iTask.getKey();
        if (key == TaskKey.TASK_KEY_DEL_MESSAGE) {
            try {
                messageTable.deleteIMMessage(y_groupId, mSession.getListid());
                if (adapter != null) {
                    for (int i = 0; i < adapter.getCount(); i++) {
                        IMChatMessageDetail item = adapter.getItem(i);
                        if (item == null || item.getMessageType() == IMChatMessageDetail.TYPE_MSG_TEXT) {
                            continue;
                        }
                        CCPUtil.delFile(item.getFilePath());
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.clearList();
                        }
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setLastItemViewSelectd(ListView listView, NonPagingResultsAdapter adapter) {
        listView.setSelection(adapter.getCount());
    }

    public Device getDeviceHelper() {
        return CCPHelper.getInstance().getDevice();
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    private void sendBroadcast(IMChatMessageDetail msgDetail) {
        Intent intent = new Intent(CCPIntentUtils.INTENT_IM_RECIVE);
        intent.putExtra(KEY_GROUP_ID, y_groupId);
        intent.putExtra(KEY_IMMESSAGE_DETAIL, msgDetail);
        sendBroadcast(intent);
    }

    private void sendBroadcast(String messageId) {
        Intent intent = new Intent(CCPIntentUtils.INTENT_IM_RECIVE);
        intent.putExtra(KEY_GROUP_ID, y_groupId);
        intent.putExtra(KEY_MESSAGE_ID, messageId);
        sendBroadcast(intent);
    }


    /**
     * 接受广播
     * @param intent
     */
    @Override
    protected void onReceiveBroadcast(Intent intent) {
        super.onReceiveBroadcast(intent);
        if (intent == null) {
            return;
        }

        if (CCPIntentUtils.INTENT_IM_RECIVE.equals(intent.getAction())
                || CCPIntentUtils.INTENT_DELETE_GROUP_MESSAGE.equals(intent.getAction())) {
            if (intent.hasExtra(KEY_GROUP_ID)) {
                String sender = intent.getStringExtra(KEY_GROUP_ID);
                IMChatMessageDetail messageDetail = null;
                /**
                 * 发送消息的时候，会回调onSendInstanceMessage方法，通知handler发送一次广播, 在这个广播中带上消息的messageId.若从数据库中查询到则展示。
                 * 到消息发送成功后，会再次回调onSendInstanceMessage方法，再次通知handler发送广播。通过messageId查询到更新过的消息并展示。
                 */
                //文件类型
                if (intent.hasExtra(KEY_MESSAGE_ID)) {
                    String messageId = intent.getStringExtra(KEY_MESSAGE_ID);
                    try {
                        messageDetail = messageTable.queryIMChatMessageByMessageId(messageId);
                    } catch (SQLException e) {
                        messageDetail = null;
                    }
                    //文本类型
                } else {
                    messageDetail = (IMChatMessageDetail) intent.getSerializableExtra(KEY_IMMESSAGE_DETAIL);
                }
                if(!messageDetail.getListId().equals(mSession.getListid())){
                    return;
                }
                if (!TextUtils.isEmpty(sender) && sender.equals(y_groupId) && IMParseUtil.getListId(messageDetail.getUserData()).equals(messageDetail.getListId())) {
                    if (messageDetail != null) {
                        adapter.addItem(messageDetail);
                        try {
                            messageTable.updateIMMessageUnreadStatusToRead(y_groupId, messageDetail.getListId());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else if (CCPIntentUtils.INTENT_REMOVE_FROM_GROUP.equals(intent.getAction())) {
            // remove from group ... 被群组删除
            this.finish();
        }
    }


    private Handler mIMChatHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle b = null;
            int reason = -1;
            if (msg.obj instanceof Bundle) {
                b = (Bundle) msg.obj;
            }

            switch (msg.what) {
                case CCPHelper.WHAT_ON_SEND_MEDIAMSG_RES:
                    if (b == null) {
                        return;
                    }
                    // receive a new IM message
                    // then shown in the list.
                    try {
                        reason = b.getInt(Device.REASON);
                        InstanceMsg instancemsg = (InstanceMsg) b.getSerializable(Device.MEDIA_MESSAGE);
                        if (instancemsg == null) {
                            return;
                        }

                        int sendType = IMChatMessageDetail.STATE_IM_SEND_FAILED;//失败
                        String messageId = null;
                        if (instancemsg instanceof IMAttachedMsg) {
                            IMAttachedMsg rMediaInfo = (IMAttachedMsg) instancemsg;
                            messageId = rMediaInfo.getMsgId();
                            if (reason == 0) {
                                sendType = IMChatMessageDetail.STATE_IM_SEND_SUCCESS;
                            } else {
                                if (reason == 230007 && isVoiceRecordCancle()) {
                                    setCancle(false);
                                    // Here need to determine whether is it right? You cancel this recording,
                                    // and callback upload failed in real-time recording uploaded case,
                                    // so we need to do that here, when cancel the upload is not prompt the user interface
                                    // 230007 is the server did not receive a normal AMR recording end for chunked...
                                    return;
                                }

                                if (GroupChatActivity.voiceMessage.containsKey(rMediaInfo.getMsgId())) {
                                    isRecordAndSend = false;
                                    return;
                                }

                                // This is a representative chunked patterns are transmitted speech file
                                // If the execution returns to the false, is not chunked or send files
                                //VoiceSQLManager.getInstance().updateIMChatMessage(rMediaInfo.getMsgId(), IMChatMsgDetail.TYPE_MSG_SEND_FAILED);
                                sendType = IMChatMessageDetail.STATE_IM_SEND_FAILED;

                            }

                        } else if (instancemsg instanceof IMTextMsg) {
                            IMTextMsg imTextMsg = (IMTextMsg) instancemsg;
                            messageId = imTextMsg.getMsgId();
                            if (reason == 0) {
                                sendType = IMChatMessageDetail.STATE_IM_SEND_SUCCESS;
                            } else {
                                // do send text message failed ..
                                sendType = IMChatMessageDetail.STATE_IM_SEND_FAILED;
                            }
                        }
                        messageTable.updateIMMessageSendStatusByMessageId(messageId, sendType);
                        sendBroadcast(messageId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case CCPHelper.WHAT_ON_AMPLITUDE:
                    double amplitude = b.getDouble(Device.VOICE_AMPLITUDE);
                    displayAmplitude(amplitude);

                    break;

                case CCPHelper.WHAT_ON_RECODE_TIMEOUT:
                    doProcesOperationRecordOver(false);
                    break;

                case CCPHelper.WHAT_ON_PLAY_VOICE_FINSHING:
                    mVoicePlayState = TYPE_VOICE_STOP;
                    releaseVoiceAnim(-1);
                    //VoiceApplication.getInstance().setSpeakerEnable(false);
                    CCPAudioManager.getInstance().resetSpeakerState(GroupChatActivity.this);
                    break;
                case WHAT_ON_COMPUTATION_TIME:
                    if (promptRecordTime() && getRecordState() == RECORD_ING) {
                        sendEmptyMessageDelayed(WHAT_ON_COMPUTATION_TIME, TONE_LENGTH_MS);
                    }

                    break;

                // This call may be redundant, but to ensure compatibility system event more,
                // not only is the system call
                case CCPHelper.WHAT_ON_RECEIVE_SYSTEM_EVENTS:
                    onPause();
                    break;

                case CCPHelper.WHAT_ON_DIMISS_DIALOG:
                    removePopuWindow();
                    break;

                default:
                    break;
            }
        }


    };

    @Override
    protected void onPause() {
        super.onPause();

        unregisterIMReceiver();
        handleMotionEventActionUp(isVoiceRecordCancle());
        // release voice play resources
        releaseVoiceAnim(-1);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mPreference == null) {
            mPreference = PreferenceManager.getDefaultSharedPreferences(this);
        }
        String text=inputBoxEidt.getText().toString();
        if(!TextUtils.isEmpty(text)){
            SharedPreferences.Editor editor = mPreference.edit();
            editor.putString(y_groupId,text);
            editor.commit();
        }else {
            if(mPreference.contains(y_groupId)){
                SharedPreferences.Editor editor = mPreference.edit();
                editor.remove(y_groupId);
                editor.commit();
            }
        }
        mPreference = null;
        super.onDestroy();
    }

    private boolean promptRecordTime() {
        if (computationTime == -1L) {
            computationTime = SystemClock.elapsedRealtime();
        }
        long period = SystemClock.elapsedRealtime() - computationTime;
        int duration;
        if (period >= 50000L && period <= 60000L) {
            if (mRecordTipsToast == null) {
                vibrate(50L);
                duration = (int) ((60000L - period) / 1000L);
                mRecordTipsToast = Toast.makeText(getApplicationContext(), getString(R.string.chatting_rcd_time_limit, duration), Toast.LENGTH_SHORT);
            }
        } else {
            if (period < 60000L) {
                //sendEmptyMessageDelayed(WHAT_ON_COMPUTATION_TIME, TONE_LENGTH_MS);
                return true;
            }
            return false;
        }

        if (mRecordTipsToast != null) {
            duration = (int) ((60000L - period) / 1000L);
            mRecordTipsToast.setText(getString(R.string.chatting_rcd_time_limit, duration));
            mRecordTipsToast.show();
        }
        return true;
    }

    public void displayAmplitude(double amplitude) {
        if (mVoiceLoading == null) {
            return;
        }
        if (mVoiceLoading.getVisibility() == View.VISIBLE) {
            // If you are in when being loaded, then send to start recording
            onRecordStart();
        }
        mVoiceRecRy.setVisibility(View.VISIBLE);
        mVoiceLoading.setVisibility(View.GONE);
        mVoiceShortLy.setVisibility(View.GONE);

        for (int i = 0; i < ampValue.length; i++) {
            if (amplitude >= ampValue[i] && amplitude < ampValue[i + 1]) {
                ampImage.setBackgroundResource(ampIcon[i]);
                return;
            }
            continue;
        }
    }

    public void onRecordStart() {
        // If you are in when being loaded, then send to start recording
        mIMChatHandler.removeMessages(WHAT_ON_COMPUTATION_TIME);
        mIMChatHandler.sendEmptyMessageDelayed(WHAT_ON_COMPUTATION_TIME, GroupChatActivity.TONE_LENGTH_MS);
    }

    AnimationDrawable mVoiceAnimation = null;
    ImageView mVoiceAnimImage;

    private static final int TYPE_VOICE_PLAYING = 3;
    private static final int TYPE_VOICE_STOP = 4;
    private int mVoicePlayState = TYPE_VOICE_STOP;
    private int mPlayPosition = -1;

    public void viewPlayAnim(final ImageView iView, String path, int position, boolean from) {
        int releasePosition = releaseVoiceAnim(position);
        if (releasePosition == position) {
            return;
        }
        mPlayPosition = position;
        try {
            // local downloaded file
            if (!TextUtils.isEmpty(path) && isLocalAmr(path)) {

                if (mVoicePlayState == TYPE_VOICE_STOP) {
                    if (!checkeDeviceHelper()) {
                        return;
                    }
                    if (from) {
                        iView.setBackgroundResource(R.anim.voice_play_from);
                    } else {
                        iView.setBackgroundResource(R.anim.voice_play_to);
                    }
                    mVoiceAnimation = (AnimationDrawable) iView.getBackground();
                    mVoiceAnimImage = iView;
                    CCPAudioManager.getInstance().switchSpeakerEarpiece(GroupChatActivity.this, isEarpiece);

                    // Here we suggest to try not to use SDK voice play interface
                    // and you can achieve Voice file playback interface
                    //  for example CCPVoiceMediaPlayManager.getInstance(GroupChatActivity.this).putVoicePlayQueue(position, path);
                    // Interface of new speakerOn parameters,(Earpiece or Speaker)
                    getDeviceHelper().playVoiceMsg(path, !isEarpiece);

                    // 3.4.1.2  TODO 话题、扬声器
                    //updateVoicePlayModelView(isEarpiece);
                    mVoiceAnimation.start();
                    mVoicePlayState = TYPE_VOICE_PLAYING;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //听筒
    private boolean isEarpiece = false;

    @Override
    protected void onResume() {
        super.onResume();

        registerIMReceiver(new String[]{CCPIntentUtils.INTENT_IM_RECIVE
                , CCPIntentUtils.INTENT_REMOVE_FROM_GROUP
                , CCPIntentUtils.INTENT_DELETE_GROUP_MESSAGE});

        // default speaker
//        isEarpiece = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean(
//                CCPPreferenceSettings.SETTING_HANDSET.getId(),
//                ((Boolean) CCPPreferenceSettings.SETTING_HANDSET
//                        .getDefaultValue()).booleanValue()
//        );

    }

//    private void updateVoicePlayModelView(boolean isEarpiece) {
//        String speakerEarpiece = null;
//        if(isEarpiece) {
//            speakerEarpiece = getString(R.string.voice_listen_earpiece);
//        } else {
//            speakerEarpiece = getString(R.string.voice_listen_speaker);
//        }
//
//        addNotificatoinToView(speakerEarpiece , Gravity.TOP);
//    }
//
//    public void addNotificatoinToView(CharSequence text,int titleGravity) {
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, titleGravity);
//        int margin = Math.round(50 * getResources().getDisplayMetrics().densityDpi / 160.0F);
//        layoutParams.topMargin = margin;
//        addNotificatoinToView(text, layoutParams);
//    }

    private int releaseVoiceAnim(int position) {
        if (mVoiceAnimation != null) {
            mVoiceAnimation.stop();
            int id = 0;
            if (mVoiceAnimImage.getId() == R.id.voice_chat_recd_tv_l) {
                id = R.drawable.voice_from_playing;
            } else if (mVoiceAnimImage.getId() == R.id.voice_chat_recd_tv_r) {
                id = R.drawable.voice_to_playing;
            }
            mVoiceAnimImage.setBackgroundResource(id);

            mVoiceAnimation = null;
            mVoiceAnimImage = null;
        }

        // if position is -1 ,then release Animatoin and can't start new Play.
        if (position == -1) {
            mPlayPosition = position;
        }

        // if amr voice file is playing ,then stop play
        if (mVoicePlayState == TYPE_VOICE_PLAYING) {
            if (!checkeDeviceHelper()) {
                return -1;
            }
            /**
             * 停止当前的语音播放
             */
            getDeviceHelper().stopVoiceMsg();
            // TODO reset speaker
            CCPAudioManager.getInstance().resetSpeakerState(GroupChatActivity.this);
            mVoicePlayState = TYPE_VOICE_STOP;

            return mPlayPosition;
        }
        return -1;
    }

    boolean isLocalAmr(String url) {
        if (new File(url).exists()) {
            return true;
        }
        Toast.makeText(this, "本地语音文件不存在或已经删除", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onEmojiItemClick(int emojiid, String emojiName) {
        if (inputBoxEidt.getText().toString().length() < 140) {
            inputBoxEidt.setEmojiText(emojiName);
        }
    }

    @Override
    public void onEmojiDelClick() {
        inputBoxEidt.requestFocus();
        mAppPanel.setVisibility(View.VISIBLE);
        inputBoxEidt.getInputConnection().sendKeyEvent(
                new KeyEvent(MotionEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        inputBoxEidt.getInputConnection().sendKeyEvent(
                new KeyEvent(MotionEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int streamType;
        if (!isEarpiece) {
            streamType = AudioManager.STREAM_MUSIC;
        } else {
            streamType = AudioManager.STREAM_VOICE_CALL;
        }
        int maxVolumeVoiceCall = mAudioManager.getStreamMaxVolume(streamType);
        int index = maxVolumeVoiceCall / 7;
        if (index == 0) {
            index = 1;
        }
        int currentVolume = mAudioManager.getStreamVolume(streamType);
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            mAudioManager.setStreamVolume(streamType, currentVolume - index, AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_PLAY_SOUND);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            mAudioManager.setStreamVolume(streamType, currentVolume + index, AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_PLAY_SOUND);
            return true;
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                performClickOnLeft();
                if (mode != 2) {
                    setMode(2, false);
                    return true;
                }
            }
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 重新发送IM信息
     * <p>Title: reSendImMessage</p>
     * <p>Description: </p>
     * @parampostion
     */
    public void reSendImMessage(IMChatMessageDetail item) {
        try {
            String uniqueID = null;
            if (checkeDeviceHelper()) {
                if (item.getMessageType() == IMChatMessageDetail.TYPE_MSG_TEXT) {
                    uniqueID = getDeviceHelper().sendInstanceMessage(y_groupId, item.getMessageContent(), null, buildUserData(IMChatMessageDetail.TYPE_MSG_TEXT));
                } else if(item.getMessageType() == IMChatMessageDetail.TYPE_MSG_VOICE) {
                    uniqueID = getDeviceHelper().sendInstanceMessage(y_groupId, null, item.getFilePath(), buildUserData(IMChatMessageDetail.TYPE_MSG_VOICE));
                }else {
                    uniqueID = getDeviceHelper().sendInstanceMessage(y_groupId, null, item.getFilePath(), buildUserData(IMChatMessageDetail.TYPE_MSG_FILE));
                }
            }
            if (TextUtils.isEmpty(uniqueID)) {
                Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                item.setImState(IMChatMessageDetail.STATE_IM_SEND_FAILED);
                return;
            }
            messageTable.deleteIMMessage(item.getSessionId(),item.getListId());
            item.setMessageId(uniqueID);
            item.setImState(IMChatMessageDetail.STATE_IM_SENDING);
            item.setCurDate(String.valueOf((new Date()).getTime()));
            item.setListId(mSession.getListid());
            messageTable.insertIMMessage(item);

            sendBroadcast(item);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * userData:
     * 字段1：id 社区id
     * 字段2：h 聊天发送人的头像
     * 字段3：t 消息的类型 1，文字 2，文件 3，语音 4，图片
     * 字段4：ids 用户id
     * 字段5：n 用户名称或群组名称
     *
     * @return
     */
    private String buildUserData(int messageType) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", mSession.getListid());
        jsonObject.put("h", mSession.getMySelfInfo().getU_icon());
        jsonObject.put("t", String.valueOf(messageType));
        jsonObject.put("ids", mSession.getIds());
        jsonObject.put("n", mSession.getUserName());
        return jsonObject.toJSONString();
    }



}
