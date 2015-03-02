package com.zuzhili.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.bussiness.utility.TimeUtils;
import com.zuzhili.bussiness.utility.UserClickableSpan;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.im.ITask;
import com.zuzhili.framework.im.TaskKey;
import com.zuzhili.model.Member;
import com.zuzhili.model.comment.Comment;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.space.CommonSpaceActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



/**
 * Created by kj on 14-8-27.
 */
public class PhotoCommentAdapter extends BaseAdapter implements Response.Listener<String>, Response.ErrorListener{

    private LayoutInflater mInflater;
    private Context mContext;
    private Session mSession;
    private ImageLoader mImageLoader;
    private List<Comment> mCommentList =new ArrayList<Comment>();
    private int mPosition;
    public PhotoCommentAdapter(Context context,
                               ImageLoader imageLoader,
                               Session session) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mSession = session;
        this.mImageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        if(mCommentList != null){
            return  mCommentList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mCommentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Comment item = mCommentList.get(position);
        ViewHolder holder;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_comment, parent, false);
            holder = new ViewHolder();

            ViewUtils.inject(holder, convertView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(holder.userHeadRequest != null) {
            holder.userHeadRequest.cancelRequest();
        }
        holder.userHeadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Member member = item.getIdentity();

                UserInfo user=((BaseActivity)mContext).getDbHelper().getUserInfoTable().getUserByUid(String.valueOf(member.getUserid()),mSession.getListid());

                intent.setClass(mContext, CommonSpaceActivity.class);
                intent.putExtra(Constants.EXTRA_MEMBER, (android.os.Parcelable) member);
                intent.putExtra(Constants.EXTRA_SPACE_MODEL, (android.os.Parcelable) user);
                intent.putExtra(Constants.EXTRA_VOIP_ID, user.getY_voip());
                mContext.startActivity(intent);
            }
        });
        holder.userHeadRequest = mImageLoader.get(item.getHeadimage()
                , ImageLoader.getImageListener(holder.userHeadImg, R.drawable.icon_head, R.drawable.icon_head));

        holder.contentTxt.setText(TextUtil.contentFilterSpan(item.getContent(), mContext, new UserClickableSpan(mContext, null), null, null));

        holder.userNameTxt.setText(item.getName());

        holder.timeTxt.setText(TimeUtils.getTimeMinute(item.getCreateTime()));

        if(String.valueOf(item.getIds()) .equals(mSession.getIds())){
            holder.deleteComment.setVisibility(View.VISIBLE);
        }else {
            holder.deleteComment.setVisibility(View.GONE);
        }

        holder.deleteComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("删除这条评论?");
                builder.setPositiveButton("删除评论", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Task.deleteComment(buildRequestParams(item.getId()),PhotoCommentAdapter.this,PhotoCommentAdapter.this);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return convertView;
    }

    public void setList(List<Comment> list){
        mCommentList.clear();
        mCommentList.addAll(list);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }

    @Override
    public void onResponse(String s) {
        if(!TextUtils.isEmpty(s)){
            mCommentList.remove(mPosition);
            notifyDataSetChanged();
        }
    }

    protected class ViewHolder {
        @ViewInject(R.id.img_user_head)
        ImageView userHeadImg;


        public ImageContainer userHeadRequest;

        @ViewInject(R.id.rla_content)
        TextView contentTxt;

        @ViewInject(R.id.txt_user_name)
        TextView userNameTxt;

        @ViewInject(R.id.txt_time)
        TextView timeTxt;

        @ViewInject(R.id.comment_delete)
        ImageView deleteComment;

    }

    private HashMap<String, String> buildRequestParams(int id) {
        final HashMap<String, String> params = new HashMap<String, String>();
        if (mSession != null) {
            params.put("commentid", String.valueOf(id));
            params.put("ids", mSession.getIds());
        }
        return params;
    }
}
