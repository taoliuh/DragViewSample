package com.sonaive.viewdraghelpersample;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by liutao on 15-7-20.
 */
public class DragLayout extends LinearLayout {

    private static final String TAG = DragLayout.class.getSimpleName();

    private ViewDragHelper mViewDragHelper;
    private View mDragView1;
    private View mDragView2;

    private boolean mDragHorizontal;
    private boolean mDragVertical;
    private boolean mCaptureView;

    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new DragHelperCallback());
    }

    @Override
    protected void onFinishInflate() {
        mDragView1 = findViewById(R.id.drag_view1);
        mDragView2 = findViewById(R.id.drag_view2);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mViewDragHelper.cancel();
            return false;
        }
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    public void setDragHorizontal(boolean dragHorizontal) {
        mDragHorizontal = dragHorizontal;
        mDragView1.setVisibility(VISIBLE);
        mDragView2.setVisibility(INVISIBLE);
    }

    public void setDragVertical(boolean dragVertical) {
        mDragVertical = dragVertical;
        mDragView2.setVisibility(VISIBLE);
        mDragView1.setVisibility(INVISIBLE);
    }

    public void setCapterView(boolean captureView) {
        mCaptureView = captureView;
        mDragView1.setVisibility(VISIBLE);
        mDragView2.setVisibility(VISIBLE);
    }

    class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View view, int i) {
            if (mCaptureView) {
                return view == mDragView1;
            }
            return true;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            Log.d(TAG, "clampViewPositionHorizontal, left is: " + left + ", dx is: " + dx);
            if (mDragHorizontal || mCaptureView) {
                int leftBound = getPaddingLeft();
                int rightBound = getWidth() - child.getWidth();
                return Math.min(Math.max(left, leftBound), rightBound);
            }
            return super.clampViewPositionHorizontal(child, left, dx);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (mDragVertical) {
                int topBound = getPaddingTop();
                int bottomBound = getHeight() - child.getHeight();
                return Math.min(Math.max(top, topBound), bottomBound);
            }
            return super.clampViewPositionVertical(child, top, dy);
        }
    }

}
