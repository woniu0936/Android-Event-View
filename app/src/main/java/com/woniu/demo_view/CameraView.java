package com.woniu.demo_view;

import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * @author woniu
 * @title CameraView
 * @description
 * @since 2017/12/6 21:12
 */
public class CameraView extends FrameLayout {

    private static final String TAG = "CameraView";

    public CameraView(@NonNull Context context) {
        this(context, null);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private ImageView mFocusIcon;           //对焦icon
    private float mTouchSlop;

    private void init(Context context) {
        inflate(context, R.layout.layout_camera_view, this);
        mFocusIcon = (ImageView) findViewById(R.id.iv_focus);
        mTouchSlop = ViewConfiguration.get(context).getScaledEdgeSlop();
        mStartPoint = new Point();
        mEndPoint = new Point();

    }

    boolean isClick;
    private static final int LONG_PRESS_TIME = 400;
    private Point mStartPoint;
    private Point mEndPoint;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        //在这里处理点击事件，点击的位置显示mFocusIcon
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isClick = true;
                mStartPoint.x = (int) e.getX();
                mStartPoint.y = (int) e.getY();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isClick = false;
                    }
                }, LONG_PRESS_TIME);
                Log.e(TAG, "ACTION_DOWN_1");
                break;
            case MotionEvent.ACTION_MOVE:
                mEndPoint.x = (int) e.getX();
                mEndPoint.y = (int) e.getY();
                if (calculateDistanceBetween(mStartPoint, mEndPoint) > mTouchSlop) {
                    isClick = false;
                }
                Log.e(TAG, "ACTION_MOVE_1");
                break;
            case MotionEvent.ACTION_UP:
                if (isClick) {
                    setFocusViewPosition(mStartPoint.x, mStartPoint.y);
                }
                Log.e(TAG, "ACTION_UP_1");
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.e(TAG, "ACTION_CANCEL_1");

                break;
        }
        return true;
    }

    private double calculateDistanceBetween(Point before, Point after) {
        return Math.sqrt(Math.pow((before.x - after.x), 2) + Math.pow((before.y - after.y), 2));
    }

    /**
     * 设置对焦icon的位置
     *
     * @param x 对焦icon相对cameraView的x坐标
     * @param y 对焦icon相对cameraView的y坐标
     */
    public void setFocusViewPosition(float x, float y) {
        mFocusIcon.setX(x - mFocusIcon.getWidth() / 2.0f);
        mFocusIcon.setY(y - mFocusIcon.getHeight() / 2.0f);
    }

}
