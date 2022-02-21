package com.hq.monitor.device.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import com.hq.commonwidget.WidgetSelectorBtn;
import com.hq.monitor.R;
import com.hq.monitor.device.widget.seekbar.IndicatorSeekBar;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SeekBarWidget extends LinearLayoutCompat implements View.OnClickListener {

    private static String TAG = "ZeTimer=" + SeekBarWidget.class.getSimpleName();

    private AppCompatTextView leftTet, rightText;
    private WidgetSelectorBtn valueIndicator;
    private ValuePackage mValuePackage;
    private OnSeekBarChange mOnSeekBarChange;

    IndicatorSeekBar ZeSeekBar;
    int mProgressNew = 0;
    int mProgressOld = 0;

    FrameLayout frameSeek;
    int i = 0;

    int mType = -1;

    /**
     * 长按可以调节，可以循环。
     */
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            if (mValuePackage == null || mOnSeekBarChange == null) {
                return;
            }

            int viewId = msg.what;
            if (viewId == R.id.left_text) {
                final int left = isHorizontal() ? -1 : 1;
                if (mValuePackage.getCurrentValue() + left < mValuePackage.getMin()) {
                    int type = mValuePackage.getType();
                    if (type == ValuePackage.TYPE_ZOOM || type == ValuePackage.TYPE_X || type == ValuePackage.TYPE_Y){
                        return;
                    } else {
                        mValuePackage.setCurrentValue(mValuePackage.getMax());
                    }
                } else {
                    mProgressNew = ZeSeekBar.getProgress();
                    mProgressNew += left;
                    seekBarUpdateData();
                    return;
//                    mValuePackage.setCurrentValue(mValuePackage.getCurrentValue() + left);
                }
            } else if (viewId == R.id.right_text) {
                final int right = isHorizontal() ? 1 : -1;
                if (mValuePackage.getCurrentValue() + right > mValuePackage.getMax()) {
                    int type = mValuePackage.getType();
                    if (type == ValuePackage.TYPE_ZOOM || type == ValuePackage.TYPE_X || type == ValuePackage.TYPE_Y){
                        return;
                    } else {
                        mValuePackage.setCurrentValue(mValuePackage.getMin());
                    }
                } else {
                    mProgressNew = ZeSeekBar.getProgress();
                    mProgressNew += right;
                    seekBarUpdateData();
                    return;
//                    mValuePackage.setCurrentValue(mValuePackage.getCurrentValue() + right);
                }
            }
            Log.d("setIndicatorValue1001", "getCurrentValue=" + mValuePackage.getCurrentValue());
            setIndicatorValue(mValuePackage);
            mOnSeekBarChange.onValueChange(mValuePackage);
        }
    };

    public SeekBarWidget(Context context) {
        this(context, null);
    }

    public SeekBarWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackground(ContextCompat.getDrawable(context, R.drawable.common_round_rect_dark_bg));
        LayoutInflater.from(context).inflate(R.layout.widget_seek_bar_layout, this);
        valueIndicator = findViewById(R.id.value_indicator);
        valueIndicator.setGravity(Gravity.CENTER);
        leftTet = findViewById(R.id.left_text);
        frameSeek = findViewById(R.id.seek_bar);
//        leftTet.setOnClickListener(this);
        rightText = findViewById(R.id.right_text);
//        rightText.setOnClickListener(this);

        ZeSeekBar = findViewById(R.id.seekBarOne);

        if (getOrientation() == VERTICAL) {
            final GraduationRuler ruler = findViewById(R.id.ruler);
            ruler.setHorizontal(false);
            ZeSeekBar.setHorizontal(false);

            ViewGroup.LayoutParams layoutParams = ZeSeekBar.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            ZeSeekBar.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = ZeSeekBar.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            ZeSeekBar.setLayoutParams(layoutParams);
        }

        leftTet.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Log.d(TAG,"ACTION_DOWN:");
                    updateAddOrSubtract(v.getId());    //手指按下时触发不停的发送消息
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    Log.d(TAG,"ACTION_UP:");
                    stopAddOrSubtract();    //手指抬起时停止发送
                }
                return true;
            }
        });

        rightText.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    Log.d(TAG,"ACTION_DOWN:");
                    updateAddOrSubtract(v.getId());    //手指按下时触发不停的发送消息
                }else if(event.getAction() == MotionEvent.ACTION_UP){
                    Log.d(TAG,"ACTION_UP:");
                    stopAddOrSubtract();    //手指抬起时停止发送
                }
                return true;
            }
        });

