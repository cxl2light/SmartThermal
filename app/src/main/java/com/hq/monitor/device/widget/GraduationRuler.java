package com.hq.monitor.device.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.hq.commonwidget.util.DensityUtil;

/**
 * Created on 2020/6/6
 * author :
 * desc :
 */
public class GraduationRuler extends View {
    private final int graduationWidth;
    private final int graduationInterval;
    private final int graduationColor = 0xFF000000;

    private final Paint mGraduationPaint;

    private boolean horizontal = true;

    public GraduationRuler(Context context) {
        this(context, null);
    }

    public GraduationRuler(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        graduationWidth = DensityUtil.dpToPx(context,1);
        graduationInterval = 4 * graduationWidth;
        mGraduationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGraduationPaint.setColor(graduationColor);
        mGraduationPaint.setStrokeWidth(graduationWidth);
        mGraduationPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (horizontal) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (horizontal) {
            final int stopY = getMeasuredHeight() - getPaddingBottom();
            final int startY = getPaddingTop();
            int startX = getPaddingLeft();
            while (startX <= getMeasuredWidth() - getPaddingRight()) {
                canvas.drawLine(startX, startY, startX, stopY, mGraduationPaint);
                startX += graduationInterval + graduationWidth;
            }
        } else {
            final int startX = getPaddingLeft();
            final int stopX = getMeasuredWidth() - getPaddingRight();
            int startY = getPaddingTop();
            while (startY <= getMeasuredHeight() - getPaddingBottom()) {
                canvas.drawLine(startX, startY, stopX, startY, mGraduationPaint);
                startY += graduationInterval + graduationWidth;
            }
        }
    }
}
