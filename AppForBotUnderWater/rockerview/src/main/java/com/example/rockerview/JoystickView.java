package com.example.rockerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Region;
import androidx.annotation.Nullable;

import android.util.AttributeSet;

/**
 * @author caibou
 */
public class JoystickView extends RockerView {

    private Region ballRegion, invalidRegion;

    private Paint paint = new Paint();
    private Point center;

    private int edgeRadius, stickRadius, dr;
    private float stickX, stickY;
    private int stickBallColor;

    private double currentAngle;

    private OnAngleUpdateListener angleUpdateListener;

    public JoystickView(Context context) {
        this(context, null);
    }

    public JoystickView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JoystickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialAttr(context, attrs);
        initialData();
    }

    private void initialAttr(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JoystickView);
        edgeRadius = typedArray.getDimensionPixelSize(R.styleable.JoystickView_edge_radius, 200);
        stickRadius = typedArray.getDimensionPixelSize(R.styleable.JoystickView_stick_radius,
                edgeRadius / 2);
        stickBallColor = typedArray.getColor(R.styleable.JoystickView_stick_color,
                getResources().getColor(R.color.stick_default_color));
        typedArray.recycle();
    }

    private void initialData() {
        dr = edgeRadius - stickRadius;
        center = centerPoint();
        stickX = center.x;
        stickY = center.y;

        Region ballRegionClip = new Region(center.x - dr, center.y - dr,
                center.x + dr, center.y + dr);
        Path rockerRulePath = new Path();
        rockerRulePath.addCircle(center.x, center.y, dr, Path.Direction.CW);
        ballRegion = new Region();
        ballRegion.setPath(rockerRulePath, ballRegionClip);

        int invalidRadius = edgeRadius / 3;
        Region invalidRegionClip = new Region(center.x - invalidRadius, center.y - invalidRadius,
                center.x + invalidRadius, center.y + invalidRadius);
        Path eventInvalidPath = new Path();
        eventInvalidPath.addCircle(center.x, center.y, invalidRadius, Path.Direction.CW);
        invalidRegion = new Region();
        invalidRegion.setPath(eventInvalidPath, invalidRegionClip);

        currentAngle = -1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        drawRockerEdge(canvas);
        drawStickBall(canvas);
    }

    protected void drawRockerEdge(Canvas canvas) {
        paint.reset();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);
        canvas.drawCircle(center.x, center.y, edgeRadius, paint);
    }

    protected void drawStickBall(Canvas canvas) {
        paint.reset();
        paint.setColor(stickBallColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(stickX, stickY, stickRadius, paint);
    }

    private void updateStickPos(float x, float y) {
        if (ballRegion.contains((int) x, (int) y)) {
            stickX = x;
            stickY = y;
        } else {
            float dx = x - center.x;
            float dy = y - center.y;
            float scale = (float) Math.sqrt((Math.pow(dx, 2) + Math.pow(dy, 2)));
            stickX = dx * dr / scale + center.x;
            stickY = dy * dr / scale + center.y;
        }
        invalidate();
    }

    private void resetStick() {
        currentAngle = -1;
        stickX = center.x;
        stickY = center.y;
        invalidate();
    }

    private void updateAngle(double angle, int action) {
        currentAngle = angle;
        if (angleUpdateListener != null) {
            angleUpdateListener.onAngleUpdate(angle, action);
        }
    }

    @Override
    protected void actionDown(float x, float y, double angle) {
        updateStickPos(x, y);
        if (!invalidRegion.contains((int) x, (int) y)) {
            updateAngle(angle, ACTION_PRESSED);
        }
    }

    @Override
    protected void actionMove(float x, float y, double angle) {
        updateStickPos(x, y);
        if (!invalidRegion.contains((int) x, (int) y)) {
            updateAngle(angle, ACTION_MOVE);
        }
    }

    @Override
    protected void actionUp(float x, float y, double angle) {
        resetStick();
        updateAngle(angle, ACTION_RELEASE);
    }

    /**
     * Returns the current angle.
     *
     * @return The current angle, -1 represent no touch action.
     */
    public double getCurrentAngle(){
        return currentAngle;
    }

    public boolean isPushedToEdgeArea(){//是否被推到边缘区域(dr/2以外)，如果推到边缘则快速运动，否则慢速运动
        float distance = (float)Math.sqrt( Math.pow(stickX-center.x,2) + Math.pow(stickY-center.y,2) ); //摇杆中心到圆心之间的距离，满距离为dr
        if(distance<=97*dr/100) return false; //仍在中心区域附近
        if(distance>97*dr/100) return true; //已经推到边缘区域
        return false;
    }

    public double getAbsoluteAngle(){//以正上方向为0°轴，顺时针旋转为正方向，取值范围[-180,180)
        float dx = stickX - center.x;
        float dy = center.y - stickY;//Android 坐标系
        double theta = Math.atan2(dx,dy);//[-pi,pi)
        double angle = theta/Math.PI*180;//transform to °
        return angle;
    }

    /**
     * Register a callback to be invoked when the angle is updated.
     *
     * @param angleUpdateListener The callback that will run.
     */
    public void setAngleUpdateListener(OnAngleUpdateListener angleUpdateListener) {
        this.angleUpdateListener = angleUpdateListener;
    }

    /**
     * Interface definition for a callback to be invoked when The angle between the finger
     * and the center of the circle update.
     */
    public interface OnAngleUpdateListener {

        /**
         * Called when angle has been clicked.
         *
         * @param angle  The angle between the finger and the center of the circle.
         * @param action action of the finger.
         */
        void onAngleUpdate(double angle, int action);
    }
}
