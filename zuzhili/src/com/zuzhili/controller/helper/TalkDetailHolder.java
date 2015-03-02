package com.zuzhili.controller.helper;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.ui.views.FaceTextView;

public class TalkDetailHolder {

    @ViewInject(R.id.txt_chat_time_l)
    public TextView lTimeTxt;

	/** 用户头像 */
	@ViewInject(R.id.img_userhead)
	public ImageView lUserHeadImg;
	
	/** 引用的动态布局 内容加图片与附件*/
	@ViewInject(R.id.frl_content)
	public FrameLayout lMsgContainerFrl;
	/**
	 * 左侧文本框内容
	 */
	@ViewInject(R.id.item_msglist_text_l)
	public FaceTextView lMsgTxt;


    /**
     * 左侧语音时长
     */
    @ViewInject(R.id.voice_content_len_l)
    public TextView lDuration;

    /**
     * 左侧语音动画
     */
    @ViewInject(R.id.voice_chat_recd_tv_l)
    public ImageView vChatContentFrom;

	/**
	 * 左侧图片
	 */
	@ViewInject(R.id.item_msglist_img_l)
	public ImageView lMsgImg;

    /**
     * 左侧进度条
     */
    @ViewInject(R.id.progressbar_upload_l)
    public ProgressBar lUploadProgress;

    /**
     * 左侧发送消息失败提示图片
     */
    @ViewInject(R.id.img_warn_l)
    public ImageView lWarnImg;

    @ViewInject(R.id.txt_chat_time_r)
    public TextView rTimeTxt;
	
	/** 右侧用户头像 */
	@ViewInject(R.id.item_msglist_name_r)
	public ImageView rUserHeadImg;
	
	/** 引用的动态布局 内容加图片与附件*/
	@ViewInject(R.id.frl_content_r)
	public FrameLayout rMsgContainerFrl;
	/**
	 * 右侧文本框内容
	 */
	@ViewInject(R.id.item_msglist_text_r)
	public FaceTextView rMsgTxt;

    /**
	 * 右侧语音时长
	 */
	@ViewInject(R.id.voice_content_len_r)
	public TextView rDuration;

    /**
	 * 右侧语音动画
	 */
	@ViewInject(R.id.voice_chat_recd_tv_r)
	public ImageView vChatContentTo;

	/**
	 * 右侧图片
	 */
	@ViewInject(R.id.item_msglist_img_r)
	public ImageView rMsgImg;

    /**
     * 右侧进度条
     */
    @ViewInject(R.id.progressbar_upload_r)
    public ProgressBar rUploadProgress;

    /**
     * 右侧发送消息失败提示图片
     */
    @ViewInject(R.id.img_warn_r)
    public ImageView rWarnImg;

    /**
     * 左侧发送人名称（在群聊中显示发送人姓名）
     */
    @ViewInject(R.id.tv_sender_name)
    public TextView senderNameView;

}
