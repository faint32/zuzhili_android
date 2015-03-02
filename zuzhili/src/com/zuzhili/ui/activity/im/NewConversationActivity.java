package com.zuzhili.ui.activity.im;

import android.os.Bundle;
import android.view.KeyEvent;

import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.fragment.im.ContactsFragment;

public class NewConversationActivity extends BaseActivity {
    private ContactsFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);
        Bundle bundle = new Bundle();
        bundle.putSerializable("CONTACT_MODE", ContactsFragment.ContactMode.MODE_SELECTION);
        bundle.putBoolean("isQueryAllGroupsAndUsers", false);
        bundle.putString(Constants.EXTRA_FROM_WHICH_PAGE, Constants.EXTRA_FROM_CHAT_ROOM_SETTINGS);
        bundle.putString(Constants.EXTRA_IM_GROUPID,  getIntent().getStringExtra(Constants.EXTRA_IM_GROUPID));
        bundle.putParcelableArrayList(Constants.EXTRA_PARCELABLE_CONTACTS, getIntent().getParcelableArrayListExtra(
                Constants.EXTRA_PARCELABLE_CONTACTS));
        fragment = new ContactsFragment();
        setCustomActionBarCallback(fragment);
        fragment.setArguments(bundle);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(fragment!=null){
            fragment.onKeyDown(keyCode,event);
        }
        return super.onKeyDown(keyCode, event);
    }
}
