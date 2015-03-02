package com.zuzhili.controller.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.zuzhili.R;
import com.zuzhili.bussiness.socket.model.AttachmentChat;
import com.zuzhili.bussiness.socket.model.ChatMessage;
import com.zuzhili.bussiness.socket.model.TextChat;
import com.zuzhili.bussiness.socket.model.TextChatMessage;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.bussiness.utility.TimeUtils;
import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.framework.utils.ScalingUtilities;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.framework.utils.VolleyImageUtils;
import com.zuzhili.model.msg.Attachment;
import com.zuzhili.model.msg.MsgDetail;
import com.zuzhili.ui.LoadImageTask;
import com.zuzhili.ui.activity.BaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GroupChatViewHelper {

    public static final int VIEW_TYPE_OTHER = 0;
    public static final int VIEW_TYPE_MYSELF = 1;

    private LayoutInflater mInflater;
    private Context mContext;
    private ImageLoader mImageLoader;

    public GroupChatViewHelper(Context context, ImageLoader imageLoader) {
        this.mContext = context;
        this.mImageLoader = imageLoader;
        this.mInflater = LayoutInflater.from(context);
    }

    /**
     * 针对不同类型的元素填充不同的view item
     *
     * @param viewType
     * @param parent
     * @return
     */
    public View populateFitItemView(int viewType, ViewGroup parent) {
        if (viewType == VIEW_TYPE_MYSELF) {
            return mInflater.inflate(R.layout.activity_letteritem_right, parent, false);
        } else {
            return mInflater.inflate(R.layout.activity_letteritem_left, parent, false);
        }
    }

    //展示左侧列表详情
    public void showLeftMsgDetail(ChatMessage chatMessage, TalkDetailHolder holder, final BaseActivity activity) {
        holder.lMsgContainerFrl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        try {
            mImageLoader.get(TextUtil.processNullString(chatMessage.getUserAvatar()), ImageLoader.getImageListener(holder.lUserHeadImg, R.drawable.default_user_head_small, R.drawable.default_user_head_small));
        } catch (Exception e) {
            holder.lUserHeadImg.setImageResource(R.drawable.default_user_head_small);
        }

        if (chatMessage instanceof TextChatMessage) {
            if (!TextUtils.isEmpty(TextUtil.processNullString(((TextChatMessage) chatMessage).getTextChat().getBody()))) {
                holder.lMsgTxt.setVisibility(View.VISIBLE);
                holder.lMsgTxt.setText(TextUtil.contentFilterSpan(((TextChatMessage) chatMessage).getTextChat().getBody(),
                        mContext, null, null, null));
            } else {
                holder.lMsgTxt.setVisibility(View.GONE);
            }
            holder.lTimeTxt.setText(TimeUtils.getTimeMinute(((TextChatMessage) chatMessage).getLastTalkTime()));
        } else if (chatMessage instanceof AttachmentChat) {
            if (!TextUtils.isEmpty(TextUtil.processNullString(((AttachmentChat) chatMessage).getName()))) {
                holder.lMsgTxt.setVisibility(View.VISIBLE);
                holder.lMsgTxt.setText(((AttachmentChat) chatMessage).getName());
            } else {
                holder.lMsgTxt.setVisibility(View.GONE);
            }
        }
//        if (null != msgDetail.getConfiglist() && msgDetail.getConfiglist().size() > 0) {
//            List<TextView> filesView = new ArrayList<TextView>();
//            filesView.add(holder.lFileOne);
//            filesView.add(holder.lFileTwo);
//            filesView.add(holder.lFileThree);
//            filesView.add(holder.lFileFour);
//            filesView.add(holder.lFileFive);
//            prcessConfig(holder.lMsgImg, filesView, msgDetail.getConfiglist(), holder.lMsgContainerLin);
//        } else {
//            holder.lMsgImg.setVisibility(View.GONE);
//            holder.lFilesContainerLin.setVisibility(View.GONE);
//        }
    }

    //展示右侧列表详情
    public void showRightMsgDetail(ChatMessage chatMessage, TalkDetailHolder holder, String userhead, final BaseActivity activity) {
//        TextChat textChat = null;
//        AttachmentChat attachmentChat = null;
        holder.rMsgContainerFrl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        try {
            mImageLoader.get(TextUtil.processNullString(userhead), ImageLoader.getImageListener(holder.rUserHeadImg, R.drawable.default_user_head_small, R.drawable.default_user_head_small));
        } catch (Exception e) {
            holder.rUserHeadImg.setImageResource(R.drawable.default_user_head_small);
        }

        if (chatMessage instanceof TextChatMessage) {
            if (!TextUtils.isEmpty(TextUtil.processNullString(((TextChatMessage) chatMessage).getTextChat().getBody()))) {
                holder.rMsgTxt.setVisibility(View.VISIBLE);
                holder.rMsgTxt.setText(TextUtil.contentFilterSpan(((TextChatMessage) chatMessage).getTextChat().getBody(), mContext, null, null, null));
            } else {
                holder.rMsgTxt.setVisibility(View.GONE);
            }
            holder.rTimeTxt.setText(TimeUtils.getTimeMinute(((TextChatMessage) chatMessage).getLastTalkTime()));
        } else if (chatMessage instanceof AttachmentChat) {
            if (!TextUtils.isEmpty(TextUtil.processNullString(((AttachmentChat) chatMessage).getName()))) {
                holder.rMsgTxt.setVisibility(View.VISIBLE);
                holder.rMsgTxt.setText(((AttachmentChat) chatMessage).getName());
            } else {
                holder.rMsgTxt.setVisibility(View.GONE);
            }
        }

//        if (null != msgDetail.getConfiglist() && msgDetail.getConfiglist().size() > 0) {
//            List<TextView> filesView = new ArrayList<TextView>();
//            filesView.add(holder.rFileOne);
//            filesView.add(holder.rFileTwo);
//            filesView.add(holder.rFileThree);
//            filesView.add(holder.rFileFour);
//            filesView.add(holder.rFileFive);
//            prcessConfig(holder.rMsgImg, filesView, msgDetail.getConfiglist(), holder.rFilesContainerLin);
//        } else {
//            holder.rMsgImg.setVisibility(View.GONE);
//            holder.rFilesContainerLin.setVisibility(View.GONE);
//        }
    }

    //展示图片和附件
    private void prcessConfig(final ImageView img, List<TextView> filesView,
                              List<Attachment> filesData, View filelayout) {
        int visiblefile = 0;
        if (filesData != null) {
            for (final Attachment file : filesData) {
                //展示图片
                if (file.getType().equals(Constants.RESOURCE_TYPE_PICTURE)) {
                    if (file.getPath().startsWith(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                        img.setTag(file.getPath());
                        new LoadImageTask(img).execute(mContext);
                    } else {
                        mImageLoader.get(file.getPath()
                                , VolleyImageUtils.getImageListener(mContext, img, R.drawable.photo_default, R.drawable.photo_default, DensityUtil.dip2px(mContext, 75), DensityUtil.dip2px(mContext, 75))
                                , DensityUtil.dip2px(mContext, 100), DensityUtil.dip2px(mContext, 100));
                        if (filelayout != null) {
                            filelayout.setVisibility(View.GONE);
                        }
                        img.setVisibility(View.VISIBLE);
                    }
                }
                //展示附件
                else if (file.getType().equals(Constants.RESOURCE_TYPE_FILE)) {
                    if (img != null) {
                        img.setVisibility(View.GONE);
                    }
                    filelayout.setVisibility(View.VISIBLE);
                    TextView tvfile = filesView.get(visiblefile);
                    tvfile.setVisibility(View.VISIBLE);
                    visiblefile++;
                    tvfile.setTag(file);
                    tvfile.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.ico_fujian, 0, 0, 0);
                    tvfile.setText(visiblefile + ". " + file.getName());
                }
            }
        }
    }

    /**
     * 按比例缩放图片 another solution
     * @param file
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    private Bitmap getScaledBitmapAnotherSolution(File file, int maxWidth, int maxHeight) {
        Bitmap scaledBitmap = null;
        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(file.getAbsolutePath(), maxWidth, maxHeight, ScalingUtilities.ScalingLogic.FIT);
            scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, maxWidth, maxHeight, ScalingUtilities.ScalingLogic.FIT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scaledBitmap;
    }

}
