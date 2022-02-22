package com.hq.monitor.device.popup;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.hq.monitor.R;
import com.hq.monitor.device.widget.AlarmSetWidget;

/**
 * 侦测报警设置pop
 * @author Administrator
 * @date 2022/2/11 0011 15:02
 */
public class AlarmSetPopMenu extends PopupWindow {

    private AlarmSetWidget alarmSetWidget;

    public AlarmSetPopMenu(Context context, boolean show){
        alarmSetWidget = new AlarmSetWidget(context, show);
        setContentView(alarmSetWidget);
        setWidth((int) context.getResources().getDimension(R.dimen.common_menu_popup_width));
        setHeight((int) context.getResources().getDimension(R.dimen.common_menu_popup_height_4));
        setBackgroundDrawable(context.getDrawable(R.drawable.selector_transparent_bg));
        setOutsideTouchable(true);
        setTouchable(true);
        setFocusable(true);
    }

    public AlarmSetPopMenu(Context context) {
        alarmSetWidget = new AlarmSetWidget(context);
        setContentView(alarmSetWidget);
        setWidth((int) context.getResources().getDimension(R.dimen.common_menu_popup_width));
        setHeight((int) context.getResources().getDimension(R.dimen.common_menu_popup_height_3));
        setBackgroundDrawable(context.getDrawable(R.drawable.selector_transparent_bg));
        setOutsideTouchable(true);
        setTouchable(true);
        setFocusable(true);
    }

    public void setOnOptionChange(AlarmSetWidget.OnOptionChange onOptionChange) {
        alarmSetWidget.setOnOptionChange(onOptionChange);
    }
}
