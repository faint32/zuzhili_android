package com.zuzhili.ui.activity.space;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.model.Member;
import com.zuzhili.service.GetIMDataIntentService;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.im.GroupChatActivity;
import com.zuzhili.ui.fragment.space.PersonalSpaceFrg;

public class SpaceActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback {

    @ViewInject(R.id.lin_container)
    private View container;

    private Member member;

    private UserInfo userInfo;

    private PullIMDataReceiver mReceiver;

    private String y_voip;

    @Override
    protected void onCreate(Bundle inState) {
        super.onCreate(inState);
        super.setContentView(R.layout.activity_frg_container);
        ViewUtils.inject(this);
        initData(getIntent());
        mSession.setSpaceActivityInstantiated(true);
        mReceiver = new PullIMDataReceiver();
        IntentFilter statusIntentFilter = new IntentFilter(
                Constants.BROADCAST_ACTION);
        statusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mReceiver,
                statusIntentFilter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initData(intent);
        updateActionBar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSession.setSpaceActivityInstantiated(false);
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private void initData(Intent intent) {
        setCustomActionBarCallback(this);
        Member tempMember = intent.getParcelableExtra(Constants.EXTRA_MEMBER);
        y_voip = intent.getStringExtra(Constants.EXTRA_VOIP_ID);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_FRAGMENT_TAG, Constants.TAG_PERSONAL_SPACE);
        if (member != null) {
            if (member.getId().equals(tempMember.getId())) {
                return;
            } else {
                bundle.putSerializable(Constants.EXTRA_MEMBER, member);
                member = tempMember;
            }
        } else {
            member = tempMember;
        }
        bundle.putSerializable(Constants.EXTRA_MEMBER, member);
        PersonalSpaceFrg f = PersonalSpaceFrg.newInstance(member);
        if (!f.isAdded()) {
            replaceFragment(container.getId(), f, Constants.TAG_PERSONAL_SPACE);
        }

        if (member.getUserid() == 0) {
            userInfo = getDbHelper().getUserInfoTable().get(y_voip, member.getListid(), Utils.getIdentity(mSession));
        } else {
            userInfo = getDbHelper().getUserInfoTable().getUserByUid(String.valueOf(member.getUserid()), mSession.getListid());
        }
        if (userInfo == null) {
            Intent in = new Intent(this, GetIMDataIntentService.class);
            in.putExtra(Constants.ACTION, Task.ACTION_GET_ALL_USERS);
            startService(in);
        }
    }

    private class PullIMDataReceiver extends BroadcastReceiver {

        private PullIMDataReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getIntExtra(Constants.EXTENDED_DATA_STATUS,
                    Constants.PULL_DATA_FAILED)) {
                case Constants.PULL_IM_USERS_FINISHED:
                    if (TextUtils.isEmpty(String.valueOf(member.getUserid()))) {
                        userInfo = getDbHelper().getUserInfoTable().get(y_voip, member.getListid(), Utils.getIdentity(mSession));
                    } else {
                        userInfo = getDbHelper().getUserInfoTable().getUserByUid(String.valueOf(member.getUserid()), mSession.getListid());
                    }
                    updateActionBar();
                    break;
            }
        }
    }

    @Override
    public boolean showCustomActionBar() {
        updateActionBar();
        return true;
    }

    private void updateActionBar() {
        if (member.getId().equals(mSession.getIds())) {
            initActionBar(R.drawable.icon_back, 0, mSession.getUserName(), false);
            setOnClickLeftListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            if (!TextUtils.isEmpty(member.getName())) {
                initActionBar(R.drawable.icon_back, R.drawable.icon_chat, member.getName(), false);
            } else if (userInfo != null && !TextUtils.isEmpty(userInfo.getU_name())) {
                initActionBar(R.drawable.icon_back, R.drawable.icon_chat, userInfo.getU_name(), false);
            }
            setOnClickLeftListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            setOnClickRightListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userInfo != null) {
                        Intent it = new Intent(SpaceActivity.this, GroupChatActivity.class);
                        it.putExtra(Constants.EXTRA_IM_YGROUPID, userInfo.getY_voip());
                        it.putExtra(Constants.EXTRA_IM_GROUP_NAME, userInfo.getU_name());
                        it.putExtra(Constants.EXTRA_IM_GROUP_USER_COUNT, "");  // 空串可以标志这是一个单聊(java.io.Serializable) friendFaceAdapter.getUserInfoList().get(0)
                        it.putExtra(Constants.EXTRA_IM_CONTACT, (android.os.Parcelable) userInfo);
                        it.putExtra(Constants.EXTRA_GROUP_CHAT_FLAG, false);
                        startActivity(it);
                    }
                }
            });
        }
    }
}
