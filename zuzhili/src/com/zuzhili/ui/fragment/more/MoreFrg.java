package com.zuzhili.ui.fragment.more;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hisun.phone.core.voice.CCPCall;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.zuzhili.R;
import com.zuzhili.bussiness.helper.CCPHelper;
import com.zuzhili.controller.MoreAdapter;
import com.zuzhili.draftbox.DraftBoxActivity;
import com.zuzhili.framework.im.ITask;
import com.zuzhili.framework.im.TaskKey;
import com.zuzhili.framework.images.ImageCacheManager;
import com.zuzhili.model.MoreItem;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.activity.PhoneContactsActivity;
import com.zuzhili.ui.activity.loginreg.LoginActivity;
import com.zuzhili.ui.activity.more.AboutUsActivity;
import com.zuzhili.ui.activity.more.FeedBackActivity;
import com.zuzhili.ui.activity.more.ModifyPasActivity;
import com.zuzhili.ui.fragment.FixedOnActivityResultBugFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zuosl on 14-2-27.
 */
public class MoreFrg extends FixedOnActivityResultBugFragment {
    List<MoreItem> itemList;
    @ViewInject(R.id.lv_more)
    private ListView lvMore;//更多视图

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_more, null);
        activity = (BaseActivity) getActivity();
        ViewUtils.inject(this, view);
        initData();
        return view;
    }


    //更多设置

    //初始化数据
    private void initData() {
        itemList = new ArrayList<MoreItem>();
        int[] resIds = new int[]{R.drawable.more_icon_draft, R.drawable.more_icon_changepass,
                R.drawable.more_icon_invitation, R.drawable.more_icon_feedback,
                R.drawable.more_icon_aboutus, R.drawable.more_icon_logout};
        String[] itemNames = new String[]{getString(R.string.draftbox), getString(R.string.changepass), getString(R.string.invitefriends),
                getString(R.string.adviseback), getString(R.string.aboutus), getString(R.string.logout)};
        for (int i = 0; i < itemNames.length; i++) {
            MoreItem item = new MoreItem();
            item.setResourceId(resIds[i]);
            item.setItemName(itemNames[i]);
            itemList.add(item);
        }
        MoreAdapter moreAdapter = new MoreAdapter(activity, itemList, ImageCacheManager.getInstance().getImageLoader());
        lvMore.setAdapter(moreAdapter);

    }
    public static final String KEY_LAST_ALBUM_NAME = "publish.image.last.album.name";
    public static final String KEY_LAST_ALBUM_ID = "publish.image.last.album.id";
    private static final String FOLDER_ID = "the.last.publish.folder.id";
    private static final String FOLDER_NAME = "the.last.publish.folder.name";

    @OnItemClick(R.id.lv_more)
    public void itemClick(AdapterView<?> parent, View view, int position, long arg3) {
        switch (position) {

            case 0:// draft box
                Intent intent = new Intent();
                intent.setClass(activity, DraftBoxActivity.class);
                startActivity(intent);
                break;

            case 1:
                Intent it = new Intent(activity, ModifyPasActivity.class);
                startActivity(it);
                break;
            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("您选择好友后，我们将通知您的好友加入您的社区");

                // Add the buttons
                builder.setPositiveButton(R.string.next_step, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent it = new Intent(activity, PhoneContactsActivity.class);
                        startActivity(it);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();

                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                textView.setTextColor(getResources().getColor(R.color.light_blue));
                break;

            case 3:
                it = new Intent(activity, FeedBackActivity.class);
                startActivity(it);
                break;
            case 4:
                it = new Intent(activity, AboutUsActivity.class);
                startActivity(it);
                break;

            case 5:
                AlertDialog.Builder build = new AlertDialog.Builder(getActivity());

                build.setMessage("注销当前登录帐号?");

                build.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mSession.setAutoLogin(false);
//                        // 注销清空数据
//                        DBHelper helper = DBHelper.getInstance(activity);
//                        try {
//                            helper.getMessageTable().deleteIMMessageByListId(mSession.getListid());
//                            helper.getGroupInfoTable().deleteGroups(mSession.getListid());
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
                        SharedPreferences mPreference = PreferenceManager.getDefaultSharedPreferences(activity);
                        SharedPreferences.Editor editor = mPreference.edit();
                        editor.putString(KEY_LAST_ALBUM_NAME,"");
                        editor.putString(KEY_LAST_ALBUM_ID,"");
                        editor.putString(FOLDER_NAME,"");
                        editor.putString(FOLDER_ID,"");
                        editor.commit();

                        // clear list info when log out.
                        mSession.setListid(null);

                        Intent it = new Intent(activity,
                                LoginActivity.class);
                        it.putExtra("from", 0);
                        getActivity().startActivity(it);
                        getActivity().finish();
                        // 注销token

                        ITask iTask = new ITask(TaskKey.KEY_SDK_UNREGIST);
                        addTask(iTask);
                    }
                });
                build.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                AlertDialog aDialog = build.create();
                aDialog.show();

                TextView textview = (TextView) aDialog.findViewById(android.R.id.message);
                textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                textview.setTextColor(getResources().getColor(R.color.light_blue));
                break;
        }
    }

    @Override
    protected void handleTaskBackGround(ITask iTask) {
        super.handleTaskBackGround(iTask);
        if (iTask.getKey() == TaskKey.KEY_SDK_UNREGIST) {   // 与云通讯断开连接
            CCPHelper.getInstance().release();
            CCPCall.shutdown();
        }
    }
}
