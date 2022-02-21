package com.hq.monitor.device.widget.seekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

import com.hq.monitor.R;

/**
 * 主页
 * <p>
 * Created by yangle on 2018/11/30.
 * Website：http://www.yangle.tech
 * https://www.jianshu.com/p/b753c4a9ddfa
 */

public class IndicatorSeekBar extends VerticalSeekBar {

    // 画笔
    private Paint mPaint;
    // 进度文字位置信息
    private Rect mProgressTextRect = new Rect();
    // 滑块按钮宽度
    private int mThumbWidth = dp2px(30);

    String progressText = "TAG";
    String progressTextOld = "TAG";

    public IndicatorSeekBar(Context context) {
        this(context, null);
    }

    public IndicatorSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.seekBarStyle);
    }

    public IndicatorSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new TextPaint();
        mPaint.setAntiAlias(true);
//        mPaint.setColor(Color.parseColor("#00574B"));
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(sp2px(12));

        // 如果不设置padding，当滑动到最左边或最右边时，滑块会显示不全
//        setPadding(mThumbWidth / 2, 0, mThumbWidth / 2, 0);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("progressTextOld","old=" + progressTextOld + ",progressText=" + progressText);
        //文字相同的时候,未赋值的时候，null的时候，不需要重新绘制。
        if (progressText == null || TextUtils.isEmpty(progressText) || (progressTextOld.equals(progressText) && progressText.equals("TAG"))){
            return;
        }

        mPaint.getTextBounds(progressText, 0, progressText.length(), mProgressTextRect);

        // 进度百分比
        float progressRatio = (float) getProgress() / getMax();
        Log.d("mThumbWidth","mThumbWidth=" + mThumbWidth + ",progressText=" + progressText + ",length=" + progressText.length() + ",progressRatio=" + progressRatio + ",mProgressTextRect=" + mProgressTextRect.width());
        if (getHorizontal()){
            // thumb偏移量
            float thumbOffset =  (mThumbWidth - mProgressTextRect.width()) / 2 - mThumbWidth * progressRatio;
            float thumbX = getWidth() * progressRatio + (0.5f-progressRatio) * mProgressTextRect.width() / 2 + thumbOffset;
            float thumbY = getHeight() / 2f + mProgressTextRect.height() / 2f;
            canvas.drawText(progressText, thumbX, thumbY, mPaint);
        } else {
            // thumb偏移量
            float thumbOffset =  (mThumbWidth - mProgressTextRect.height()) / 2 - mThumbWidth * progressRatio;
            float thumbX = getWidth() / 2f - mProgressTextRect.width() / 2f;
            float thumbY = getHeight() * progressRatio + thumbOffset + (0.5f-progressRatio) * mProgressTextRect.height();
            Path mPath = new Path();
            mPath.moveTo(thumbY,0);
            mPath.lineTo(thumbY,1000);
            canvas.drawTextOnPath(progressText,mPath, thumbX, 0, mPaint);
            Log.d("ZeSeek","progressText:" + progressText + ",thumbX:" + thumbX + ",thumbY:" + thumbY + ",getX:" + getX() + ",getY:" + getY() + ",getLeft" + getLeft() + ",getTop:" + getTop());
        }

        progressTextOld = progressText;
    }

    public void setProgressText(String text){
        progressText = text;
        notifyProgress();
        postInvalidate(); //动态绘制文字
    }

    public void setIndicatorProgress(int progress,String text) {
        setProgress(progress);
        setProgressText(text);
    }

    /**
     * dp转px
     *
     * @param dp dp值
     * @return px值
     */
    public int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param sp sp值
     * @return px值
     */
    private int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }
}
