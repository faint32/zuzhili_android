package com.zuzhili.framework.images;

import android.widget.AbsListView;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by liutao on 14-6-20.
 */
public class PauseOnScrollListener implements AbsListView.OnScrollListener {

    private ImageLoader.ImageContainer imageContainer;

    private final boolean pauseOnScroll;
    private final boolean pauseOnFling;
    private final AbsListView.OnScrollListener externalListener;

    /**
     * Constructor
     *
     * @param imageContainer   {@linkplain ImageLoader} instance for controlling
     * @param pauseOnScroll
     * @param pauseOnFling
     */
    public PauseOnScrollListener(ImageLoader.ImageContainer imageContainer, boolean pauseOnScroll, boolean pauseOnFling) {
        this(imageContainer, pauseOnScroll, pauseOnFling, null);
    }

    /**
     * Constructor
     *
     * @param imageContainer    {@linkplain ImageLoader} instance for controlling
     * @param pauseOnScroll
     * @param pauseOnFling
     * @param customListener Your custom {@link OnScrollListener} for {@linkplain AbsListView list view} which also
     *                       will be get scroll events
     */
    public PauseOnScrollListener(ImageLoader.ImageContainer imageContainer, boolean pauseOnScroll, boolean pauseOnFling,
                                 AbsListView.OnScrollListener customListener) {
        this.imageContainer = imageContainer;
        this.pauseOnScroll = pauseOnScroll;
        this.pauseOnFling = pauseOnFling;
        externalListener = customListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                if (pauseOnScroll) {
                    imageContainer.cancelRequest();
                }
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                if (pauseOnFling) {
                    imageContainer.cancelRequest();
                }
                break;
        }
        if (externalListener != null) {
            externalListener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (externalListener != null) {
            externalListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }
}
