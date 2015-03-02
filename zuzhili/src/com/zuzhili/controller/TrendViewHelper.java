package com.zuzhili.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.bussiness.utility.UserClickableSpan;
import com.zuzhili.model.Config;
import com.zuzhili.model.MiniBlog;
import com.zuzhili.model.folder.Photo;
import com.zuzhili.ui.activity.im.ImageBrowserActivity;

import java.util.ArrayList;
import java.util.List;

import static com.zuzhili.controller.TrendAdapter.TYPE_FILE;
import static com.zuzhili.controller.TrendAdapter.TYPE_MEDIA;
import static com.zuzhili.controller.TrendAdapter.TYPE_NULL;
import static com.zuzhili.controller.TrendAdapter.TYPE_PIC;
import static com.zuzhili.controller.TrendAdapter.VIEW_TYPE_QUOTED_TREND_WITH_MUTIL_MEDIA;
import static com.zuzhili.controller.TrendAdapter.VIEW_TYPE_QUOTED_TREND_WITH_TEXT_ONLY;
import static com.zuzhili.controller.TrendAdapter.VIEW_TYPE_TREND_TEXT_ONLY;
import static com.zuzhili.controller.TrendAdapter.VIEW_TYPE_TREND_WITH_MULTI_MEDIA;
import static com.zuzhili.controller.TrendAdapter.VIEW_TYPE_HEADVIEW_QUOTED_TREND_DETAIL_WITH_MUTIL_MEDIA;
import static com.zuzhili.controller.TrendAdapter.VIEW_TYPE_HEADVIEW_QUOTED_TREND_DETAIL_WITH_TEXT_ONLY;
import static com.zuzhili.controller.TrendAdapter.VIEW_TYPE_HEADVIEW_TREND_DETAIL_TEXT_ONLY;
import static com.zuzhili.controller.TrendAdapter.VIEW_TYPE_HEADVIEW_TREND_DETAIL_WITH_MULTI_MEDIA;


/**
 * Created by liutao on 14-2-25.
 */
public class TrendViewHelper {

    private Context mContext;

    private ImageLoader mImageLoader;

    private LayoutInflater mInflater;

    private final String BLANK_SPACE = "        ";

    public TrendViewHelper(Context context, ImageLoader imageLoader) {
        this.mContext = context;
        this.mImageLoader = imageLoader;
        this.mInflater = LayoutInflater.from(context);
    }

    /**
     * 根据数据的类型选择填充合适的item view
     * @param item
     * @param isDetailPage 动态详情页面head view
     */
    public int getViewType(MiniBlog item, boolean isDetailPage) {
        if(item.getChildAbs() != null) {
            if(item.getChildAbs().getConfiglist() != null && item.getChildAbs().getConfiglist().size() > 0) {
                return isDetailPage ? VIEW_TYPE_HEADVIEW_QUOTED_TREND_DETAIL_WITH_MUTIL_MEDIA : VIEW_TYPE_QUOTED_TREND_WITH_MUTIL_MEDIA;
            } else {
                return isDetailPage ? VIEW_TYPE_HEADVIEW_QUOTED_TREND_DETAIL_WITH_TEXT_ONLY : VIEW_TYPE_QUOTED_TREND_WITH_TEXT_ONLY;
            }
        } else {
            if(item.getConfiglist() != null && item.getConfiglist().size() > 0) {
                return isDetailPage ? VIEW_TYPE_HEADVIEW_TREND_DETAIL_WITH_MULTI_MEDIA : VIEW_TYPE_TREND_WITH_MULTI_MEDIA;
            } else {
                return isDetailPage ? VIEW_TYPE_HEADVIEW_TREND_DETAIL_TEXT_ONLY : VIEW_TYPE_TREND_TEXT_ONLY;
            }
        }
    }

