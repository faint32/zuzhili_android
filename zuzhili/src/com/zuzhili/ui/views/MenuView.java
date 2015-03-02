package com.zuzhili.ui.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zuzhili.R;

/**
 * Created by liutao on 14-8-28.
 */
public class MenuView extends RelativeLayout {

    private Drawable backgroundDrawable;

    private float textSize;

    private ColorStateList textColor;

    private boolean isShowDividers;

    private Drawable dividerDrawable;

    private TextView nameTxt;

    public MenuView(Context context) {
        this(context, null);
    }

    public MenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        // Attribute initialization
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MenuView,
                defStyle, 0);

        backgroundDrawable = a.getDrawable(R.styleable.MenuView_backgroundDrawable);
        if (backgroundDrawable != null) {
            backgroundDrawable.setCallback(this);
        }

        textSize = a.getDimension(R.styleable.MenuView_textSize, 0);
        textColor = a.getColorStateList(R.styleable.MenuView_textColor);

        isShowDividers = a.getBoolean(R.styleable.MenuView_showDivider, isShowDividers);
        dividerDrawable = a.getDrawable(R.styleable.MenuView_dividerDrawable);

        a.recycle();

        RelativeLayout menu = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.menu, this, true);
        menu.setBackgroundDrawable(backgroundDrawable);
        nameTxt = (TextView) menu.findViewById(R.id.menuTxt);
        nameTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        nameTxt.setTextColor(textColor);

        View divider = menu.findViewById(R.id.menu_divider);
        divider.setVisibility(isShowDividers ? View.VISIBLE : View.GONE);
        if (dividerDrawable != null) {
            dividerDrawable.setCallback(this);
            divider.setBackgroundDrawable(dividerDrawable);
        }

    }

    public void setName(String name) {
        nameTxt.setText(name);
    }

}
