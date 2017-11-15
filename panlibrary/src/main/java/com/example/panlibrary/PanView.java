package com.example.panlibrary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ye on 2017/5/3/0003.
 */

public class PanView extends View {

    private static final String TAG = PanView.class.getSimpleName();
    private static final int CIRCLE_ANGLE = 360;
    private static float DECELERATION_RATE = (float) (Math.log(0.78) / Math.log(0.9));
    private float mFlingFriction = ViewConfiguration.getScrollFriction();
    private float mPhysicalCoeff;
    private static final float INFLEXION = 0.35f; // Tension lines cross at (INFLEXION, 1)

    private VelocityTracker velocityTracker;
    private double downDegrees;
    public double lastMoveDegrees;
    private ObjectAnimator rotationAnimator;
    private boolean clockWise;

    int colors[] = {
            R.color.red,
            R.color.green,
            R.color.orange,
            R.color.light_blue,
            R.color.indigo,
            R.color.blue_grey,
    };
    /*      R.color.blue,
            R.color.deep_purple,
            R.color.light_green,
            R.color.lime,
            R.color.yellow,
            R.color.amber,
            R.color.cyan,
            R.color.teal,
            R.color.deep_orange,
            R.color.pink,
            R.color.brown,
            R.color.purple,
            R.color.grey,
*/
    private List<String> itemNames = new ArrayList<>();
    private FixedSizeList<Double> fixedSizeList = new FixedSizeList<>();

    private float centerOnScreenX = 0;
    private float centerOnScreenY = 0;
    private RectF oval;
    private Rect textRect = new Rect();

    private Paint arcPaint;
    private Paint textPaint;
    private float strokeWidth = 10f;
    private static final int wrapWidth = 1000;
    private static final int wrapHeight = 1000;

