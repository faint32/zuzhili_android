package com.zuzhili.controller;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.zuzhili.R;
import com.zuzhili.model.multipart.MusicLocal;

/**
 * @Title: MusicListAdapter.java
 * @Package com.zuzhili.mediaselect.adapter
 * @Description: 音乐列表适配器
 * @author gengxin
 * @date 2013-4-23 下午13:35:17
 */
public class MusicListAdapter extends BaseAdapter {

	private Context context;
	private List<MusicLocal> musicLocalList;
	private static HashMap<Integer, Boolean> isSelected;
	private Cursor cursor;
	public MusicListAdapter(Context context, List<MusicLocal> musicLocalList, Cursor cursor) {
		this.context = context;
		this.musicLocalList = musicLocalList;
		this.cursor = cursor;
		isSelected = new HashMap<Integer, Boolean>();
		initDate();
	}

	private void initDate() {
		for (int i = 0; i < musicLocalList.size(); i++) {
			getIsSelected().put(i, false);
		}
	}

	@Override
	public int getCount() {
		return musicLocalList.size();
	}

	@Override
	public Object getItem(int position) {
		return musicLocalList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MusicLocal musicLocal = musicLocalList.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.listview_item_local_music, null);
			holder = new ViewHolder();
			holder.musicNameTV = (TextView) convertView.findViewById(R.id.music_name_tv);
			holder.musicSizeTV = (TextView) convertView.findViewById(R.id.size_tv);
			holder.selectCBX = (CheckBox) convertView.findViewById(R.id.select_cbx);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.musicNameTV.setText(musicLocal.getName());
		holder.musicSizeTV.setText(musicLocal.getSize() + " MB");
		holder.selectCBX.setChecked(getIsSelected().get(position));
		return convertView;
	}
	
	public static class ViewHolder {
		public  CheckBox selectCBX;
		public  TextView musicNameTV;
		public  TextView musicSizeTV;
	}
	
	public static HashMap<Integer,Boolean> getIsSelected() {
        return isSelected;
    }
    public static void setIsSelected(HashMap<Integer,Boolean> isSelected) {
    	MusicListAdapter.isSelected = isSelected;
    }
}
