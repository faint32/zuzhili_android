package com.zuzhili.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.ui.activity.HomeTabActivity;
import com.zuzhili.ui.views.BadgeView;
import com.zuzhili.ui.views.PullRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liutao on 14-3-19.
 */
public class MessageListFrg extends FixedOnActivityResultBugFragment implements AdapterView.OnItemClickListener {

    private PullRefreshListView pullRefreshListView;

    private ProgressBar progressBar;

    //private SimpleAdapter adapter;
    private MyMessageAdapter adapter;

    private String[] categorys;

    private HomeTabActivity activity;

    private List<Integer> messageCount;

    private List<MessageItem> mList;

    private List<BadgeView> views=new ArrayList<BadgeView>();

    private Drawable drawable;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (HomeTabActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.listview_layout, container, false);
        pullRefreshListView = (PullRefreshListView) view.findViewById(R.id.listView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        update();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            reFresh();
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    public void reFresh(){
        messageCount = activity.getMessageCount();
        for(int i=0;i<mList.size();i++){
            mList.get(i).setCount(messageCount.get(i));
        }
        adapter.notifyDataSetChanged();
    }

    private void update() {
        drawable = activity.getResources().getDrawable(R.drawable.more_icon_rightback);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());

        if(adapter==null){
            initData();
            adapter=new MyMessageAdapter();

        }
        pullRefreshListView.setDivider(getResources().getDrawable(R.drawable.divider));
        pullRefreshListView.setOnItemClickListener(this);
        pullRefreshListView.setDividerHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, activity.getResources().getDisplayMetrics()));
        views.clear();
        pullRefreshListView.setAdapter(adapter);
    }

    private void initData() {
        int[] iconResIds = new int[] {R.drawable.msg_at, R.drawable.msg_comment/*, R.drawable.msg_agree*/};
        categorys = getResources().getStringArray(R.array.message);
        messageCount = activity.getMessageCount();
        mList=new ArrayList<MessageItem>();
        for (int i = 0; i < iconResIds.length;i++) {
            MessageItem item=new MessageItem();
            item.setIcon(iconResIds[i]);
            item.setName(categorys[i]);
            item.setCount(messageCount.get(i));
            mList.add(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 1) {
            if(activity.getAtFeedCount()>0){
                mSession.setUIShouldUpdate(Constants.PAGE_AT_CONTENT);
            }else if(activity.getAtCommentCount()>0){
                mSession.setUIShouldUpdate(Constants.PAGE_AT_COMMENT);
            }else if(activity.getAtFeedCount()>0 && activity.getAtCommentCount()>0){
                mSession.setUIShouldUpdate(Constants.PAGE_AT_CONTENT);
                mSession.setUIShouldUpdate(Constants.PAGE_AT_COMMENT);
            }
            adapter.notifyDataSetChanged();

            activity.setCurrentFragmentTag(Constants.TAG_AT_ME);
            activity.updateActionBar();
            activity.attachFragment(activity.getMenuDrawer().getContentContainer().getId(), activity.getFragment(Constants.TAG_AT_ME), Constants.TAG_AT_ME);

        } else if (position == 2) {
            if(activity.getCommentCount()>0){
                mSession.setUIShouldUpdate(Constants.PAGE_COMMENT_RECEIVE);
            }
            adapter.notifyDataSetChanged();
            activity.setCurrentFragmentTag(Constants.TAG_COMMENT);
            activity.updateActionBar();
            activity.attachFragment(activity.getMenuDrawer().getContentContainer().getId(), activity.getFragment(Constants.TAG_COMMENT), Constants.TAG_COMMENT);

        }

    }

    class MyMessageAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mList.size();
        }
        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder ;
            if(convertView==null){
                convertView=LayoutInflater.from(activity).inflate(R.layout.listview_item_message,parent,false);
                holder =new ViewHolder();
                holder.icon = (ImageView) convertView.findViewById(R.id.img_icon);
                holder.name = (TextView) convertView.findViewById(R.id.txt_category_name);
                holder.point = convertView.findViewById(R.id.view_holder);

                BadgeView badgeView = new BadgeView(activity, holder.point);
                badgeView.setBadgePosition(BadgeView.POSITION_CENTER);
                views.add(position,badgeView);
                convertView.setTag(holder);
            }else {
                holder=(ViewHolder)convertView.getTag();
            }

            MessageItem item = mList.get(position);
            holder.icon.setBackgroundResource(item.getIcon());
            holder.name.setText(item.getName());

            BadgeView badgeView = views.get(position);
            if(item.getCount()!=0){
                badgeView.show();
                holder.name.setCompoundDrawables(null,null,null,null);
                badgeView.setText(item.getCount()+"");
            }else {
                badgeView.hide();
                holder.name.setCompoundDrawables(null, null, drawable, null);
            }

            return convertView;
        }
    }

    class ViewHolder{
        ImageView icon;
        TextView name;
        View point;
    }

    class MessageItem{
        private int icon;
        private String name;
        private int count;

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
