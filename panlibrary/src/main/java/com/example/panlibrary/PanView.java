package com.example.panlibrary;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Ye on 2017/5/3/0003.
 */

public class PanView extends View {

    private static final String TAG = PanView.class.getSimpleName();
    private static final int CIRCLE_ANGLE = 360;

    private VelocityTracker velocityTracker;
    private double downDegrees;
    private double lastRotation;
    public double lastMoveDegrees;
    private ObjectAnimator rotationAnimator;
    private boolean clockWise;

    private float centerOnScreenX = 0;
    private float centerOnScreenY = 0;
    private RectF oval;
    private Paint paint;
    private float strokeWidth = 10f;
    private static final int wrapWidth = 1000;
    private static final int wrapHeight = 1000;

    private int part = 6;
    private int arcAngle;

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        invalidate();
    }

    public void setPart(int part) {
        this.part = part;
        arcAngle = CIRCLE_ANGLE / part;
        invalidate();
    }

    public PanView(Context context) {
        super(context);
        init();
    }

    public PanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public PanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        oval = new RectF();
        arcAngle = CIRCLE_ANGLE / part;
        velocityTracker = VelocityTracker.obtain();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = wrapWidth;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }


    private int measureHeight(int heightMeasureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = wrapHeight;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int startAngle = 0;
        for (int i = 0; i < part; i++) {
            canvas.drawArc(oval, startAngle += arcAngle, arcAngle, true, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        velocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downDegrees = getDegrees(event);
                lastRotation = getRotation();
                stopAnimator();
                break;
            case MotionEvent.ACTION_MOVE:
                double moveDegrees = getDegrees(event);
                clockWise = isClockWise(moveDegrees);
                double deltaDegrees = moveDegrees - downDegrees;
                int rotation = (int) (lastRotation + deltaDegrees) % 180;
                setRotation((float) rotation);
                lastMoveDegrees = moveDegrees;
                break;
            case MotionEvent.ACTION_UP:
                velocityTracker.computeCurrentVelocity(1000, 15000f);
                int xVelocity = (int) velocityTracker.getXVelocity();
                int yVelocity = (int) velocityTracker.getYVelocity();
                if (Math.abs(xVelocity) < 10 && Math.abs(yVelocity) < 10)
                    break;

                int rotationTo = (int) (Math.max(Math.abs(xVelocity), Math.abs(yVelocity)) * 0.5f);
                if (!clockWise)
                    rotationTo = -rotationTo;

                long durarion = (long) Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity);
//                Log.i(TAG, durarion + "/////" + rotationTo);
                rotationAnimator = ObjectAnimator.ofFloat(this, "rotation", getRotation(), rotationTo);
                rotationAnimator.setDuration((long) (durarion * 0.8f));
                rotationAnimator.setInterpolator(new DecelerateInterpolator(1f));
                rotationAnimator.start();
                break;
        }
        return true;
    }

    public boolean isClockWise(double moveDegrees) {

        Log.i(TAG, "/////" + moveDegrees);
        if (Math.abs(moveDegrees) > 170 && moveDegrees < 0 && lastMoveDegrees > 0) {
            return true;
        } else if (Math.abs(moveDegrees) > 170 && moveDegrees > 0 && lastMoveDegrees < 0) {
            return false;
        } else {
            if (moveDegrees > lastMoveDegrees)
                return true;
            else
                return false;
        }
    }

    /**
     * @param event 角度 = Math.atan((dpPoint.y-dpCenter.y) / (dpPoint.x-dpCenter.x)) / π（3.14） * 180度
     *              Math.atan2(deltaY, deltaX) / Math.PI * 180;
     * @return
     */
    private double getDegrees(MotionEvent event) {
        double deltaY = event.getRawY() - centerOnScreenY;
        double deltaX = event.getRawX() - centerOnScreenX;
//        return Math.atan(deltaY / deltaX) / Math.PI * 180;
        return Math.atan2(deltaY, deltaX) / Math.PI * 180;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int[] locationOnScreen = new int[2];
        getLocationOnScreen(locationOnScreen);

        int width = getWidth();
        int height = getHeight();
        if (height > width) {
            oval.set(0, (height - width) / 2, width, height / 2 + width / 2);
        } else {
            oval.set((width - height) / 2, 0, height / 2 + width / 2, height);
        }
        oval.inset(strokeWidth, strokeWidth);
        centerOnScreenX = oval.centerX() + locationOnScreen[0];
        centerOnScreenY = oval.centerY() + locationOnScreen[1];
        invalidate();
    }

    private void stopAnimator() {
        if (rotationAnimator != null) {
            if (rotationAnimator.isRunning())
                rotationAnimator.cancel();
        }
    }


    private void releaseVelocityTracker() {
        if (null != velocityTracker) {
            velocityTracker.clear();
            velocityTracker.recycle();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseVelocityTracker();
        stopAnimator();
    }

    private void drawCircle(Canvas canvas) {
        float x = getMeasuredWidth() / 2;
        float y = getMeasuredHeight() / 2;

        canvas.drawCircle(x, y, Math.min(x, y) - strokeWidth, paint);

    }

}
