package com.wsl.library.widget.demo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.wsl.library.widget.DdTrigger;

/**
 * 下拉刷新头部
 * Created by wsl on 17/4/20.
 */

public class HeaderView extends AppCompatTextView implements DdTrigger{

    private static final int STATE_DRAG_ORIGIN = -1;
    private static final int STATE_DRAG_OVER_FALSE = 0;
    private static final int STATE_DRAG_OVER_TRUE = 1;

    private int mLastDragState = STATE_DRAG_ORIGIN;

    public HeaderView(Context context) {
        this(context, null);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDrag(int triggerHeight, int offset, int max) {
        int state = STATE_DRAG_OVER_FALSE;
        if(offset > triggerHeight) {
            state = STATE_DRAG_OVER_TRUE;
        }
        if(mLastDragState == state) {
            return;
        }
        mLastDragState = state;
        switch (mLastDragState) {
            case STATE_DRAG_OVER_FALSE:
                setText("下拉刷新");
                break;
            case STATE_DRAG_OVER_TRUE:
                setText("松开刷新");
                break;
        }
    }

    @Override
    public void onReturn() {
        setText("回滚");
    }

    @Override
    public void onWait() {
        setText("正在刷新");
    }

    @Override
    public void onReset() {
        setText("");
    }
}
