package com.zuzhili.ui.fragment;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zuzhili.R;
import com.zuzhili.provider.Contract;
import com.zuzhili.ui.activity.loginreg.WebViewActivity;
import com.zuzhili.ui.views.PullRefreshListView;


/**
 * Created by liutao on 15-1-3.
 */
public class FourthCategoryListFrg extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = FourthCategoryListFrg.class.getSimpleName();

    private static final String ARG_PARENT_ID = "parent_id";

    private PullRefreshListView pullRefreshListView;

    private ProgressBar progressBar;

    //private SimpleAdapter adapter;
    private CategoryCursorAdapter mAdapter;

    private String parentId;

    interface SubMenuQuery {
        String[] PROJECTION = new String[] {
                BaseColumns._ID,
                Contract.SubMenu.SUBMENU_COLUMN_ID,
                Contract.SubMenu.SUBMENU_COLUMN_NAME,
                Contract.SubMenu.SUBMENU_COLUMN_LEVEL,
                Contract.SubMenu.SUBMENU_COLUMN_PARENTID,
                Contract.SubMenu.SUBMENU_COLUMN_SEARCHURL,
                Contract.SubMenu.SUBMENU_COLUMN_ICON,
                Contract.SubMenu.SUBMENU_COLUMN_CATEGORYURL
        };

        final int _ID = 0;
        final int SUBMENU_ID = 1;
        final int NAME = 2;
        final int LEVEL = 3;
        final int PARENTID = 4;
        final int SEARCHURL = 5;
        final int ICON = 6;
        final int CATEGORYURL = 7;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            parentId = savedInstanceState.getString(ARG_PARENT_ID);
        } else {
            parentId = getArguments().getString("parent_id");
        }
        mAdapter = new CategoryCursorAdapter(getActivity(), null, 0);
        Bundle args = new Bundle();
        args.putString("parent_id", parentId);
        getLoaderManager().initLoader(0, args, new ThirdLevelSubmenusLoaderCallback());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("parent_id", parentId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.listview_layout, container, false);
        pullRefreshListView = (PullRefreshListView) view.findViewById(R.id.listView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);
        pullRefreshListView.setDivider(getResources().getDrawable(R.drawable.divider));
        pullRefreshListView.setOnItemClickListener(this);
        pullRefreshListView.setDividerHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getActivity().getResources().getDisplayMetrics()));
        pullRefreshListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor item = (Cursor) parent.getAdapter().getItem(position);
        String searchUrl = item.getString(item.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_SEARCHURL));
        Intent it = new Intent(getActivity(), WebViewActivity.class);
        it.putExtra("url", item.getString(item.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_CATEGORYURL)));
        if (searchUrl != null && !searchUrl.isEmpty() && !searchUrl.equals("null")) {
            it.putExtra("searchUrl", searchUrl);
        }
        getActivity().startActivity(it);
    }

    class CategoryCursorAdapter extends CursorAdapter {

        public CategoryCursorAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.listview_item_category, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.cateName = (TextView) view.findViewById(R.id.txt_category_name);
            view.setTag(holder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (cursor != null) {
                holder.cateName.setText(cursor.getString(cursor.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_NAME)));
            }
        }
    }

    class ViewHolder{
        TextView cateName;
    }

    class ThirdLevelSubmenusLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String parentId = args.getString("parent_id");
            String selectionClause = "submenu_column_parentid=?";
            String[] selectionArgs = new String[] {parentId};
            return new CursorLoader(getActivity(),
                    Contract.SubMenu.CONTENT_URI,
                    SubMenuQuery.PROJECTION,
                    selectionClause,
                    selectionArgs,
                    Contract.SubMenu.SUBMENU_COLUMN_ID + " ASC");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            progressBar.setVisibility(View.GONE);
            mAdapter.swapCursor(data);
            if (data != null) {
                data.moveToPosition(-1);
                int i = 0;
                while (data.moveToNext()) {
                    int id = data.getInt(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_ID));
                    String name = data.getString(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_NAME));
                    int level = data.getInt(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_LEVEL));
                    int parentId = data.getInt(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_PARENTID));
                    String icon = data.getString(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_ICON));
                    String searchUrl = data.getString(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_SEARCHURL));
                    String categoryUrl = data.getString(data.getColumnIndex(Contract.SubMenu.SUBMENU_COLUMN_CATEGORYURL));
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.swapCursor(null);
        }
    }
}
