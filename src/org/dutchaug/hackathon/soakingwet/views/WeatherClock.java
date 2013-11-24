package org.dutchaug.hackathon.soakingwet.views;

import org.dutchaug.hackathon.soakingwet.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class WeatherClock extends View {

    /**
     * The radius of the weahter wheel.
     */
    private int mWheelRadius;
    private int mPrefererWheelRadius;
    private int mPointerRadius;
    private float mTranslationOffset;

    private RectF mWheelRectangle = new RectF();

    private Paint mWheelPaint;
    private Paint mTextPaint;

    private float mAngle;

    private String[] mWeatherTimes = {"00", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55"};

    public WeatherClock(Context context) {
        super(context);
        init(null, 0);
    }

    public WeatherClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public WeatherClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WeatherClock, defStyle, 0);
        final Resources b = getContext().getResources();

        mWheelRadius = a.getDimensionPixelSize(R.styleable.WeatherClock_weather_wheel_radius, b.getDimensionPixelSize(R.dimen.weather_wheel_radius));
        mPrefererWheelRadius = mWheelRadius;
        mPointerRadius = a.getDimensionPixelSize(R.styleable.WeatherClock_weather_pointer_radius, b.getDimensionPixelSize(R.dimen.pointer_radius));

        a.recycle();

        if (!isInEditMode())
            mWeatherTimes = b.getStringArray(R.array.weather_times);

        mWheelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mWheelPaint.setColor(Color.RED);
        mWheelPaint.setStyle(Paint.Style.STROKE);
        mWheelPaint.setStrokeWidth(20);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(b.getColor(R.color.weather_text));

        mAngle = (float) (-Math.PI / 2);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(mTranslationOffset, mTranslationOffset);
        // canvas.drawOval(mWheelRectangle, mWheelPaint);

        // float[] pointerPosition = calculatePointerPosition(mAngle);

        for (int i = 0; i < 12; i++) {
            mAngle = (float) +((-Math.PI / 2) + ((Math.PI / 6) * i));
            float[] pointerPosition = calculatePointerPosition(mAngle);
            canvas.drawText(mWeatherTimes[i], pointerPosition[0], pointerPosition[1], mTextPaint);
            // mAngle = (float) (-Math.PI / 2);
        }
        
        
        // TODO Auto-generated method stub
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int intrinsicSize = 2 * (mPrefererWheelRadius + mPointerRadius);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        }
        else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(intrinsicSize, widthSize);
        }
        else {
            width = intrinsicSize;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        }
        else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(intrinsicSize, heightSize);
        }
        else {
            height = intrinsicSize;
        }

        int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        mTranslationOffset = min * 0.5f;

        // fill the rectangle instances.
        mWheelRadius = min / 2 - mPointerRadius;
        mWheelRectangle.set(-mWheelRadius, -mWheelRadius, mWheelRadius, mWheelRadius);
    }

    private float[] calculatePointerPosition(float angle) {
        float x = (float) (mWheelRadius * Math.cos(angle));
        float y = (float) (mWheelRadius * Math.sin(angle));

        return new float[] {x, y};
    }

}
