package com.zuzhili.controller;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;

public class TrendViewHolder {
	
	/***********************************************************
	 *
	 *	头部控件
	 *
	 **********************************************************/
    @ViewInject(R.id.rla_header_container)
    public RelativeLayout rlaHeaderContainer;

	/** 用户头像 */
	@ViewInject(R.id.img_user_head)
	public ImageView userHeadImg;
	
	public ImageContainer userHeadRequest;
	
	/** 动态内容的类型 */
	@ViewInject(R.id.img_content_flag)
	public ImageView contentFlagImg;
	/** 用户名称 */
	@ViewInject(R.id.txt_user_name)
	public TextView userNameTxt;
	
	/***********************************************************
	 *
	 *	动态的所有控件
	 *
	 **********************************************************/
	/** 引用的动态布局 */
	@ViewInject(R.id.rla_trend)
	public RelativeLayout rendGroupRla;
	/** 动态内容*/
	@ViewInject(R.id.txt_trend_content)
	public TextView trendContentTxt;
	/** 发布时间 */
	@ViewInject(R.id.txt_time)
	public TextView publishTimeTxt;
	/** 动态标题 */
	@ViewInject(R.id.txt_trend_title)
	public TextView trendTitleTxt;
    @ViewInject(R.id.img_top_indi)
    public ImageView topIndiImg;
    @ViewInject(R.id.img_top_indi2)
    public ImageView topIndiImgAlignContent;
	
	/** 附件group */
	@ViewInject(R.id.rla_trend_attachment)
	public RelativeLayout trendAttachmentGroupRla;
	/** 附件 */
	@ViewInject(R.id.txt_trend_attachment1)
	public TextView trendAttachment1Txt;
	@ViewInject(R.id.txt_trend_attachment2)
	public TextView trendAttachment2Txt;
	@ViewInject(R.id.txt_trend_attachment3)
	public TextView trendAttachment3Txt;
	@ViewInject(R.id.txt_trend_attachment4)
	public TextView trendAttachment4Txt;
	@ViewInject(R.id.txt_trend_attachment5)
	public TextView trendAttachment5Txt;
	
	/** 动态图片group */
	@ViewInject(R.id.lin_trend_images_container)
	public LinearLayout trendImagesGroupLin;
	/** 动态图片 */
	@ViewInject(R.id.img_trend_image1)
	public ImageView trendImage1Img;
	
	public ImageContainer trendImage1ImgRequest;
	
	@ViewInject(R.id.img_trend_image2)
	public ImageView trendImage2Img;
	
	public ImageContainer trendImage2ImgRequest;
	
	@ViewInject(R.id.img_trend_image3)
	public ImageView trendImage3Img;
	
	public ImageContainer trendImage3ImgRequest;
	
	/** 动态发布在公共空间位置 */
	@ViewInject(R.id.txt_trend_publish_position)
	public TextView trendPublishInSpace;
	
	/***********************************************************
	 *
	 *	引用的动态的所有控件
	 *
	 **********************************************************/
	/** 引用的动态布局 */
	@ViewInject(R.id.rla_quoted_trend)
	public RelativeLayout quotedTrendGroupRla;
	/** 引用的动态内容*/
	@ViewInject(R.id.txt_quoted_trend_content)
	public TextView quotedTrendContentTxt;
	/** 动态标题 */
	@ViewInject(R.id.txt_quoted_trend_title)
	public TextView quotedTrendTitleTxt;
	
	/** 引用的附件group */
	@ViewInject(R.id.rla_quoted_trend_attachment)
	public RelativeLayout quotedTrendAttachmentGroupRla;
	/** 引用的附件 */
	@ViewInject(R.id.txt_quoted_trend_attachment1)
	public TextView quotedTrendAttachment1Txt;
	@ViewInject(R.id.txt_quoted_trend_attachment2)
	public TextView quotedTrendAttachment2Txt;
	@ViewInject(R.id.txt_quoted_trend_attachment3)
	public TextView quotedTrendAttachment3Txt;
	@ViewInject(R.id.txt_quoted_trend_attachment4)
	public TextView quotedTrendAttachment4Txt;
	@ViewInject(R.id.txt_quoted_trend_attachment5)
	public TextView quotedTrendAttachment5Txt;
	
	/** 动态图片group */
	@ViewInject(R.id.lin_quoted_trend_images_container)
	public LinearLayout quotedTrendImagesGroupLin;
	/** 动态图片 */
	@ViewInject(R.id.img_quoted_image1)
	public ImageView quotedTrendImage1Img;
	
	public ImageContainer quotedTrendImage1ImgRequest;
	
	@ViewInject(R.id.img_quoted_image2)
	public ImageView quotedTrendImage2Img;
	
	public ImageContainer quotedTrendImage2ImgRequest;
	
	@ViewInject(R.id.img_quoted_image3)
	public ImageView quotedTrendImage3Img;
	
	public ImageContainer quotedTrendImage3ImgRequest;
	
	/** 引用的动态发布在公共空间的位置 */
	@ViewInject(R.id.txt_quoted_trend_publish_position)
	public TextView quoteTrendPublishInSpace;
	
	/***********************************************************
	 *
	 *	底部所有控件
	 *
	 **********************************************************/
	/** 评论数目 */
	@ViewInject(R.id.txt_comment_num)
	public TextView commentNumTxt;
	/** 转发数目 */
	@ViewInject(R.id.txt_repost_num)
	public TextView repostNumTxt;
	/** 收藏数目 */
	@ViewInject(R.id.txt_collect_num)
	public TextView collectNumTxt;
	/** 评论图标 */
	@ViewInject(R.id.img_comment_flag)
	public ImageView commentImg;
	/** 转发图标 */
	@ViewInject(R.id.img_repost_flag)
	public ImageView repostImg;
	/** 收藏图标 */
	@ViewInject(R.id.img_collect_flag)
	public ImageView collectImg;
	/** 设备标志 */
	@ViewInject(R.id.txt_published_by_device_type)
	public TextView publishedByDeviceType;
    @ViewInject(R.id.txt_footer_time)
    public TextView publishTimeFooterTxt;
	
}