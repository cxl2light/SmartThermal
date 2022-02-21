package com.hq.commonwidget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.hq.commonwidget.util.DensityUtil;

/**
 * author :
 * desc : 有选择器的AppCompatImageView，省去新建drawable的繁琐工作
 */
public class WidgetImageTextView extends LinearLayoutCompat {

    private Drawable drawableNormal;
    private Drawable drawableSelected;
    private Drawable drawablePressed;
    private Drawable drawableDisable;
    private Drawable drawableLeft;

    private final AppCompatImageView mImageView;
    private final AppCompatTextView mTextView;

    public WidgetImageTextView(Context context) {
        this(context, null);
    }

    public WidgetImageTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WidgetImageTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mImageView = new AppCompatImageView(context);
        mTextView = new AppCompatTextView(context, attrs);
        addView(mImageView, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addView(mTextView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            final TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.WidgetImageTextView);
            initTextView(array);

            drawableNormal = array.getDrawable(R.styleable.WidgetImageTextView_normal_src);
            drawableSelected = array.getDrawable(R.styleable.WidgetImageTextView_selected_src);
            drawablePressed = array.getDrawable(R.styleable.WidgetImageTextView_pressed_src);
            drawableDisable = array.getDrawable(R.styleable.WidgetImageTextView_disable_src);
            drawableLeft = array.getDrawable(R.styleable.WidgetImageTextView_left_src);

            final int width = (int) array.getDimension(R.styleable.WidgetImageTextView_image_w, -1);
            final int height = (int) array.getDimension(R.styleable.WidgetImageTextView_image_h, -1);
            if (width > 0 && height > 0) {
                final ViewGroup.LayoutParams layoutParams = mImageView.getLayoutParams();
                layoutParams.width = width;
                layoutParams.height = height;
                mImageView.setLayoutParams(layoutParams);
            }
            final boolean imageVisible = array.getBoolean(R.styleable.WidgetImageTextView_image_visible, true);
            mImageView.setVisibility(imageVisible ? VISIBLE : GONE);

            array.recycle();
        }
        if (drawableNormal != null) {
            if (drawableSelected != null || drawablePressed != null || drawableDisable != null) {
                final StateListDrawable stateListDrawable = new StateListDrawable();
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
                mImageView.setImageDrawable(stateListDrawable);
            } else {
                mImageView.setImageDrawable(drawableNormal);
            }
        }
        if (drawableLeft != null){
//            https://blog.csdn.net/catoop/article/details/39959175
            drawableLeft.setBounds(0, 0, drawableLeft.getMinimumWidth(), drawableLeft.getMinimumHeight());
            mTextView.setCompoundDrawablePadding(-10);
            mTextView.setCompoundDrawables(drawableLeft, null, null, null);
        }
    }

    private int[] textColor = {0xff000000, 0xff000000, 0xff000000, 0xff000000};

    private void initTextView(@NonNull TypedArray array) {
        final int maxLines = array.getInt(R.styleable.WidgetImageTextView_text_max_lines, -1);
        if (maxLines > 0) {
            mTextView.setMaxLines(maxLines);
        }
        final float textSize = array.getDimension(R.styleable.WidgetImageTextView_text_size, DensityUtil.dpToPx(getContext(), 12));
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        final int maxLength = array.getInt(R.styleable.WidgetImageTextView_max_length, -1);
        if (maxLength > 0) {
            mTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        }
        final String text = array.getString(R.styleable.WidgetImageTextView_text_str);
        mTextView.setText(text);
        final int imageTextSpace = (int) array.getDimension(R.styleable.WidgetImageTextView_image_text_space, -1);
        if (imageTextSpace > 0) {
            if (getOrientation() == VERTICAL) {
                mTextView.setPadding(mTextView.getPaddingLeft(), mTextView.getPaddingTop() + imageTextSpace,
                        mTextView.getPaddingRight(), mTextView.getPaddingBottom());
            } else {
                mTextView.setPadding(mTextView.getPaddingLeft() + imageTextSpace, mTextView.getPaddingTop(),
                        mTextView.getPaddingRight(), mTextView.getPaddingBottom());
            }
        }

        textColor[0] = array.getColor(R.styleable.WidgetImageTextView_selected_color, textColor[0]);
        textColor[1] = array.getColor(R.styleable.WidgetImageTextView_disable_color, textColor[1]);
        textColor[2] = array.getColor(R.styleable.WidgetImageTextView_not_selected_color, textColor[2]);
        textColor[3] = array.getColor(R.styleable.WidgetImageTextView_normal_color, textColor[3]);
        final int[][] mStates = new int[4][];
        mStates[0] = new int[]{android.R.attr.state_selected};
        mStates[1] = new int[]{-android.R.attr.state_enabled};
        mStates[2] = new int[]{-android.R.attr.state_pressed};
        mStates[3] = new int[]{};
        final ColorStateList colorStateList = new ColorStateList(mStates, textColor);
        mTextView.setTextColor(colorStateList);
    }

    @NonNull
    public AppCompatTextView getTextView() {
        return mTextView;
    }

    @NonNull
    public AppCompatImageView getImageView() {
        return mImageView;
    }

}
