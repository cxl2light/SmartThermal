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
 * desc : 设置控件
 */
public class ColourAtlaWidget extends LinearLayoutCompat implements View.OnClickListener {
    private final WidgetImageTextView atlaBlackBtn, atlaWhiteBtn, atlaRedBtn, atlaGreenBtn, atlaBrownnessBtn, atlaRustBtn;
    private WidgetImageTextView mPreSelected = null;

    private OnPaletteChange mOnPaletteChange;

    public ColourAtlaWidget(Context context) {
        this(context, null);
    }

    public ColourAtlaWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.widget_colour_atla_layout, this);
        atlaBlackBtn = findViewById(R.id.atla_black);
        atlaBlackBtn.setOnClickListener(this);
        atlaWhiteBtn = findViewById(R.id.atla_white);
        atlaWhiteBtn.setOnClickListener(this);
        atlaRedBtn = findViewById(R.id.atla_red);
        atlaRedBtn.setOnClickListener(this);
        atlaGreenBtn = findViewById(R.id.atla_green);
        atlaGreenBtn.setOnClickListener(this);
        atlaBrownnessBtn = findViewById(R.id.atla_brownness);
        atlaBrownnessBtn.setOnClickListener(this);
        atlaRustBtn = findViewById(R.id.atla_rust);
        atlaRustBtn.setOnClickListener(this);
    }

    public void updateValue(@NonNull DeviceConfig deviceConfig) {
        switch (deviceConfig.getPalette()) {
            case 0:
                toggleSelected(atlaBlackBtn);
                break;
            case 1:
                toggleSelected(atlaWhiteBtn);
                break;
            case 2:
                toggleSelected(atlaRedBtn);
                break;
            case 3:
                toggleSelected(atlaGreenBtn);
                break;
            case 4:
                toggleSelected(atlaBrownnessBtn);
                break;
            case 5:
                toggleSelected(atlaRustBtn);
                break;
            default:
                break;
        }
    }

    public void setOnPaletteChange(OnPaletteChange onPaletteChange) {
        this.mOnPaletteChange = onPaletteChange;
    }

    @Override
    public void onClick(View v) {
        if (mOnPaletteChange == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.atla_black:
                // 黑色
                toggleSelected(atlaBlackBtn);
                mOnPaletteChange.onPaletteValue(0);
                break;
            case R.id.atla_white:
                // 白热
                toggleSelected(atlaWhiteBtn);
                mOnPaletteChange.onPaletteValue(1);
                break;
            case R.id.atla_red:
                // 红热
                toggleSelected(atlaRedBtn);
                mOnPaletteChange.onPaletteValue(2);
                break;
            case R.id.atla_green:
                // 绿热
                toggleSelected(atlaGreenBtn);
                mOnPaletteChange.onPaletteValue(3);
                break;
            case R.id.atla_brownness:
                // 褐色
                toggleSelected(atlaBrownnessBtn);
                mOnPaletteChange.onPaletteValue(4);
                break;
            case R.id.atla_rust:
                // 铁红
                toggleSelected(atlaRustBtn);
                mOnPaletteChange.onPaletteValue(5);
                break;
        }
    }

    private void toggleSelected(WidgetImageTextView widgetImageTextView) {
        if (widgetImageTextView == null) {
            return;
        }
        if (mPreSelected == widgetImageTextView && widgetImageTextView.isSelected()) {
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

    public interface OnPaletteChange {

        void onPaletteValue(int value);

    }

}
