package com.zuzhili.ui.views;

import com.zuzhili.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class CustomDialog extends Dialog {
	protected Context mContext;
	protected int posX;
	protected int posY;
	private Window window;
	private View mContentView;
	private InputMethodManager imm;
	
	public CustomDialog(Context context, int style) {
		super(context, style);
		mContext = context;
	}
	
	@Override
	public void dismiss() {
		if(imm != null){
			imm.hideSoftInputFromWindow(mContentView.getWindowToken(), 0);
		}
		super.dismiss();
	}
	
	public void setDisplayView(View view, InputMethodManager imm) {
		if (imm != null) {
			this.imm = imm;
		}
		mContentView = view;
		setContentView(view);	// 设置对话框的布局
	}

	public void setDisplayView(String title, String content, String leftBtnTxt,
			String rightBtnTxt) {
		LayoutInflater factory = LayoutInflater.from(mContext);
		final View v = factory.inflate(R.layout.confirm_dialog, null);
		mContentView = v;
		TextView txtContent = (TextView) v.findViewById(R.id.content);
		if (content != null) {
			txtContent.setText(content);
		} else {
			txtContent.setVisibility(View.GONE);
		}
		Button left = (Button) v.findViewById(R.id.ok);
		Button right = (Button) v.findViewById(R.id.cancel);
		if (leftBtnTxt != null) {
			left.setText(leftBtnTxt);
		} else {
			left.setVisibility(View.GONE);
		}
		if (rightBtnTxt != null) {
			right.setText(rightBtnTxt);
		} else {
			right.setVisibility(View.GONE);
		}
		setContentView(v);
		setProperty();
	}

    public void setList(String[] items, final OnClickListener listener) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ListView singleChoiceListView = new ListView(mContext);
        addContentView(singleChoiceListView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        singleChoiceListView.setCacheColorHint(Color.argb(0, 0, 0, 0));// 把listView的缓存色为透明
        // 数组适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.simple_list_item, items);

        singleChoiceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                listener.onClick(CustomDialog.this, position);
            }
        });
        singleChoiceListView.setAdapter(adapter);
        setProperty();
    }


    public void setLBtnListner(View.OnClickListener listener) {
		if (mContentView != null) {
			Button left = (Button) mContentView.findViewById(R.id.ok);
			left.setOnClickListener(listener);
		}
	}

	public void setRBtnListner(View.OnClickListener listener) {
		if (mContentView != null) {
			Button right = (Button) mContentView.findViewById(R.id.cancel);
			right.setOnClickListener(listener);
		}
	}

	// 要显示这个对话框，只要创建该类对象．然后调用该函数即可．
	public void setProperty() {
		window = getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		WindowManager m = ((Activity) mContext).getWindowManager();
		Display d = m.getDefaultDisplay();
		wl.width = (int) (d.getWidth() * 0.95);
	}

	public void setPropertyTop() {
		window = getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		WindowManager m = ((Activity) mContext).getWindowManager();
		Display d = m.getDefaultDisplay();
		DisplayMetrics dMetrics = new DisplayMetrics();
		d.getMetrics(dMetrics);
		wl.y = (int) (100 * dMetrics.density);
		window.setAttributes(wl);
	}

	public void setProperty(float height) {
		window = getWindow();// 　　　
		WindowManager.LayoutParams wl = window.getAttributes();
		WindowManager m = ((Activity) mContext).getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		wl.alpha = 0.5f;
		window.setAttributes(wl);
	}

	public void setPropertyTop(int x, int y, double ratio) {
		window = getWindow();// 　　　
		WindowManager.LayoutParams wl = window.getAttributes();
		WindowManager m = ((Activity) mContext).getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		wl.width = (int) (d.getWidth() * ratio); // 宽度设置为屏幕的0.95
		wl.x = x;
		wl.y = y;
		wl.gravity = Gravity.TOP;
		window.setAttributes(wl);
	}
	
	public void setPropertyCenter(double centi) {
		window = getWindow();
		WindowManager.LayoutParams wl = window.getAttributes();
		WindowManager m = ((Activity) mContext).getWindowManager();
		Display d = m.getDefaultDisplay();
		wl.width = (int) (d.getWidth() * centi);
		wl.gravity = Gravity.CENTER;
		window.setAttributes(wl);
	}
}