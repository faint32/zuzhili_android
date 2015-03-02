package com.zuzhili.ui.activity.comment;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
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

public class CommentEditActivity extends BaseActivity implements Listener<String>, ErrorListener,
        BaseActivity.TimeToShowActionBarCallback, BaseMemberFrg.OnMemberSelectedListener, EmojiGrid.OnEmojiItemClickListener, FixedOnActivityResultBugFragment.OnActionBarUpdateListener {

    private RelativeLayout container;

    //发送内容
    @ViewInject(R.id.edit_publish_write_content)
    private CCPEditText writeContentEdit;
    //#
    @ViewInject(R.id.img_well)
    private ImageView well_img;
    //相关人
    @ViewInject(R.id.focus_img)
    private ImageView focusImg;
    //表情
    @ViewInject(R.id.face_img)
    private ImageView faceImg;
    //表情列表
    @ViewInject(R.id.chatting_app_panel)
    private AppPanel mAppPanel;
    //发送选项
    @ViewInject(R.id.send_option)
    private CheckBox priorityCbx;

    private String tocommentid = "";
    //源编号
    private String absid;

    //子编号
    private String priabsid;

    private String from;

    private String action;

    private int mode;

    private InputMethodManager mInputMethodManager;

    private String atInfo;

    private String iscomment;

    private String isforward;

    private String draftId;
    private String draftContent;

    //是否连续按了两次
    private boolean isNext;

    /**
     * 验证输入规则
     *
     * @return
     */
    private boolean checkInput() {
        String contentStr = writeContentEdit.getText().toString().trim();

        if (contentStr.equals("")) {
            if (action != null && action.equals(Constants.ACTION_REPOST)) {
                writeContentEdit.setText("转发内容");
            } else {
                Utils.makeEventToast(getApplicationContext(), "请输入内容", false);
                return false;
            }
        }

        return true;
    }

    /**
     * 获取发布参数
     *
     * @return
     */
    private HashMap<String, String> getParams(int tag) {

        HashMap<String, String> params = new HashMap<String, String>();

        switch (tag) {
            case 1:
                params.put("ids", mSession.getIds());// 用户的id
                params.put("curnetid", mSession.getListid());// 社区的id
                params.put("from", "1");//来源
                params.put("content", writeContentEdit.getText().toString().trim());// 评论内容
                if (priorityCbx.isChecked() || (action != null && action.equals(Constants.ACTION_REPOST))) {
                    isforward = "true";
                } else {
                    isforward = "false";
                }
                if (action != null && action.equals(Constants.ACTION_PIC_COMMENT)) {
                    params.put("picid", absid);// 图片的id
                    if (!TextUtils.isEmpty(tocommentid)) {
                        params.put("tocommentid", tocommentid);
                    }
                } else {
                    params.put("absid", absid);// 源动态的id
                    if (!TextUtils.isEmpty(tocommentid)) {
                        params.put("tocommentid", tocommentid);
                    }
                }

                params.put("isforward", isforward);
                break;

            case 2:
                params.put("ids", mSession.getIds());// 用户的id
                params.put("listid", mSession.getListid());// 社区的id
                String iscomment = null;
                if (priorityCbx.isChecked() && (action != null && action.equals(Constants.ACTION_REPOST))) {
                    iscomment = "true";
                } else {
                    iscomment = "false";
                }
                params.put("iscomment", iscomment);
                params.put("content", writeContentEdit.getText().toString().trim());
                params.put("absid", absid);
                params.put("priabsid", priabsid);
                break;
        }

        params.put("action", action);
        params.put("is_selected", String.valueOf(priorityCbx.isChecked()));

        return params;
    }

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        super.setContentView(R.layout.activity_publishcomment);
        ViewUtils.inject(this);
        setCustomActionBarCallback(this);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mAppPanel.setOnEmojiItemClickListener(this);
        absid = getIntent().getStringExtra(Constants.EXTRA_TREND_ABSID);
        tocommentid = getIntent().getStringExtra(Constants.EXTRA_TREND_TOCOMMENTID);
        priabsid = getIntent().getStringExtra(Constants.EXTRA_TREND_PRIABSID);
        from = getIntent().getStringExtra(Constants.EXTRA_FROM_WHICH_PAGE);
        action = getIntent().getStringExtra(Constants.ACTION);
        container = (RelativeLayout) findViewById(R.id.fla_container);
        if (from != null && from.equals(Constants.EXTRA_FROM_AT_ME_COMMENT)) {
            writeContentEdit.setText(getIntent().getStringExtra(Constants.EXTRA_AT_INFO));
            writeContentEdit.setSelection(getIntent().getStringExtra(Constants.EXTRA_AT_INFO).length());
        } else if (from != null && action != null && action.equals(Constants.ACTION_PIC_COMMENT)) {
            writeContentEdit.setText(getIntent().getStringExtra(Constants.EXTRA_PICCOMMENT_INFO));
            if (!TextUtils.isEmpty(writeContentEdit.getText().toString())) {
                writeContentEdit.setSelection(getIntent().getStringExtra(Constants.EXTRA_PICCOMMENT_INFO).length());
            }
        } else if (from != null && action.equals(Constants.ACTION_COMMENT)) {
            writeContentEdit.setText(getIntent().getStringExtra(Constants.EXTRA_COMMENT_INFO));
            if (!TextUtils.isEmpty(writeContentEdit.getText().toString())) {
                writeContentEdit.setSelection(getIntent().getStringExtra(Constants.EXTRA_COMMENT_INFO).length());
            }
        }

        writeContentEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mAppPanel.getVisibility() == View.VISIBLE) {
                    mAppPanel.setVisibility(View.GONE);
                }
                return false;
            }
        });

        String source = getIntent().getStringExtra(Constants.EXTRA_TREND_SOURCETEXT);
        atInfo = getIntent().getStringExtra(Constants.EXTRA_REPOST_TEXT);
        LogUtils.i("source" + source + ",repost_text" + atInfo);
        if (!TextUtils.isEmpty(atInfo)) {
            writeContentEdit.setEmojiText("//" + atInfo);
            writeContentEdit.requestFocus();
            writeContentEdit.setSelection(0);
        }
        writeContentEdit.requestFocus();
        priorityCbx.setText(getString(R.string.publishtrend));


        // draft box related.
        draftId = getIntent().getStringExtra("draft_id");
        draftContent = getIntent().getStringExtra(DraftBoxActivity.DRAFTBOX_CONTENT);
        if (draftContent != null) {
            JSONObject obj = JSON.parseObject(draftContent);
            absid = obj.getString("absid");
            priabsid = obj.getString("priabsid");
            tocommentid = obj.getString("tocommentid");
            String text = obj.getString("content");
            writeContentEdit.setText(text);

            String isSelected = obj.getString("is_selected");
            priorityCbx.setChecked(isSelected.trim().equals("true"));
        }
    }

    /**
     * 添加表情
     *
     * @param view
     */
    @OnClick(R.id.face_img)
    public void face(View view) {
        if (writeContentEdit.isFocused()) {
            showFace();
        }
    }


    /**
     * 标注话题
     *
     * @param view
     */
    @OnClick(R.id.img_well)
    public void topic(View view) {
        int index = writeContentEdit.getSelectionStart();
        writeContentEdit.getText().insert(index, "##");
        writeContentEdit.setSelection(index + 1);
        if (mAppPanel.getVisibility() == View.VISIBLE) {
            mAppPanel.setVisibility(View.GONE);
        }
        mInputMethodManager.showSoftInput(writeContentEdit, InputMethodManager.SHOW_FORCED);
    }

    /**
     * @param view
     * @某人
     */
    @OnClick(R.id.focus_img)
    public void atUser(View view) {
        //标题不能@某人
        if (!writeContentEdit.isFocused()) {
            showInvalidOpDialog();
        } else if (writeContentEdit.isFocused()) {
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
     * 选择表情弹窗
     */
    private void showFace() {
//		FaceSelector.lis = new OnFaceSelectListener() {
//			@Override
//			public void onSelected(String name) {
//				int index = writeContentEdit.getSelectionStart();
//				writeContentEdit.getText().insert(index, name);
//				writeContentEdit.setSelection(index + name.length());
//			}
//		};
//		FaceSelector.showDialog(this);
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
     * 无效操作提示
     */
    private void showInvalidOpDialog() {

    }

    @Override
    public boolean showCustomActionBar() {
        from = getIntent().getStringExtra(Constants.EXTRA_FROM_WHICH_PAGE);
        if (from != null && from.equals(Constants.EXTRA_FROM_TREND_DETAIL)) {
            if (action != null && (action.equals(Constants.ACTION_COMMENT) || action.equals(Constants.ACTION_PIC_COMMENT))) {
                if (TextUtils.isEmpty(tocommentid)) {
                    initActionBar(R.drawable.icon_back, R.drawable.icon_publish, getString(R.string.title_publish_comment), false);
                } else {
                    initActionBar(R.drawable.icon_back, R.drawable.icon_publish, getString(R.string.title_reply_comment), false);
                }
                priorityCbx.setText(getString(R.string.publishevent));

            } else if (action != null && action.equals(Constants.ACTION_REPOST)) {
                initActionBar(R.drawable.icon_back, R.drawable.icon_publish, getString(R.string.title_edit_repost), false);
            }

        } else if (from != null && from.equals(Constants.EXTRA_FROM_AT_ME_COMMENT)) {
            initActionBar(R.drawable.icon_back, R.drawable.icon_publish, getString(R.string.title_reply_comment), false);
            priorityCbx.setText(getString(R.string.publishevent));
        } else if (from != null && from.equals(Constants.EXTRA_FROM_AT_ME_CONTENT_ACTION_REPOST)) {
            initActionBar(R.drawable.icon_back, R.drawable.icon_publish, getString(R.string.title_edit_repost), false);
            priorityCbx.setText(getString(R.string.publishtrend));
        } else if (from != null && from.equals(Constants.EXTRA_FROM_AT_ME_CONTENT_ACTION_COMMENT)) {
            initActionBar(R.drawable.icon_back, R.drawable.icon_publish, getString(R.string.title_publish_comment), false);
            priorityCbx.setText(getString(R.string.publishevent));
        }

        setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performClickOnLeft();
            }
        });
        setOnClickRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput() && !isNext) {
                    if (action != null && action.equals(Constants.ACTION_REPOST)) {
                        Task.publishTrend(getParams(2), CommentEditActivity.this, CommentEditActivity.this);
                    } else if (action != null && action.equals(Constants.ACTION_PIC_COMMENT)) {
                        //图片相关接口,回复评论只需要加tocommentid
                        Task.publishPicComment(getParams(1), CommentEditActivity.this, CommentEditActivity.this);
                    } else if (action != null && action.equals(Constants.ACTION_COMMENT) && !TextUtils.isEmpty(tocommentid)) {
                        Task.publishAddComment(getParams(1), CommentEditActivity.this, CommentEditActivity.this);
                    } else {
                        Task.publishComment(getParams(1), CommentEditActivity.this, CommentEditActivity.this);
                    }


                    Utils.makeEventToast(CommentEditActivity.this, getString(R.string.sending_hint), false);
                    if (!TextUtils.isEmpty(iscomment)) {
                        mSession.setUIShouldUpdate(Constants.PAGE_TREND);
                    }
                    if (isforward != null && isforward.equals("true")) {
                        mSession.setUIShouldUpdate(Constants.PAGE_TREND);
                    }

                    isNext = true;
                }
            }
        });
        return true;
    }

    @Override
    public boolean performClickOnLeft() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        String text = writeContentEdit.getText().toString();
        if (!TextUtils.isEmpty(atInfo)) {
            isShowDialog(text);
        } else {
            isShowDialog(text);
        }
        return super.performClickOnLeft();
    }

    private void isShowDialog(String text) {
        if (!TextUtils.isEmpty(text) && !text.equals("//" + atInfo)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CommentEditActivity.this);

            Map map = action.equals(Constants.ACTION_REPOST) ? getParams(2) : getParams(1);
            final String jsonString = new JSONObject(map).toJSONString();
            if (draftId != null) {
                if (jsonString.equals(draftContent)) {
                    finish();
                    return;
                } else {
                    builder.setMessage("改草稿已存在，是否覆盖？");
                }
            } else {
                builder.setMessage(R.string.if_save_to_draftbox).setTitle("保存草稿");
            }

            builder.setTitle("保存草稿");

            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    // save content to draft box.
                    ContentValues values = new ContentValues();
                    values.put(DraftContract.Draft.COLUMN_NAME_LIST_ID, mSession.getListid());
                    values.put(DraftContract.Draft.COLUMN_NAME_IDS, mSession.getIds());
                    values.put(DraftContract.Draft.COLUMN_NAME_CONTENT_TYPE, DraftContract.Draft.CONTENT_TYPE_COMMENT);
                    values.put(DraftContract.Draft.COLUMN_NAME_CONTENT, jsonString);

                    if (draftId != null) {
                        getContentResolver().update(Uri.withAppendedPath(DraftContract.Draft.CONTENT_ID_URI_BASE, draftId), values, null, null);
                    } else {
                        getContentResolver().insert(DraftContract.Draft.CONTENT_URI, values);
                    }

                    Toast.makeText(CommentEditActivity.this, "已保存至草稿箱", Toast.LENGTH_LONG).show();

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
    public void onErrorResponse(VolleyError error) {

        if (draftId != null) {
            // update draft
            ContentValues values = new ContentValues();
            values.put(DraftContract.Draft.COLUMN_NAME_LIST_ID, mSession.getListid());
            values.put(DraftContract.Draft.COLUMN_NAME_IDS, mSession.getIds());
            values.put(DraftContract.Draft.COLUMN_NAME_CONTENT_TYPE, DraftContract.Draft.CONTENT_TYPE_COMMENT);
            final String jsonString = new JSONObject((Map) getParams(1)).toJSONString();
            values.put(DraftContract.Draft.COLUMN_NAME_CONTENT, jsonString);
            getContentResolver().update(Uri.withAppendedPath(DraftContract.Draft.CONTENT_ID_URI_BASE, draftId), values, null, null);
        }

        isNext = false;
        callback.onException(error);
    }

    @Override
    public void onResponse(String response) {
        Utils.makeEventToast(getApplicationContext(), "发布成功", false);
        setResult(Activity.RESULT_OK);

        // delete draft
        if (draftId != null) {
            Uri uri = ContentUris.withAppendedId(DraftContract.Draft.CONTENT_ID_URI_BASE, Long.parseLong(draftId));
            getContentResolver().delete(uri, null, null);
        }

        finish();
    }

    @Override
    public void onMemberSelected(Member member) {
        detachFragment(getFragment(Constants.TAG_MEMBERS));
        int index = writeContentEdit.getSelectionStart();
        writeContentEdit.getText().insert(index, getAtString(member));
        showCustomActionBar();
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
