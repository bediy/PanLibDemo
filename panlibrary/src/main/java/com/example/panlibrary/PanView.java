package com.example.panlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Ye on 2017/5/3/0003.
 *
 */

public class PanView extends View {

    private static final String TAG = PanView.class.getSimpleName();
    private static final int CIRCLE_ANGLE = 360;

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


    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        oval = new RectF();
        arcAngle = CIRCLE_ANGLE / part;
    }

    /**
     * @param event
     * 角度 = Math.atan((dpPoint.y-dpCenter.y) / (dpPoint.x-dpCenter.x)) / π（3.14） * 180度
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                double deltaY = event.getRawY() - centerOnScreenY;
                double deltaX = event.getRawX() - centerOnScreenX;

//                double degrees = Math.atan2(deltaY, deltaX) / Math.PI * 180;
                double degrees = Math.atan(deltaY / deltaX) / Math.PI * 180;

                Log.i(TAG, event.getRawY() + "/////" + centerOnScreenY);
//                Log.i(TAG, degrees + "/////" );
                setRotation((float) degrees);

                break;
        }
        return true;
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

    private void drawCircle(Canvas canvas) {
        float x = getMeasuredWidth() / 2;
        float y = getMeasuredHeight() / 2;

        canvas.drawCircle(x, y, Math.min(x, y) - strokeWidth, paint);

    }

}
