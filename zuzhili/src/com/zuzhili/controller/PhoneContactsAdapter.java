package com.zuzhili.controller;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.utility.pinyin.CharacterParser;
import com.zuzhili.bussiness.utility.pinyin.PinyinComparator;
import com.zuzhili.model.ContactRec;

import java.util.Collections;
import java.util.List;

/**
 * Created by liutao on 14-5-16.
 */
public class PhoneContactsAdapter extends NonPagingResultsAdapter<ContactRec> implements SectionIndexer, AdapterView.OnItemClickListener {

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;

    private SparseBooleanArray checkBoxStatus = new SparseBooleanArray();

    private PinyinComparator pinyinComparator;

    private OnContactSelectedListener onContactSelectedListener;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (onContactSelectedListener != null) {
            onContactSelectedListener.onContactSelected((ContactRec) parent.getAdapter().getItem(position));
        }
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.select);
        if (checkBox != null) {
            if (checkBox.isChecked()) {
                checkBox.setChecked(false);
            } else {
                checkBox.setChecked(true);
            }
        }
        checkBoxStatus.put(position - mListView.getHeaderViewsCount(), checkBox.isChecked());
    }

    public interface OnContactSelectedListener {
        public void onContactSelected(ContactRec contactRec);
    }

    public void setOnContactSelectedListener(OnContactSelectedListener onContactSelectedListener) {
        this.onContactSelectedListener = onContactSelectedListener;
    }

    public PhoneContactsAdapter(Context context,
                            ListView listView,
                            ImageLoader imageLoader) {
        super(context, listView, imageLoader);
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        ContactRec item = getItem(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_contact, parent, false);
            holder = new ViewHolder();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(item.getName());
        holder.phone.setText(item.getPhone());

        holder.selectContactCheckbox.setChecked(checkBoxStatus.get(position));
        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        // 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position == getPositionForSection(section)) {
            holder.sortKey.setVisibility(View.VISIBLE);
            holder.sortKey.setText(item.getSortKey());
        }else{
            holder.sortKey.setVisibility(View.GONE);
        }

        return convertView;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return mDataList.get(position).getSortKey().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mDataList.get(i).getSortKey();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String  sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    public void sortList(List dataList) {
        // 根据a-z进行排序源数据
        Collections.sort(dataList, pinyinComparator);
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public void setList(List<ContactRec> data) {
        for (int i = 0; i < data.size(); i++) {
            checkBoxStatus.put(i, false);
        }
        super.setList(data);
    }

    class ViewHolder {

        @ViewInject(R.id.sortKey)
        TextView sortKey;

        @ViewInject(R.id.name)
        TextView name;

        @ViewInject(R.id.phone)
        TextView phone;

        @ViewInject(R.id.select)
        CheckBox selectContactCheckbox;
    }
}
