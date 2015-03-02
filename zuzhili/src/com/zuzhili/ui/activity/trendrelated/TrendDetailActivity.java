package com.zuzhili.ui.activity.trendrelated;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.controller.CommentAdapter;
import com.zuzhili.controller.TrendViewHelper;
import com.zuzhili.controller.TrendViewHolder;
import com.zuzhili.db.DBHelper;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.MiniBlog;
import com.zuzhili.model.comment.Comment;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.HomeTabActivity;
import com.zuzhili.ui.activity.article.ArticleDetailActivity;
import com.zuzhili.ui.activity.comment.CommentEditActivity;
import com.zuzhili.ui.activity.space.CommonSpaceActivity;
import com.zuzhili.ui.views.PullRefreshListView;

import java.util.HashMap;

import static com.zuzhili.controller.TrendAdapter.VIEW_TYPE_HEADVIEW_QUOTED_TREND_DETAIL_WITH_MUTIL_MEDIA;
import static com.zuzhili.controller.TrendAdapter.VIEW_TYPE_HEADVIEW_QUOTED_TREND_DETAIL_WITH_TEXT_ONLY;
import static com.zuzhili.controller.TrendAdapter.VIEW_TYPE_HEADVIEW_TREND_DETAIL_TEXT_ONLY;
import static com.zuzhili.controller.TrendAdapter.VIEW_TYPE_HEADVIEW_TREND_DETAIL_WITH_MULTI_MEDIA;

/**
 * Created by liutao on 14-2-25
 */
