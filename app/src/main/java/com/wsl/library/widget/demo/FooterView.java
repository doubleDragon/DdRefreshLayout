package com.wsl.library.widget.demo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.wsl.library.widget.DdTrigger;

/**
 * 加载更多底部
 * Created by wsl on 17/4/20.
 */

public class FooterView extends AppCompatTextView implements DdTrigger{

    private static final int STATE_DRAG_ORIGIN = -1;
    private static final int STATE_DRAG_OVER_FALSE = 0;
    private static final int STATE_DRAG_OVER_TRUE = 1;

    private int mLastDragState = STATE_DRAG_ORIGIN;

    public FooterView(Context context) {
        super(context);
    }

    public FooterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FooterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onDrag(int triggerHeight, int offset, int max) {
        int state = STATE_DRAG_OVER_FALSE;
        if(-offset > triggerHeight) {
            state = STATE_DRAG_OVER_TRUE;
        }
        if(mLastDragState == state) {
            return;
        }
        mLastDragState = state;
        switch (mLastDragState) {
            case STATE_DRAG_OVER_FALSE:
                setText("上拉加载");
                break;
            case STATE_DRAG_OVER_TRUE:
                setText("松开加载");
                break;
        }
    }

    @Override
    public void onReturn() {
        setText("回滚");
    }

    @Override
    public void onWait() {
        setText("正在加载");
    }

    @Override
    public void onReset() {
        setText("");
    }
}
