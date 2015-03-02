package com.zuzhili.controller;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.IMParseUtil;
import com.zuzhili.db.DBHelper;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.im.IMConversation;
import com.zuzhili.model.social.JoinedInSocial;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.views.BadgeView;

public class JoinedInSocialAdapter extends BaseAdapter {
    //基础Activity
    private BaseActivity context;
    private List<JoinedInSocial> sociallist;
    //Layout
    public LayoutInflater mfactory;
    //获取上下文
    private final ImageLoader mImageLoader;

    private DBHelper helper;

    class ViewHolder {
        View viewimg;
        TextView listitemnametxt;
        TextView listitemdomaintxt;
        NetworkImageView logoimg;
        Button socialunread;
        BadgeView badgeView;
    }

    public JoinedInSocialAdapter(BaseActivity context, List<JoinedInSocial> list, ImageLoader mImageLoader) {
        this.context = context;
        sociallist = list;
        this.mImageLoader = mImageLoader;
        helper = DBHelper.getInstance(context);
    }

    public void setList(List<JoinedInSocial> list) {
        if (list != null) {
            sociallist = list;
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_social, parent, false);
            holder = new ViewHolder();
            holder.listitemnametxt = (TextView) convertView.findViewById(R.id.txt_social_listitem_name);
            holder.listitemdomaintxt = (TextView) convertView.findViewById(R.id.txt_social_listitem_domain);
            holder.logoimg = (NetworkImageView) convertView.findViewById(R.id.img_social_logo);
            holder.socialunread = (Button) convertView.findViewById(R.id.btn_social_unreadcounthint);
            holder.badgeView = new BadgeView(context, holder.socialunread);
            holder.badgeView.setBadgePosition(BadgeView.POSITION_CENTER);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        JoinedInSocial social = sociallist.get(position);

        //设置社区的头像
        if (null != social.getLoginlogo()) {
            holder.logoimg.setImageUrl(social.getLoginlogo(), mImageLoader);
            holder.logoimg.setDefaultImageResId(R.drawable.default_social_logo);
            holder.logoimg.setErrorImageResId(R.drawable.default_social_logo);
        }
        holder.listitemnametxt.setText(social.getListname());
        holder.listitemdomaintxt.setText(social.getListdesc());
        holder.socialunread.setVisibility(View.GONE);

        int unreadNum = getUnreadCount(social);
        if (unreadNum > 0) {
            holder.badgeView.setText(String.valueOf(unreadNum));
            holder.badgeView.show();
        } else {
            holder.badgeView.hide();
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return sociallist.size();
    }

    @Override
    public Object getItem(int arg0) {
        return sociallist.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return sociallist.get(arg0).getId();
    }

    // 获取未读消息数量
    private int getUnreadCount(JoinedInSocial s) {

        List<IMConversation> imConversations = null;
        try {
            helper.getTable().setDbUtils(helper.getDbUtils());
            imConversations = helper.getTable().queryIMConversation(buildIdentity(s));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (imConversations != null) {
            int unreadMsg = 0;
            for (IMConversation conversation : imConversations) {
                unreadMsg += Integer.valueOf(conversation.getUnReadNum());
            }
            return s.getUnreadnewatcomment() + s.getUnreadnewatfeed()
                    + s.getUnreadnewcomment()
                    + unreadMsg;
        }
        return s.getUnreadnewatcomment() + s.getUnreadnewatfeed()
                + s.getUnreadnewcomment() + s.getUnreadnewmsgcount();
    }

    private String buildIdentity(JoinedInSocial s) {

        StringBuilder builder = new StringBuilder();
        builder.append(s.getIdentity().getListid())
                .append(Constants.SYMBOL_PERIOD)
                .append(s.getIdentity().getId());
        return  builder.toString();

    }

}