public class TrendDetailActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback,
        BaseActivity.HandleProgressBarVisibilityCallback ,
        Response.Listener<String> , Response.ErrorListener {

    @ViewInject(R.id.listView)
    private PullRefreshListView listView;

    @ViewInject(R.id.rla_action_repost)
    private RelativeLayout repostRla;

    @ViewInject(R.id.rla_action_comment)
    private RelativeLayout commentRla;

    @ViewInject(R.id.rla_action_collect)
    private RelativeLayout collectRla;

    @ViewInject(R.id.progressbar)
    private ProgressBar progressBar;

    private MiniBlog miniBlog;

    private ImageLoader imageLoader = ImageCacheManager.getInstance().getImageLoader();

    private TrendViewHelper trendViewHelper;

    private TrendViewHolder holder;

    private static final int FLAG_ACTIVITY_ADD_COMMENT = 0;
    private static final int FLAG_ACTIVITY_REPOST = 1;

    private CommentAdapter mAdapter;

    private RepostAndCommentBarHolder repostAndCommentBarHolder;
    private TextView collectV;
    private int replynum;
    private int fowardnum;


    @OnClick(R.id.rla_action_comment)
    public void addComment(View view){
        Intent intent=new Intent(this,CommentEditActivity.class);
        intent.putExtra(Constants.ACTION, Constants.ACTION_COMMENT);
        intent.putExtra(Constants.EXTRA_TREND_ABSID, String.valueOf(miniBlog.getId()));
        intent.putExtra(Constants.EXTRA_FROM_WHICH_PAGE, Constants.EXTRA_FROM_TREND_DETAIL);
        super.startActivityForResult(intent,FLAG_ACTIVITY_ADD_COMMENT);
    }

    @OnClick(R.id.rla_action_repost)
    public void repost(View view){
        Intent intent=new Intent(this,CommentEditActivity.class);
        intent.putExtra(Constants.ACTION, Constants.ACTION_REPOST);
        intent.putExtra(Constants.EXTRA_TREND_PRIABSID, String.valueOf(miniBlog.getId()));
        if (miniBlog.getChildAbs() != null) {
            intent.putExtra(Constants.EXTRA_TREND_ABSID, String.valueOf(miniBlog.getChildAbs().getId()));
            intent.putExtra(Constants.EXTRA_TREND_SOURCETEXT, TextUtil.composeReforwdReforwdStr(miniBlog.getUserName(), miniBlog.getIds(), miniBlog.getTitle()));
        } else {
            intent.putExtra(Constants.EXTRA_TREND_ABSID, String.valueOf(miniBlog.getId()));
        }

        intent.putExtra(Constants.EXTRA_FROM_WHICH_PAGE, Constants.EXTRA_FROM_TREND_DETAIL);
        if("1".equals(miniBlog.getMessagetype())){
            intent.putExtra(Constants.EXTRA_REPOST_TEXT, trendViewHelper.getTrendTitle(holder));
        }
        super.startActivityForResult(intent,FLAG_ACTIVITY_REPOST);
    }

    @OnClick(R.id.rla_action_collect)
    public void collect(View view) {
        TextView v = (TextView) view.findViewById(R.id.collectTxt);
        if (v.getText() != null && v.getText().equals("取消")) {
            Task.cancelCollection(buildRequestCancelCollectionParams(miniBlog), this, this);
        } else {
            Task.addCollection(buildRequestAddCollectionParams(miniBlog), this, this);
        }
    }
  
    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_news_detail);
        ViewUtils.inject(this);
        setCustomActionBarCallback(this);
        String from = getIntent().getStringExtra(Constants.EXTRA_FROM_WHICH_PAGE);
        holder = new TrendViewHolder();
        trendViewHelper = new TrendViewHelper(this, imageLoader);
        if (from != null && from.equals(Constants.EXTRA_FROM_AT_ME_COMMENT)) {
            String absId = getIntent().getStringExtra(Constants.EXTRA_TREND_ABSID);
            Task.getSpecificTrend(buildRequestSpecificTrendParams(absId), this, this);
        } else {
            miniBlog = (MiniBlog) getIntent().getSerializableExtra(Constants.EXTRA_TREND_ITEM);
            if (miniBlog != null) {
                initView();
            }
        }

    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, R.drawable.icon_home, getString(R.string.title_trend_detail), false);
        return true;
    }

    @Override
    public boolean performClickOnLeft() {
        finish();
        return super.performClickOnLeft();
    }

    @Override
    public boolean performClickOnRight() {
        Intent it = new Intent(this, HomeTabActivity.class);
        it.putExtra(Constants.EXTRA_ANIM_REVERSE, true);
        startActivity(it);
        return super.performClickOnRight();
    }

    private void initView() {
        View trendHeadView = trendViewHelper.populateFitItemView(trendViewHelper.getViewType(miniBlog, true), null);
        ViewUtils.inject(holder, trendHeadView);
        listView.addHeaderView(trendHeadView, null, false);
        if(null!=holder.trendContentTxt){
            holder.trendContentTxt.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View arg0) {
                    Task.canArticleDetail(buildRequestArticleDetailParams(), TrendDetailActivity.this, TrendDetailActivity.this);
                }
            });
        }
        if(null!=holder.quotedTrendContentTxt) {
            holder.quotedTrendContentTxt.setOnClickListener(new OnClickListener(){
    			@Override
    			public void onClick(View arg0) {
                    Task.canArticleDetail(buildRequestArticleDetailParams(), TrendDetailActivity.this, TrendDetailActivity.this);
    			}
            });
        }
        
        repostAndCommentBarHolder = new RepostAndCommentBarHolder();
        View repostAndCommentBar = mLayoutInflater.inflate(R.layout.listview_item_headview_repost_and_comment_bar, null);
        ViewUtils.inject(repostAndCommentBarHolder, repostAndCommentBar);
        listView.addHeaderView(repostAndCommentBar, null, false);

        replynum=miniBlog.getReplynum();
        if(replynum > 0) {
            repostAndCommentBarHolder.commentNumTxtV.setVisibility(View.VISIBLE);
            repostAndCommentBarHolder.commentNumTxtV.setText(getString(R.string.action_comment) + replynum);
        }
        fowardnum=miniBlog.getFowardnum();
        if (fowardnum > 0) {
            repostAndCommentBarHolder.repostNumTxtV.setVisibility(View.VISIBLE);
            repostAndCommentBarHolder.repostNumTxtV.setText(getString(R.string.action_repost) + fowardnum);
        }
        collectV = (TextView) collectRla.findViewById(R.id.collectTxt);

        mAdapter=new CommentAdapter(this, listView, imageLoader, mSession, buildRequestParams(), this, false);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Comment comment=(Comment)parent.getAdapter().getItem(position);
                Intent intent = new Intent(TrendDetailActivity.this,CommentEditActivity.class);
                intent.putExtra(Constants.ACTION, Constants.ACTION_COMMENT);
                intent.putExtra(Constants.EXTRA_COMMENT_INFO, "回复  @"+comment.getName()+"("+comment.getIds()+")"+" :");
                intent.putExtra(Constants.EXTRA_TREND_TOCOMMENTID,comment.getId()+"" );
                intent.putExtra(Constants.EXTRA_TREND_ABSID, miniBlog.getId()+"");
                intent.putExtra(Constants.EXTRA_FROM_WHICH_PAGE, Constants.EXTRA_FROM_TREND_DETAIL);
                startActivityForResult(intent,FLAG_ACTIVITY_ADD_COMMENT);

            }
        });

        holder.rlaHeaderContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(TrendDetailActivity.this, CommonSpaceActivity.class);
                UserInfo user = DBHelper.getInstance(TrendDetailActivity.this).getUserInfoTable().getUserByIds(miniBlog.getIds(), miniBlog.getListid());
                intent.putExtra(Constants.EXTRA_SPACE_MODEL, (android.os.Parcelable) user);
                startActivity(intent);
            }
        });

        holder.userHeadRequest = imageLoader.get(miniBlog.getUserhead()
                , ImageLoader.getImageListener(holder.userHeadImg, R.drawable.icon_head, R.drawable.icon_head));
        holder.publishTimeFooterTxt.setText(miniBlog.getCreatetime());

        holder.userNameTxt.setText(miniBlog.getUserName());
        trendViewHelper.showPublishByDeviceType(holder, miniBlog.getComefrom());

        switch (trendViewHelper.getViewType(miniBlog, true)) {
        case VIEW_TYPE_HEADVIEW_TREND_DETAIL_TEXT_ONLY:
            trendViewHelper.showTrendTitle(holder, miniBlog.getTitle(), false);
            trendViewHelper.showTrendContent(holder, miniBlog.getContent(), miniBlog.getTitle(), false);
            break;
        case VIEW_TYPE_HEADVIEW_TREND_DETAIL_WITH_MULTI_MEDIA:
            trendViewHelper.showTrendContent(holder, miniBlog.getContent(), miniBlog.getTitle(), false);
            trendViewHelper.showConfigs(holder, miniBlog.getConfiglist(), false);
            break;
        case VIEW_TYPE_HEADVIEW_QUOTED_TREND_DETAIL_WITH_TEXT_ONLY:
            trendViewHelper.showTrendTitle(holder, miniBlog.getTitle(), false);
            trendViewHelper.showQuotedTrendTitle(miniBlog.getChildAbs(), holder, miniBlog.getChildAbs().getTitle());
            trendViewHelper.showQuotedTrendContent(miniBlog.getChildAbs(), holder, miniBlog.getChildAbs().getContent());
            break;
        case VIEW_TYPE_HEADVIEW_QUOTED_TREND_DETAIL_WITH_MUTIL_MEDIA:
            trendViewHelper.showTrendTitle(holder, miniBlog.getTitle(), false);
            trendViewHelper.showQuotedTrendContent(miniBlog.getChildAbs(), holder, miniBlog.getChildAbs().getContent());
            trendViewHelper.showConfigs(holder, miniBlog.getChildAbs().getConfiglist(), true);
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if(requestCode == FLAG_ACTIVITY_REPOST){
                repostAndCommentBarHolder.repostNumTxtV.setVisibility(View.VISIBLE);
                repostAndCommentBarHolder.repostNumTxtV.setText(getString(R.string.action_repost) + ++fowardnum);
                progressBar.setVisibility(View.VISIBLE);
                mAdapter.onRefresh();
            }else if (requestCode == FLAG_ACTIVITY_ADD_COMMENT) {
                repostAndCommentBarHolder.commentNumTxtV.setVisibility(View.VISIBLE);
                repostAndCommentBarHolder.commentNumTxtV.setText(getString(R.string.action_comment) + ++replynum);
                progressBar.setVisibility(View.VISIBLE);
                mAdapter.onRefresh();
            }
        }
    }

    private HashMap<String, String> buildRequestParams() {
        final HashMap<String, String> params = new HashMap<String, String>();
        if(mSession != null) {
            params.put("curnetid", mSession.getListid());
            params.put("absid", String.valueOf(miniBlog.getId()));
            params.put("istimedesc", "1");
            params.put("start", "0");
            params.put("length", String.valueOf(Constants.PAGE_SIZE));
        }
        return params;
    }

    private HashMap<String, String> buildRequestSpecificTrendParams(String absId) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("absid", absId);
        return params;
    }

    private HashMap<String, String> buildRequestArticleDetailParams() {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("absid", String.valueOf(miniBlog.getId()));
        params.put("ids", mSession.getIds());
        return params;
    }

    private HashMap<String, String> buildRequestAddCollectionParams(MiniBlog item) {
        final HashMap<String, String> params = new HashMap<String, String>();
        if(mSession != null) {
            params.put("curnetid", mSession.getListid());
            params.put("ids", mSession.getIds());
            params.put("absid", String.valueOf(item.getId()));
        }
        return params;
    }

    private HashMap<String, String> buildRequestCancelCollectionParams(MiniBlog item) {
        final HashMap<String, String> params = new HashMap<String, String>();
        if(mSession != null) {
            params.put("curnetid", mSession.getListid());
            params.put("ids", mSession.getIds());
            params.put("absid", String.valueOf(item.getId()));
            params.put("listid", mSession.getListid());
        }
        return params;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Utils.makeEventToast(this, getString(R.string.trend_not_exist), false);
        progressBar.setVisibility(View.GONE);
        finish();
    }

    @Override
    public void onResponse(String s) {

        JSONObject jsonObject = JSONObject.parseObject(s);
        if(jsonObject.getString("info") != null){
            String info=jsonObject.get("info").toString();
            if (!TextUtils.isEmpty(info)) {
                if (collectV != null && collectV.getText().equals("收藏")) {
                    int flag=jsonObject.getInteger("opflag");
                    if(flag==-1){
                        info="动态已收藏";
                    }
                    collectV.setText("取消");
                } else {
                    collectV.setText("收藏");
                }
                Utils.makeEventToast(this, info, false);
            }
        }else if(jsonObject.getString("search") != null){
            String auth=jsonObject.get("search").toString();
            //有权限
            if("1".equals(auth)){
                Intent intent=new Intent(TrendDetailActivity.this,ArticleDetailActivity.class);
                String apptype=miniBlog.getApptype();
                if(!apptype.equals("3")){
                    return;
                }
                if(null!=holder.trendContentTxt){
                    intent.putExtra("apptypeid", String.valueOf(miniBlog.getApptypeid()));
                }else if(null!=holder.quotedTrendContentTxt){
                    intent.putExtra("apptypeid", String.valueOf(miniBlog.getChildAbs().getApptypeid()));
                }
                startActivity(intent);
            }else{
                Toast.makeText(this,"您没有查看权限",Toast.LENGTH_SHORT).show();
            }
        }else{
            MiniBlog miniBlog;
            try {
                miniBlog = JSON.parseObject(jsonObject.getString("abs"), MiniBlog.class);
            } catch (JSONException e) {
                miniBlog = null;
            }
            if (miniBlog != null) {
                this.miniBlog = miniBlog;
                initView();
            } else {
                Utils.makeEventToast(this, getString(R.string.trend_not_exist), false);
                progressBar.setVisibility(View.GONE);
                finish();
            }
        }
    }

    @Override
    public void setProgressBarVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }


    class RepostAndCommentBarHolder {
        @ViewInject(R.id.txt_repost_bar)
        public TextView repostNumTxtV;

        @ViewInject(R.id.txt_comment_bar)
        public TextView commentNumTxtV;

    }
}