    /**
     * 针对不同类型的元素填充不同的view item
     * @param viewType
     * @param parent
     * @return
     */
    public View populateFitItemView(int viewType, ViewGroup parent) {
        switch (viewType) {
            case VIEW_TYPE_TREND_TEXT_ONLY:
                return mInflater.inflate(R.layout.listview_item_trend_text_only, parent, false);
            case VIEW_TYPE_TREND_WITH_MULTI_MEDIA:
                return mInflater.inflate(R.layout.listview_item_trend_multi_media, parent, false);
            case VIEW_TYPE_QUOTED_TREND_WITH_TEXT_ONLY:
                return mInflater.inflate(R.layout.listview_item_quoted_trend_text_only, parent, false);
            case VIEW_TYPE_QUOTED_TREND_WITH_MUTIL_MEDIA:
                return mInflater.inflate(R.layout.listview_item_quoted_trend_multi_media, parent, false);
            case VIEW_TYPE_HEADVIEW_TREND_DETAIL_TEXT_ONLY:
                return mInflater.inflate(R.layout.listview_item_headview_trend_text_only, parent, false);
            case VIEW_TYPE_HEADVIEW_TREND_DETAIL_WITH_MULTI_MEDIA:
                return mInflater.inflate(R.layout.listview_item_headview_trend_multi_media, parent, false);
            case VIEW_TYPE_HEADVIEW_QUOTED_TREND_DETAIL_WITH_TEXT_ONLY:
                return mInflater.inflate(R.layout.listview_item_headview_quoted_trend_text_only, parent, false);
            case VIEW_TYPE_HEADVIEW_QUOTED_TREND_DETAIL_WITH_MUTIL_MEDIA:
                return mInflater.inflate(R.layout.listview_item_headview_quoted_trend_multi_media, parent, false);

            default:
                return null;
        }
    }

    /**
     * 显示评论数目
     */
    public void showCommentNum(TrendViewHolder holder, int commentNum) {
        if(commentNum > 0) {
            holder.commentImg.setVisibility(View.VISIBLE);
            holder.commentImg.setBackgroundResource(R.drawable.icon_trend_item_comment_gray);
            holder.commentNumTxt.setVisibility(View.VISIBLE);
            holder.commentNumTxt.setText(String.valueOf(commentNum));
        } else {
            holder.commentImg.setVisibility(View.GONE);
            holder.commentNumTxt.setVisibility(View.GONE);
        }
    }

    /**
     * 显示转发数目
     */
    public void showRepostNum(TrendViewHolder holder, int repostNum) {
        if(repostNum > 0) {
            holder.repostImg.setVisibility(View.VISIBLE);
            holder.repostImg.setBackgroundResource(R.drawable.icon_trend_item_repost_gray);
            holder.repostNumTxt.setVisibility(View.VISIBLE);
            holder.repostNumTxt.setText(String.valueOf(repostNum));
        } else {
            holder.repostImg.setVisibility(View.GONE);
            holder.repostNumTxt.setVisibility(View.GONE);
        }
    }

    /**
     * 显示收藏数目
     */
    public void showCollectNum(TrendViewHolder holder, int collectNum) {
        if(collectNum > 0) {
            holder.collectImg.setVisibility(View.VISIBLE);
            holder.collectImg.setBackgroundResource(R.drawable.icon_trend_item_collect_gray);
            holder.collectNumTxt.setVisibility(View.VISIBLE);
            holder.collectNumTxt.setText(String.valueOf(collectNum));
        } else {
            holder.collectImg.setVisibility(View.GONE);
            holder.collectNumTxt.setVisibility(View.GONE);
        }
    }

    /**
     * 显示发出的设备
     * @param holder
     * @param publishByDeviceType
     */
    public void showPublishByDeviceType(TrendViewHolder holder, String publishByDeviceType) {
        holder.publishedByDeviceType.setText(publishByDeviceType);
    }

