package com.zuzhili.controller;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.bussiness.utility.pinyin.CharacterParser;
import com.zuzhili.bussiness.utility.pinyin.PinyinComparatorM;
import com.zuzhili.db.CacheType;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.Member;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.space.SpaceActivity;
import com.zuzhili.ui.fragment.member.BaseMemberFrg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liutao on 14-3-4.
 */
public class MemberAdapter extends ResultsAdapter<Member> implements SectionIndexer,
        ResultsAdapter.SortDataListCallback, AdapterView.OnItemClickListener {

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparatorM pinyinComparator;

    private List<Member> sourceList;

    private BaseActivity.HandleProgressBarVisibilityCallback handleProgressBarVisibilityCallback;

    private BaseMemberFrg.OnMemberSelectedListener onMemberSelectedListener;

    private Session mSession;

    public MemberAdapter(Context context, ListView listView,
                         ImageLoader imageLoader,
                         HashMap params, Session session, String cacheType,
                         BaseActivity.HandleProgressBarVisibilityCallback handleProgressBarVisibilityCallback,
                         boolean isDataLoaded) {
        super(context, listView, imageLoader, params, cacheType);
        this.mSession=session;
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparatorM();
        sourceList = new ArrayList<Member>();
        setSortDataListCallback(this);
        this.handleProgressBarVisibilityCallback = handleProgressBarVisibilityCallback;
        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.VISIBLE);
        if(!isDataLoaded) Task.getCache(context, params, this, this, cacheType, getIdentity(session));
        loadNextPage();
    }

    public void setOnMemberSelectedListener(BaseMemberFrg.OnMemberSelectedListener listener) {
        this.onMemberSelectedListener = listener;
    }

    public void setOnItemClickListener() {
        mListView.setOnItemClickListener(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        handleProgressBarVisibilityCallback.setProgressBarVisibility(View.GONE);
        ViewHolder holder;
        Member item = getItem(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item_member, parent, false);
            holder = new ViewHolder();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(shouldLoadNextPage(mDataList, position)) {
            loadedPage++;
            updateRequestParams(mParams);
            loadNextPage();
            mListView.onFooterRefreshBegin();
            LogUtils.e("load page: " + loadedPage);
        }

        if(holder.userHeadRequest != null) {
            holder.userHeadRequest.cancelRequest();
        }

        try {
            holder.userHeadRequest = mImageLoader.get(TextUtil.processNullString(item.getUserhead())
                    , ImageLoader.getImageListener(holder.userHead, R.drawable.default_user_head_small, R.drawable.default_user_head_small));
        } catch (Exception e) {
            holder.userHead.setImageResource(R.drawable.default_user_head_small);
        }

        holder.name.setText(TextUtil.processNullString(item.getName()));

        if(Utils.isValidString(item.getPhone())) {
            holder.call.setVisibility(View.VISIBLE);
        } else {
            holder.call.setVisibility(View.GONE);
        }

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if(position == getPositionForSection(section)){
            holder.sortKey.setVisibility(View.VISIBLE);
            holder.sortKey.setText(item.getSortKey());
        }else{
            holder.sortKey.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public List parseList(String response) {
        try {
            JSONObject jsonObject = JSON.parseObject(response);
            List<Member> memberList = JSON.parseArray(jsonObject.getString("list"), Member.class);
            updateSortKey(memberList);

            Collections.sort(memberList, new PinyinComparatorM());
            return memberList;
        } catch (JSONException e) {
            return new ArrayList<Member>();
        }
    }

    @Override
    public String getIdentity(Session session) {
        StringBuilder builder = new StringBuilder();
        return builder.append(session.getListid())
                .append(Constants.SYMBOL_PERIOD)
                .append(session.getIds()).toString();
    }

    @Override
    public void loadNextPage() {
        isLoading = true;
        Task.getMembers(mParams, this, this, getMemberType());
    }

    /**
     * 添加sortKey
     * @param dataList
     * @return
     */
    private void updateSortKey(List<Member> dataList){

        for (int i = 0; i < dataList.size(); i++) {
            Member member = dataList.get(i);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(TextUtil.processNullString(member.getName()));
            if(TextUtils.isEmpty(pinyin)){
                continue;
            }
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                member.setSortKey(sortString.toUpperCase());
            } else {
                member.setSortKey("#");
            }
        }
        return;
    }

    /**
     * 跳转到拨号界面
     * @param number
     */
    private void dial(String number){
        Uri uri = Uri.parse("tel:" + number);
        Intent it = new Intent(Intent.ACTION_DIAL, uri);
        mContext.startActivity(it);
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        if (!TextUtils.isEmpty(mDataList.get(position).getSortKey()))
            return mDataList.get(position).getSortKey().charAt(0);
        return 35;
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mDataList.get(i).getSortKey();
            //TODO
            if(TextUtils.isEmpty(sortStr)){
               break;
            }
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

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public void onRefresh() {
        isPullOnRefreshEnd = false;
        if (mParams != null) {
            mParams.put("start", FIRST_PAGE);
            Task.getMembers(mParams, this, this, getMemberType());
        }
    }

    private String getMemberType() {
        if (cacheType.equals(CacheType.CACHE_GET_ALL_MEMBERS)) {
            return Constants.TYPE_ALL_MEMBERS;
        } else if (cacheType.equals(CacheType.CACHE_GET_FOCUS_MEMBERS)) {
            return Constants.TYPE_FOCUS_MEMBERS;
        } else {
            return Constants.TYPE_RECENT_CONTACT_MEMBERS;
        }
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    public void filterData(String filterStr) {
        List<Member> filterDataList = new ArrayList<Member>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDataList = sourceList;
            isLoading = false;
        } else {
            filterDataList.clear();
            for (Member member : sourceList) {
                String name = member.getName();
                if (name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())) {
                    filterDataList.add(member);
                }
            }
            isLoading = true;
        }

        // 根据a-z进行排序
        sortList(filterDataList);
        updateDataList(filterDataList);
        notifyDataSetChanged();
    }

    @Override
    public void sortList(List dataList) {
        // 根据a-z进行排序源数据
        Collections.sort(dataList, pinyinComparator);
    }

    @Override
    public void updateSortList(List dataList) {
        sourceList.clear();
        for (int i = 0; i < dataList.size(); i++) {
            sourceList.add((Member) dataList.get(i));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Member member = (Member) parent.getItemAtPosition(position);
        if (onMemberSelectedListener != null) {
            onMemberSelectedListener.onMemberSelected(member);
        } else {
            // 进行跳转
            Intent it = new Intent(mContext,SpaceActivity.class);
            it.putExtra(Constants.EXTRA_MEMBER, (java.io.Serializable) member);
            if (mSession.isSpaceActivityInstantiated()) {
                it.putExtra(Constants.EXTRA_ANIM_REVERSE, true);
            }
            mContext.startActivity(it);
        }
    }

    class ViewHolder {

        @ViewInject(R.id.txt_sort_key)
        TextView sortKey;

        @ViewInject(R.id.txt_name)
        TextView name;

        @ViewInject(R.id.txt_department)
        TextView department;

        @ViewInject(R.id.img_user_head)
        ImageView userHead;

        ImageLoader.ImageContainer userHeadRequest;

        @ViewInject(R.id.img_send_msg)
        ImageView sendMsg;

        @ViewInject(R.id.img_call)
        ImageView call;
    }
}
