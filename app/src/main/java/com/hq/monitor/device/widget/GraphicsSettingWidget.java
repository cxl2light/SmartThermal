package com.hq.monitor.device.widget;

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
 * desc : 图像设置控件
 */
public class GraphicsSettingWidget extends LinearLayoutCompat implements View.OnClickListener {
    private final WidgetImageTextView acuityBtn, contrastRatioBtn, brightnessBtn, noiseReductionBtn;
    private WidgetImageTextView mPreSelected = null;
    private OnGraphicsSettingClickCallback clickCallback;
    private final ValuePackage acuityPackage, contrastRatioPackage, brightnessPackage, noiseReductionPackage;

    public GraphicsSettingWidget(Context context) {
        this(context, null);
    }

    public GraphicsSettingWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.widget_graphics_setting_layout, this);

        acuityBtn = findViewById(R.id.acuity_btn);
        acuityBtn.setOnClickListener(this);
        contrastRatioBtn = findViewById(R.id.contrast_ratio_btn);
        contrastRatioBtn.setOnClickListener(this);
        brightnessBtn = findViewById(R.id.brightness_btn);
        brightnessBtn.setOnClickListener(this);
        noiseReductionBtn = findViewById(R.id.noise_reduction_btn);
        noiseReductionBtn.setOnClickListener(this);

        acuityPackage = new ValuePackage(ValuePackage.TYPE_ACUITY, 0, 10);
        acuityPackage.setMinMaxStr("S-","S+");
        contrastRatioPackage = new ValuePackage(ValuePackage.TYPE_CONTRAST_RATIO, 1, 10);
        contrastRatioPackage.setMinMaxStr("C-","C+");
        brightnessPackage = new ValuePackage(ValuePackage.TYPE_BRIGHTNESS, 1, 10);
        brightnessPackage.setMinMaxStr("B-","B+");
        noiseReductionPackage = new ValuePackage(ValuePackage.TYPE_NOISE_REDUCTION, 0, 10);
        noiseReductionPackage.setMinMaxStr("F-", "F+");
    }

    public void updateValue(DeviceConfig deviceConfig){
        if (deviceConfig == null) {
            return;
        }
        acuityPackage.setCurrentValue(deviceConfig.getSharpness());
        contrastRatioPackage.setCurrentValue(deviceConfig.getContrast());
        brightnessPackage.setCurrentValue(deviceConfig.getBrightness());
        noiseReductionPackage.setCurrentValue(deviceConfig.getNoiseReduction());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.acuity_btn:
                // 锐度
                toggleSelected(acuityBtn);
                if (clickCallback != null) {
                    clickCallback.onAcuityClick(v, acuityPackage);
                }
                break;
            case R.id.contrast_ratio_btn:
                // 对比度
                toggleSelected(contrastRatioBtn);
                if (clickCallback != null) {
                    clickCallback.onContrastRatioClick(v, contrastRatioPackage);
                }
                break;
            case R.id.brightness_btn:
                // 亮度
                toggleSelected(brightnessBtn);
                if (clickCallback != null) {
                    clickCallback.onBrightnessClick(v, brightnessPackage);
                }
                break;
            case R.id.noise_reduction_btn:
                // 降噪
                toggleSelected(noiseReductionBtn);
                if (clickCallback != null) {
                    clickCallback.onNoiseReductionClick(v, noiseReductionPackage);
                }
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

    public void setOnClickCallback(OnGraphicsSettingClickCallback onClickCallback) {
        this.clickCallback = onClickCallback;
    }

    public interface OnGraphicsSettingClickCallback {

        /**
         * 锐度点击
         */
        void onAcuityClick(View v, @NonNull ValuePackage valuePackage);

        /**
         * 对比度点击
         */
        void onContrastRatioClick(View v, @NonNull ValuePackage valuePackage);

        /**
         * 亮度点击
         */
        void onBrightnessClick(View v, @NonNull ValuePackage valuePackage);

        /**
         * 降噪点击
         */
        void onNoiseReductionClick(View v, @NonNull ValuePackage valuePackage);

    }

}
