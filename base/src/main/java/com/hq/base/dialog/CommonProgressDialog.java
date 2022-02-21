package com.hq.base.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.hq.base.R;
import com.hq.commonwidget.CommonProgressWidget;

/**
 * Created on 2020/5/28
 * author :
 * desc :
 */
public class CommonProgressDialog extends BaseDialog implements View.OnClickListener {
    private final CommonProgressWidget commonProgressWidget;

    public CommonProgressDialog(Context context) {
        this(context, R.style.ProgressStyle);
    }

    public CommonProgressDialog(Context context, int theme) {
        super(context, theme);
        commonProgressWidget = new CommonProgressWidget(context);
        commonProgressWidget.setBackgroundResource(android.R.color.transparent);
        commonProgressWidget.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        setContentView(commonProgressWidget);
        setWidthPercent(-1);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    public void setProgressTip(String tip) {
        commonProgressWidget.setProgressTip(tip);
    }
}
