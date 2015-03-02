package com.zuzhili.bussiness.utility.pinyin;

import android.text.TextUtils;
import android.util.Log;

import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.model.SortModel;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class PinyinComparator implements Comparator<UserInfo> {

    public int compare(UserInfo o1, UserInfo o2) {
        CharacterParser characterParser = CharacterParser.getInstance();
        if (!TextUtils.isEmpty(o1.getSortKey()) && !TextUtils.isEmpty(o2.getSortKey())
                && !TextUtils.isEmpty(o1.getU_name()) && !TextUtils.isEmpty(o2.getU_name())) {
            if (o1.getSortKey().equals("@")
                    || o2.getSortKey().equals("#")) {
                return -1;
            } else if (o1.getSortKey().equals("#")
                    || o2.getSortKey().equals("@")) {
                return 1;
            } else {
                String o1Name = characterParser.getSelling(o1.getU_name());
                String o2Name =characterParser.getSelling(o2.getU_name());
                return o1Name.compareTo(o2Name);
            }
        }
        return 1;
    }
}
