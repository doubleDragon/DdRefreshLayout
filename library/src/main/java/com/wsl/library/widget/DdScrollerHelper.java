package com.wsl.library.widget;

import android.util.Log;

/**
 * Created by wsl on 17/4/18.
 */

public final class DdScrollerHelper {

    private static final String TAG = DdScrollerHelper.class.getSimpleName();

    DdRefreshLayout mRefreshLayout;

    public DdScrollerHelper(DdRefreshLayout refreshLayout) {
        this.mRefreshLayout = refreshLayout;
    }

    final int scroll(int dy, int minOffset, int maxOffset) {
        int current = getTopBottomOffsetForScrollingSibling();
        Log.d(TAG , "scroll current[" + current + "]---dy[" + dy + "]");
        return setHeaderTopBottomOffset(getTopBottomOffsetForScrollingSibling() - dy, minOffset, maxOffset);
    }

    int getTopBottomOffsetForScrollingSibling() {
        return getTopAndBottomOffset();
    }

    int getTopAndBottomOffset() {
        return -mRefreshLayout.getScrollY();
    }

    int setHeaderTopBottomOffset(int newOffset,
                                 int minOffset, int maxOffset) {
        final int curOffset = getTopAndBottomOffset();
        int consumed = 0;

        Log.d(TAG , "setHeaderTopBottomOffset---curOffset: " + curOffset + "---newOffset: " + newOffset + "---minOffset: " + minOffset + "---maxOffset: " + maxOffset);

        if (curOffset >= minOffset && curOffset <= maxOffset) {
            // If we have some scrolling range, and we're currently within the min and max
            // offsets, calculate a new offset
            newOffset = MathUtils.constrain(newOffset, minOffset, maxOffset);

            if (curOffset != newOffset) {
                setTopAndBottomOffset(newOffset);
                // Update how much dy we have consumed
                consumed = curOffset - newOffset;
            }
        }

        return consumed;
    }

    void setTopAndBottomOffset(int offset) {
        Log.d(TAG , "setTopAndBottomOffset : " + offset);
        mRefreshLayout.scrollTo(0, -offset);
    }
}
