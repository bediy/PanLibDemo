package com.example.panlibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Ye on 2017/5/3/0003.
 *
 */

public class PanView extends View {

    private Paint paint;
    private float strokeWidth = 10f;
    private int wrapWidth = 1000;
    private int wrapHeight = 1000;

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
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
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
        super.onDraw(canvas);
        float x = getMeasuredWidth() / 2;
        float y = getMeasuredHeight() / 2;
        canvas.drawCircle(x, y, Math.min(x, y) - strokeWidth, paint);
    }
}
