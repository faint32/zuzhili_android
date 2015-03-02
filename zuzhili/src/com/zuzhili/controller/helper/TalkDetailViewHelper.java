package com.zuzhili.controller.helper;

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.toolbox.ImageLoader;
import com.zuzhili.R;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.IMParseUtil;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.framework.utils.ScalingUtilities;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.framework.utils.VolleyImageUtils;
import com.zuzhili.model.Member;
import com.zuzhili.model.folder.Photo;
import com.zuzhili.model.im.IMChatMessageDetail;
import com.zuzhili.model.msg.Attachment;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.im.ForwardActivity;
import com.zuzhili.ui.activity.im.GroupChatActivity;
import com.zuzhili.ui.activity.im.ImageBrowserActivity;
import com.zuzhili.ui.activity.space.CommonSpaceActivity;
import com.zuzhili.ui.activity.space.SpaceActivity;

public class TalkDetailViewHelper {

    public static final int VIEW_TYPE_OTHER = 0;
    public static final int VIEW_TYPE_MYSELF = 1;

    public static final long FIVE_MINUTES = 5 * 60 * 1000;

    private LayoutInflater mInflater;
    private Context mContext;
    private ImageLoader mImageLoader;
    private Session session;
    public TalkDetailViewHelper(Context context, ImageLoader imageLoader,Session session) {
        this.mContext = context;
        this.mImageLoader = imageLoader;
        this.session = session;
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
    public void showLeftMsgDetail(IMChatMessageDetail msgDetail, TalkDetailHolder holder, final GroupChatActivity activity, String lastMessageCreateTime, String groupId) {
        holder.lMsgImg.setImageResource(0);
        holder.lMsgImg.setVisibility(View.GONE);
        holder.lMsgContainerFrl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        try {
            mImageLoader.get(IMParseUtil.getSenderAvatar(msgDetail.getUserData()), ImageLoader.getImageListener(holder.lUserHeadImg, R.drawable.default_user_head_small, R.drawable.default_user_head_small));
        } catch (Exception e) {
            holder.lUserHeadImg.setImageResource(R.drawable.default_user_head_small);
        }
        holder.lMsgContainerFrl.setVisibility(View.VISIBLE);
        holder.lUserHeadImg.setOnClickListener(new OnUserHeadClickListener(msgDetail.getGroupSender(),msgDetail.getUserData()));
        // 显示消息发送时间
        if (shouldShowCurrentMessageCreateTime(lastMessageCreateTime, msgDetail.getCurDate())) {
            holder.lTimeTxt.setVisibility(View.VISIBLE);
            holder.lTimeTxt.setText(Utils.getFriendlyTime(Long.valueOf(msgDetail.getCurDate())));
        } else {
            holder.lTimeTxt.setVisibility(View.GONE);
        }

        holder.lDuration.setVisibility(View.GONE);
        holder.vChatContentFrom.setVisibility(View.GONE);

        // 显示消息内容
        if (!TextUtils.isEmpty(TextUtil.processNullString(msgDetail.getMessageContent()))) {
            holder.lMsgTxt.setVisibility(View.VISIBLE);
            holder.lMsgTxt.setEmojiText(msgDetail.getMessageContent());
        } else {
            holder.lMsgTxt.setVisibility(View.GONE);
        }
        // 显示发送状态
        if (msgDetail.getImState() == IMChatMessageDetail.STATE_IM_SENDING) {
            holder.lUploadProgress.setVisibility(View.VISIBLE);
            holder.lWarnImg.setVisibility(View.GONE);
        } else if (msgDetail.getImState() == IMChatMessageDetail.STATE_IM_SEND_FAILED) {
            holder.lUploadProgress.setVisibility(View.GONE);
            holder.lWarnImg.setVisibility(View.VISIBLE);
        } else {
            holder.lUploadProgress.setVisibility(View.GONE);
            holder.lWarnImg.setVisibility(View.GONE);
        }

        // indicate display name of sender
        if (groupId.startsWith("g")) {
            holder.senderNameView.setText(IMParseUtil.getSenderUserName(msgDetail.getUserData()));
            holder.senderNameView.setVisibility(View.VISIBLE);
        }else{
            holder.senderNameView.setVisibility(View.GONE);
        }
    }

    //展示右侧列表详情
    public void showRightMsgDetail(IMChatMessageDetail msgDetail, TalkDetailHolder holder,final GroupChatActivity activity, String lastMessageCreateTime) {
        holder.rMsgImg.setImageResource(0);
        holder.rMsgImg.setVisibility(View.GONE);
        holder.rMsgContainerFrl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        try {
            mImageLoader.get(IMParseUtil.getSenderAvatar(msgDetail.getUserData()), ImageLoader.getImageListener(holder.rUserHeadImg, R.drawable.default_user_head_small, R.drawable.default_user_head_small));
        } catch (Exception e) {
            holder.rUserHeadImg.setImageResource(R.drawable.default_user_head_small);
        }
        holder.rMsgContainerFrl.setVisibility(View.VISIBLE);
        holder.rUserHeadImg.setOnClickListener(new OnUserHeadClickListener(msgDetail.getGroupSender(),msgDetail.getUserData()));

        // 显示消息发送时间
        if (shouldShowCurrentMessageCreateTime(lastMessageCreateTime, msgDetail.getCurDate())) {
            holder.rTimeTxt.setVisibility(View.VISIBLE);
            holder.rTimeTxt.setText(Utils.getFriendlyTime(Long.valueOf(msgDetail.getCurDate())));
        } else {
            holder.rTimeTxt.setVisibility(View.GONE);
        }

        holder.rDuration.setVisibility(View.GONE);
        holder.vChatContentTo.setVisibility(View.GONE);

        // 显示消息内容
        if (!TextUtils.isEmpty(TextUtil.processNullString(msgDetail.getMessageContent()))) {
            holder.rMsgTxt.setVisibility(View.VISIBLE);
            holder.rMsgTxt.setEmojiText(msgDetail.getMessageContent());
        } else {
            holder.rMsgTxt.setVisibility(View.GONE);
        }
        // 显示发送状态
        if (msgDetail.getImState() == IMChatMessageDetail.STATE_IM_SENDING) {
            holder.rUploadProgress.setVisibility(View.VISIBLE);
            holder.rWarnImg.setVisibility(View.GONE);
        } else if (msgDetail.getImState() == IMChatMessageDetail.STATE_IM_SEND_FAILED) {
            holder.rUploadProgress.setVisibility(View.GONE);
            holder.rWarnImg.setVisibility(View.VISIBLE);
        } else {
            holder.rUploadProgress.setVisibility(View.GONE);
            holder.rWarnImg.setVisibility(View.GONE);
        }
        holder.rWarnImg.setOnClickListener(new OnWarnImgClickListener(msgDetail,activity));
    }


    //展示左侧语音
    public void showLeftVoiceDetail(final IMChatMessageDetail msgDetail, final TalkDetailHolder holder, final int position, final GroupChatActivity activity, String lastMessageCreateTime, final String groupId) {
        holder.lMsgImg.setImageResource(0);
        holder.lMsgImg.setVisibility(View.GONE);
        holder.lMsgContainerFrl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        try {
            mImageLoader.get(IMParseUtil.getSenderAvatar(msgDetail.getUserData()), ImageLoader.getImageListener(holder.lUserHeadImg, R.drawable.default_user_head_small, R.drawable.default_user_head_small));
        } catch (Exception e) {
            holder.lUserHeadImg.setImageResource(R.drawable.default_user_head_small);
        }
        holder.lMsgContainerFrl.setVisibility(View.VISIBLE);
        holder.lUserHeadImg.setOnClickListener(new OnUserHeadClickListener(msgDetail.getGroupSender(),msgDetail.getUserData()));
        // 显示消息发送时间
        if (shouldShowCurrentMessageCreateTime(lastMessageCreateTime, msgDetail.getCurDate())) {
            holder.lTimeTxt.setVisibility(View.VISIBLE);
            holder.lTimeTxt.setText(Utils.getFriendlyTime(Long.valueOf(msgDetail.getCurDate())));
        } else {
            holder.lTimeTxt.setVisibility(View.GONE);
        }

        holder.lDuration.setVisibility(View.VISIBLE);
        holder.vChatContentFrom.setVisibility(View.VISIBLE);

        int duration = 0;
        if (activity.checkeDeviceHelper()) {
            duration = (int) Math.ceil(activity.getDeviceHelper().getVoiceDuration(msgDetail.getFilePath()) / 1000);
        }
        duration = duration == 0 ? 1 : duration;
        holder.lDuration.setText(duration + "''");
        holder.lMsgTxt.setText(getLenByDuration(duration));

        holder.lMsgContainerFrl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(msgDetail.getFilePath()) && new File(msgDetail.getFilePath()).exists()) {
                    activity.viewPlayAnim(holder.vChatContentFrom, msgDetail.getFilePath(), position, true);
                    //CCPVoiceMediaPlayManager.getInstance(GroupChatActivity.this).putVoicePlayQueue(position, item.getFilePath());
                } else {
                    Toast.makeText(mContext, R.string.media_ejected, Toast.LENGTH_LONG).show();
                }
            }
        });
        holder.vChatContentFrom.setBackgroundResource(R.drawable.voice_from_playing);

        // 显示发送状态
        if (msgDetail.getImState() == IMChatMessageDetail.STATE_IM_SENDING) {
            holder.lUploadProgress.setVisibility(View.VISIBLE);
            holder.lWarnImg.setVisibility(View.GONE);
        } else if (msgDetail.getImState() == IMChatMessageDetail.STATE_IM_SEND_FAILED) {
            holder.lUploadProgress.setVisibility(View.GONE);
            holder.lDuration.setVisibility(View.GONE);
            holder.lWarnImg.setVisibility(View.VISIBLE);
        } else {
            holder.lUploadProgress.setVisibility(View.GONE);
            holder.lWarnImg.setVisibility(View.GONE);
        }

        // indicate display name of sender
        if (groupId.startsWith("g")) {
            holder.senderNameView.setText(IMParseUtil.getSenderUserName(msgDetail.getUserData()));
            holder.senderNameView.setVisibility(View.VISIBLE);
        }else{
            holder.senderNameView.setVisibility(View.GONE);
        }
    }

    //展示右侧语音
    public void showRightVoiceDetail(final IMChatMessageDetail msgDetail, final TalkDetailHolder holder, final int position, final GroupChatActivity activity, String lastMessageCreateTime) {
        holder.rMsgImg.setImageResource(0);
        holder.rMsgImg.setVisibility(View.GONE);
        holder.rMsgContainerFrl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        try {
            mImageLoader.get(IMParseUtil.getSenderAvatar(msgDetail.getUserData()), ImageLoader.getImageListener(holder.rUserHeadImg, R.drawable.default_user_head_small, R.drawable.default_user_head_small));
        } catch (Exception e) {
            holder.rUserHeadImg.setImageResource(R.drawable.default_user_head_small);
        }
        holder.rMsgContainerFrl.setVisibility(View.VISIBLE);

        holder.rUserHeadImg.setOnClickListener(new OnUserHeadClickListener(msgDetail.getGroupSender(),msgDetail.getUserData()));
        // 显示消息发送时间
        if (shouldShowCurrentMessageCreateTime(lastMessageCreateTime, msgDetail.getCurDate())) {
            holder.rTimeTxt.setVisibility(View.VISIBLE);
            holder.rTimeTxt.setText(Utils.getFriendlyTime(Long.valueOf(msgDetail.getCurDate())));
        } else {
            holder.rTimeTxt.setVisibility(View.GONE);
        }

        holder.rDuration.setVisibility(View.VISIBLE);
        holder.vChatContentTo.setVisibility(View.VISIBLE);
        int duration = 0;

        if (activity.checkeDeviceHelper()) {
            duration = (int) Math.ceil(activity.getDeviceHelper().getVoiceDuration(msgDetail.getFilePath()) / 1000);
        }
        duration = duration == 0 ? 1 : duration;
        holder.rDuration.setText(duration + "''");
        holder.rMsgTxt.setText(getLenByDuration(duration));

        //播放语音
        holder.rMsgContainerFrl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // It shows only in the presence of the voice files
                if (!TextUtils.isEmpty(msgDetail.getFilePath()) && new File(msgDetail.getFilePath()).exists()) {
                    activity.viewPlayAnim(holder.vChatContentTo, msgDetail.getFilePath(), position, false);
                    //CCPVoiceMediaPlayManager.getInstance(GroupChatActivity.this).putVoicePlayQueue(position, item.getFilePath());
                } else {
                    Toast.makeText(mContext, R.string.media_ejected, Toast.LENGTH_LONG).show();
                }
            }
        });
        holder.vChatContentTo.setBackgroundResource(R.drawable.voice_to_playing);

        // 显示发送状态
        if (msgDetail.getImState() == IMChatMessageDetail.STATE_IM_SENDING) {
            holder.rUploadProgress.setVisibility(View.VISIBLE);
            holder.rWarnImg.setVisibility(View.GONE);
        } else if (msgDetail.getImState() == IMChatMessageDetail.STATE_IM_SEND_FAILED) {
            holder.rUploadProgress.setVisibility(View.GONE);
            holder.rDuration.setVisibility(View.GONE);
            holder.rWarnImg.setVisibility(View.VISIBLE);
        } else {
            holder.rUploadProgress.setVisibility(View.GONE);
            holder.rWarnImg.setVisibility(View.GONE);
        }
        holder.rWarnImg.setOnClickListener(new OnWarnImgClickListener(msgDetail,activity));
    }

    //展示左侧图片
    public void showLeftPicDetail(final IMChatMessageDetail msgDetail, TalkDetailHolder holder, final GroupChatActivity activity, String lastMessageCreateTime, final String groupId) {
        holder.lMsgContainerFrl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        try {
            mImageLoader.get(IMParseUtil.getSenderAvatar(msgDetail.getUserData()), ImageLoader.getImageListener(holder.lUserHeadImg, R.drawable.default_user_head_small, R.drawable.default_user_head_small));
        } catch (Exception e) {
            holder.lUserHeadImg.setImageResource(R.drawable.default_user_head_small);
        }

        holder.lUserHeadImg.setOnClickListener(new OnUserHeadClickListener(msgDetail.getGroupSender(),msgDetail.getUserData()));

        // 显示消息发送时间
        if (shouldShowCurrentMessageCreateTime(lastMessageCreateTime, msgDetail.getCurDate())) {
            holder.lTimeTxt.setVisibility(View.VISIBLE);
            holder.lTimeTxt.setText(Utils.getFriendlyTime(Long.valueOf(msgDetail.getCurDate())));
        } else {
            holder.lTimeTxt.setVisibility(View.GONE);
        }

        holder.lDuration.setVisibility(View.GONE);
        holder.vChatContentFrom.setVisibility(View.GONE);
        holder.lMsgContainerFrl.setVisibility(View.GONE);
        holder.lMsgTxt.setText("");
        holder.lMsgImg.setVisibility(View.VISIBLE);
//        holder.lMsgImg.setImageResource(R.drawable.photo_default);
        holder.lMsgImg.setImageResource(0);

        // 显示图片
        if (!TextUtils.isEmpty(msgDetail.getFilePath())) {
            holder.lMsgImg.setTag(msgDetail.getFilePath());
            new LoadImage(holder.lMsgImg).execute();
        }
//        else {
//            ImageCacheManager.getInstance().getImageLoader().get(msgDetail.getFilePath()
//                    , VolleyImageUtils.getImageListener(mContext, holder.lMsgImg, R.drawable.photo_default, R.drawable.photo_default, DensityUtil.dip2px(mContext, 75), DensityUtil.dip2px(mContext, 75))
//                    , DensityUtil.dip2px(mContext, 100), DensityUtil.dip2px(mContext, 100));
//        }
        holder.lMsgImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ImageBrowserActivity.class);
                intent.putExtra(ImageBrowserActivity.IMAGE_TYPE,ImageBrowserActivity.TYPE_PHOTO);
                intent.putExtra(Constants.TAG_CHAT_CONTACTS,true);
                Photo photo=new Photo(null,msgDetail.getFilePath(),null);
                intent.putExtra("photo", photo);
                mContext.startActivity(intent);
                activity.overridePendingTransition(R.anim.zoom_enter, 0);
            }
        });

        holder.lMsgImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                builder.setTitle("消息操作");
                builder.setPositiveButton(R.string.action_repost,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(activity, ForwardActivity.class);
                        intent.putExtra(Constants.MSG_FORWORD, msgDetail.getFilePath());
                        intent.putExtra(Constants.MSG_TYPE, IMChatMessageDetail.TYPE_MSG_PIC);

                        activity.startActivity(intent);
                        activity.finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });
        // 显示发送状态
        if (msgDetail.getImState() == IMChatMessageDetail.STATE_IM_SENDING) {
            holder.lUploadProgress.setVisibility(View.VISIBLE);
            holder.lWarnImg.setVisibility(View.GONE);
        } else if (msgDetail.getImState() == IMChatMessageDetail.STATE_IM_SEND_FAILED) {
            holder.lUploadProgress.setVisibility(View.GONE);
            holder.lWarnImg.setVisibility(View.VISIBLE);
        } else {
            holder.lUploadProgress.setVisibility(View.GONE);
            holder.lWarnImg.setVisibility(View.GONE);
        }

        // indicate display name of sender
        if (groupId.startsWith("g")) {
            holder.senderNameView.setText(IMParseUtil.getSenderUserName(msgDetail.getUserData()));
            holder.senderNameView.setVisibility(View.VISIBLE);
        }else{
            holder.senderNameView.setVisibility(View.GONE);
        }
    }


    //展示右侧图片
    public void showRightPicDetail(final IMChatMessageDetail msgDetail, final TalkDetailHolder holder,final GroupChatActivity activity, String lastMessageCreateTime) {
        holder.rMsgContainerFrl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        try {
            mImageLoader.get(IMParseUtil.getSenderAvatar(msgDetail.getUserData()), ImageLoader.getImageListener(holder.rUserHeadImg, R.drawable.default_user_head_small, R.drawable.default_user_head_small));
        } catch (Exception e) {
            holder.rUserHeadImg.setImageResource(R.drawable.default_user_head_small);
        }

        holder.rUserHeadImg.setOnClickListener(new OnUserHeadClickListener(msgDetail.getGroupSender(),msgDetail.getUserData()));

        // 显示消息发送时间
        if (shouldShowCurrentMessageCreateTime(lastMessageCreateTime, msgDetail.getCurDate())) {
            holder.rTimeTxt.setVisibility(View.VISIBLE);
            holder.rTimeTxt.setText(Utils.getFriendlyTime(Long.valueOf(msgDetail.getCurDate())));
        } else {
            holder.rTimeTxt.setVisibility(View.GONE);
        }

        holder.rDuration.setVisibility(View.GONE);
        holder.vChatContentTo.setVisibility(View.GONE);
        holder.rMsgContainerFrl.setVisibility(View.GONE);
        holder.rMsgTxt.setText("");
        holder.rMsgImg.setVisibility(View.VISIBLE);
        holder.rMsgImg.setImageResource(0);

        // 显示图片
        if (!TextUtils.isEmpty(msgDetail.getFilePath())) {
            holder.rMsgImg.setTag(msgDetail.getFilePath());
            new LoadImage(holder.rMsgImg).execute();
        }

        holder.rMsgImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ImageBrowserActivity.class);
                intent.putExtra(ImageBrowserActivity.IMAGE_TYPE,ImageBrowserActivity.TYPE_PHOTO);
                intent.putExtra(Constants.TAG_CHAT_CONTACTS,true);
                Photo photo=new Photo(null,msgDetail.getFilePath(),null);
                intent.putExtra("photo", photo);
                mContext.startActivity(intent);
                activity.overridePendingTransition(R.anim.zoom_enter, 0);
            }
        });
        holder.rMsgImg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                builder.setTitle("消息操作");
                builder.setPositiveButton(R.string.action_repost,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(activity, ForwardActivity.class);
                        intent.putExtra(Constants.MSG_FORWORD, msgDetail.getFilePath());
                        intent.putExtra(Constants.MSG_TYPE, IMChatMessageDetail.TYPE_MSG_PIC);

                        activity.startActivity(intent);
                        activity.finish();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });


        // 显示发送状态
        if (msgDetail.getImState() == IMChatMessageDetail.STATE_IM_SENDING) {
            holder.rUploadProgress.setVisibility(View.VISIBLE);
            holder.rWarnImg.setVisibility(View.GONE);
        } else if (msgDetail.getImState() == IMChatMessageDetail.STATE_IM_SEND_FAILED) {
            holder.rUploadProgress.setVisibility(View.GONE);
            holder.rWarnImg.setVisibility(View.VISIBLE);
        } else {
            holder.rUploadProgress.setVisibility(View.GONE);
            holder.rWarnImg.setVisibility(View.GONE);
        }
        holder.rWarnImg.setOnClickListener(new OnWarnImgClickListener(msgDetail,activity));
    }

    private String getLenByDuration(int duration){
        duration=duration>10?10:duration;
        String s=" ";
        for(int i=0;i<duration;i++){
            s+="　";
        }
        return s;
    }

    /**
     * 头像点击事件
     */
    private class OnUserHeadClickListener implements View.OnClickListener{
        private String userData;
        private String y_id;
        OnUserHeadClickListener(String y_id,String userData){
            this.userData=userData;
            this.y_id=y_id;
        }

        @Override
        public void onClick(View v) {
            Member member = new Member();
            member.setListid(IMParseUtil.getListId(userData));
            member.setId(IMParseUtil.getIds(userData));
            member.setUserhead(IMParseUtil.getSenderAvatar(userData));

            UserInfo user=((BaseActivity)mContext).getDbHelper().getUserInfoTable().get(y_id,session.getListid(),Utils.getIdentity(session));
            Intent it = new Intent();
            it.setClass(mContext, CommonSpaceActivity.class);
            it.putExtra(Constants.EXTRA_MEMBER, (android.os.Parcelable) member);
            it.putExtra(Constants.EXTRA_SPACE_MODEL, (android.os.Parcelable) user);
            it.putExtra(Constants.EXTRA_VOIP_ID,y_id);
            mContext.startActivity(it);
        }
    }

    /**
     * 错误按钮点击事件
     */
    private class OnWarnImgClickListener implements View.OnClickListener{
        private IMChatMessageDetail msgDetail;
        private GroupChatActivity activity;
        OnWarnImgClickListener(IMChatMessageDetail msgDetail,GroupChatActivity activity){
            this.msgDetail=msgDetail;
            this.activity=activity;
        }

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(R.string.sure_resend);
            builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    activity.reSendImMessage(msgDetail);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            textView.setTextColor(mContext.getResources().getColor(R.color.light_blue));
        }
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
                        new LoadImage(img).execute();
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
                    tvfile.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ico_fujian, 0, 0, 0);
                    tvfile.setText(visiblefile + ". " + file.getName());
                }
            }
        }
    }



    class LoadImage extends AsyncTask<Object, Void, Bitmap> {

        private ImageView imv;
        private String path;

        public LoadImage(ImageView imv) {
            this.imv = imv;
            this.path = imv.getTag().toString();
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
            int[] desiredImageDimension = VolleyImageUtils.getDesiredImageDimension(path, DensityUtil.dip2px(mContext, 100), DensityUtil.dip2px(mContext, 100));
            return VolleyImageUtils.getScaledBitmap(new File(path), desiredImageDimension[0], desiredImageDimension[1]);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (!imv.getTag().toString().equals(path)) {
               /* The path is not same. This means that this
                  image view is handled by some other async task.
                  We don't do anything and return. */
                return;
            }

            if(result != null && imv != null){
                imv.setVisibility(View.VISIBLE);
                imv.setImageBitmap(result);
            }else{
                imv.setVisibility(View.GONE);
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

    private boolean shouldShowCurrentMessageCreateTime(String lastMessageCreateTime, String currentMessageCreateTime) {
        if (lastMessageCreateTime == null || currentMessageCreateTime == null)
            return false;
        return shouldShowCurrentMessageCreateTime(Long.valueOf(lastMessageCreateTime), Long.valueOf(currentMessageCreateTime));
    }

    private boolean shouldShowCurrentMessageCreateTime(long lastMessageCreateTime, long currentMessageCreateTime) {
        if (Math.abs(currentMessageCreateTime - lastMessageCreateTime) < FIVE_MINUTES) {
            return false;
        }
        return true;
    }

}
