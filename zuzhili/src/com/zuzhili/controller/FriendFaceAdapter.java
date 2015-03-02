package com.zuzhili.controller;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.ui.views.SquareImageView;

/**
 * Created by liutao on 14-4-16.
 */
public class FriendFaceAdapter extends BaseAdapter {

	private Context context;

	private ArrayList<UserInfo> userInfoList;

    private ImageLoader imageLoader;

	public FriendFaceAdapter(Context context, ImageLoader imageLoader) {
		this.context = context;
        this.imageLoader = imageLoader;
        this.userInfoList = new ArrayList<UserInfo>();
    }

    public ArrayList<UserInfo> getUserInfoList() {
        return userInfoList;
    }

    @Override
	public int getCount() {
		return userInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return userInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        UserInfo item = userInfoList.get(position);

		if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.view_contact, null);
            holder = new ViewHolder();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {
            imageLoader.get(item.getU_icon(), ImageLoader.getImageListener(holder.userAvatar, R.drawable.default_user_head_small, R.drawable.default_user_head_small));
        } catch (Exception e) {
            holder.userAvatar.setImageResource(R.drawable.default_user_head_small);
        }



		return convertView;
	}

    public void addOrRemoveItem(UserInfo item) {
        if (userInfoList.contains(item)) {
            userInfoList.remove(item);
        } else {
            userInfoList.add(userInfoList.size(), item);
        }
        notifyDataSetChanged();
    }

    public void addItem(UserInfo item) {
        userInfoList.add(userInfoList.size(), item);
        notifyDataSetChanged();
    }

    public void addItem(int position, UserInfo item) {
        userInfoList.add(0, item);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        userInfoList.remove(position);
        notifyDataSetChanged();
    }

    public void setList(ArrayList<UserInfo> list) {
        this.userInfoList = list;
    }


    class ViewHolder {
        @ViewInject(R.id.img_user_avatar)
        SquareImageView userAvatar;
    }

}
