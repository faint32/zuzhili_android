package com.zuzhili.controller;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zuzhili.R;
import com.zuzhili.bussiness.utility.ValidationUtils;
import com.zuzhili.bussiness.utility.ViewHolder;
import com.zuzhili.model.multipart.MultiUpload;

import java.util.List;

/**
 * Created by addison on 2/21/14.
 */
public class MusicSelectedAdapter extends BaseAdapter{
    private List<MultiUpload> list;
    private Context context;

    public MusicSelectedAdapter(List<MultiUpload> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.gridview_item_music_publish, null);
        }
        TextView nameTxt = ViewHolder.get(convertView, R.id.name_txt);
        ImageView musicCoverImg = ViewHolder.get(convertView, R.id.music_publish_img);
        if(position == list.size()){
            musicCoverImg.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.plus));
            nameTxt.setVisibility(View.GONE);
            if(list.size() == 5){
                musicCoverImg.setVisibility(View.GONE);
                nameTxt.setVisibility(View.GONE);
            }
        } else {
            if(ValidationUtils.validationString(list.get(position).getNewfilename())){
                nameTxt.setText(list.get(position).getNewfilename());
                nameTxt.setVisibility(View.VISIBLE);
                musicCoverImg.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.music_default));
            }
        }
        return convertView;
    }
}
