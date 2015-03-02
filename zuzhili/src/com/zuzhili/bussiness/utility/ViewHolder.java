package com.zuzhili.bussiness.utility;

import android.util.SparseArray;
import android.view.View;
/**
 * @Title: ViewHolder.java
 * @Package: com.zuzhili.bussiness.utility
 * @Description: 通用Viewholder
 * @author: gengxin
 * @date: 2014-2-14
 */
public class ViewHolder {
    // I added a generic return type to reduce the casting noise in client code
    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}
