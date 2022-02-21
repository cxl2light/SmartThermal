package com.hq.monitor.device.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.hq.basebean.device.DeviceConfig;
import com.hq.commonwidget.WidgetImageTextView;
import com.hq.monitor.R;

/**
 * Created on 2020/5/3.
 * author :
 * desc : 设置控件
 */
public class SettingWidget extends LinearLayoutCompat implements View.OnClickListener {
    private final WidgetImageTextView polarizationBtn, coordinateAxisBtn, trackBtn, gpsBtn;
    private WidgetImageTextView mPreSelected = null;
    private OnSettingClickCallback onSettingClickCallback;
    private final ValuePackage polarizationPackage, xValuePackage, yValuePackage;

    public SettingWidget(Context context) {
        this(context, null);
    }

    public SettingWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.widget_setting_layout, this);
        polarizationBtn = findViewById(R.id.polarization_btn);
        polarizationBtn.setOnClickListener(this);
        coordinateAxisBtn = findViewById(R.id.coordinate_axis_btn);
        coordinateAxisBtn.setOnClickListener(this);
        trackBtn = findViewById(R.id.track_btn);
        trackBtn.setOnClickListener(this);
        gpsBtn = findViewById(R.id.gps_btn);
        gpsBtn.setOnClickListener(this);
        findViewById(R.id.close_btn).setOnClickListener(this);
        polarizationPackage = new ValuePackage(ValuePackage.TYPE_POLARIZATION, 0, 7);
        polarizationPackage.setMinMaxStr("R-","R+");
        xValuePackage = new ValuePackage(ValuePackage.TYPE_X, -512, 512);
        xValuePackage.setMinMaxStr("X-", "X+");
        xValuePackage.setCurrentValue(0);
        yValuePackage = new ValuePackage(ValuePackage.TYPE_Y, -384, 384);
        yValuePackage.setMinMaxStr("Y+", "Y-");
        yValuePackage.setCurrentValue(0);
    }

    public void updateValue(DeviceConfig deviceConfig) {
        if (deviceConfig == null) {
            return;
        }
        polarizationPackage.setCurrentValue(deviceConfig.getReticle());
        xValuePackage.setCurrentValue(deviceConfig.getX());
        yValuePackage.setCurrentValue(deviceConfig.getY());
        changeTintColor(trackBtn, deviceConfig.getTrackEn());
        changeTintColor(coordinateAxisBtn, deviceConfig.getPipEnable());
        gpsBtn.setSelected(deviceConfig.getGpsEn());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.polarization_btn:
                // 分划
                toggleSelected(polarizationBtn);
                if (onSettingClickCallback != null) {
                    onSettingClickCallback.onPolarizationClick(v, polarizationPackage);
                }
                break;
            case R.id.coordinate_axis_btn:
                // 画中画
                toggleSelected(coordinateAxisBtn);
                if (onSettingClickCallback != null) {
                    onSettingClickCallback.onPipClick(v);
                }
                break;
            case R.id.track_btn:
                // 跟踪
                changeTintColor(trackBtn, !trackBtn.isSelected());
                if (onSettingClickCallback != null) {
                    onSettingClickCallback.onTrackClick(v);
                }
                break;
            case R.id.gps_btn:
                // GPS
                toggleSelected(gpsBtn);
                if (onSettingClickCallback != null) {
                    onSettingClickCallback.onGPSClick(v);
                }
                break;
            case R.id.close_btn:
                final Context context = getContext();
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
                break;
            default:
                break;
        }
    }

    public void cancelSelected() {
        toggleSelected(mPreSelected);
        mPreSelected = null;
    }

    private void toggleSelected(WidgetImageTextView widgetImageTextView) {
        if (widgetImageTextView == null) {
            return;
        }
        if (mPreSelected == widgetImageTextView && widgetImageTextView.isSelected()) {
            changeTintColor(widgetImageTextView, !widgetImageTextView.isSelected());
            mPreSelected = null;
            return;
        }
        changeTintColor(mPreSelected, false);
        changeTintColor(widgetImageTextView, !widgetImageTextView.isSelected());
        mPreSelected = widgetImageTextView;
    }

    private void changeTintColor(WidgetImageTextView widgetImageTextView, boolean selected) {
        if (widgetImageTextView == null) {
            return;
        }
        widgetImageTextView.setSelected(selected);
        widgetImageTextView.getTextView().setSelected(selected);
        widgetImageTextView.getImageView().setSelected(selected);
        final Drawable drawable = widgetImageTextView.getImageView().getDrawable();
        if (drawable == null) {
            return;
        }
        drawable.setTint(getContext().getColor(selected ?
                R.color.tint_color_selected_dark_bg : R.color.tint_color_normal_dark_bg));
    }

    public void setOnSettingClickCallback(OnSettingClickCallback onSettingClickCallback) {
        this.onSettingClickCallback = onSettingClickCallback;
    }

    public interface OnSettingClickCallback {

        /**
         * 分划点击
         */
        void onPolarizationClick(View v, @NonNull ValuePackage valuePackage);

        /**
         * X-Y坐标点击
         */
        void onCoordinateClick(View v, @NonNull ValuePackage xValuePackage, @NonNull ValuePackage yValuePackage);

        /**
         * 跟踪点击
         */
        void onTrackClick(View v);

        /**
         * 画中画点击
         */
        void onPipClick(View v);

        /**
         * GPS点击
         */
        void onGPSClick(View v);

    }

}