//        valueIndicator.setOnLongClickListener(new OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//
//                return false;
//            }
//        });

        ZeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("ZeSeekBarPro:" , "progress:" + progress + ",max=" + seekBar.getMax() + ",curValue=" + mValuePackage.getCurrentValue());
                mProgressOld = mProgressNew;
                mProgressNew = progress;

                Log.d("ZeBar","mProgressOld=" + mProgressOld + ",mProgressNew=" + mProgressNew);

                //初次进入，放大倍数与设备倍数不同步，更新的时候会出现不同倍数切换，视图闪烁。
                // 过滤掉本地的数据，只更新获取设备的最新数据
                if (mType != mValuePackage.getType()){  //由于使用的是同一个seekbarOne，注意type切换
                    mType = mValuePackage.getType();
                    i= 0;
                }
                i++;
                if (i > 1){
                    seekBarUpdateData();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("ZeSeekBarTrack:" , "start:" + mValuePackage.getCurrentValue());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarUpdateData();
                Log.d("ZeSeekBarTrack:" , "stop:" + mValuePackage.getCurrentValue() + ",mProgressNew=" + mProgressNew );
            }
        });
    }

    private void seekBarUpdateData() {
        Log.d("ZeSeekBarUpdate:" , "update:" + mValuePackage.getCurrentValue() + ",mProgressNew=" + mProgressNew );
        mValuePackage.setCurrentValue(mProgressNew + mValuePackage.getMin());
        mOnSeekBarChange.onValueChange(mValuePackage);
        updateValue();
        Log.d("ZeSeekBarUpdate:" , "update:" + mValuePackage.getCurrentValue() + ",mProgressNew=" + mProgressNew );
    }

    public void setBackGroundRes(Drawable drawable){
        setBackground(drawable);
    }

    public void setOnSeekBarChange(OnSeekBarChange onSeekBarChange) {
        this.mOnSeekBarChange = onSeekBarChange;
    }

    private boolean isHorizontal(){
        return getOrientation() == HORIZONTAL;
    }

    @Override
    public void onClick(View v) {
        if (mValuePackage == null || mOnSeekBarChange == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.left_text:
                final int left = isHorizontal() ? -1 : 1;
                if (mValuePackage.getCurrentValue() + left < mValuePackage.getMin()) {
                    return;
                }
                mValuePackage.setCurrentValue(mValuePackage.getCurrentValue() + left);
                break;
            case R.id.right_text:
                final int right = isHorizontal() ? 1 : -1;
                if (mValuePackage.getCurrentValue() + right > mValuePackage.getMax()) {
                    return;
                }
                mValuePackage.setCurrentValue(mValuePackage.getCurrentValue() + right);
                break;
            default:
                break;
        }

        Log.d("setIndicatorValue1002", "getCurrentValue=" + mValuePackage.getCurrentValue());
        setIndicatorValue(mValuePackage);
        mOnSeekBarChange.onValueChange(mValuePackage);
    }

    public void setLeftRightText(String left, String right) {
        leftTet.setText(left);
        rightText.setText(right);
    }

    public void setData(@NonNull ValuePackage valuePackage) {
        mValuePackage = valuePackage;
        Log.d("setIndicatorValue1003", "getCurrentValue=" + mValuePackage.getCurrentValue());
        setIndicatorValue(valuePackage);
        setLeftRightText(valuePackage.getMinStr(), valuePackage.getMaxStr());

        ZeSeekBar.setProgress(valuePackage.getCurrentValue() - valuePackage.getMin());
    }

    private void setIndicatorValue(@NonNull ValuePackage valuePackage) {
        int curValue = mValuePackage.getCurrentValue();
        int maxValue = mValuePackage.getMax();
        int minValue = mValuePackage.getMin();
        Log.d("setIndicatorValue","curValue=" + curValue + ",maxValue=" + maxValue + ",minValue=" + minValue);
        ZeSeekBar.setMax(maxValue - minValue);
        int progress = curValue - minValue;

        if (curValue < minValue){
            curValue = minValue;
        }
        if (curValue > maxValue){
            curValue = maxValue;
        }

        if (valuePackage.getType() == ValuePackage.TYPE_ZOOM) {
//            valueIndicator.setText(String.format("%.1f", curValue / 10f));
            ZeSeekBar.setIndicatorProgress(progress,String.format("%.1f", curValue / 10f));
            Log.d("TYPE_ZOOM","curValue=" + curValue + ",maxValue=" + maxValue + ",minValue=" + minValue);
        } else {
//            valueIndicator.setText(String.valueOf(curValue));
            ZeSeekBar.setIndicatorProgress(progress,String.valueOf(curValue));
            Log.d("TYPE_OTHRE","curValue=" + curValue + ",maxValue=" + maxValue + ",minValue=" + minValue);
        }
    }

    public void updateValue() {
        if (mValuePackage == null) {
            return;
        }
        Log.d("setIndicatorValue1004", "getCurrentValue=" + mValuePackage.getCurrentValue());
        setIndicatorValue(mValuePackage);
    }

    public interface OnSeekBarChange {

        void onValueChange(@NonNull ValuePackage valuePackage);

    }

    private ScheduledExecutorService scheduledExecutor;
    private void updateAddOrSubtract(int viewId) {
        final int vid = viewId;
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = vid;
                handler.sendMessage(msg);
            }
        }, 0, 400, TimeUnit.MILLISECONDS);    //每间隔100ms发送Message
    }

    private void stopAddOrSubtract() {
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdownNow();
            scheduledExecutor = null;
        }
    }

}
