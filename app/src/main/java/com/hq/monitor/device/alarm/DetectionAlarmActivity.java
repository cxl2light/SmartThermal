package com.hq.monitor.device.alarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.hq.base.ui.BaseActivity;
import com.hq.base.util.StatusBarUtil;
import com.hq.base.widget.CommonTitleBar;
import com.hq.monitor.R;
import com.hq.monitor.util.SpUtils;
import com.hq.monitor.util.Utils;

/**
 * 侦测警报
 * @author Administrator
 * @date 2022/2/10 0010 11:00
 */
public class DetectionAlarmActivity extends BaseActivity implements View.OnClickListener {

    private TextView textBtnSet, textBtnRecord, textBtnExit;
    private int[] openType = {R.string.detection_alarm_open, R.string.detection_alarm_close};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection_alarm);
        StatusBarUtil.setStatusBarWhiteMode(this);
        initView();
    }

    private void initView() {

        textBtnSet = findViewById(R.id.text_set);
        textBtnSet.setOnClickListener(this);
        textBtnRecord = findViewById(R.id.text_record);
        textBtnRecord.setOnClickListener(this);
        textBtnExit = findViewById(R.id.text_exit);
        textBtnExit.setOnClickListener(this);
        boolean isOpen = SpUtils.getBoolean(this, SpUtils.ALARM_NOTIFICATION_SWITCH, false);
        if (isOpen){
            textBtnExit.setText(getResources().getString(openType[1]));
        }
        else {
            textBtnExit.setText(getResources().getString(openType[0]));
        }


        CommonTitleBar mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setImageShareAndClick(R.mipmap.ic_menu_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.initShowMenuDialog(getSupportFragmentManager(), SpUtils.getBoolean(mActivity,Utils.DEVICE_CONNECT,false));
            }
        });
    }

    public static void startActivity(Context context) {
        if (context == null) {
            return;
        }
        final Intent intent = new Intent(context, DetectionAlarmActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_set:
                resetTextColor();
                textBtnSet.setTextColor(getResources().getColor(R.color.text_color_selected));
                DetectionAlarmSetActivity.startActivity(this);
                break;
            case R.id.text_record:
                resetTextColor();
                textBtnRecord.setTextColor(getResources().getColor(R.color.text_color_selected));
                DetectionAlarmListActivity.startActivity(this);
                break;
            case R.id.text_exit:
                resetTextColor();
                initBtn();
                break;
            default:
                break;
        }
    }

    private void initBtn() {
        textBtnExit.setTextColor(getResources().getColor(R.color.text_color_selected));
        boolean isOpen = SpUtils.getBoolean(this, SpUtils.ALARM_NOTIFICATION_SWITCH, false);
        if (isOpen){
            textBtnExit.setText(getResources().getString(openType[0]));
        }
        else {
            textBtnExit.setText(getResources().getString(openType[1]));
        }
        SpUtils.saveBoolean(this, SpUtils.ALARM_NOTIFICATION_SWITCH, !isOpen);
    }

    private void resetTextColor() {
        int colorWhite = getResources().getColor(R.color.white);
        textBtnSet.setTextColor(colorWhite);
        textBtnRecord.setTextColor(colorWhite);
        textBtnExit.setTextColor(colorWhite);
    }
}
