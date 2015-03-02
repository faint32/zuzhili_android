package com.zuzhili.ui.views.quickreturn;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ScrollView;

import com.zuzhili.ui.views.quickreturn.widget.ObservableScrollView;
import com.zuzhili.ui.views.quickreturn.widget.QuickReturnTargetView;


public abstract class QuickReturnAttacher {

    public static QuickReturnAttacher forView(ViewGroup viewGroup) {
        if (viewGroup instanceof AbsListView)
            return new AbsListViewQuickReturnAttacher((AbsListView) viewGroup);
        else if (viewGroup instanceof ScrollView)
            return new ScrollViewQuickReturnAttacher((ObservableScrollView) viewGroup);

        throw new UnsupportedOperationException("Invalid viewGroup instance. It must be a subclass of AbsListView or ObservableScrollView");
    }

    public abstract QuickReturnTargetView addTargetView(final View view, final int position);

    public abstract QuickReturnTargetView addTargetView(final View view, final int position, final int viewHeight);
}
