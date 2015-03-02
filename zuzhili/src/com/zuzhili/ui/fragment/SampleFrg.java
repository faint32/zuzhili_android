package com.zuzhili.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zuzhili.R;
import com.zuzhili.framework.utils.Utils;
import com.zuzhili.ui.activity.BaseActivity;

/**
 * Created by liutao on 14-3-4.
 */
public class SampleFrg extends Fragment {

    private static final String ARG_TEXT = "net.simonvt.menudrawer.samples.SampleFragment.text";

    public static SampleFrg newInstance(String text) {
        SampleFrg f = new SampleFrg();

        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sample, container, false);
        ((TextView) v.findViewById(R.id.text)).setText(getArguments().getString(ARG_TEXT));
        return v;
    }
}