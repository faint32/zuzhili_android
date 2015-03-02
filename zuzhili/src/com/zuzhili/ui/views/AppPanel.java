package com.zuzhili.ui.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.hisun.phone.core.voice.util.Log4Util;
import com.zuzhili.R;
import com.zuzhili.bussiness.helper.CCPHelper;
import com.zuzhili.bussiness.utility.CCPUtil;
import com.zuzhili.framework.im.EmoticonUtil;

import com.zuzhili.ui.views.EmojiGrid.OnEmojiItemClickListener;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <p>Title: AppPanel</p>
 */
public class AppPanel extends LinearLayout implements AdapterView.OnItemClickListener
														, CCPFlipper.OnFlipperPageListener
														, CCPFlipper.OnCCPFlipperMeasureListener{
	
	private static int APP_PANEL_HEIGHT_MIN = 122;
	private static int APP_PANEL_HEIGHT_MAX = 179;
	
	/**
	 * default App panel emoji.
	 */
	public static String APP_PANEL_NAME_DEFAULT = "emoji";
	  
	private Context mContext;
	
	private WindowManager mWindowManager;
	
	private OnEmojiItemClickListener mItemClickListener;
	
	private CCPFlipper mFlipper;
	private CCPDotView mDotView;

	private ArrayList<EmojiGrid> mArrayList;

	public AppPanel(Context context) {
		this(context , null);
	}

	public AppPanel(Context context, AttributeSet attrs) {
		this(context, attrs , 0);
	}

	public AppPanel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		this.mContext = context;
		initAppPanel();
	}

	private void initAppPanel() {
		
		View.inflate(getContext(), R.layout.app_panel, this);
		
		mFlipper = (CCPFlipper) findViewById(R.id.app_panel_flipper);
		mDotView = (CCPDotView) findViewById(R.id.app_panel_dot);
		mFlipper.setOnFlipperListner(this);
		
		mArrayList = new ArrayList<EmojiGrid>();
		
		// Initialize the Emoji Panel
		initAppPanelChildView();
	}
	
	/**
	 * init AppPanel Child View 
	 * {@link EmojiGrid}
	 */
	private void initAppPanelChildView() {
		mFlipper.removeAllViews();
		mFlipper.setOnFlipperListner(this);
		mFlipper.setOnCCPFlipperMeasureListener(this);
		
		View panelDisplayView = findViewById(R.id.app_panel_display_view);
		LayoutParams layoutParams = (LayoutParams) panelDisplayView.getLayoutParams();
		if(getWindowDisplayMode() == 2) {
			layoutParams.height = CCPUtil.fromDPToPix(getContext(), APP_PANEL_HEIGHT_MIN);
		} else {
			layoutParams.height = CCPUtil.fromDPToPix(getContext(), APP_PANEL_HEIGHT_MAX);
		}
		panelDisplayView.setLayoutParams(layoutParams);
		
		if(mArrayList != null && mArrayList.size() > 0) {
			Iterator<EmojiGrid> iterator = mArrayList.iterator();
			while (iterator.hasNext()) {
				EmojiGrid emojiGrid = (EmojiGrid) iterator.next();
				if(emojiGrid != null) emojiGrid.releaseEmojiView();
			}
		}
		
		mArrayList.clear();
		setVisibility(View.GONE);
	}
	
	public void swicthToPanel(String panelName) {
		Log4Util.d(CCPHelper.DEMO_TAG, "AppPanel.swicthToPanel panelName: " + panelName);
		setVisibility(View.VISIBLE);
		if(TextUtils.isEmpty(panelName)) {
			return ;
		}
		
		if(APP_PANEL_NAME_DEFAULT.equals(panelName)) {
			doEmoji(mArrayList);
		}
	}
	
	/**
	 * @param mArrayList
	 */
	private void doEmoji(ArrayList<EmojiGrid> mArrayList) {
		
		mFlipper.removeAllViews();
		if(mArrayList == null || mArrayList.size() <= 0) {
			doEmoji();
			return;
		}
		
		Iterator<EmojiGrid> iterator = mArrayList.iterator();
		while (iterator.hasNext()) {
			EmojiGrid emojiGrid = (EmojiGrid) iterator.next();
			mFlipper.addView(emojiGrid, new LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
			emojiGrid.setOnEmojiItemClickListener(mItemClickListener);
		}
		
		if(mArrayList.size() <= 1) {
			mDotView.setVisibility(View.INVISIBLE);
			return;
		}
		
		mDotView.setVisibility(View.VISIBLE);
		mDotView.setDotCount(mArrayList.size());
		mDotView.setSelectedDot(mFlipper.getVisiableIndex() );
	}
	
	private void doEmoji() {
		
		mFlipper.removeAllViews();
        EmoticonUtil.getInstace().initEmoji(mContext);
		int pageCount = (int) Math.ceil(EmoticonUtil.getInstace().getEmojiSize() / 20 + 0.1);
		Log4Util.d(CCPHelper.DEMO_TAG, "AppPanel.doEmoji emoji total pageCount :" + pageCount);
		
		for (int i = 0; i < pageCount; i++) {
			EmojiGrid emojiGrid = new EmojiGrid(getContext());
			emojiGrid.initEmojiGrid(20, i, pageCount, i + 1, 7, getWidth());
			emojiGrid.setOnEmojiItemClickListener(mItemClickListener);
			mFlipper.addView(emojiGrid, new LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT));
			
			mFlipper.setInterceptTouchEvent(true);
			mArrayList.add(emojiGrid);
		}
		if(mArrayList.size() <= 1) {
			mDotView.setVisibility(View.INVISIBLE);
			return;
		}
		
		mDotView.setVisibility(View.VISIBLE);
		mDotView.setDotCount(pageCount);
		mDotView.setSelectedDot(0);
	}
	
	public void doChatTools() {
		setVisibility(View.GONE);
	}
	
	/**
	 */
	public boolean isPanelVisible() {
		
		return getVisibility() == View.VISIBLE ;
	}

	public void  setPanelGone() {
		mFlipper.removeAllViews();
		mDotView.removeAllViews();
		doChatTools();
	}
	/**
	 * release AppPanel view.
	 */
	public void releaseAppPanel() {
		setPanelGone();
		mFlipper.setOnFlipperListner(null);
		mFlipper.setOnCCPFlipperMeasureListener(null);
		
		if(mArrayList != null && mArrayList.size() > 0) {
			Iterator<EmojiGrid> iterator = mArrayList.iterator();
			while (iterator.hasNext()) {
				EmojiGrid emojiGrid = (EmojiGrid) iterator.next();
				if(emojiGrid != null) emojiGrid.releaseEmojiView();
			}
		}
		
		mArrayList.clear();
		mFlipper = null;
		mDotView = null;
		
	}
	
	private int getWindowDisplayMode() {
		if(mWindowManager == null ) {
			mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		}
		Display localDisplay = mWindowManager.getDefaultDisplay();
		return localDisplay.getWidth() < localDisplay.getHeight() ? 1 : 2;
	}
	
	 /**
     * Register a callback to be invoked when an item in this EmojiGird View has
     * been clicked.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnEmojiItemClickListener(OnEmojiItemClickListener listener) {
    	mItemClickListener = listener;
    }

    /**
     * @return The callback to be invoked with an item in this EmojiGird View has
     *         been clicked, or null id no callback has been set.
     */
    public final OnEmojiItemClickListener getOnEmojiItemClickListener() {
        return mItemClickListener;
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}


	@Override
	public void onFlipperPage(int startIndex, int finalIndex) {
		if(mDotView == null) {
			return;
		}
		if(finalIndex > mDotView.getDotCount()) {
			finalIndex = mDotView.getDotCount();
		}
		mDotView.setSelectedDot(finalIndex);
	}

	@Override
	public void onCCPFlipperMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
	}

}