    /**
     * 显示动态内容
     * @param holder
     * @param trendContent
     */
    public void showTrendContent(TrendViewHolder holder, String trendContent, String trendTitle, boolean isUp) {
        if(trendContent != null && !TextUtils.isEmpty(trendContent)) {
            trendContent = TextUtil.Html2Text(trendContent.replace("null", ""));
            if(trendContent.contains("\r\n[")){
                holder.trendContentTxt.setLineSpacing(1.2f,1.2f);
            }
            if(trendContent.length() > 140) {
                trendContent = trendContent.substring(0, 140) + "...";
            }
            holder.trendContentTxt.setVisibility(View.VISIBLE);

            if (isUp && (trendTitle == null || TextUtils.isEmpty(trendTitle))) {
                holder.topIndiImgAlignContent.setVisibility(View.VISIBLE);
                holder.trendContentTxt.setText(TextUtil.contentFilterSpan(BLANK_SPACE + trendContent, mContext, new UserClickableSpan(mContext, null), null, null));
            } else {
                holder.topIndiImgAlignContent.setVisibility(View.GONE);
                holder.trendContentTxt.setText(TextUtil.contentFilterSpan(trendContent, mContext, new UserClickableSpan(mContext, null), null, null));
            }
        } else {
            holder.trendContentTxt.setVisibility(View.GONE);
        }
    }


    /**
     * 显示引用动态的内容
     * @param holder
     * @param quotedTrendContent
     */
    public void showQuotedTrendContent(MiniBlog quotedMiniBlog, TrendViewHolder holder, String quotedTrendContent) {
        if(quotedTrendContent != null && !TextUtils.isEmpty(quotedTrendContent)) {
            quotedTrendContent = TextUtil.Html2Text(quotedTrendContent.replace("null", ""));
            if(quotedTrendContent.length() > 140) {
                quotedTrendContent = quotedTrendContent.substring(0, 140) + "...";
            }
            holder.quotedTrendContentTxt.setVisibility(View.VISIBLE);
            // 判断是否要添加被引用动态的作者，如果无标题，就在内容前添加
            if(quotedMiniBlog.getTitle() != null && !TextUtils.isEmpty(quotedMiniBlog.getTitle())) {
                holder.quotedTrendContentTxt.setText(TextUtil.contentFilter(quotedTrendContent, mContext));
            } else {
                holder.quotedTrendContentTxt.setText(TextUtil.contentFilterSpan(appendQuotedUsername(quotedMiniBlog, quotedTrendContent), mContext, new UserClickableSpan(mContext, null), null, null));
            }
        } else {
            holder.quotedTrendContentTxt.setVisibility(View.GONE);
        }
    }

    /**
     * 显示动态的标题
     * @param holder
     * @param trendTitle
     */
    public void showTrendTitle(TrendViewHolder holder, String trendTitle, boolean isUp) {
        if(trendTitle != null && !TextUtils.isEmpty(trendTitle)) {
            trendTitle = TextUtil.Html2Text(trendTitle.replace("null", ""));
            holder.trendTitleTxt.setVisibility(View.VISIBLE);


            if (isUp && trendTitle != null && !TextUtils.isEmpty(trendTitle)) {
                holder.topIndiImg.setVisibility(View.VISIBLE);
                holder.trendTitleTxt.setText(TextUtil.contentFilterSpan(BLANK_SPACE + trendTitle, mContext, new UserClickableSpan(mContext, null), null, null));
            } else {
                holder.topIndiImg.setVisibility(View.GONE);
                holder.trendTitleTxt.setText(TextUtil.contentFilterSpan(trendTitle, mContext, new UserClickableSpan(mContext, null), null, null));
            }
        } else {
            holder.trendTitleTxt.setVisibility(View.GONE);
            holder.topIndiImg.setVisibility(View.GONE);
        }
    }

    /**
     * 获取动态标题内容
     * @param holder
     * @paramtrendContent
     */
    public String getTrendTitle(TrendViewHolder holder){

        return holder.trendTitleTxt.getText().toString();
    }

