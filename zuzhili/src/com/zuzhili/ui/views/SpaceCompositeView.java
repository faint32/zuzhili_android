package com.zuzhili.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zuzhili.R;

/**
 * Created by fanruikang on 14-8-18.
 */
public class SpaceCompositeView extends RelativeLayout {

    private ImageView mIcon;
    private TextView mTitle;
    private TextView mSubTitle;
    private ImageView mArrow;

    public SpaceCompositeView(Context context) {
        this(context, null);
    }

    public SpaceCompositeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpaceCompositeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater.from(context).inflate(R.layout.space_composite_view, this, true);

        mIcon = (ImageView) findViewById(R.id.space_menu_icon);
        mTitle = (TextView) findViewById(R.id.space_menu_title);
        mSubTitle = (TextView) findViewById(R.id.space_menu_subtitle);
        mArrow = (ImageView) findViewById(R.id.space_menu_arrow);
    }

    public void update(int icon, String title, String subTitle) {
        mIcon.setImageResource(icon);
        mTitle.setText(title);

        if (subTitle == null) {
            mSubTitle.setVisibility(GONE);
        } else {
            mSubTitle.setText(subTitle);
            mSubTitle.setVisibility(VISIBLE);
        }
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        findViewById(R.id.realRoot).setOnClickListener(l);
    }
}
