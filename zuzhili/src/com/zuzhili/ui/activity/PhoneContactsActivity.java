package com.zuzhili.ui.activity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ProgressBar;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.bussiness.utility.pinyin.CharacterParser;
import com.zuzhili.bussiness.utility.pinyin.PinyinComparator;
import com.zuzhili.controller.PhoneContactsAdapter;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.framework.utils.DensityUtil;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.ContactRec;
import com.zuzhili.ui.views.PagingListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liutao on 14-5-16.
 * 邀请好友
 */
public class PhoneContactsActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback, Response.Listener<String>, Response.ErrorListener {

    @ViewInject(R.id.listView)
    protected PagingListView listView;

    @ViewInject(R.id.progressbar)
    protected ProgressBar progressBar;

    private ContentResolver mContentResolver;

    private HashMap<String, ContactRec> mContactsPhoneHash;

    private CharacterParser characterParser;

    private List<ContactRec> contactRecList = new ArrayList<ContactRec>();

    private List<ContactRec> selectedList = new ArrayList<ContactRec>();

    private PhoneContactsAdapter adapter;



    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.paginglistview_layout);
        ViewUtils.inject(this);
        setCustomActionBarCallback(this);
        mContentResolver = getContentResolver();
        characterParser = new CharacterParser();
        new Thread(new Runnable() {

            @Override
            public void run() {
                getContacts();
            }
        }).start();
    }

    private void getContacts() {
        HashMap<Integer, ContactRec> temp = new HashMap<Integer, ContactRec>();
        Cursor cur1 = mContentResolver.query(
                ContactsContract.Contacts.CONTENT_URI, new String[] {
                        BaseColumns._ID,

                        ContactsContract.Contacts.DISPLAY_NAME, "sort_key" },
                null, null, null);
        if (cur1 != null) {
            if (cur1.moveToFirst()) {
                do {
                    int id = cur1.getInt(0);
                    String name = cur1.getString(1);
                    ContactRec rec = new ContactRec();
                    rec.setName(name);
                    String sortkey = cur1.getString(2);
                    rec.setSortKey(sortkey);
                    temp.put(id, rec);

                } while (cur1.moveToNext());
            }
            cur1.close();
        }
        mContactsPhoneHash = new HashMap<String, ContactRec>();
        Cursor cur = mContentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[] {
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.NUMBER }, null,
                null, null);
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    int id = cur.getInt(0);
                    ContactRec temprec = temp.get(id);
                    String strAddress = cur.getString(1);
                    ContactRec rec = new ContactRec();
                    if (temprec == null) {
                        continue;
                    }
                    rec.setName(temprec.getName());
                    rec.setPhone(strAddress);
                    rec.setSortKey(updateSortKey(temprec.getSortKey()));
                    contactRecList.add(rec);
                    mContactsPhoneHash.put(strAddress, rec);
                } while (cur.moveToNext());
            }
            cur.close();
        }
        //todo
        //Collections.sort(contactRecList, new PinyinComparator());
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                listView.setDividerHeight(DensityUtil.dip2px(PhoneContactsActivity.this, 1));
                listView.removeHeadView();
                adapter = new PhoneContactsAdapter(PhoneContactsActivity.this, listView, ImageCacheManager.getInstance().getImageLoader());
                adapter.setOnContactSelectedListener(
                        new PhoneContactsAdapter.OnContactSelectedListener() {
                            @Override
                            public void onContactSelected(ContactRec contactRec) {
                                if (selectedList.contains(contactRec)) {
                                    selectedList.remove(contactRec);
                                } else {
                                    selectedList.add(contactRec);
                                }
                            }
                        }
                );
                adapter.setList(contactRecList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(adapter);
            }
        });
    }

    private String updateSortKey(String s) {
        //汉字转换成拼音
        String pinyin = characterParser.getSelling(TextUtil.processNullString(s));
//        LogUtils.e("userName: " + friendInfo.getUserName() + ", pinyin: " + pinyin);
        String sortString;
        if (pinyin != null && pinyin.length() > 0) {
            sortString = pinyin.substring(0, 1).toUpperCase();
        } else {
            sortString = "#";
        }
        return sortString;
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, getString(R.string.inviate), getString(R.string.title_member), false);
        setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setOnClickRightListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSelectPhones() != null && !getSelectPhones().isEmpty()) {
                    Task.invite(buildRequestParams(), PhoneContactsActivity.this, PhoneContactsActivity.this);
                } else {
                    Utils.makeEventToast(PhoneContactsActivity.this, "请选择至少一个联系人", false);
                }
            }
        });
        return false;
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }

    @Override
    public void onResponse(String s) {
        LogUtils.e("onResponse: " + s);
        JSONObject jsonObject = JSON.parseObject(s);
        if (jsonObject != null && jsonObject.getString("errmsg") != null && jsonObject.getString("errmsg").equals("ok")) {
            Utils.makeEventToast(this, "发生邀请成功", false);
        } else {
            Utils.makeEventToast(this, "发生邀请失败", false);
        }
        finish();
    }

    private HashMap<String, String> buildRequestParams() {
        final HashMap<String, String> params = new HashMap<String, String>();
        if(mSession != null) {
            params.put("listid", mSession.getListid());
            params.put("ids", mSession.getIds());
            params.put("mobiles", getSelectPhones());
        }
        return params;
    }

    String getSelectPhones() {
        String ret = "";
        if (selectedList != null) {
            for (ContactRec item : selectedList) {
                if (ret.length() == 0) {

                    ret += item.getPhone().replaceAll(" ", "");
                } else {
                    ret += "," + item.getPhone().replaceAll(" ", "");
                }
            }
            return ret;
        }
        return null;
    }
}