    /**
     * 显示引用动态的标题
     * @param holder
     * @param quotedTrendTitle
     */
    public void showQuotedTrendTitle(MiniBlog quotedMiniBlog, TrendViewHolder holder, String quotedTrendTitle) {
        if(quotedTrendTitle != null && !TextUtils.isEmpty(quotedTrendTitle)) {
            quotedTrendTitle = TextUtil.Html2Text(quotedTrendTitle.replace("null", ""));
            holder.quotedTrendTitleTxt.setVisibility(View.VISIBLE);
            holder.quotedTrendTitleTxt.setText(TextUtil.contentFilterSpan(appendQuotedUsername(quotedMiniBlog, quotedTrendTitle), mContext, new UserClickableSpan(mContext, null), null, null));
        } else {
            holder.quotedTrendTitleTxt.setVisibility(View.GONE);
        }
    }

    /**
     * 添加被引用动态的作者
     * @param s
     * @return
     */
    public String appendQuotedUsername(MiniBlog quotedMiniBlog, String s) {
        StringBuilder builder = new StringBuilder();
        builder.append("@")
                .append(quotedMiniBlog.getUserName())
                .append("(")
                .append(quotedMiniBlog.getIds())
                .append("): ")
                .append(s);
        return builder.toString();
    }

    /**
     * 最多只显示5个附件，多出的隐藏不显示
     * @param attachmentsTxt
     * @param configs
     */
    public void controlAttachmentsVisiblity(TextView[] attachmentsTxt, List<Config> configs) {
        for(int i=0; i< configs.size() && i<5; i++){
            attachmentsTxt[i].setVisibility(View.VISIBLE);
            attachmentsTxt[i].setText((i+1) + "." + configs.get(i).getName());
            for(int j=0;j<5;j++){
                if(j > i){
                    attachmentsTxt[j].setVisibility(View.GONE);
                }
            }
        }
    }

    /**
     * 显示动态里的附件
     * @param holder
     * @param configs
     */
    public void showTrendAttachment(TrendViewHolder holder, List<Config> configs) {
        if (holder.contentFlagImg != null) {
            holder.contentFlagImg.setImageResource(0);
            holder.contentFlagImg.setVisibility(View.VISIBLE);
            holder.contentFlagImg.setImageResource(R.drawable.icon_timeline_item_file_flag);
        }
        holder.trendAttachmentGroupRla.setVisibility(View.VISIBLE);
        holder.trendImagesGroupLin.setVisibility(View.GONE);
        TextView[] attachmentsTxt = {holder.trendAttachment1Txt
                , holder.trendAttachment2Txt
                , holder.trendAttachment3Txt
                , holder.trendAttachment4Txt
                , holder.trendAttachment5Txt
        };
        controlAttachmentsVisiblity(attachmentsTxt, configs);
    }

    /**
     * 显示引用动态里的附件
     * @param holder
     * @param configs
     */
    public void showQuotedTrendAttachment(TrendViewHolder holder, List<Config> configs) {
        if (holder.contentFlagImg != null) {
            holder.contentFlagImg.setImageResource(0);
            holder.contentFlagImg.setVisibility(View.VISIBLE);
            holder.contentFlagImg.setImageResource(R.drawable.icon_timeline_item_file_flag);
        }
        holder.quotedTrendAttachmentGroupRla.setVisibility(View.VISIBLE);
        holder.quotedTrendImagesGroupLin.setVisibility(View.GONE);
        TextView[] attachmentsTxt = {holder.quotedTrendAttachment1Txt
                , holder.quotedTrendAttachment2Txt
                , holder.quotedTrendAttachment3Txt
                , holder.quotedTrendAttachment4Txt
                , holder.quotedTrendAttachment5Txt
        };
        controlAttachmentsVisiblity(attachmentsTxt, configs);
    }

