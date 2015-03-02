package com.zuzhili.ui.activity.space;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lidroid.xutils.util.LogUtils;
import com.zuzhili.R;
import com.zuzhili.bussiness.Task;
import com.zuzhili.bussiness.socket.model.GroupInfo;
import com.zuzhili.bussiness.socket.model.UserInfo;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.bussiness.utility.TextUtil;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.model.space.GroupInfoSummary;
import com.zuzhili.model.space.UserInfoSummary;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.im.GroupChatActivity;
import com.zuzhili.ui.activity.publish.AlbumListActivity;
import com.zuzhili.ui.activity.publish.PublishMainActivity;
import com.zuzhili.ui.fragment.im.ContactListActivity;
import com.zuzhili.ui.views.BadgeView;
import com.zuzhili.ui.views.SpaceCompositeView;

import java.io.Serializable;
import java.util.HashMap;

interface TitleChangeListener {
    public void titleChanged(String title);
}

public class CommonSpaceActivity extends BaseActivity implements BaseActivity.TimeToShowActionBarCallback, TitleChangeListener {

    public static String listid = null;
    public static String ids = null;
    public static String userId;
    private String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listid = mSession.getListid();
        ids = mSession.getIds();
        userId = mSession.getUid();

        setCustomActionBarCallback(this);

        setContentView(R.layout.activity_common_space);

        if (savedInstanceState == null) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            getFragmentManager().beginTransaction()
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

    @Override
    public void titleChanged(String title) {
        this.title = title;
    }

    public interface IPopulator extends Serializable {

        void injectSummary(Object summary);

        void populateLogo(ImageView view);

        void populateTitle(TextView view);

        void populateSpaceMainAction(Button view);

        void populateAction1(ImageButton view);

        void populateAction2(ImageButton view);

        void populateAction3(Button view);

        void populateAction4(Button view, View placeHolder);

        void populateDetail(SpaceCompositeView view);

        void populateTrend(SpaceCompositeView view);

        void populatePicture(SpaceCompositeView view);

        void populateNetworking(SpaceCompositeView view);

        void populateLibrary(SpaceCompositeView view);

        void populateFavorite(SpaceCompositeView view);

        void populateMember(SpaceCompositeView view);
    }

    /**
     * This class is used to populate View with GroupInfo
     */
    public static class GroupSpacePopulator implements IPopulator {

        private GroupInfo groupInfo;
        private GroupInfoSummary groupInfoSummary;
        private Context mContext;

        public GroupSpacePopulator(GroupInfo groupInfo, Context mContext) {
            this.groupInfo = groupInfo;
            this.mContext = mContext;
        }

        @Override
        public void injectSummary(Object summary) {
            groupInfoSummary = (GroupInfoSummary) summary;
        }

        @Override
        public void populateLogo(ImageView view) {
            ImageCacheManager.getInstance().getImageLoader().get(TextUtil.processNullString(
                            groupInfoSummary == null ? "" : groupInfoSummary.getAvatar()),
                    ImageLoader.getImageListener(view, R.drawable.default_social_logo, R.drawable.default_social_logo));
        }

        @Override
        public void populateTitle(TextView view) {
            if (groupInfoSummary == null) {
                return;
            }

            view.setText(groupInfoSummary.getAdminChenghu() + ":" + groupInfoSummary.getAdminInentiys());
        }

