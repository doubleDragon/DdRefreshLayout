package com.wsl.library.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * 下拉和上拉的容器，只提供基础的scroll
 * 需要在子类中自己实现状态
 * Created by wsl on 17/4/18.
 */

public class DdNestedLayout extends ViewGroup implements NestedScrollingParent {

    private static final String TAG = DdNestedLayout.class.getSimpleName();

    private View mContentView;
    @Nullable
    private View mHeaderView;
    private int mHeaderHeight;
    @Nullable
    private View mFooterView;
    private int mFooterHeight;



    private DdNestedScrollerHelper<DdNestedLayout> mScrollerHelper;
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private boolean mNestedScrollStart;

    public DdNestedLayout(Context context) {
        this(context, null);
    }

    public DdNestedLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DdNestedLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mScrollerHelper = new DdNestedScrollerHelper<>(this);
        mScrollerHelper.setListener(new DdNestedScrollerHelper.Listener() {

            @Override
            public void onOffsetChange(int offset) {
                onCurrentOffsetChange(offset);

            }
        });
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    }

    void onCurrentOffsetChange(int offset) {
        //do nothing
    }

    public int getFooterHeight() {
        return mFooterHeight;
    }

    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    View getHeaderView(){
        return mHeaderView;
    }

    View getFooterView(){
        return mFooterView;
    }


    public boolean isNestedScrollStart() {
        return mNestedScrollStart;
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        mNestedScrollStart = true;
    }

    /**
     * @param dy       dy<0 scrolling down, dy>0 scrolling up
     * @param consumed 初始值为0
     */
    @Override
    public void onNestedPreScroll(View child, int dx, int dy, int[] consumed) {
        if (dy != 0 && getCurrentOffset() != 0) {
            //如果有偏移量则优先child处理scroll, 场景是先下后上或者先上后下的滑动场景
            int min, max;
            if (dy < 0) {
                //scrolling down
                min = -getHeight();
                max = 0;
            } else {
                //scrolling up
                min = 0;
                max = getHeight();
            }
            consumed[1] = scroll(dy, min, max);
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (dyUnconsumed != 0) {
            //child没消耗完，refreshLayout接着处理
            int min, max;
            if (dyUnconsumed < 0) {
                //scrolling down
                min = 0;
                max = getHeight();
            } else {
                min = -getHeight();
                max = 0;
            }
            scroll(dyUnconsumed, min, max);
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
//        super.onNestedFling(target, velocityX, velocityY, consumed);
        return false;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onStopNestedScroll(View child) {
        mNestedScrollingParentHelper.onStopNestedScroll(child);
        mNestedScrollStart = false;
    }

    int getCurrentOffset() {
        return mScrollerHelper.getTopAndBottomOffset();
    }

    int scroll(int dy, int min, int max) {
        return mScrollerHelper.scroll(dy, min, max);
    }

    /**
     * @param dy 移动的距离,而不是某个具体的点
     */
    void autoScroll(int dy) {
        mScrollerHelper.autoScroll(dy);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mContentView = findViewById(R.id.dd_refresh_content);
        if (mContentView == null) {
            throw new RuntimeException("content must not null");
        }
        mHeaderView = findViewById(R.id.dd_refresh_header);
        mFooterView = findViewById(R.id.dd_refresh_footer);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //measure content
        final View content = mContentView;
        measureChild(content, widthMeasureSpec, heightMeasureSpec);
        //measure header
        if (mHeaderView != null) {
            final View header = mHeaderView;
            measureChild(header, widthMeasureSpec, heightMeasureSpec);
            mHeaderHeight = header.getMeasuredHeight();
        }

        //measure footer
        if (mFooterView != null) {
            final View footer = mFooterView;
            measureChild(footer, widthMeasureSpec, heightMeasureSpec);
            mFooterHeight = footer.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getWidth();
        int height = getHeight();

        //layout header
        if (mHeaderView != null) {
            mHeaderView.layout(0, -mHeaderHeight, width, 0);
        }

        //layout content
        mContentView.layout(0, 0, width, height);

        //layout footer
        if (mFooterView != null) {
            mFooterView.layout(0, height, width, height + mFooterHeight);
        }
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    private void logD(String text) {
        if(DdConfig.DEBUG) {
            android.util.Log.d(TAG, text);
        }
    }
}
