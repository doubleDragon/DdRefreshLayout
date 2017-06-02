package com.wsl.library.widget;

import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Nested scroll helper class
 * Created by wsl on 17/4/18.
 */

final class DdNestedScrollerHelper<Layout extends ViewGroup> {

    private static final String TAG = DdNestedScrollerHelper.class.getSimpleName();
    private static final int DEFAULT_AUTO_SCROLL_DURATION = 500;

    interface Listener {
        void onOffsetChange(int offset);
    }

    private Listener mListener;

    private Layout mLayout;
    private ScrollerCompat mScroller;
    private ScrollRunnable mScrollRunnable;

    DdNestedScrollerHelper(Layout layout) {
        this.mLayout = layout;
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }

    final int scroll(int dy, int minOffset, int maxOffset) {
        return setHeaderTopBottomOffset(getTopBottomOffsetForScrollingSibling() - dy, minOffset, maxOffset);
    }

    private int getTopBottomOffsetForScrollingSibling() {
        return getTopAndBottomOffset();
    }

    int getTopAndBottomOffset() {
        return -mLayout.getScrollY();
    }

    private int setHeaderTopBottomOffset(int newOffset) {
        return setHeaderTopBottomOffset(newOffset, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private int setHeaderTopBottomOffset(int newOffset,
                                         int minOffset, int maxOffset) {
        final int curOffset = getTopAndBottomOffset();
        int consumed = 0;

        if (curOffset >= minOffset && curOffset <= maxOffset) {
            // If we have some scrolling range, and we're currently within the min and max
            // offsets, calculate a new offset
            newOffset = MathUtils.constrain(newOffset, minOffset, maxOffset);

            if (curOffset != newOffset) {
                setTopAndBottomOffset(newOffset);
                // Update how much dy we have consumed
                consumed = curOffset - newOffset;
                if (mListener != null) {
                    mListener.onOffsetChange(getTopAndBottomOffset());
                }
            }
        }

        return consumed;
    }

    private void setTopAndBottomOffset(int offset) {
        mLayout.scrollTo(0, -offset);
    }

    final boolean autoScroll(int dy) {
        return autoScroll(dy, DEFAULT_AUTO_SCROLL_DURATION);
    }

    private boolean autoScroll(int dy, int duration) {
        if (mScrollRunnable != null) {
            mLayout.removeCallbacks(mScrollRunnable);
            mScrollRunnable = null;
        }

        if (mScroller == null) {
            mScroller = ScrollerCompat.create(mLayout.getContext());
        }
        mScroller.startScroll(0, getTopAndBottomOffset(), 0, dy, duration);
        if (mScroller.computeScrollOffset()) {
            mScrollRunnable = new ScrollRunnable(mLayout);
            ViewCompat.postOnAnimation(mLayout, mScrollRunnable);
            return true;
        }
        return false;
    }

    private class ScrollRunnable implements Runnable {
        private final Layout mLayout;

        ScrollRunnable(Layout layout) {
            mLayout = layout;
        }

        @Override
        public void run() {
            if (mLayout != null && mScroller != null && mScroller.computeScrollOffset()) {
                setHeaderTopBottomOffset(mScroller.getCurrY());

                // Post ourselves so that we run on the next animation
                ViewCompat.postOnAnimation(mLayout, this);
            }
        }
    }
}
