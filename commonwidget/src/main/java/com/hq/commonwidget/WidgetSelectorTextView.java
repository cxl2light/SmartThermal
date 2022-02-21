package com.hq.commonwidget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * @author :
 * @desc : 具有颜色选择器的textView，避免每次设置选择器颜色时需要创建一个xml文件
 * @time : 2018/01/02 16:32.
 */
public class WidgetSelectorTextView extends AppCompatTextView {

    private int[] color = {0xff000000, 0xff000000, 0xff000000, 0xff000000};
    private int[][] mStates;

    public WidgetSelectorTextView(Context context) {
        this(context, null);
    }

    public WidgetSelectorTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WidgetSelectorTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.WidgetSelectorTextView);
            color[0] = array.getColor(R.styleable.WidgetSelectorTextView_selected_color, color[0]);
            color[1] = array.getColor(R.styleable.WidgetSelectorTextView_disable_color, color[1]);
            color[2] = array.getColor(R.styleable.WidgetSelectorTextView_not_selected_color, color[2]);
            color[3] = array.getColor(R.styleable.WidgetSelectorTextView_normal_color, color[3]);
            array.recycle();
        }
        mStates = new int[4][];
        mStates[0] = new int[] { android.R.attr.state_selected };
        mStates[1] = new int[]{-android.R.attr.state_enabled};
        mStates[2] = new int[]{-android.R.attr.state_pressed};
        mStates[3] = new int[] {};
        ColorStateList colorStateList = new ColorStateList(mStates, color);
        setTextColor(colorStateList);
    }

    public void setSelectedColor(int color) {
        this.color[0] = color;
        ColorStateList colorStateList = new ColorStateList(mStates, this.color);
        setTextColor(colorStateList);
    }

    public void setNormalColor(int color) {
        this.color[2] = color;
        ColorStateList colorStateList = new ColorStateList(mStates, this.color);
        setTextColor(colorStateList);
    }

    public void setPressedColor(int color) {
        this.color[3] = color;
        ColorStateList colorStateList = new ColorStateList(mStates, this.color);
        setTextColor(colorStateList);
    }

    public void setEnabledColor(int color) {
        this.color[1] = color;
        ColorStateList colorStateList = new ColorStateList(mStates, this.color);
        setTextColor(colorStateList);
    }
}
