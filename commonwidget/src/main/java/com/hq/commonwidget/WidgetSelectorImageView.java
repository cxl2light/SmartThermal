package com.hq.commonwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * author :
 * desc : 有选择器的AppCompatImageView，省去新建drawable的繁琐工作
 */
public class WidgetSelectorImageView extends AppCompatImageView {

    private Drawable drawableNormal;
    private Drawable drawableSelected;
    private Drawable drawablePressed;
    private Drawable drawableDisable;

    private StateListDrawable stateListDrawable;

    public WidgetSelectorImageView(Context context) {
        this(context, null);
    }

    public WidgetSelectorImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WidgetSelectorImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.WidgetSelectorImageView);
            drawableNormal = array.getDrawable(R.styleable.WidgetSelectorImageView_normal_src);
            drawableSelected = array.getDrawable(R.styleable.WidgetSelectorImageView_selected_src);
            drawablePressed = array.getDrawable(R.styleable.WidgetSelectorImageView_pressed_src);
            drawableDisable = array.getDrawable(R.styleable.WidgetSelectorImageView_disable_src);
            array.recycle();
        }
        stateListDrawable = new StateListDrawable();
        if (drawablePressed != null) {
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed}, drawablePressed);
        }
        if (drawableSelected != null) {
            stateListDrawable.addState(new int[]{android.R.attr.state_selected}, drawableSelected);
        }
        if (drawableDisable != null) {
            stateListDrawable.addState(new int[]{-android.R.attr.state_enabled}, drawableDisable);
        }
        if (drawableNormal != null) {
            stateListDrawable.addState(new int[]{}, drawableNormal);
        }
        setImageDrawable(stateListDrawable);
    }

}
