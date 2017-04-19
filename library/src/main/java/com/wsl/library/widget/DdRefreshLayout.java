package com.wsl.library.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 在NestedLayout基础上增加各种状态
 * Created by wsl on 17/4/19.
 */

public class DdRefreshLayout extends DdNestedLayout {

    private static final String TAG = DdRefreshLayout.class.getSimpleName();

    //初始状态
    private static final int STATE_ORIGIN = 0;

    //正在下拉
    private static final int STATE_PULL_DRAG = 1;
    //下拉松手后偏移量达到下拉刷新条件，DRAG->WAIT
    private static final int STATE_PULL_RETURN = 2;
    //下拉完成后，正在刷新
    private static final int STATE_PULL_WAIT = 3;
    //下拉刷新完成，WAIT->ORIGIN
    private static final int STATE_PULL_RESET = 4;
    //下拉刷新的2种情况分别是:0->1->0,  0-1-2-3-4-0

    //正在上拉
    private static final int STATE_LOAD_DRAG = -1;
    //上拉松手后偏移量达到加载更多条件，DRAG->WAIT
    private static final int STATE_LOAD_RETURN = -2;
    //上拉完成后，正在加载更多
    private static final int STATE_LOAD_WAIT = -3;
    //加载完成，回滚状态
    private static final int STATE_LOAD_RESET = -4;
    //上拉加载更多的2种情况分别是:0->(-1)->0,  0->(-1)->(-2)->(-3)->(-4)->0


    //下拉刷新的临界值,必须大于或者等于header高度
    private int mPullCriticalValue;
    //上拉加载的临界值,必须大于或者等于footer高度
    private int mLoadCriticalValue;
    private int mState = STATE_ORIGIN;

    public DdRefreshLayout(Context context) {
        this(context, null);
    }

    public DdRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DdRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DdRefreshLayout);
        mPullCriticalValue = a.getInt(R.styleable.DdRefreshLayout_dd_pull_critical, 0);
        mLoadCriticalValue = a.getInt(R.styleable.DdRefreshLayout_dd_load_critical, 0);
        a.recycle();
    }

    void setState(int state) {
        this.mState = state;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mPullCriticalValue < getHeaderHeight()) {
            mPullCriticalValue = getHeaderHeight();
        }
        if(mLoadCriticalValue < getFooterHeight()) {
            mLoadCriticalValue = getFooterHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    void onCurrentOffsetChange(int offset) {
        if(offset == 0) {
            setState(STATE_ORIGIN);
        } else if(offset > 0) {
            if(isNestedScrollStart()) {
                //正在下拉
                setState(STATE_PULL_DRAG);
                return;
            }
            if(offset > mPullCriticalValue) {
                setState(STATE_PULL_RETURN);
            } else if(offset == mPullCriticalValue) {
                setState(STATE_PULL_WAIT);
            } else {
                setState(STATE_PULL_RESET);
            }
        } else {
            if(isNestedScrollStart()) {
                //正在下拉
                setState(STATE_LOAD_DRAG);
                return;
            }
            if(-offset > mLoadCriticalValue) {
                setState(STATE_LOAD_RETURN);
            } else if(-offset == mLoadCriticalValue) {
                setState(STATE_LOAD_WAIT);
            } else {
                setState(STATE_LOAD_RESET);
            }
        }
    }

    private boolean isReturnOrResetState() {
        return mState > STATE_PULL_DRAG ||
                mState < STATE_LOAD_DRAG;
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        boolean start = super.onStartNestedScroll(child, target, nestedScrollAxes);
        return start && !isReturnOrResetState();
    }

    @Override
    public void onStopNestedScroll(View child) {
        super.onStopNestedScroll(child);
        int offset = getCurrentOffset();
        if (offset != 0) {
            if (Math.abs(offset) > mPullCriticalValue ||
                    Math.abs(offset) > mLoadCriticalValue) {
                int newOffset;
                if (offset > 0) {
                    //滚动到下拉刷新状态, 向上回滚所以是个负值
                    newOffset = -(offset - getHeaderHeight());
                } else {
                    //滚动到加载更多状态
                    newOffset = -offset - getFooterHeight();
                }
                autoScroll(newOffset);
                return;
            }
            autoScroll(-offset);
        }
    }

    private void logD(String text) {
        Log.d(TAG, text);
    }
}
