package com.zuzhili.bussiness.utility.pinyin;

import android.text.TextUtils;

import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.model.Member;

import java.util.Comparator;

public class PinyinComparatorM implements Comparator<Member> {
    public int compare(Member o1, Member o2) {
        CharacterParser characterParser = CharacterParser.getInstance();
        if (!TextUtils.isEmpty(o1.getSortKey()) && !TextUtils.isEmpty(o2.getSortKey())
                && !TextUtils.isEmpty(o1.getName()) && !TextUtils.isEmpty(o2.getName())) {
            if (o1.getSortKey().equals("@")
                    || o2.getSortKey().equals("#")) {
                return -1;
            } else if (o1.getSortKey().equals("#")
                    || o2.getSortKey().equals("@")) {
                return 1;
            } else {
                String o1Name = characterParser.getSelling(o1.getName());
                String o2Name =characterParser.getSelling(o2.getName());
                return o1Name.compareTo(o2Name);
            }
        }
        return 1;
    }
}
