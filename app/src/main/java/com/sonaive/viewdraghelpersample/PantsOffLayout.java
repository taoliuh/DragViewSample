package com.sonaive.viewdraghelpersample;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by liutao on 15-7-22.
 */
public class PantsOffLayout extends FrameLayout {

    private static final String TAG = PantsOffLayout.class.getSimpleName();
    private static final double AUTO_OPEN_SPEED_LIMIT = 0f;
    private ViewDragHelper mDragHelper;

    private View mPantsBelt;
    private int mTop;
    private int mDragRange;
    private int mDraggingState;

    public PantsOffLayout(Context context) {
        super(context);
    }

    public PantsOffLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDragHelper = ViewDragHelper.create(this, 1.0f, new TakeOffPantsCallback());
    }

    public boolean isDragging() {
        return mDraggingState == ViewDragHelper.STATE_DRAGGING ||
                mDraggingState == ViewDragHelper.STATE_SETTLING;
    }

    @Override
    protected void onFinishInflate() {
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("Must add two child view in PantsOffLayout in xml.");
        }
        mPantsBelt = findViewById(R.id.pants_belt);
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mDragRange = (int) (getHeight() * 0.66f);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }
        return isPantsBeltLoosened(ev) && mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If finger leaves the draggable area while it is still in draggable state, then continue to process touch event
        if (isPantsBeltLoosened(event) || isDragging()) {
            mDragHelper.processTouchEvent(event);
            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }

    // If the pants belt is loosened, then it should be easy to take off the pants
    private boolean isPantsBeltLoosened(MotionEvent motionEvent) {
        int[] location = new int[2];
        mPantsBelt.getLocationOnScreen(location);
        int screenX = (int) motionEvent.getRawX();
        int screenY = (int) motionEvent.getRawY();
        return screenX >= location[0]
                && screenX <= location[0] + mPantsBelt.getWidth()
                && screenY >= location[1]
                && screenY <= location[1] + mPantsBelt.getHeight();
    }

    class TakeOffPantsCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child.getId() == R.id.main_layout;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            boolean shouldDragDown = false;
            int settleDownPosition;
            if (yvel > AUTO_OPEN_SPEED_LIMIT) {
                shouldDragDown = true;
            } else if (yvel < -AUTO_OPEN_SPEED_LIMIT) {
                shouldDragDown = false;
            } else if (mTop > mDragRange / 2f) {
                shouldDragDown = true;
            } else if (mTop <= mDragRange / 2f) {
                shouldDragDown = false;
            } else {
                shouldDragDown = false;
            }
            settleDownPosition = shouldDragDown ? mDragRange : 0;
            if (mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), settleDownPosition)) {
                ViewCompat.postInvalidateOnAnimation(PantsOffLayout.this);
            }
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (state == mDraggingState) {
                return;
            }
            mDraggingState = state;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            mTop = top;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int topBound = getPaddingTop();
            int bottomBound = mDragRange;
            return Math.min(Math.max(top, topBound), bottomBound);
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mDragRange;
        }
    }
}
