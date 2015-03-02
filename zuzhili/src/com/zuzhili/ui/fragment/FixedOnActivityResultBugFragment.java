package com.zuzhili.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

import com.zuzhili.R;
import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.db.DBHelper;
import com.zuzhili.framework.Session;
import com.zuzhili.framework.im.CCPIntentUtils;
import com.zuzhili.framework.im.ITask;
import com.zuzhili.framework.im.ThreadPoolManager;
import com.zuzhili.ui.activity.BaseActivity;
import com.zuzhili.ui.views.CustomSearchView;

/**
 * User: liutao
 * Date: 13-9-8
 * <p/>
 * see https://code.google.com/p/android/issues/detail?id=40537
 * <p/>
 * nested fragment wont receive onActivityResult, 2013.9.8
 */
public class FixedOnActivityResultBugFragment extends Fragment implements BaseActivity.TimeToShowActionBarCallback, ThreadPoolManager.OnTaskDoingLinstener {

    private final SparseIntArray mRequestCodes = new SparseIntArray();
    protected Session mSession;
    protected BaseActivity activity;
    protected LayoutInflater mLayoutInflater;
    protected DBHelper dbHelper;

    protected OnFrgmentInstantiationListener onFrgmentInstantiationListener;

    protected OnActionBarUpdateListener onActionBarUpdateListener;

    public interface OnFrgmentInstantiationListener {
        public void onFragmentInstantiation(FixedOnActivityResultBugFragment baseFragment);
    }

    public void setOnFrgmentInstantiationListener(OnFrgmentInstantiationListener onFrgmentInstantiationListener) {
        this.onFrgmentInstantiationListener = onFrgmentInstantiationListener;
    }

    public interface OnActionBarUpdateListener {
        public void shouldUpdateActionBar();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSession = getSession();
        activity = (BaseActivity) getActivity();
        dbHelper = activity.getDbHelper();
        mLayoutInflater = activity.getLayoutInflater();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
    	super.onResume();
    	this.mSession = getSession();
        activity = (BaseActivity) getActivity();
        dbHelper = activity.getDbHelper();
        mLayoutInflater = activity.getLayoutInflater();
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	super.onViewCreated(view, savedInstanceState);
    	this.mSession = getSession();
        activity = (BaseActivity) getActivity();
        dbHelper = activity.getDbHelper();
        mLayoutInflater = activity.getLayoutInflater();
    }
    
    /**
     * Registers request code (used in
     * {@link #startActivityForResult(android.content.Intent, int)}).
     *
     * @param requestCode the request code.
     * @param id          the fragment ID (can be {@link android.support.v4.app.Fragment#getId()} of
     *                    {@link android.support.v4.app.Fragment#hashCode()}).
     */
    public void registerRequestCode(int requestCode, int id) {
        mRequestCodes.put(requestCode, id);
    }// registerRequestCode()

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (getParentFragment() instanceof FixedOnActivityResultBugFragment) {
            ((FixedOnActivityResultBugFragment) getParentFragment()).registerRequestCode(
                    requestCode, hashCode());
            getParentFragment().startActivityForResult(intent, requestCode);
        } else
            super.startActivityForResult(intent, requestCode);
    }// startActivityForResult()

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!checkNestedFragmentsForResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }// onActivityResult()

    @Override
    public boolean showCustomActionBar() {
        return true;
    }

    /**
     * Checks to see whether there is any children fragments which has been
     * registered with {@code requestCode} before. If so, let it handle the
     * {@code requestCode}.
     *
     * @param requestCode the code from {@link #onActivityResult(int, int, android.content.Intent)}.
     * @param resultCode  the code from {@link #onActivityResult(int, int, android.content.Intent)}.
     * @param data        the data from {@link #onActivityResult(int, int, android.content.Intent)}.
     * @return {@code true} if the results have been handed over to some child
     *         fragment. {@code false} otherwise.
     */
    protected boolean checkNestedFragmentsForResult(int requestCode,
                                                    int resultCode, Intent data) {
        final int id = mRequestCodes.get(requestCode);
        if (id == 0)
            return false;

        mRequestCodes.delete(requestCode);

        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments == null)
            return false;

        for (Fragment fragment : fragments) {
            if (fragment.hashCode() == id) {
                fragment.onActivityResult(requestCode, resultCode, data);
                return true;
            }
        }

        return false;
    }// checkNestedFragmentsForResult()

    /**
     * 获取Session对象
     * @return
     */
    public Session getSession() {
        if(getActivity() != null) {
            return ((BaseActivity)getActivity()).getSession();
        } else {
            return null;
        }
    }
    
    public void addTask(ITask iTask){
        ThreadPoolManager.getInstance().setOnTaskDoingLinstener(this);
        ThreadPoolManager.getInstance().addTask(iTask);
    }

    @Override
    public void doTaskBackGround(ITask iTask) {
        handleTaskBackGround(iTask);
    }

    protected void handleTaskBackGround(ITask iTask) {

    }
}