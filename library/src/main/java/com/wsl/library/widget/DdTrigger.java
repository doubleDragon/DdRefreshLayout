package com.wsl.library.widget;

/**
 * Created by wsl on 17/4/20.
 */

public interface DdTrigger {

    void onDrag(int triggerHeight, int offset, int max);
    void onReturn();
    void onWait();
    void onReset();
}
