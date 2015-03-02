package com.zuzhili.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.SearchView;

import com.zuzhili.R;


/**
 * class that display the search results into gridView
 */
public class AddContactSearchView extends FrameLayout {

    private SearchView searchView;

    private GridView gridView;

    public AddContactSearchView(Context context) {
        super(context);
        init(context);
    }

    public AddContactSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AddContactSearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_add_contact_search, this, true);
        searchView = (SearchView) findViewById(R.id.searchView);
        gridView = (GridView) findViewById(R.id.gird_contacts);
    }

}
