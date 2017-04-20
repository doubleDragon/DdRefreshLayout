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

    private DdRefreshListener mPullListener;
    private DdLoadListener mLoadListener;


    //下拉刷新的临界值,必须大于或者等于header高度
    private int mPullTriggerHeight;
    //上拉加载的临界值,必须大于或者等于footer高度
    private int mLoadTriggerHeight;
    private int mState = STATE_ORIGIN;

    private boolean mDebug = false;

    public DdRefreshLayout(Context context) {
        this(context, null);
    }

    public DdRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DdRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DdRefreshLayout);
        mPullTriggerHeight = a.getInt(R.styleable.DdRefreshLayout_dd_pull_trigger_height, 0);
        mLoadTriggerHeight = a.getInt(R.styleable.DdRefreshLayout_dd_load_trigger_height, 0);
        a.recycle();
    }



    private void setStateAndCallback(int state) {
        setState(state);
        if(state > 0) {
            callbackPull(state);
        } else {
            callbackLoad(state);
        }
    }

    /**
     * 下拉刷新回调给child, child刷新view
     */
    private void callbackPull(int state) {
        if(state == STATE_PULL_WAIT && mPullListener != null) {
            mPullListener.onRefresh();
        }
        final View header = getHeaderView();
        if(header == null || !(header instanceof DdTrigger)) {
            return;
        }
        DdTrigger ddTrigger = (DdTrigger) header;
        switch (state) {
            case STATE_PULL_DRAG:
                ddTrigger.onDrag(mPullTriggerHeight, getCurrentOffset(), getHeight());
                break;
            case STATE_PULL_RETURN:
                ddTrigger.onReturn();
                break;
            case STATE_PULL_WAIT:
                ddTrigger.onWait();
                break;
            case STATE_PULL_RESET:
                ddTrigger.onReset();
                break;
        }
    }

    /**
     * 上拉回调给child, child刷新view
     */
    private void callbackLoad(int state) {
        if(state == STATE_LOAD_WAIT && mLoadListener != null) {
            mLoadListener.onLoadMore();
        }
        final View footer = getFooterView();
        if(footer == null || !(footer instanceof DdTrigger)) {
            return;
        }
        DdTrigger ddTrigger = (DdTrigger) footer;
        switch (state) {
            case STATE_LOAD_DRAG:
                ddTrigger.onDrag(mLoadTriggerHeight, getCurrentOffset(), getHeight());
                break;
            case STATE_LOAD_RETURN:
                ddTrigger.onReturn();
                break;
            case STATE_LOAD_WAIT:
                ddTrigger.onWait();
                break;
            case STATE_LOAD_RESET:
                ddTrigger.onReset();
                break;
        }
    }

    private void setState(int state) {
        this.mState = state;
        if(mDebug) {
            dumpState(state);
        }
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mPullTriggerHeight < getHeaderHeight()) {
            mPullTriggerHeight = getHeaderHeight();
        }
        if(mLoadTriggerHeight < getFooterHeight()) {
            mLoadTriggerHeight = getFooterHeight();
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
                setStateAndCallback(STATE_PULL_DRAG);
                return;
            }
            if(offset > mPullTriggerHeight) {
                setStateAndCallback(STATE_PULL_RETURN);
            } else if(offset == mPullTriggerHeight) {
                setStateAndCallback(STATE_PULL_WAIT);
            } else {
                setStateAndCallback(STATE_PULL_RESET);
            }
        } else {
            if(isNestedScrollStart()) {
                //正在下拉
                setStateAndCallback(STATE_LOAD_DRAG);
                return;
            }
            if(-offset > mLoadTriggerHeight) {
                setStateAndCallback(STATE_LOAD_RETURN);
            } else if(-offset == mLoadTriggerHeight) {
                setStateAndCallback(STATE_LOAD_WAIT);
            } else {
                setStateAndCallback(STATE_LOAD_RESET);
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
            if (Math.abs(offset) > mPullTriggerHeight ||
                    Math.abs(offset) > mLoadTriggerHeight) {
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

    public void setRefreshListener(DdRefreshListener listener) {
        this.mPullListener = listener;
    }

    public void setLoadListener(DdLoadListener listener) {
        this.mLoadListener = listener;
    }

    public void setRefresh(boolean refresh) {
        if(refresh) {
            if(mState == STATE_ORIGIN) {
                //自动滚动刷新的临界点
                autoScroll(getHeaderHeight());
            }
        } else {
            autoScroll(-getCurrentOffset());
        }
    }

    public void setLoad(boolean load) {
        if(load) {
            if(mState == STATE_ORIGIN) {
                //自动滚动加载的临界点
                autoScroll(-getFooterHeight());
            }
        } else {
            autoScroll(-getCurrentOffset());
        }
    }

    private void dumpState(int state) {
        StringBuilder sb = new StringBuilder();
        sb.append("current state = ");
        switch(state) {
            case STATE_PULL_DRAG:
                sb.append("pull drag");
                break;
            case STATE_PULL_RETURN:
                sb.append("pull return");
                break;
            case STATE_PULL_WAIT:
                sb.append("pull wait");
                break;
            case STATE_PULL_RESET:
                sb.append("pull reset");
                break;
            case STATE_LOAD_DRAG:
                sb.append("load drag");
                break;
            case STATE_LOAD_RETURN:
                sb.append("load return");
                break;
            case STATE_LOAD_WAIT:
                sb.append("load wait");
                break;
            case STATE_LOAD_RESET:
                sb.append("load reset");
                break;
        }
        logD(sb.toString());
    }


//    private void setDebug(boolean debug) {
//        this.mDebug = debug;
//    }

    private void logD(String text) {
        if(mDebug) {
            Log.d(TAG, text);
        }
    }
}