        @Override
        public void populateSpaceMainAction(final Button view) {
            String status = groupInfoSummary.getStatus();
            view.setText(status.equals("0") ? "申请加入" : status.equals("1") ? "退出" : "审核中");

            // Disable apply button when the user has not yet joined the group and the group do not allow join action.
            if (status.equals("0") && !groupInfoSummary.isCanApplyFlag()) {
                view.setEnabled(false);
                view.setText("暂不接受申请");
            }

            // Disable the button if the current user is the creator for the group.
            if (groupInfo.getCreatorid().equals(userId)) {
                view.setEnabled(false);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (groupInfoSummary.getStatus().equals("0")) {
                        // join group
                        final HashMap<String, String> params = new HashMap<String, String>();
                        params.put("ids", ids);
                        params.put("listid", listid);
                        params.put("spaceid", groupInfo.getZ_gid());
                        final ProgressDialog progressDialog = ProgressDialog.show(mContext, null, null, true);

                        // provide apply reason if space require approval
                        if (groupInfoSummary.isNeedApply()) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            // Get the layout inflater
                            LayoutInflater inflater = LayoutInflater.from(mContext);
                            View view = inflater.inflate(R.layout.dialog_apply_space, null);
                            final TextView applyReasonTxt = (TextView) view.findViewById(R.id.space_apply_reason);
                            // Inflate and set the layout for the dialog
                            // Pass null as the parent view because its going in the dialog layout
                            builder.setView(view)
                                    // Add action buttons
                                    .setPositiveButton(R.string.social_apply_confirm, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int id) {
                                            params.put("reason", applyReasonTxt.getText().toString().trim());
                                            Task.requestJoinSpace(params, new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String s) {
                                                    JSONObject result = JSON.parseObject(s);
                                                    String code = result.getString("code");
                                                    String desc = result.getString("desc");
                                                    String errmsg = result.getString("errmsg");
                                                    groupInfoSummary.setStatus(code);
                                                    ((Button) v).setText(code.equals("0") ? "申请加入" : code.equals("1") ? "退出" : "审核中");
                                                    progressDialog.dismiss();
                                                    Toast.makeText(mContext, desc, Toast.LENGTH_LONG).show();
                                                    groupInfoSummary.setStatus(String.valueOf(code));
                                                }
                                            }, new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError volleyError) {
                                                    String msg = volleyError.getMessage();
                                                    String msgObj = JSON.parseObject(msg).getString("errmsg");
                                                    progressDialog.dismiss();
                                                    Toast.makeText(mContext, msgObj, Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    });
                            builder.create().show();
                        } else {
                            Task.requestJoinSpace(params, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String s) {
                                    JSONObject result = JSON.parseObject(s);
                                    String code = result.getString("code");
                                    String desc = result.getString("desc");
                                    String errmsg = result.getString("errmsg");
                                    groupInfoSummary.setStatus(code);
                                    ((Button) v).setText(code.equals("0") ? "申请加入" : code.equals("1") ? "退出" : "审核中");
                                    progressDialog.dismiss();
                                    Toast.makeText(mContext, desc, Toast.LENGTH_LONG).show();
                                    groupInfoSummary.setStatus(String.valueOf(code));
                                    if (code.equals("1")) {
                                        groupInfo.setIsmember("1");
                                        populateAction2(PlaceholderFragment.mStartChat);
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    String msg = volleyError.getMessage();
                                    String msgObj = JSON.parseObject(msg).getString("errmsg");
                                    progressDialog.dismiss();
                                    Toast.makeText(mContext, msgObj, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } else if (groupInfoSummary.getStatus().equals("1")) {
                        // exit group
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("ids", ids);
                        params.put("listid", listid);
                        params.put("spaceid", groupInfo.getZ_gid());
                        final ProgressDialog dialog = ProgressDialog.show(mContext, null, null, true);
                        Task.requestQuitSpace(params, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                JSONObject result = JSON.parseObject(s);
//                                String code = result.getString("code");
//                                String desc = result.getString("desc");
                                String errmsg = result.getString("errmsg");
//                                groupInfoSummary.setStatus(code);
//                                ((Button) v).setText(code.equals("0") ? "申请加入" : code.equals("1") ? "退出" : "审核中");
                                if (errmsg.equals("ok")) {
                                    ((Button) v).setText("申请加入");
                                    groupInfoSummary.setStatus("0");
                                    Toast.makeText(mContext, "退出成功", Toast.LENGTH_LONG).show();
                                    groupInfo.setIsmember("0");
                                    populateAction2(PlaceholderFragment.mStartChat);
                                }
                                dialog.dismiss();
//                                Toast.makeText(mContext, desc, Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                String msg = volleyError.getMessage();
                                String msgObj = JSON.parseObject(msg).getString("errmsg");
                                dialog.dismiss();
                                Toast.makeText(mContext, msgObj, Toast.LENGTH_LONG).show();
                            }
                        });
                    } else if (groupInfoSummary.getStatus().equals("2")) {
                        // prompt user the state is in process
                        Toast.makeText(mContext, "申请已提交，请等待审核。", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void populateAction1(ImageButton view) {
            view.setImageResource(R.drawable.space_publish_selector);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(Constants.ACTIVITY_FROM_BUNDLE_SPACEID, groupInfo.getZ_gid());
                    intent.setClass(mContext, PublishMainActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public void populateAction2(ImageButton view) {
            boolean isMember = groupInfo.getIsmember() == null || groupInfo.getIsmember().equals("1");
            if (!isMember) {
                view.setImageResource(R.drawable.space_cannot_chat);
                view.setEnabled(false);
                return;
            }

            view.setTag(groupInfo);
            view.setEnabled(true);
            view.setImageResource(R.drawable.space_start_chat_selector);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupInfo info = (GroupInfo) v.getTag();

                    Intent intent = new Intent(mContext, GroupChatActivity.class);
                    intent.putExtra(Constants.EXTRA_IM_GROUP, (java.io.Serializable) info);
                    intent.putExtra(Constants.EXTRA_IM_GROUP_USER_COUNT, info.getG_ucount());
                    intent.putExtra(Constants.EXTRA_IM_GROUPID, info.getId());
                    intent.putExtra(Constants.EXTRA_IM_YGROUPID, info.getY_gid());
                    intent.putExtra(Constants.EXTRA_IM_NEED_GET_GROUP_USER, true);
                    intent.putExtra(Constants.EXTRA_IM_CHAT_ROOM_TYPE, info.getG_type());
                    intent.putExtra(Constants.EXTRA_IM_GROUP_NAME, info.getG_name());
                    intent.putExtra(Constants.EXTRA_GROUP_CHAT_FLAG, true);

                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public void populateAction3(Button view) {
            view.setVisibility(View.GONE);
        }

        @Override
        public void populateAction4(Button view, View placeHolder) {
            view.setVisibility(View.GONE);
        }

        @Override
        public void populateDetail(SpaceCompositeView view) {
            view.setVisibility(View.GONE);
        }

        @Override
        public void populateTrend(SpaceCompositeView view) {
            if (groupInfoSummary == null) {
                view.update(R.drawable.space_trend, "动态", null);
                return;
            }

            view.update(R.drawable.space_trend, "动态", groupInfoSummary.getNewscount());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, CommonTrendActivity.class);
                    intent.putExtra(CommonTrendActivity.SPACE_ID, groupInfo.getZ_gid());
                    intent.putExtra(CommonTrendActivity.SPACE_TITLE, "动态");
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public void populatePicture(SpaceCompositeView view) {
            if (groupInfoSummary == null) {
                view.update(R.drawable.space_picture, "图片", null);
                return;
            }

            view.update(R.drawable.space_picture, "图片", groupInfoSummary.getPiccount());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, AlbumListActivity.class);
                    intent.putExtra(Constants.FROM_SPACE, true);
                    intent.putExtra(Constants.SPACE_ALBUM_NAME, groupInfo.getG_name());
                    intent.putExtra(Constants.ACTIVITY_FROM_BUNDLE_SPACEID, groupInfo.getZ_gid());
                    intent.putExtra(Constants.FROM_SPACE, true);
                    mContext.startActivity(intent);

                }
            });
        }

        @Override
        public void populateNetworking(SpaceCompositeView view) {
//            view.update(R.drawable.space_networking, "人脉", groupInfoSummary.get);
            view.setVisibility(View.GONE);
        }

        @Override
        public void populateLibrary(SpaceCompositeView view) {

            // Hide temporarily
            view.setVisibility(View.GONE);
            if (true) {
                return;
            }

            if (groupInfoSummary == null) {
                view.update(R.drawable.space_library, "资料库", null);
                return;
            }

            view.update(R.drawable.space_library, "资料库", groupInfoSummary.getFilecount());
        }

        @Override
        public void populateFavorite(SpaceCompositeView view) {
            view.setVisibility(View.GONE);
        }

        @Override
        public void populateMember(SpaceCompositeView view) {
            if (groupInfoSummary == null) {
                view.update(R.drawable.space_member, "成员", null);
                return;
            }

            view.update(R.drawable.space_member, "成员", groupInfoSummary.getMembercount());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, ContactListActivity.class);
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("ids", ids);
                    params.put("listid", listid);
                    params.put("spaceid", groupInfo.getZ_gid());
                    intent.putExtra("params", params);
                    intent.putExtra("flag", "api.members");
                    intent.putExtra(ContactListActivity.LIST_TITLE, "成员");
                    mContext.startActivity(intent);
                }
            });
        }
    }

    /**
     * This class is used to populate View with UserInfo.
     */
    public static class PersonalSpacePopulator implements IPopulator {

        private UserInfo userInfo;
        private UserInfoSummary userInfoSummary;
        private Context mContext;
        private boolean isUserSelf;
        private Session session;
        private BadgeView badgeView;

        public PersonalSpacePopulator(UserInfo userInfo, Context mContext, boolean isUserSelf, Session session) {
            this.userInfo = userInfo;
            this.mContext = mContext;
            this.isUserSelf = isUserSelf;
            this.session = session;
        }

        @Override
        public void injectSummary(Object summary) {
            if (!(summary instanceof UserInfoSummary)) {
                throw new RuntimeException("Error");
            }
            userInfoSummary = (UserInfoSummary) summary;
        }

        @Override
        public void populateLogo(ImageView view) {
            ImageCacheManager.getInstance().getImageLoader().get(TextUtil.processNullString(
                            userInfoSummary == null ? "" : userInfoSummary.getAvatar()),
                    ImageLoader.getImageListener(view, R.drawable.default_user_head_small, R.drawable.default_user_head_small));
        }

        @Override
        public void populateTitle(TextView view) {
            view.setText(userInfo.getU_name());// position needed
        }

        @Override
        public void populateSpaceMainAction(Button view) {
            String actionName = null;
            if (isUserSelf) {
                actionName = "编辑个人资料";
            } else {
                int i = queryRelationship();
                switch (i) {
                    case 0:
                        actionName = "添加关注";
                        break;

                    case 1:
                        actionName = "已关注/取消";
                        break;

                    case 2:
                        actionName = "相互关注/取消";
                        break;

                    default:
                        actionName = "Unknown";
                        break;
                }
            }
            view.setText(actionName);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    if (isUserSelf) {
                        Toast.makeText(mContext, "该功能还在完善，敬请期待", Toast.LENGTH_SHORT).show();
                        return;// return back if it is 'Edit Profile'
                    }

                    HashMap<String, String> params = new HashMap<String, String>();
                    int i = queryRelationship();
                    params.put("type", i == 0 ? "0" : "1");
                    params.put("ids", session.getIds());
                    params.put("tid", userInfo.getU_ids());

                    final ProgressDialog dialog = ProgressDialog.show(mContext, null, null, true);

                    Task.changeFollowState(params, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            JSONObject result = JSON.parseObject(s);
                            String nowstate = result.getString("nowstatue");
                            String errinfo = result.getString("errinfo");
                            userInfoSummary.setIsmyfocus(nowstate);
                            ((Button) v).setText(nowstate.equals("0") ? "添加关注" : nowstate.equals("1") ? "已关注/取消" : nowstate.equals("2") ? "相互关注/取消" : "Unknown");
                            dialog.dismiss();
                            Toast.makeText(mContext, errinfo, Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            String msg = volleyError.getMessage();
                            String msgObj = JSON.parseObject(msg).getString("errmsg");
                            dialog.dismiss();
                            Toast.makeText(mContext, msgObj, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }

        /**
         * Make call action.
         *
         * @param view
         */
        @Override
        public void populateAction1(ImageButton view) {
            if (isUserSelf) {
                view.setVisibility(View.GONE);
                return;
            }

            if (TextUtils.isEmpty(userInfo.getU_phone())) {
                view.setEnabled(false);
                view.setImageResource(R.drawable.space_cannnot_make_call);
                return;
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtils.d("Make call to :" + userInfo.getU_name());
                    String u_phone = userInfo.getU_phone();
                    if (TextUtils.isEmpty(u_phone)) {
                        return;
                    }
                    Uri number = Uri.parse("tel:" + u_phone);
                    Intent intent = new Intent(Intent.ACTION_DIAL, number);
                    mContext.startActivity(intent);
                }
            });
        }

        /**
         * Chat action.
         *
         * @param view
         */
        @Override
        public void populateAction2(ImageButton view) {
            if (isUserSelf) {
                view.setVisibility(View.GONE);
                return;
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, GroupChatActivity.class);
                    it.putExtra(Constants.EXTRA_IM_YGROUPID, userInfo.getY_voip());
                    it.putExtra(Constants.EXTRA_IM_GROUP_NAME, userInfo.getU_name());
                    it.putExtra(Constants.EXTRA_IM_GROUP_USER_COUNT, "");
                    it.putExtra(Constants.EXTRA_IM_CONTACT, (android.os.Parcelable) userInfo);
                    it.putExtra(Constants.EXTRA_GROUP_CHAT_FLAG, false);
                    mContext.startActivity(it);
                }
            });
        }

        /**
         * View follow action
         *
         * @param view
         */
        @Override
        public void populateAction3(Button view) {
            if (isUserSelf) {
                view.setText("关注" + userInfoSummary.getMylovercount());
                view.setVisibility(View.VISIBLE);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("ids", userInfo.getU_ids());
                        Intent intent = new Intent();
                        intent.setClass(mContext, ContactListActivity.class);
                        intent.putExtra("params", params);
                        intent.putExtra("flag", "api.follow");
                        intent.putExtra(ContactListActivity.LIST_TITLE, "关注");
                        mContext.startActivity(intent);
                    }
                });
            } else {
                view.setVisibility(View.GONE);
            }
        }

        /**
         * View be-follow action.
         *
         * @param view
         * @param placeHolder
         */
        @Override
        public void populateAction4(Button view, View placeHolder) {
            if (isUserSelf) {
                view.setText("被关注" + userInfoSummary.getLovemecount());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Clear flag if there are some new followers.
                        if (badgeView != null) {
                            badgeView.hide();
                            badgeView = null;
                        }

                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("ids", userInfo.getU_ids());
                        Intent intent = new Intent();
                        intent.setClass(mContext, ContactListActivity.class);
                        intent.putExtra("params", params);
                        intent.putExtra("flag", "api.be_follow");
                        intent.putExtra(ContactListActivity.LIST_TITLE, "被关注");
                        mContext.startActivity(intent);
                    }
                });

                // init badge view
                if (userInfoSummary.getNewFocusCount() != null && !userInfoSummary.getNewFocusCount().equals("0")) {
                    badgeView = new BadgeView(mContext, placeHolder);
                    badgeView.setBadgeMargin(50, 0);
                    badgeView.setBadgePosition(BadgeView.POSITION_RIGHT_CENTER);
                    badgeView.setClickable(false);
                    badgeView.setText(userInfoSummary.getNewFocusCount());
                    badgeView.show();
                }

                view.setVisibility(View.VISIBLE);

            } else {
                view.setVisibility(View.GONE);
            }
        }

        @Override
        public void populateDetail(SpaceCompositeView view) {
            view.update(R.drawable.space_detail, "详细资料", null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, PersonalDetailActivity.class);
                    intent.putExtra(Constants.EXTRA_IDS, userInfo.getU_ids());
                    intent.putExtra(Constants.EXTRA_USER_NAME, userInfo.getU_name());
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public void populateTrend(SpaceCompositeView view) {
            if (userInfoSummary == null) {
                view.update(R.drawable.space_trend, "动态", null);
                return;
            }

            view.update(R.drawable.space_trend, "动态", userInfoSummary.getNewscount());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, CommonTrendActivity.class);
                    intent.putExtra(CommonTrendActivity.USER_IDS, userInfo.getU_ids());
                    intent.putExtra(CommonTrendActivity.SPACE_TITLE, "动态");
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public void populatePicture(SpaceCompositeView view) {
            if (userInfoSummary == null) {
                view.update(R.drawable.space_picture, "图片", null);
                return;
            }

            view.update(R.drawable.space_picture, "图片", userInfoSummary.getPhotocount());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, AlbumListActivity.class);
                    intent.putExtra(Constants.FROM_SPACE, true);
                    intent.putExtra(Constants.SPACE_ALBUM_NAME, userInfo.getU_name());
                    intent.putExtra(Constants.ACTIVITY_FROM_BUNDLE_USERID, userInfo.getU_ids());
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public void populateNetworking(SpaceCompositeView view) {
//            view.update(R.drawable.space_networking, "人脉", userInfoSummary.get);
            view.setVisibility(View.GONE);
        }

        @Override
        public void populateLibrary(SpaceCompositeView view) {

            // Hide temporarily
            view.setVisibility(View.GONE);
            if (true) {
                return;
            }

            if (userInfoSummary == null) {
                view.update(R.drawable.space_library, "资料库", null);
                return;
            }

            view.update(R.drawable.space_library, "资料库", userInfoSummary.getFilecount());
        }

        @Override
        public void populateFavorite(SpaceCompositeView view) {
            if (userInfoSummary == null) {
                if (isUserSelf) {
                    view.update(R.drawable.space_favorite, "收藏", null);
                } else {
                    view.setVisibility(View.GONE);
                }
                return;
            }

            if (isUserSelf) {
                view.update(R.drawable.space_favorite, "收藏", userInfoSummary.getCollectioncount());
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, CommonTrendActivity.class);
                        intent.putExtra(CommonTrendActivity.USER_IDS, userInfo.getU_ids());
                        intent.putExtra(CommonTrendActivity.APP_TYPE, "0");
                        intent.putExtra(CommonTrendActivity.SPACE_TITLE, "收藏");
                        mContext.startActivity(intent);
                    }
                });
            } else {
                view.setVisibility(View.GONE);
            }
        }

        @Override
        public void populateMember(SpaceCompositeView view) {
            view.setVisibility(View.GONE);
        }

        private int queryRelationship() {
            return Integer.parseInt(userInfoSummary.getIsmyfocus());
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private IPopulator populator;
        private ImageView mSpaceLogo;
        private TextView mSpaceTitle;
        private Button mSpaceAction;
        private ImageButton mMakeCall;
        public static ImageButton mStartChat;
        private Button mFollow;
        private Button mBeFollowed;
        private UserInfo userInfo;
        private UserInfoSummary userInfoSummary;
        private GroupInfo groupInfo;
        private GroupInfoSummary groupInfoSummary;
        private SpaceCompositeView detailView;
        private SpaceCompositeView trendView;
        private SpaceCompositeView pictureView;
        private SpaceCompositeView networkingView;
        private SpaceCompositeView libView;
        private SpaceCompositeView favoriteView;
        private SpaceCompositeView memberView;

        private TitleChangeListener titleChangeListener;
        private View placeHolder;

        public PlaceholderFragment() {
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            if (activity instanceof TitleChangeListener) {
                this.titleChangeListener = (TitleChangeListener) activity;
            } else {
                throw new RuntimeException("The activity must implement TitleChangeListener interface.");
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            boolean isUserSelf = false;

            Object model = getActivity().getIntent().getParcelableExtra(Constants.EXTRA_SPACE_MODEL);
            if (model instanceof UserInfo) {
                userInfo = (UserInfo) model;
                isUserSelf = ((BaseActivity) getActivity()).mSession.getMySelfInfo().getU_id().equals(userInfo.getU_id());
                populator = new PersonalSpacePopulator(userInfo, getActivity(), isUserSelf, ((BaseActivity) getActivity()).mSession);
                titleChangeListener.titleChanged(userInfo.getU_name());
            } else if (model instanceof GroupInfo) {
                groupInfo = (GroupInfo) model;
                populator = new GroupSpacePopulator(groupInfo, getActivity());
                titleChangeListener.titleChanged(groupInfo.getG_name());
            }


            View rootView = inflater.inflate(R.layout.listview_item_space_head_copy, container, false);
            LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.rootLayout);

            mSpaceLogo = (ImageView) layout.findViewById(R.id.iv_space_icon);
            populator.populateLogo(mSpaceLogo);

            mSpaceTitle = (TextView) layout.findViewById(R.id.tv_space_title);
//            populator.populateTitle(mSpaceTitle);

            mSpaceAction = (Button) layout.findViewById(R.id.bt_space_action);

            mMakeCall = (ImageButton) layout.findViewById(R.id.ib_action_call);
            populator.populateAction1(mMakeCall);

            mStartChat = (ImageButton) layout.findViewById(R.id.ib_action_chat);
            populator.populateAction2(mStartChat);

            mFollow = (Button) layout.findViewById(R.id.bt_space_follow);
            mBeFollowed = (Button) layout.findViewById(R.id.bt_space_be_follow);
            placeHolder = layout.findViewById(R.id.placeholder);


            // Hide im action bar when make call button is hidden.
            ViewGroup imLayout = (ViewGroup) layout.findViewById(R.id.layout_space_im);
            imLayout.setVisibility(mMakeCall.getVisibility());

            // Show follow bar when displaying user self info.
            ViewGroup followLayout = (ViewGroup) layout.findViewById(R.id.layout_space_follow);
            followLayout.setVisibility(userInfo != null && isUserSelf ? View.VISIBLE : View.GONE);


            LinearLayout.LayoutParams layoutParams;

            // Detail
            detailView = new SpaceCompositeView(getActivity());
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            detailView.setLayoutParams(layoutParams);
            layout.addView(detailView);
            populator.populateDetail(detailView);

            // Trend
            trendView = new SpaceCompositeView(getActivity());
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            trendView.setLayoutParams(layoutParams);
            layout.addView(trendView);
            populator.populateTrend(trendView);

            // Picture
            pictureView = new SpaceCompositeView(getActivity());
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            pictureView.setLayoutParams(layoutParams);
            layout.addView(pictureView);
            populator.populatePicture(pictureView);

            // Networking
            networkingView = new SpaceCompositeView(getActivity());
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            networkingView.setLayoutParams(layoutParams);
            layout.addView(networkingView);
            populator.populateNetworking(networkingView);

            // Library
            libView = new SpaceCompositeView(getActivity());
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            libView.setLayoutParams(layoutParams);
            layout.addView(libView);
            populator.populateLibrary(libView);

            // Favorite
            favoriteView = new SpaceCompositeView(getActivity());
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            favoriteView.setLayoutParams(layoutParams);
            layout.addView(favoriteView);
            populator.populateFavorite(favoriteView);

            // Member
            memberView = new SpaceCompositeView(getActivity());
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            memberView.setLayoutParams(layoutParams);
            layout.addView(memberView);
            populator.populateMember(memberView);

            if (userInfo != null && groupInfo == null) {
                HashMap<String, String> params = new HashMap<String, String>();
                String curids = ((BaseActivity) getActivity()).mSession.getIds();
                params.put("curids", curids);
//                params.put("ids", userInfo.getU_ids());
                params.put("ids", curids);
                Task.queryUserSpaceSummary(params, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = JSON.parseObject(response);
                        userInfoSummary = JSON.parseObject(jsonObject.getString("usersummary"), UserInfoSummary.class);
                        userInfoSummary.setNewFocusCount(jsonObject.getString("newFocusCount"));
                        userInfoSummary.setAvatar(jsonObject.getString("headimage"));
                        populator.injectSummary(userInfoSummary);
                        populator.populateLogo(mSpaceLogo);
                        populator.populateTitle(mSpaceTitle);
                        populator.populateSpaceMainAction(mSpaceAction);
                        populator.populateAction3(mFollow);
                        populator.populateAction4(mBeFollowed, placeHolder);
                        populator.populateTrend(trendView);
                        populator.populatePicture(pictureView);
                        populator.populateNetworking(networkingView);
                        populator.populateLibrary(libView);
                        populator.populateFavorite(favoriteView);
                        populator.populateMember(memberView);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
            } else if (userInfo == null && groupInfo != null) {
                HashMap<String, String> params = new HashMap<String, String>();
                String ids = ((BaseActivity) getActivity()).mSession.getIds();
                params.put("ids", ids);
                params.put("spaceid", groupInfo.getZ_gid());
                Task.queryGroupSpaceSummary(params, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = JSON.parseObject(response);
                        groupInfoSummary = JSON.parseObject(jsonObject.getString("spacesummary"), GroupInfoSummary.class);
                        groupInfoSummary.setAdminInentiys(jsonObject.getString("adminInentiys"));
                        groupInfoSummary.setAdminChenghu(jsonObject.getString("admin_chenghu"));
                        groupInfoSummary.setCanApplyFlag(jsonObject.getBoolean("canApplyFlag"));
                        groupInfoSummary.setAvatar(jsonObject.getString("headimage"));
                        groupInfoSummary.setNeedApply(jsonObject.getBoolean("isactive") == null ? false : jsonObject.getBoolean("isactive"));
                        populator.injectSummary(groupInfoSummary);
                        populator.populateLogo(mSpaceLogo);
                        populator.populateTitle(mSpaceTitle);
                        populator.populateSpaceMainAction(mSpaceAction);
                        populator.populateAction3(mFollow);
                        populator.populateAction4(mBeFollowed, placeHolder);
                        populator.populateTrend(trendView);
                        populator.populatePicture(pictureView);
                        populator.populateNetworking(networkingView);
                        populator.populateLibrary(libView);
                        populator.populateFavorite(favoriteView);
                        populator.populateMember(memberView);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
            }

            return rootView;
        }
    }
}
