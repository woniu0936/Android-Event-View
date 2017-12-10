package com.woniu.demo_view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ImageView;

/**
 * @author woniu
 * @title DragConstraintLayout
 * @description
 * @since 2017/11/22 15:43
 */
public class DragConstraintLayout extends ConstraintLayout {

    private static final String TAG = "DragConstraintLayout";

    private static final int LONG_PRESS_TIME = 400;     //单位毫秒

    private int mDragViewResId;
    private ImageView mDragView;

    private Point mStartPoint;
    private Point mEndPoint;

    private Runnable mLongPress;
    private Runnable mReleaseLongPress;
    private boolean mOnLongPressing = false;

    private float mTouchSlop;

    public DragConstraintLayout(Context context) {
        this(context, null);
    }

    public DragConstraintLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DragConstraintLayout);
        mDragViewResId = ta.getResourceId(R.styleable.DragConstraintLayout_drag_view_id, -1);
        ta.recycle();
        mStartPoint = new Point();
        mEndPoint = new Point();
        mTouchSlop = ViewConfiguration.get(context).getScaledEdgeSlop();

        mLongPress = new Runnable() {
            @Override
            public void run() {
                if (null != mDragView) {
                    //告诉父View不要拦截事件
                    getParent().requestDisallowInterceptTouchEvent(true);
                    scrollView(mStartPoint.x, mStartPoint.y);
                    mDragView.setVisibility(VISIBLE);
                    mOnLongPressing = true;
                }
            }
        };
        mReleaseLongPress = new Runnable() {
            @Override
            public void run() {
                if (null != mDragView) {
                    //告诉父view拦截事件
                    getParent().requestDisallowInterceptTouchEvent(false);
                    removeCallbacks(mLongPress);
                    mDragView.setVisibility(INVISIBLE);
                    mOnLongPressing = false;
                }
            }
        };
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "ACTION_DOWN");
                mStartPoint.x = (int) e.getX();
                mStartPoint.y = (int) e.getY();
                if (null != mDragView) {
                    mDragView.postDelayed(mLongPress, LONG_PRESS_TIME);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "ACTION_MOVE");
                mEndPoint.x = (int) e.getX();
                mEndPoint.y = (int) e.getY();
                if (mOnLongPressing) {
                    scrollView(mEndPoint.x, mEndPoint.y);
                }
                if (calculateDistanceBetween(mStartPoint, mEndPoint) > mTouchSlop) {
                    removeCallbacks(mLongPress);
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "ACTION_UP");
                mReleaseLongPress.run();
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.e(TAG, "ACTION_CANCEL");
                mReleaseLongPress.run();
                break;
        }
        return false;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (-1 != mDragViewResId) {
            mDragView = (ImageView) findViewById(mDragViewResId);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 计算手指位移的距离
     *
     * @param before
     * @param after
     * @return
     */
    private double calculateDistanceBetween(Point before, Point after) {
        return Math.sqrt(Math.pow((before.x - after.x), 2) + Math.pow((before.y - after.y), 2));
    }

    /**
     * 移动需要滑动的view
     *
     * @param x
     * @param y
     */
    private void scrollView(int x, int y) {
        if (null != mDragView) {
            mDragView.setX(x - mDragView.getWidth() / 2);
            mDragView.setY(y - mDragView.getHeight() / 2);
        }
    }

}
