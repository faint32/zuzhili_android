package com.zuzhili.ui.activity.space;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.controller.PhotoCommentAdapter;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.model.comment.Comment;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.comment.CommentEditActivity;
import com.zuzhili.ui.views.PullRefreshListView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by kj on 2014/8/27.  图片评论列表
 */
public class PhotoCommentActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback , Response.Listener<String>, Response.ErrorListener{

    @ViewInject(R.id.listView)
    private PullRefreshListView listView;

    @ViewInject(R.id.progressbar)
    private ProgressBar progressBar;

    private ImageLoader imageLoader = ImageCacheManager.getInstance().getImageLoader();
    private String picId;
    private PhotoCommentAdapter adapter;
    private static final int FLAG_ACTIVITY_RESULT_WRITE = 0;
    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_image_comment);
        ViewUtils.inject(this);
        setCustomActionBarCallback(this);
        progressBar.setVisibility(View.VISIBLE);
        picId = getIntent().getStringExtra(Constants.PHOTO_ID);
        Task.getPhotoComment(buildRequestParams(), this, this);

        adapter = new PhotoCommentAdapter(this,imageLoader,mSession);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Comment comment=(Comment)parent.getAdapter().getItem(position);
                Intent intent = new Intent(PhotoCommentActivity.this,CommentEditActivity.class);
                intent.putExtra(Constants.ACTION, Constants.ACTION_PIC_COMMENT);
                intent.putExtra(Constants.EXTRA_PICCOMMENT_INFO, "回复  @"+comment.getName()+"("+comment.getIds()+")"+" :");
                intent.putExtra(Constants.EXTRA_TREND_TOCOMMENTID,comment.getId()+"" );
                intent.putExtra(Constants.EXTRA_TREND_ABSID, picId);
                intent.putExtra(Constants.EXTRA_FROM_WHICH_PAGE, Constants.EXTRA_FROM_TREND_DETAIL);
                startActivityForResult(intent, FLAG_ACTIVITY_RESULT_WRITE);
            }
        });
    }

    private HashMap<String, String> buildRequestParams() {
        final HashMap<String, String> params = new HashMap<String, String>();
        if (mSession != null) {
            params.put("length", "100");
            params.put("listid", mSession.getListid());
            params.put("picid", picId);
            params.put("start", "0");
            params.put("ids", mSession.getIds());
        }
        return params;
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, R.drawable.actionbar_comment, getString(R.string.picture_comment), false);
        return true;
    }

    @Override
    public boolean performClickOnLeft() {
        finish();
        return super.performClickOnLeft();
    }


    @Override
    public boolean performClickOnRight() {
        Intent intent = new Intent(this,CommentEditActivity.class);
        intent.putExtra(Constants.ACTION, Constants.ACTION_PIC_COMMENT);
        intent.putExtra(Constants.EXTRA_TREND_ABSID, picId);
        intent.putExtra(Constants.EXTRA_FROM_WHICH_PAGE, Constants.EXTRA_FROM_TREND_DETAIL);
        startActivityForResult(intent, FLAG_ACTIVITY_RESULT_WRITE);
        return super.performClickOnRight();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == FLAG_ACTIVITY_RESULT_WRITE) {
                progressBar.setVisibility(View.VISIBLE);
                Task.getPhotoComment(buildRequestParams(), this, this);
            }
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResponse(String s) {
        progressBar.setVisibility(View.GONE);
        JSONObject jsonObject = JSONObject.parseObject(s);
        if (jsonObject.getString("list") != null){
            List<Comment> commentList = JSON.parseArray(jsonObject.getString("list"), Comment.class);
            adapter.setList(commentList);
            adapter.notifyDataSetChanged();
        }
    }
}
