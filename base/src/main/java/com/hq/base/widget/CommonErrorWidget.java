package com.hq.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.hq.base.R;

/**
 * Created on 2020/4/5.
 * author :
 * desc :
 */
public class CommonErrorWidget extends LinearLayoutCompat implements View.OnClickListener {
    private AppCompatTextView mTip;

    public CommonErrorWidget(Context context) {
        this(context, null);
    }

    public CommonErrorWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonErrorWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.widget_common_error, this);
        mTip = findViewById(R.id.tip);
    }

    @Override
    public void onClick(View v) {

    }

    public void setTip(String tip) {
        mTip.setText(tip);
    }

}
