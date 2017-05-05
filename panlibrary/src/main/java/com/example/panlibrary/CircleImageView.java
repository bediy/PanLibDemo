package com.example.panlibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Ye on 2017/5/5/0005.
 */

public class CircleImageView extends AppCompatImageView {

    private Paint paint;
    private float strokeWidth = 10f;


    public CircleImageView(Context context) {
        super(context);
        init();
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
//        setScaleType(ScaleType.CENTER_CROP);
//        paint.setColor(Color.BLACK);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(strokeWidth);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawCircle(canvas);
        drawCircleImage(canvas);
    }

    private void drawCircleImage(Canvas canvas) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.photo_2017);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int needLength = Math.min(measuredWidth, measuredHeight);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, needLength, needLength, true);
        BitmapShader bitmapShader = new BitmapShader(scaledBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(bitmapShader);
//        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);
        drawCircle(canvas);
    }

    private void drawCircle(Canvas canvas) {
        float x = getMeasuredWidth() / 2;
        float y = getMeasuredHeight() / 2;

        canvas.drawCircle(x, y, Math.min(x, y) - strokeWidth, paint);

    }
}
