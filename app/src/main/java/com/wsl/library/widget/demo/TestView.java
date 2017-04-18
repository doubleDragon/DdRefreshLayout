package com.wsl.library.widget.demo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by wsl on 17/4/18.
 */

public class TestView extends View {

    private static final String TAG = TestView.class.getSimpleName();

    public TestView(Context context) {
        super(context);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int actionMasked = event.getActionMasked();
        Log.d("debug", TAG + "---onTouchEvent event: " + actionMasked);
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            return false;
        }
        return true;
//        return super.onTouchEvent(event);
    }

}
