package com.wsl.library.widget.demo;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wsl on 17/4/18.
 */

public class TestLayout extends ViewGroup{

    private static final String TAG = TestLayout.class.getSimpleName();

    public TestLayout(Context context) {
        super(context);
    }

    public TestLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        if(count == 1) {
            View child = getChildAt(0);
            int padding = 10;
            child.layout(l + padding, t+padding, r-padding, b-padding);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int actionMasked = event.getActionMasked();
        Log.d("debug", TAG + "---onTouchEvent event: " + actionMasked);
//        if (actionMasked == MotionEvent.ACTION_DOWN) {
//            return false;
//        }
        return true;
//        return super.onTouchEvent(event);
    }

}