    /**
     * 显示动态里上传的图片
     * @param holder
     * @param images
     */
    public void showTrendImages(TrendViewHolder holder, final List<Config> images) {
        if(images == null || (images != null && images.size() == 0)) {
            holder.trendImagesGroupLin.setVisibility(View.GONE);
            holder.contentFlagImg.setVisibility(View.GONE);
        } else {
            holder.trendImagesGroupLin.setVisibility(View.VISIBLE);
            if (holder.contentFlagImg != null) {
                holder.contentFlagImg.setImageResource(0);
                holder.contentFlagImg.setVisibility(View.VISIBLE);
                holder.contentFlagImg.setImageResource(R.drawable.icon_timeline_item_pic_flag);
            }

            if(holder.trendImage1ImgRequest != null) {
                holder.trendImage1ImgRequest.cancelRequest();
            }

            if(holder.trendImage2ImgRequest != null) {
                holder.trendImage2ImgRequest.cancelRequest();
            }

            if(holder.trendImage3ImgRequest != null) {
                holder.trendImage3ImgRequest.cancelRequest();
            }

            if(images.size() == 1) {
                holder.trendImage1Img.setVisibility(View.VISIBLE);
                holder.trendImage2Img.setVisibility(View.GONE);
                holder.trendImage3Img.setVisibility(View.GONE);

                holder.trendImage1ImgRequest = mImageLoader.get(images.get(0).getPath()
                        , ImageLoader.getImageListener(holder.trendImage1Img, R.drawable.icon_head, R.drawable.icon_head));
                Photo photo=new Photo(images.get(0).getId(),images.get(0).getSourcepath(),images.get(0).getDesc());
                holder.trendImage1Img.setOnClickListener(new OnIconClickListener(photo));
            } else if(images.size() == 2) {
                holder.trendImage1Img.setVisibility(View.VISIBLE);
                holder.trendImage2Img.setVisibility(View.VISIBLE);
                holder.trendImage3Img.setVisibility(View.GONE);

                holder.trendImage1ImgRequest = mImageLoader.get(images.get(0).getPath()
                        , ImageLoader.getImageListener(holder.trendImage1Img, R.drawable.icon_head, R.drawable.icon_head));

                holder.trendImage2ImgRequest = mImageLoader.get(images.get(1).getPath()
                        , ImageLoader.getImageListener(holder.trendImage2Img, R.drawable.icon_head, R.drawable.icon_head));

                ArrayList<Photo> list=new ArrayList<Photo>();
                Photo photo=new Photo(images.get(0).getId(),images.get(0).getSourcepath(),images.get(0).getDesc());
                Photo photo2=new Photo(images.get(1).getId(),images.get(1).getSourcepath(),images.get(1).getDesc());
                list.add(photo);
                list.add(photo2);

                holder.trendImage1Img.setOnClickListener(new OnAlbumClickListener(list, 0));
                holder.trendImage2Img.setOnClickListener(new OnAlbumClickListener(list,1));
            } else {
                holder.trendImage1Img.setVisibility(View.VISIBLE);
                holder.trendImage2Img.setVisibility(View.VISIBLE);
                holder.trendImage3Img.setVisibility(View.VISIBLE);

                holder.trendImage1ImgRequest = mImageLoader.get(images.get(0).getPath()
                        , ImageLoader.getImageListener(holder.trendImage1Img, R.drawable.icon_head, R.drawable.icon_head));

                holder.trendImage2ImgRequest = mImageLoader.get(images.get(1).getPath()
                        , ImageLoader.getImageListener(holder.trendImage2Img, R.drawable.icon_head, R.drawable.icon_head));

                holder.trendImage3ImgRequest = mImageLoader.get(images.get(2).getPath()
                        , ImageLoader.getImageListener(holder.trendImage3Img, R.drawable.icon_head, R.drawable.icon_head));

                ArrayList<Photo> list=new ArrayList<Photo>();
                Photo photo=new Photo(images.get(0).getId(),images.get(0).getSourcepath(),images.get(0).getDesc());
                Photo photo2=new Photo(images.get(1).getId(),images.get(1).getSourcepath(),images.get(1).getDesc());
                Photo photo3=new Photo(images.get(2).getId(),images.get(2).getSourcepath(),images.get(2).getDesc());
                list.add(photo);
                list.add(photo2);
                list.add(photo3);

                holder.trendImage1Img.setOnClickListener(new OnAlbumClickListener(list,0));
                holder.trendImage2Img.setOnClickListener(new OnAlbumClickListener(list,1));
                holder.trendImage3Img.setOnClickListener(new OnAlbumClickListener(list,2));
            }
        }
    }

