package com.zuzhili.ui.views;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;

/**
 * Created by liutao on 14-2-22.
 */
public class CustomSearchView extends SearchView {

    private OnSearchViewCollapsedEventListener onSearchViewCollapsedEventListener;

    private OnSearchViewExpandedEventListener onSearchViewExpandedEventListener;

    public interface OnSearchViewExpandedEventListener {
        public void onSearchViewExpanded();
    }

    public interface OnSearchViewCollapsedEventListener {
        public void onSearchViewCollapsed();
    }

    public void setOnSearchViewCollapsedEventListener(OnSearchViewCollapsedEventListener onSearchViewCollapsedEventListener) {
        this.onSearchViewCollapsedEventListener = onSearchViewCollapsedEventListener;
    }

    public void setOnSearchViewExpandedEventListener(OnSearchViewExpandedEventListener onSearchViewExpandedEventListener) {
        this.onSearchViewExpandedEventListener = onSearchViewExpandedEventListener;
    }

    public CustomSearchView(Context context) {
        super(context);
    }

    public CustomSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onActionViewCollapsed() {
        if (onSearchViewCollapsedEventListener != null) {
            onSearchViewCollapsedEventListener.onSearchViewCollapsed();
        }
        super.onActionViewCollapsed();
    }

    @Override
    public void onActionViewExpanded() {
        if(onSearchViewExpandedEventListener != null) {
            onSearchViewExpandedEventListener.onSearchViewExpanded();
        }
        super.onActionViewExpanded();
    }
}
