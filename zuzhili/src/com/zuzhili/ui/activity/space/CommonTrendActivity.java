package com.zuzhili.ui.activity.space;

import android.os.Bundle;

import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.db.CacheType;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.fragment.NewsTimeLineFrg;

import java.util.HashMap;

public class CommonTrendActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback {

    public static final String SPACE_ID = "space.space_id";
    public static final String USER_IDS = "space.user_id";
    public static final String APP_TYPE = "app_type";
    public static final String SPACE_TITLE = "space_title";

    private String title = "返回";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_trend);

        setCustomActionBarCallback(this);

        String spaceId = getIntent().getStringExtra(SPACE_ID);
        String userIds = getIntent().getStringExtra(USER_IDS);
        String appType = getIntent().getStringExtra(APP_TYPE);

        String spaceTitle = getIntent().getStringExtra(SPACE_TITLE);
        if (spaceTitle != null) {
            title = spaceTitle;
        }

        if (savedInstanceState == null) {

            NewsTimeLineFrg fragment = new NewsTimeLineFrg();

            if (spaceId != null) {
                Bundle bundle = new Bundle();
                bundle.putString(NewsTimeLineFrg.CACHE_TYPE, CacheType.CACHE_GET_SPECIFIC_GROUP_TRENDS);
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("ids", mSession.getIds());
                params.put("listid", mSession.getListid());
                params.put("spaceid", spaceId);
                params.put("start", "0");
                params.put("length", String.valueOf(Constants.PAGE_SIZE));
                bundle.putSerializable("params", params);
                fragment.setArguments(bundle);
            } else if (userIds != null) {
                if (appType != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(NewsTimeLineFrg.CACHE_TYPE, CacheType.CACHE_GET_USER_COLLECTION);
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("ids", mSession.getIds());
                    params.put("apptype", appType);
                    params.put("start", "0");
                    params.put("length", String.valueOf(Constants.PAGE_SIZE));
                    bundle.putSerializable("params", params);
                    fragment.setArguments(bundle);
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString(NewsTimeLineFrg.CACHE_TYPE, CacheType.CACHE_GET_SPECIFIC_USER_TRENDS);
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("curids", mSession.getIds());
                    params.put("ids", userIds);
                    params.put("listid", mSession.getListid());
                    params.put("start", "0");
                    params.put("length", String.valueOf(Constants.PAGE_SIZE));
                    bundle.putSerializable("params", params);
                    fragment.setArguments(bundle);
                }
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean showCustomActionBar() {
        initActionBar(R.drawable.icon_back, 0, title, false);
        return false;
    }

    @Override
    public boolean performClickOnLeft() {
        finish();
        return false;
    }

}
