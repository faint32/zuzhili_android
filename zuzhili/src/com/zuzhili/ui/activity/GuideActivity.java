package com.zuzhili.ui.activity;

import com.zuzhili.bussiness.utility.Constants;
import com.zuzhili.ui.activity.loginreg.LoginActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;

import com.zuzhili.R;
import java.util.ArrayList;
import java.util.List;

/**
 * kj
 */
public class GuideActivity extends Activity {

    private ViewPager mViewPager;
    private List<View> views;
    private LinearLayout guideButtom;
    private Button goLogin;
    private SharedPreferences mPreference;
    private int screenHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_guide);

        mViewPager = (ViewPager)findViewById(R.id.guide_viewpager);
        mViewPager.setOnPageChangeListener(new GuidePageChangeListener());


        //将要分页显示的View装入数组中
        LayoutInflater mLi = LayoutInflater.from(this);
        View view1 = mLi.inflate(R.layout.guide_first, null);
        View view2 = mLi.inflate(R.layout.guide_second, null);
        View view3 = mLi.inflate(R.layout.guide_third, null);

        guideButtom = (LinearLayout)view3.findViewById(R.id.guide_to_login);
        goLogin=(Button)view3.findViewById(R.id.btn_go_login);
        WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        screenHeight = wm.getDefaultDisplay().getHeight();

        mPreference = PreferenceManager.getDefaultSharedPreferences(this);
        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sp 登陆
                SharedPreferences.Editor editor=mPreference.edit();
                editor.putBoolean(Constants.EXTRA_GUIDE,true);
                editor.commit();

                Intent it = new Intent(GuideActivity.this, LoginActivity.class);
                startActivity(it);
            }
        });

        //每个页面的view数据
        views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);

        //填充ViewPager的数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public void destroyItem(View container, int position, Object object) {
                ((ViewPager)container).removeView(views.get(position));
            }



            @Override
            public Object instantiateItem(View container, int position) {
                ((ViewPager)container).addView(views.get(position));
                return views.get(position);
            }
        };

        mViewPager.setAdapter(mPagerAdapter);


    }

    class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int position) {
            if(position == views.size()-1){
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        guideButtom.setVisibility(View.VISIBLE);
                        Animation trans = new TranslateAnimation(0, 0, screenHeight, 0);
                        trans.setFillAfter(true);
                        trans.setDuration(500);
                        guideButtom.setAnimation(trans);
                    }
                }, 800);
            }
        }
    }
}
