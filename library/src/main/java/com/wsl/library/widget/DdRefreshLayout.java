package com.wsl.library.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wsl on 17/4/18.
 */

public class DdRefreshLayout extends ViewGroup implements NestedScrollingParent {

    private static final String TAG = DdRefreshLayout.class.getSimpleName();

    @NonNull
    private View mContentView;
    @Nullable
    private View mHeaderView;
    private int mHeaderHeight;
    @Nullable
    private View mFooterView;
    private int mFooterHeight;

    private DdScrollerHelper mScrollerHelper;
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private boolean mSkipNestedPreScroll;

    private boolean mChildFirstItemFullVisible;
    private boolean mChildLastItemFullVisible;

    public DdRefreshLayout(Context context) {
        this(context, null);
    }

    public DdRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DdRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        mScrollerHelper = new DdScrollerHelper(this);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        mChildFirstItemFullVisible = !ViewCompat.canScrollVertically(target, -1);
        if(!mChildFirstItemFullVisible) {
            mChildLastItemFullVisible = !ViewCompat.canScrollVertically(target, 1);
        }
//        Log.d(TAG, "onStartNestedScroll firstItemFullVisible =" + mChildFirstItemFullVisible + "---lastItemFullVisible: " + mChildLastItemFullVisible);

//        boolean canScrollDown = ViewCompat.canScrollVertically(target, -1);
//        boolean canScrollUp = ViewCompat.canScrollVertically(target, 1);
//        Log.d(TAG, "onStartNestedScroll canScrollDown =" + canScrollDown + "---canScrollUp: " + canScrollUp);


        boolean started = (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0
                && (mChildFirstItemFullVisible || mChildLastItemFullVisible);

        Log.d(TAG , "onStartNestedScroll ---started: " + started);

        return started;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        super.onNestedScrollAccepted(child, target, axes);
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onNestedPreScroll(View child, int dx, int dy, int[] consumed) {
        Log.d(TAG , "onNestedPreScroll ---dy: " + dy);
        //consumed[1]初始值为0
        if (dy != 0 && !mSkipNestedPreScroll) {
            int min, max;
            if (dy < 0 && mChildFirstItemFullVisible) {
                // We're scrolling down
                if(mChildFirstItemFullVisible) {

                }
                min = 0;
                max = getHeight();

                Log.d(TAG , "onNestedPreScroll ---scroll down " + dy);
                consumed[1] = mScrollerHelper.scroll(dy, min, max);
            } else if(dy > 0){
                // We're scrolling up
                if(mChildLastItemFullVisible) {

                }
                if(mChildFirstItemFullVisible) {
                    //先向下滑动，再向上滑动
                }
                min = -getHeight();
                max = 0;

                Log.d(TAG , "onNestedPreScroll ---scroll up " + dy);
                consumed[1] = mScrollerHelper.scroll(dy, min, max);
            }

//            consumed[1] = mScrollerHelper.scroll(dy, min, max);
        }
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        Log.d(TAG, "onNestedScroll  [" + dxConsumed + "," + dyConsumed + "," + dxUnconsumed + "," + dyUnconsumed + "]");
//        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return true;
//        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return true;
//        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public void onStopNestedScroll(View child) {
        Log.d(TAG , "onStopNestedScroll ----------");
        mNestedScrollingParentHelper.onStopNestedScroll(child);
        super.onStopNestedScroll(child);
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
}
