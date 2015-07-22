package com.sonaive.viewdraghelpersample;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liutao on 15-7-21.
 */
public class YoutubeLayout extends ViewGroup {

    private static final String TAG = YoutubeLayout.class.getSimpleName();

    private View mHead;
    private View mDesc;

    private ViewDragHelper mDragHelper;

    private int mTop;
    private int mDragRange;
    private float mDragOffset;

    private float mInitialMotionX;
    private float mInitialMotionY;
    private int mTouchSlop;

    public YoutubeLayout(Context context) {
        this(context, null, 0);
    }

    public YoutubeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YoutubeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragCallback());
        mTouchSlop = mDragHelper.getTouchSlop();
    }

    @Override
    protected void onFinishInflate() {
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("Must add two child view in YoutubeLayout in xml.");
        }
        mHead = getChildAt(0);
        mDesc = getChildAt(1);
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void maximize() {
        smoothSlideTo(0f);
    }

    class DragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mHead;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            Log.d(TAG, "onViewPositionChanged, top is: " + top);
            int correction;
            mTop = top;
            if (top < getPaddingTop()) {
                correction = 0;
            } else {
                correction = top - getPaddingTop();
            }
            mDragOffset = correction / (float) mDragRange;
            mHead.setPivotX(mHead.getWidth());
            mHead.setPivotY(mHead.getHeight());
            mHead.setScaleX(1 - mDragOffset / 2);
            mHead.setScaleY(1 - mDragOffset / 2);
            Log.d(TAG, "ScaleX is: " + mHead.getScaleX() + ", scaleY is: " + mHead.getScaleY());

            mDesc.setAlpha(1 - mDragOffset);
            requestLayout();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mDragRange;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            // The settle down position, if current y coordinate velocity is positive,
            // or current y coordinate velocity is zero while drag offset exceeds half
            // of the drag range, then set the settle down position to the bottom.
            // Otherwise set the settle down position to the top.
            int top = getPaddingTop();
            if (yvel > 0 || (yvel == 0 && mDragOffset > 0.5f)) {
                top += mDragRange;
            }
            mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), top);
            invalidate();
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int topBound = getPaddingTop();
            int bottomBound = getHeight() - mHead.getHeight() - getPaddingBottom();
            Log.d(TAG, "Top bound is: " + topBound + ", bottom bound is: " + bottomBound + ", top is: " + top + ", dy is: " + dy);
            return Math.min(Math.max(top, topBound), bottomBound);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mDragRange = getHeight() - mHead.getHeight() - getPaddingTop() - getPaddingBottom();
        if (mTop == 0) {
            mTop = getPaddingTop();
        }

        mHead.layout(0,
                mTop ,
                r,
                mTop + mHead.getMeasuredHeight());

        mDesc.layout(0,
                mTop + mHead.getMeasuredHeight(),
                r,
                mTop + mHead.getMeasuredHeight() + mDesc.getMeasuredHeight());

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }

        final float x = ev.getX();
        final float y = ev.getY();
        boolean interceptTap = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mInitialMotionX = x;
                mInitialMotionY = y;
                interceptTap = mDragHelper.isViewUnder(mHead, (int) x, (int) y);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                float dx = Math.abs(x - mInitialMotionX);
                float dy = Math.abs(y - mInitialMotionY);
                if (dx > dy || dy < mTouchSlop) {
                    mDragHelper.cancel();
                    return false;
                }
            }
        }
        return mDragHelper.shouldInterceptTouchEvent(ev) || interceptTap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        int action = event.getActionMasked();

        final float x = event.getX();
        final float y = event.getY();
        boolean isHeadViewUnder = mDragHelper.isViewUnder(mHead, (int) x, (int) y);
        Log.d(TAG, "Head view is under given point: " + isHeadViewUnder);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mInitialMotionX = x;
                mInitialMotionY = y;
                break;
            }
            case MotionEvent.ACTION_UP:
                float dx = x - mInitialMotionX;
                float dy = y - mInitialMotionY;

                if (dx * dx + dy * dy < mTouchSlop * mTouchSlop && isHeadViewUnder) {
                    if (mDragOffset == 0) {
                        smoothSlideTo(1);
                    } else {
                        smoothSlideTo(0);
                    }
                }
                break;
        }
        boolean isViewHitHead = isViewHit(mHead, x, y);
        boolean isViewHitDesc = isViewHit(mDesc, x, y);
        Log.d(TAG, "isViewHitHead: " + isViewHitHead + ", isViewHitDesc: " + isViewHitDesc);
        return isHeadViewUnder && isViewHitHead || isViewHitDesc;
    }

    private boolean smoothSlideTo(float slideOffset) {
        int topBound = getPaddingTop();
        int y = (int) (topBound + slideOffset * mDragRange);
        if (mDragHelper.smoothSlideViewTo(mHead, mHead.getLeft(), y)) {
            ViewCompat.postInvalidateOnAnimation(this);
            return true;
        }
        return false;
    }

    private boolean isViewHit(View view, float x, float y) {
        int[] headLoc = new int[2];
        view.getLocationOnScreen(headLoc);

        int[] parentLoc = new int[2];
        getLocationOnScreen(parentLoc);

        int screenX = (int) (x + parentLoc[0]);
        int screenY = (int) (y + parentLoc[1]);
        return screenX >= headLoc[0]
                && screenX < headLoc[0] + view.getWidth()
                && screenY >= headLoc[1]
                && screenY < headLoc[1] + view.getHeight();
    }
}
