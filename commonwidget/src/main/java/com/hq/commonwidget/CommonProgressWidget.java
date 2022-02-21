package com.hq.commonwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

/**
 * Created on 2020/5/24
 * author :
 * desc :
 */
public class CommonProgressWidget extends LinearLayoutCompat {
    private final RotateAnimation mRotateAnimation;
    private AppCompatImageView progressImg;
    private AppCompatTextView progressTip;

    public CommonProgressWidget(Context context) {
        this(context, null);
    }

    public CommonProgressWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        LayoutInflater.from(context).inflate(R.layout.widget_common_progress, this);
        progressImg = findViewById(R.id.progress_img);
        progressTip = findViewById(R.id.progress_tip);

        mRotateAnimation = new RotateAnimation(0, 359,
                Animation.RELATIVE_TO_SELF, 0.5F,
                Animation.RELATIVE_TO_SELF, 0.5F);
        mRotateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setDuration(2000);
        if (getVisibility() == VISIBLE) {
            startAnim();
        }
    }

    public void setProgressTip(CharSequence charSequence) {
        progressTip.setText(charSequence);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            startAnim();
            return;
        }
        stopAnim();
    }

    private void startAnim() {
        progressImg.startAnimation(mRotateAnimation);
    }

    private void stopAnim() {
        progressImg.clearAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
    }
}
