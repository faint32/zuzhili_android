package com.zuzhili.ui.activity.publish;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.draftbox.DraftBoxActivity;
import com.zuzhili.draftbox.DraftContract;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.Member;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.fragment.FixedOnActivityResultBugFragment;
import com.zuzhili.ui.fragment.member.BaseMemberFrg;
import com.zuzhili.ui.views.AppPanel;
import com.zuzhili.ui.views.CCPEditText;
import com.zuzhili.ui.views.EmojiGrid;

import java.util.HashMap;
import java.util.Map;

public class PublishWriteActivity extends BaseActivity implements Listener<String>
        , ErrorListener
        , BaseActivity.TimeToShowActionBarCallback
        , EmojiGrid.OnEmojiItemClickListener
        , BaseMemberFrg.OnMemberSelectedListener
        , FixedOnActivityResultBugFragment.OnActionBarUpdateListener {

    @ViewInject(R.id.rla_container)
    private RelativeLayout container;

    @ViewInject(R.id.edit_publish_write_title)
    private EditText writeTitleEdit;

    @ViewInject(R.id.edit_publish_write_content)
    private CCPEditText writeContentEdit;

    @ViewInject(R.id.img_well)
    private ImageView wellImg;

    @ViewInject(R.id.img_focus)
    private ImageView focusImg;

    @ViewInject(R.id.img_face)
    private ImageView faceImg;

    //表情列表
    @ViewInject(R.id.chatting_app_panel)
    private AppPanel mAppPanel;

    @ViewInject(R.id.cbx_visiable)
    private CheckBox priorityCbx;

//	@ViewInject(R.id.only_for_member)
//	private LinearLayout priorityLin;

    private String spaceid;

    private InputMethodManager mInputMethodManager;

    private int mode;
    private String draftId;
    private String draftContent;
    private boolean onlyVisibleForMembers = false;

    @Override
    protected void onCreate(Bundle inState) {
        // TODO Auto-generated method stub
        super.onCreate(inState);
        setContentView(R.layout.activity_publish_write);
        setCustomActionBarCallback(this);
        ViewUtils.inject(this);

        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        mInputMethodManager.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
        mAppPanel.setOnEmojiItemClickListener(this);

        writeContentEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mAppPanel.getVisibility() == View.VISIBLE) {
                    mAppPanel.setVisibility(View.GONE);
                }
                return false;
            }
        });
        writeTitleEdit.requestFocus();
        writeTitleEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mAppPanel.getVisibility() == View.VISIBLE) {
                    mAppPanel.setVisibility(View.GONE);
                }
                return false;
            }
        });
        initData();
    }


    /**
     * 初始化数据
     */
    private void initData() {
        spaceid = getIntent().getStringExtra(Constants.ACTIVITY_FROM_BUNDLE_SPACEID);

        // draft box related.
        draftId = getIntent().getStringExtra("draft_id");
        draftContent = getIntent().getStringExtra(DraftBoxActivity.DRAFTBOX_CONTENT);
        if (draftContent != null) {
            JSONObject obj = JSON.parseObject(draftContent);
            String title = obj.getString("title");
            writeTitleEdit.setText(title);
            String text = obj.getString("content");
            writeContentEdit.setText(text);

            // visible for member
            String authority = obj.getString("authority");
            if (authority != null) {
                this.onlyVisibleForMembers = authority.equals("1") ? true : false;
                priorityCbx.setChecked(onlyVisibleForMembers);
                priorityCbx.setVisibility(View.VISIBLE);
            }

            // restore space id
            if (spaceid == null) {
                spaceid = obj.getString("spaceid");
            }
        }

        //个人发布不显示仅成员可见权限
        if (spaceid == null || spaceid.trim().equals("")) {
            priorityCbx.setVisibility(View.GONE);
        } else {
            priorityCbx.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 验证输入规则
     *
     * @return
     */
    private boolean checkInput() {
        String titleStr = writeTitleEdit.getText().toString().trim();
        if (titleStr == null || titleStr.equals("")) {
//            Utils.makeEventToast(getApplicationContext(), "请输入标题", false);
//            return false;
        } else if (titleStr.length() > 30) {
            Utils.makeEventToast(getApplicationContext(), "标题长度不能超过30个字符", false);
            return false;
        }

        String contentStr = writeContentEdit.getText().toString().trim();
        if (contentStr == null || contentStr.equals("")) {
            Utils.makeEventToast(getApplicationContext(), "请输入内容", false);
            return false;
        }

        return true;
    }

    /**
     * 标注话题
     *
     * @param view
     */
    @OnClick(R.id.img_well)
    public void topic(View view) {
        //标题不能不能标注话题
        if (writeTitleEdit.isFocused()) {
            showInvalidOpDialog();
        } else if (writeContentEdit.isFocused()) {
            int index = writeContentEdit.getSelectionStart();
            writeContentEdit.getText().insert(index, "##");
            writeContentEdit.setSelection(index + 1);
            if (mAppPanel.getVisibility() == View.VISIBLE) {
                mAppPanel.setVisibility(View.GONE);
            }
            mInputMethodManager.showSoftInput(writeContentEdit, InputMethodManager.SHOW_FORCED);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mInputMethodManager.hideSoftInputFromWindow(writeContentEdit.getWindowToken(), 0); //强制隐藏键盘
    }

    /**
     * @param view
     * @某人
     */
    @OnClick(R.id.img_focus)
    public void at(View view) {
        //标题不能@某人
        if (writeTitleEdit.isFocused()) {
            showInvalidOpDialog();
        } else if (writeContentEdit.isFocused()) {
            //添加@某人
            resetChatFooter(true, false);
            mInputMethodManager.hideSoftInputFromWindow(writeContentEdit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            //添加@某人
            Bundle bundle = new Bundle();
            bundle.putString(Constants.EXTRA_FRAGMENT_TAG, Constants.TAG_MEMBERS);
            bundle.putString(Constants.EXTRA_FROM_WHICH_PAGE, Constants.EXTRA_FROM_COMMENT_EDIT);
            attachFragment(container.getId(), getFragment(bundle), Constants.TAG_MEMBERS);
        }
    }

    /**
     * 添加表情
     *
     * @param view
     */
    @OnClick(R.id.img_face)
    public void face(View view) {
        //标题不能添加表情
        if (writeTitleEdit.isFocused()) {
            showInvalidOpDialog();
        } else if (writeContentEdit.isFocused()) {
            showFace();
        }
    }

    /**
     * 选择表情弹窗
     */
    private void showFace() {
//		FaceSelector.lis = new OnFaceSelectListener() {
//
//			@Override
//			public void onSelected(String name) {
//				int index = writeContentEdit.getSelectionStart();
//				writeContentEdit.getText().insert(index, name);
//				writeContentEdit.setSelection(index + name.length());
//			}
//		};
//		FaceSelector.showDialog(this);
        mInputMethodManager.hideSoftInputFromWindow(writeContentEdit.getWindowToken(), 0); //强制隐藏键盘
        writeContentEdit.requestFocus();
        int mode = mAppPanel.isPanelVisible() ? 1 : 2;
        setMode(mode, false);
    }

    public void setMode(int mode, boolean input) {
        this.mode = mode;
        switch (mode) {
            case 1:
                resetChatFooter(true, false);
                break;
            case 2:
                resetChatFooter(true, true);
                break;
        }

        if (input) {
            mInputMethodManager.showSoftInput(writeContentEdit, 0);
        } else {
            mInputMethodManager.hideSoftInputFromWindow(writeContentEdit.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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


    @Override
    public void onEmojiItemClick(int emojiid, String emojiName) {
        writeContentEdit.setEmojiText(emojiName);
    }

    @Override
    public void onEmojiDelClick() {
        writeContentEdit.requestFocus();
        mAppPanel.setVisibility(View.VISIBLE);
        writeContentEdit.getInputConnection().sendKeyEvent(
                new KeyEvent(MotionEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
        writeContentEdit.getInputConnection().sendKeyEvent(
                new KeyEvent(MotionEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
    }

    /**
     * 获取发布参数
     *
     * @return
     */
    private HashMap<String, String> getParams() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("ids", mSession.getIds());
        params.put("listid", mSession.getListid());
        if (priorityCbx.isChecked()) {
            params.put("authority", "1");    //仅空间成员可见
        } else {
            params.put("authority", "0");
        }
        if (spaceid != null) {
            params.put("spaceid", spaceid);
        }
        params.put("title", writeTitleEdit.getText().toString());
        params.put("content", writeContentEdit.getText().toString());
        params.put("from", Constants.APP_FROM_ANDROID);
        return params;
    }

    /**
     * 无效操作提示
     */
    private void showInvalidOpDialog() {

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        removeLoading();
        callback.onException(error);
    }

    @Override
    public void onResponse(String response) {
        removeLoading();
        Utils.makeEventToast(getApplicationContext(), "发布成功", false);
        mSession.setUIShouldUpdate(Constants.PAGE_TREND);
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, R.drawable.icon_publish, getString(R.string.publish_text), false);
        setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performClickOnLeft();
            }
        });
        setOnClickRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    Task.publishWrite(getParams(), new Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            PublishWriteActivity.this.onResponse(s);
                            if (!TextUtils.isEmpty(draftId)) {
                                // delete draft
                                Uri uri = ContentUris.withAppendedId(DraftContract.Draft.CONTENT_ID_URI_BASE, Long.parseLong(draftId));
                                getContentResolver().delete(uri, null, null);
                            }

                            finish();
                        }
                    }, new ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            PublishWriteActivity.this.onErrorResponse(volleyError);
                            //save content to draft box.
                            ContentValues values = new ContentValues();
                            values.put(DraftContract.Draft.COLUMN_NAME_LIST_ID, mSession.getListid());
                            values.put(DraftContract.Draft.COLUMN_NAME_IDS, mSession.getIds());
                            values.put(DraftContract.Draft.COLUMN_NAME_CONTENT_TYPE, DraftContract.Draft.CONTENT_TYPE_TEXT);
                            String jsonString = new JSONObject((Map) getParams()).toJSONString();
                            values.put(DraftContract.Draft.COLUMN_NAME_CONTENT, jsonString);

                            if (!TextUtils.isEmpty(draftId)) {
                                getContentResolver().update(Uri.withAppendedPath(DraftContract.Draft.CONTENT_ID_URI_BASE, draftId), values, null, null);
                            } else {
                                getContentResolver().insert(DraftContract.Draft.CONTENT_URI, values);
                            }

//                            Utils.makeEventToast(PublishWriteActivity.this, "发送失败，已保存至草稿箱", true);
//                            Toast.makeText(PublishWriteActivity.this, "发送失败，已保存至草稿箱", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });

                    Utils.makeEventToast(PublishWriteActivity.this, getString(R.string.sending_hint), true);
                    setResult(RESULT_OK);
                }
            }
        });
        return true;
    }

    @Override
    public boolean performClickOnLeft() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        if (!TextUtils.isEmpty(writeTitleEdit.getText().toString()) || !TextUtils.isEmpty(writeContentEdit.getText().toString())) {

            final String jsonString = new JSONObject((Map) getParams()).toJSONString();

            AlertDialog.Builder builder = new AlertDialog.Builder(PublishWriteActivity.this);
            if (!TextUtils.isEmpty(draftId)) {// 编辑草稿
                if (draftContent.equals(jsonString)) {
                    finish();// 未修改直接返回
                    return true;
                } else {
                    builder.setMessage("改草稿已存在，是否覆盖？");
                }
            } else {
                builder.setMessage(R.string.if_save_to_draftbox);
            }
            builder.setTitle("保存草稿");
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    //save content to draft box.
                    ContentValues values = new ContentValues();
                    values.put(DraftContract.Draft.COLUMN_NAME_LIST_ID, mSession.getListid());
                    values.put(DraftContract.Draft.COLUMN_NAME_IDS, mSession.getIds());
                    values.put(DraftContract.Draft.COLUMN_NAME_CONTENT_TYPE, DraftContract.Draft.CONTENT_TYPE_TEXT);
                    values.put(DraftContract.Draft.COLUMN_NAME_CONTENT, jsonString);

                    if (draftId != null) {
                        getContentResolver().update(Uri.withAppendedPath(DraftContract.Draft.CONTENT_ID_URI_BASE, draftId), values, null, null);
                    } else {
                        getContentResolver().insert(DraftContract.Draft.CONTENT_URI, values);
                    }

                    Toast.makeText(PublishWriteActivity.this, "已保存至草稿箱", Toast.LENGTH_LONG).show();

                    finish();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            builder.show();
        } else {
            finish();
        }
        return super.performClickOnLeft();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            performClickOnLeft();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onMemberSelected(Member member) {
        detachFragment(getFragment(Constants.TAG_MEMBERS));
        showCustomActionBar();
        int index = writeContentEdit.getSelectionStart();
        writeContentEdit.getText().insert(index, getAtString(member));
        resetChatFooter(true, false);
        mInputMethodManager.showSoftInput(writeContentEdit, 0);
    }

    @Override
    public void shouldUpdateActionBar() {
        showCustomActionBar();
        resetChatFooter(true, false);
        mInputMethodManager.showSoftInput(writeContentEdit, 0);
    }
}
