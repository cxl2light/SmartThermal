package com.hq.monitor.device.alarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hq.base.ui.BaseActivity;
import com.hq.base.util.StatusBarUtil;
import com.hq.base.widget.CommonTitleBar;
import com.hq.monitor.R;
import com.hq.monitor.device.popup.AlarmSetPopMenu;
import com.hq.monitor.util.SpUtils;
import com.hq.monitor.util.Utils;

/**
 * 侦测警报设置
 * @author Administrator
 * @date 2022/2/10 0010 18:35
 */
public class DetectionAlarmSetActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout LLMode, LLSaveTime, LLInterval;
    private TextView textMode, textSaveTime, textInterval;
    private TextView tagMode, tagSaveTime, tagInterval;

    private int[] modes = {R.string.detection_alarm_mode_mute, R.string.detection_alarm_mode_vibrate, R.string.detection_alarm_mode_sound};
    private int[] saveTimes = {R.string.detection_alarm_save_one_day, R.string.detection_alarm_save_a_week, R.string.detection_alarm_save_one_month};
    private int[] intervals = {R.string.detection_alarm_interval_1_minute, R.string.detection_alarm_interval_10_minutes, R.string.detection_alarm_interval_30_minutes};

    private PopupWindow preShowingPopup;
    private AlarmSetPopMenu setModePopupWindow, setSaveTimePopupWindow, setIntervalPopupWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection_alarm_set);
        StatusBarUtil.setStatusBarWhiteMode(this);
        initView();
    }

    private void initView() {

        LLMode = findViewById(R.id.ll_alarm_mode);
        LLMode.setOnClickListener(this);
        LLSaveTime = findViewById(R.id.ll_alarm_save_time);
        LLSaveTime.setOnClickListener(this);
        LLInterval = findViewById(R.id.ll_alarm_interval);
        LLInterval.setOnClickListener(this);

        textMode = findViewById(R.id.text_mode);
        textMode.setText(modes[SpUtils.getInt(this, SpUtils.ALARM_MODE_STRING, 1)]);
        textSaveTime = findViewById(R.id.text_save_time);
        textSaveTime.setText(saveTimes[SpUtils.getInt(this, SpUtils.ALARM_SAVE_TIME_STRING, 0)]);
        textInterval = findViewById(R.id.text_interval);
        textInterval.setText(intervals[SpUtils.getInt(this, SpUtils.ALARM_INTERVAL_STRING, 0)]);
        tagMode = findViewById(R.id.text_tag_mode);
        tagSaveTime = findViewById(R.id.text_tag_save_time);
        tagInterval = findViewById(R.id.text_tag_interval);

        CommonTitleBar mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setImageShareAndClick(R.mipmap.ic_menu_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.initShowMenuDialog(getSupportFragmentManager(), SpUtils.getBoolean(mActivity,Utils.DEVICE_CONNECT,false));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_alarm_mode:
                showSetModePopup();
                break;
            case R.id.ll_alarm_save_time:
                showSetSaveTimePopup();
                break;
            case R.id.ll_alarm_interval:
                showSetIntervalPopup();
                break;
            default:
                break;
        }
    }

    /**
     * 设置提醒方式POP
     */
    private void showSetModePopup() {
        SpUtils.saveInt(this,SpUtils.ALARM_SET_POP_TYPE, SpUtils.ALARM_MODE);
        resetTagTextView(tagMode);
        if (setModePopupWindow == null) {
            setModePopupWindow = new AlarmSetPopMenu(mActivity);
            setModePopupWindow.setOnOptionChange(value -> {
                setTextContent(textMode, value);
                SpUtils.saveInt(this,SpUtils.ALARM_MODE_STRING, value);
                setModePopupWindow.dismiss();
            });
        }
        int right = (int) getResources().getDimension(R.dimen.common_menu_popup_right_margin);
        int top = (int) getResources().getDimension(R.dimen.common_menu_popup_top_margin);
        showPop(setModePopupWindow, textMode, right, top);
    }

    /**
     * 保存通知时间POP
     */
    private void showSetSaveTimePopup() {
        SpUtils.saveInt(this,SpUtils.ALARM_SET_POP_TYPE, SpUtils.ALARM_SAVE_TIME);
        resetTagTextView(tagSaveTime);
        if (setSaveTimePopupWindow == null) {
            setSaveTimePopupWindow = new AlarmSetPopMenu(mActivity);
            setSaveTimePopupWindow.setOnOptionChange(value -> {
                setTextContent(textSaveTime, value);
                SpUtils.saveInt(this,SpUtils.ALARM_SAVE_TIME_STRING, value);
                setSaveTimePopupWindow.dismiss();
            });
        }
        int right = (int) getResources().getDimension(R.dimen.common_menu_popup_right_margin);
        int top = (int) getResources().getDimension(R.dimen.common_menu_popup_top_margin_2);
        showPop(setSaveTimePopupWindow, textSaveTime, right, top);
    }

    /**
     * 通知时间间隔POP
     */
    private void showSetIntervalPopup() {
        SpUtils.saveInt(this,SpUtils.ALARM_SET_POP_TYPE, SpUtils.ALARM_INTERVAL);
        resetTagTextView(tagInterval);
        if (setIntervalPopupWindow == null) {
            setIntervalPopupWindow = new AlarmSetPopMenu(mActivity);
            setIntervalPopupWindow.setOnOptionChange(value -> {
                setTextContent(textInterval, value);
                SpUtils.saveInt(this,SpUtils.ALARM_INTERVAL_STRING, value);
                setIntervalPopupWindow.dismiss();
            });
        }
        int right = (int) getResources().getDimension(R.dimen.common_menu_popup_right_margin);
        int top = (int) getResources().getDimension(R.dimen.common_menu_popup_top_margin_3);
        showPop(setIntervalPopupWindow, textInterval, right, top);
    }

    /**
     * 显示pop
     * @param popupWindow
     * @param curView
     * @param xOffset
     * @param yOffset
     */
    private void showPop(@NonNull PopupWindow popupWindow, @NonNull View curView, int xOffset, int yOffset) {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
            return;
        }
        if (preShowingPopup != null && preShowingPopup.isShowing()) {
            preShowingPopup.dismiss();
        }
        if (isFinishing() || isDestroyed()) {
            return;
        }
        popupWindow.showAtLocation(curView, Gravity.TOP|Gravity.RIGHT,
                xOffset, yOffset);
        preShowingPopup = popupWindow;
    }

    /**
     * 设置选中字体颜色
     * @param tagView
     */
    private void resetTagTextView(TextView tagView) {
        int colorWhite = getResources().getColor(R.color.white);
        if (tagMode != null)
        {
            tagMode.setTextColor(colorWhite);
        }
        if (tagSaveTime != null)
        {
            tagSaveTime.setTextColor(colorWhite);
        }
        if (tagInterval != null)
        {
            tagInterval.setTextColor(colorWhite);
        }

        if (tagView != null){
            tagView.setTextColor(getResources().getColor(R.color.text_color_selected));
        }
    }

    /**
     * 跟新设置
     * @param btnView
     * @param value
     */
    private void setTextContent(TextView btnView, int value) {
        int POP_TYPE = SpUtils.getInt(this,SpUtils.ALARM_SET_POP_TYPE, 0);
        if (POP_TYPE == SpUtils.ALARM_MODE){
            btnView.setText(getResources().getString(modes[value]));
        }
        else if (POP_TYPE == SpUtils.ALARM_SAVE_TIME){
            btnView.setText(getResources().getString(saveTimes[value]));
        }
        else if (POP_TYPE == SpUtils.ALARM_INTERVAL){
            btnView.setText(getResources().getString(intervals[value]));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preShowingPopup != null && preShowingPopup.isShowing()) {
            preShowingPopup.dismiss();
        }
    }

    public static void startActivity(Context context) {
        if (context == null) {
            return;
        }
        final Intent intent = new Intent(context, DetectionAlarmSetActivity.class);
        context.startActivity(intent);
    }
}
