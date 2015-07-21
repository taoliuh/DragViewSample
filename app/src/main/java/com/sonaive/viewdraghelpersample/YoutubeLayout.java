package com.sonaive.viewdraghelpersample;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
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

    public YoutubeLayout(Context context) {
        this(context, null, 0);
    }

    public YoutubeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public YoutubeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDragHelper = ViewDragHelper.create(this, 1.0f, new DragCallback());
    }

    @Override
    protected void onFinishInflate() {
        mHead = getChildAt(0);
        mDesc = getChildAt(1);
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    class DragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == mHead;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            mTop = top;
            mDragOffset = mTop / (float) mDragRange;
            mHead.setPivotX(mHead.getWidth());
            mHead.setPivotY(mHead.getHeight());
            mHead.setScaleX(1 - mDragOffset / 2);
            mHead.setScaleY(1 - mDragOffset / 2);

            mDesc.setAlpha(1 - mDragOffset);
            requestLayout();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return mDragRange;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {

            mDragHelper.settleCapturedViewAt(releasedChild.getLeft(), mTop);
            invalidate();
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int topBound = child.getPaddingTop();
            int bottomBound = getHeight() - mHead.getHeight() - mHead.getPaddingBottom();

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
        mDragRange = getHeight() - mHead.getHeight();
        mHead.layout(0,
                mTop,
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

        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mDragHelper.processTouchEvent(event);
        return true;
    }
}