    private int part = 6;
    private int sweepAngle;
    private int mMaximumVelocity;
    private int mMinimumVelocity;
    private Scroller scroller;
    private int startAngle = 0;
    private int rotation;
    private double totalRotation;
    private double lastDegrees;

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        invalidate();
    }

    public void setPart(int part) {
        this.part = part;
        sweepAngle = CIRCLE_ANGLE / part;
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
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();

        final float ppi = getContext().getResources().getDisplayMetrics().density * 160.0f;
        mPhysicalCoeff = SensorManager.GRAVITY_EARTH // g (m/s^2)
                * 39.37f // inch/meter
                * ppi
                * 0.84f; // look and feel tuning

        arcPaint = new Paint();
        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(Paint.Style.FILL);
//        arcPaint.setColor(Color.BLUE);
//        arcPaint.setStrokeWidth(strokeWidth);
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(100);
        textPaint.setTextAlign(Paint.Align.CENTER);
        oval = new RectF();
        sweepAngle = CIRCLE_ANGLE / part;
        velocityTracker = VelocityTracker.obtain();
        itemNames.add("叶云");
        itemNames.add("吴昊");
        itemNames.add("杨盛晖");
        scroller = new Scroller(getContext());
    }

    public void setItemNames(List<String> itemNames) {
        this.itemNames = itemNames;
    }

    private int getColorById(int id) {
        return ContextCompat.getColor(getContext(), id);
    }

    public void reset() {
        rotation = 0;
        totalRotation = 0;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        for (int i = 0; i < part; i++) {
            String itemName = getItemName(i);
            textPaint.getTextBounds(itemName, 0, itemName.length() - 1, textRect);
            arcPaint.setColor(getColorById(getColor(i)));
            canvas.drawArc(oval, rotation, sweepAngle, true, arcPaint);
            canvas.rotate(sweepAngle, oval.centerX(), oval.centerY());
            /*canvas.save();
            canvas.translate(oval.centerX(), oval.centerY());
            canvas.rotate(-sweepAngle);
            canvas.drawText(itemName, oval.width() / 4, textRect.height() / 2, textPaint);
            canvas.restore();*/
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        velocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                downDegrees = getDegrees(event);
                break;
            case MotionEvent.ACTION_MOVE:
                final double moveDegrees = getDegrees(event);
                clockWise = isClockWise(moveDegrees);
                fixedSizeList.add(moveDegrees);
                final double deltaDegrees = moveDegrees - downDegrees;
                rotation = (int) (totalRotation + deltaDegrees);
                Log.i(TAG, "/////" + totalRotation + "..." + deltaDegrees);
                Log.i(TAG, "development merge test, for merging to master." + totalRotation + "..." + deltaDegrees);
                invalidate();
                lastDegrees = deltaDegrees;
                lastMoveDegrees = moveDegrees;
                break;
            case MotionEvent.ACTION_UP:
                totalRotation += lastDegrees;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);//28000
                int xVelocity = (int) velocityTracker.getXVelocity();
                int yVelocity = (int) velocityTracker.getYVelocity();
                if (Math.abs(xVelocity) < mMinimumVelocity && Math.abs(yVelocity) < mMinimumVelocity)
                    break;

                final double velocity = Math.sqrt(xVelocity * xVelocity + yVelocity * yVelocity);
//                long duration = (long) (velocity * 0.42);
                int rotationTo = (int) (velocity * 0.5);
//                long duration = getSplineFlingDuration((int) velocity);
//                int rotationTo = (int) getSplineFlingRotation((int) velocity);

                if (!clockWise)
                    rotationTo = -rotationTo;

//                startAnimator(rotationTo, duration);
                scroller.fling(0, rotation, 0, (int) velocity, 0, 0, 0, rotationTo);
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            rotation = scroller.getCurrY();
            postInvalidate();
        }
    }

    private String getItemName(int index) {
        int size = itemNames.size();
        return itemNames.get(index % size);
    }

    private int getColor(int index) {
        int length = colors.length;
        return colors[index % length];
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private double getSplineFlingRotation(int velocity) {
        final double l = getSplineDeceleration(velocity);
        final double decelMinusOne = DECELERATION_RATE - 1.0;
        return mFlingFriction * mPhysicalCoeff * Math.exp(DECELERATION_RATE / decelMinusOne * l);
    }

    /* Returns the duration, expressed in milliseconds */
    private int getSplineFlingDuration(int velocity) {
        final double l = getSplineDeceleration(velocity);
        final double decelMinusOne = DECELERATION_RATE - 1.0;
        return (int) (1000.0 * Math.exp(l / decelMinusOne));
    }

    private void startAnimator(int rotationTo, long duration) {
        rotationAnimator = ObjectAnimator.ofFloat(this, "rotation", getRotation(), rotationTo);
        rotationAnimator.setDuration(duration);
        rotationAnimator.setInterpolator(new DecelerateInterpolator(1.3f));
        rotationAnimator.addListener(new AnimatorListener());
        rotationAnimator.start();
    }

    private double getSplineDeceleration(int velocity) {
        return Math.log(INFLEXION * Math.abs(velocity) / (mFlingFriction * mPhysicalCoeff));
    }

    private boolean isClockWise(double moveDegrees) {
//        Log.i(TAG, "/////" + moveDegrees + lastMoveDegrees);
        if (Math.abs(moveDegrees - lastMoveDegrees) > 300)
            return moveDegrees < lastMoveDegrees;
        return moveDegrees > lastMoveDegrees;
    }

    private int getWinnerByRotation(float rotation) {
        int result = 0;
        if (rotation < 0) {
            for (int i = 1; i <= part; i++) {
                if (rotation > -i * sweepAngle) {
                    return i - 1;
                }
            }
        } else {
            for (int i = 1; i <= part; i++) {
                if (rotation < i * sweepAngle) {
                    return part - i;
                }
            }
        }
        return result;
    }

    /**
     * @param event 角度 = Math.atan((dpPoint.y-dpCenter.y) / (dpPoint.x-dpCenter.x)) / π（3.14） * 180度
     *              Math.atan2(deltaY, deltaX) / Math.PI * 180;
     * @return
     */
    private double getDegrees(MotionEvent event) {
        double deltaY = event.getRawY() - centerOnScreenY;
        double deltaX = event.getRawX() - centerOnScreenX;
        double degrees = Math.atan2(deltaY, deltaX) / Math.PI * 180;
        if (degrees < 0)
            degrees += 360;
        return degrees;
    }

    private class AnimatorListener extends AnimatorListenerAdapter {
        @Override
        public void onAnimationEnd(Animator animation) {
            getRotation();
        }

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

        canvas.drawCircle(x, y, Math.min(x, y) - strokeWidth, arcPaint);

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

    private static class FixedSizeList<E> implements Iterable<E> {

        private List<E> linkedList = new LinkedList<>();

        static final int MAX_SIZE = 3;

        public boolean add(E e) {
            if (linkedList.size() >= MAX_SIZE)
                linkedList.remove(0);
            linkedList.add(e);
            return true;
        }

        public E get(int index) {
            if (index >= MAX_SIZE)
                throw new IllegalArgumentException("The max size is 3");
            return linkedList.get(index);
        }

        public int size() {
            return linkedList.size();
        }

        public Double getAverage() {
            Double sum = 0d;
            List<E> list = linkedList;
            for (E e : list) {
                sum += (Double) e;
            }
            return sum;
        }

        @Override
        public Iterator<E> iterator() {
            return null;
        }
    }

}
