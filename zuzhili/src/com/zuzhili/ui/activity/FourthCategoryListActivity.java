package com.zuzhili.ui.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;

import com.zuzhili.R;
import com.zuzhili.ui.fragment.FourthCategoryListFrg;

/**
 * Created by liutao on 1/3/15.
 */
public class FourthCategoryListActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback {

    private static final String ARG_PARENT_ID = "parent_id";
    private static final String ARG_TITLE = "title";

    private String parentId;

    private String title;

    private FourthCategoryListFrg mFourthCategoryListFrg;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        setContentView(R.layout.activity_frg_container);
        setCustomActionBarCallback(this);
        if (inState != null) {
            parentId = inState.getString(ARG_PARENT_ID);
            title = inState.getString(ARG_TITLE);
        } else {
            parentId = getIntent().getStringExtra("parent_id");
            title = getIntent().getStringExtra("title");
        }
        FragmentManager fm = getFragmentManager();
        if (mFourthCategoryListFrg == null) {
            mFourthCategoryListFrg = new FourthCategoryListFrg();
            Bundle args = new Bundle();
            args.putString("parent_id", parentId);
            mFourthCategoryListFrg.setArguments(args);
            fm.beginTransaction().add(R.id.lin_container, mFourthCategoryListFrg).commit();
        }
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, 0, title, false);
        setOnClickLeftListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return false;
    }
}