    private class OnIconClickListener implements View.OnClickListener {
        private Photo photo;

        public OnIconClickListener(Photo photo) {
            this.photo = photo;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext,ImageBrowserActivity.class);
            intent.putExtra(ImageBrowserActivity.IMAGE_TYPE,ImageBrowserActivity.TYPE_PHOTO);
            intent.putExtra("photo", photo);
            mContext.startActivity(intent);
        }
    }

    private class OnAlbumClickListener implements View.OnClickListener {
        private ArrayList<Photo> list;
        private int position;
        public OnAlbumClickListener(ArrayList<Photo> list, int position) {
            this.list = list;
            this.position=position;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext,ImageBrowserActivity.class);
            intent.putExtra(ImageBrowserActivity.IMAGE_TYPE,ImageBrowserActivity.TYPE_ALBUM);
            intent.putExtra("position", position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("photos",list);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        }
    }

    /**
     * 显示引用动态里的图片
     * @param holder
     * @param images
     */
    public void showQuotedTrendImages(TrendViewHolder holder, List<Config> images) {
        if(images == null || (images != null && images.size() == 0)) {
            holder.quotedTrendImagesGroupLin.setVisibility(View.GONE);
            holder.contentFlagImg.setVisibility(View.GONE);
        } else {
            holder.quotedTrendImagesGroupLin.setVisibility(View.VISIBLE);
            if (holder.contentFlagImg != null) {
                holder.contentFlagImg.setImageResource(0);
                holder.contentFlagImg.setVisibility(View.VISIBLE);
                holder.contentFlagImg.setImageResource(R.drawable.icon_timeline_item_pic_flag);
            }

            if(holder.quotedTrendImage1ImgRequest != null) {
                holder.quotedTrendImage1ImgRequest.cancelRequest();
            }

            if(holder.quotedTrendImage2ImgRequest != null) {
                holder.quotedTrendImage1ImgRequest.cancelRequest();
            }

            if(holder.quotedTrendImage3ImgRequest != null) {
                holder.quotedTrendImage1ImgRequest.cancelRequest();
            }

            if(images.size() == 1) {
                holder.quotedTrendImage1Img.setVisibility(View.VISIBLE);
                holder.quotedTrendImage2Img.setVisibility(View.GONE);
                holder.quotedTrendImage3Img.setVisibility(View.GONE);

                holder.quotedTrendImage1ImgRequest = mImageLoader.get(images.get(0).getPath()
                        , ImageLoader.getImageListener(holder.quotedTrendImage1Img, R.drawable.icon_head, R.drawable.icon_head));
                Photo photo=new Photo();
                photo.setId(images.get(0).getId());
                photo.setUrl_source(images.get(0).getSourcepath());
                photo.setDescription(images.get(0).getDesc());
                holder.quotedTrendImage1Img.setOnClickListener(new OnIconClickListener(photo));
            } else if(images.size() == 2) {
                holder.quotedTrendImage1Img.setVisibility(View.VISIBLE);
                holder.quotedTrendImage2Img.setVisibility(View.VISIBLE);
                holder.quotedTrendImage3Img.setVisibility(View.GONE);

                holder.quotedTrendImage1ImgRequest = mImageLoader.get(images.get(0).getPath()
                        , ImageLoader.getImageListener(holder.quotedTrendImage1Img, R.drawable.icon_head, R.drawable.icon_head));

                holder.quotedTrendImage2ImgRequest = mImageLoader.get(images.get(1).getPath()
                        , ImageLoader.getImageListener(holder.quotedTrendImage2Img, R.drawable.icon_head, R.drawable.icon_head));
                ArrayList<Photo> list=new ArrayList<Photo>();
                Photo photo=new Photo(images.get(0).getId(),images.get(0).getSourcepath(),images.get(0).getDesc());
                Photo photo2=new Photo(images.get(1).getId(),images.get(1).getSourcepath(),images.get(1).getDesc());
                list.add(photo);
                list.add(photo2);

                holder.quotedTrendImage1Img.setOnClickListener(new OnAlbumClickListener(list,0));
                holder.quotedTrendImage2Img.setOnClickListener(new OnAlbumClickListener(list,1));

            } else {
                holder.quotedTrendImage1Img.setVisibility(View.VISIBLE);
                holder.quotedTrendImage2Img.setVisibility(View.VISIBLE);
                holder.quotedTrendImage3Img.setVisibility(View.VISIBLE);

                holder.quotedTrendImage1ImgRequest = mImageLoader.get(images.get(0).getPath()
                        , ImageLoader.getImageListener(holder.quotedTrendImage1Img, R.drawable.icon_head, R.drawable.icon_head));

                holder.quotedTrendImage2ImgRequest = mImageLoader.get(images.get(1).getPath()
                        , ImageLoader.getImageListener(holder.quotedTrendImage2Img, R.drawable.icon_head, R.drawable.icon_head));

                holder.quotedTrendImage3ImgRequest = mImageLoader.get(images.get(2).getPath()
                        , ImageLoader.getImageListener(holder.quotedTrendImage3Img, R.drawable.icon_head, R.drawable.icon_head));
                ArrayList<Photo> list=new ArrayList<Photo>();
                Photo photo=new Photo(images.get(0).getId(),images.get(0).getSourcepath(),images.get(0).getDesc());
                Photo photo2=new Photo(images.get(1).getId(),images.get(1).getSourcepath(),images.get(1).getDesc());
                Photo photo3=new Photo(images.get(2).getId(),images.get(2).getSourcepath(),images.get(2).getDesc());
                list.add(photo);
                list.add(photo2);
                list.add(photo3);

                holder.quotedTrendImage1Img.setOnClickListener(new OnAlbumClickListener(list,0));
                holder.quotedTrendImage2Img.setOnClickListener(new OnAlbumClickListener(list,1));
                holder.quotedTrendImage3Img.setOnClickListener(new OnAlbumClickListener(list,2));
            }
        }
    }

    /**
     * 获取文件类型
     * @param configs
     * @return
     */
    public String getConfigType(List<Config> configs) {
        if(configs != null && configs.size() > 0) {
            String type = configs.get(0).getType();
            if(type.equals("6")) {
                return TYPE_PIC;
            } else if(type.equals("9")) {
                return TYPE_FILE;
            } else {
                return TYPE_MEDIA;
            }
        } else {
            return TYPE_NULL;
        }
    }

    public void showConfigs(TrendViewHolder holder, List<Config> configs, boolean isQuotedTrend) {
        String configType = getConfigType(configs);
        if(configType.equals(TYPE_PIC)) {
            if(isQuotedTrend) {
                holder.quotedTrendAttachmentGroupRla.setVisibility(View.GONE);
                showQuotedTrendImages(holder, configs);
            } else {
                holder.trendAttachmentGroupRla.setVisibility(View.GONE);
                showTrendImages(holder, configs);
            }
            return;
        }
        if(configType.equals(TYPE_FILE)) {
            if(isQuotedTrend) {
                holder.quotedTrendImagesGroupLin.setVisibility(View.GONE);
                showQuotedTrendAttachment(holder, configs);
            } else {
                holder.trendImagesGroupLin.setVisibility(View.GONE);
                showTrendAttachment(holder, configs);
            }
            return;
        }
        // TODO:视频封面未显示
    }

}