package com.hq.monitor.device.alarm;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hq.base.ui.BaseActivity;
import com.hq.base.util.Logger;
import com.hq.base.util.ScreenUtils;
import com.hq.base.util.StatusBarUtil;
import com.hq.base.widget.CommonTitleBar;
import com.hq.basebean.device.DeviceBaseInfo;
import com.hq.monitor.R;
import com.hq.monitor.adapter.AlarmClassInfo;
import com.hq.monitor.app.MyApplication;
import com.hq.monitor.db.NotificationInfo;
import com.hq.monitor.device.popup.AlarmRecordClassPop;
import com.hq.monitor.util.DateUtils;
import com.hq.monitor.util.SpUtils;
import com.hq.monitor.util.Utils;

import java.util.ArrayList;

/**
 * 侦测警报列表
 * @author Administrator
 * @date 2022/2/11 0011 19:42
 */
public class DetectionAlarmListActivity extends BaseActivity implements View.OnClickListener {

    private FrameLayout FLTab1, FLTab2, FLTab3, FLTab4;
    private TextView textTab1, textTab2, textTab3, textTab4;

    private ArrayList<AlarmClassInfo> deviceListData = new ArrayList<>();
    private ArrayList<AlarmClassInfo> classListData = new ArrayList<>();
    private ArrayList<AlarmClassInfo> timeListData = new ArrayList<>();
    private ArrayList<AlarmClassInfo> deleteListData = new ArrayList<>();

    private PopupWindow preShowingPopup;
    private AlarmRecordClassPop setDevicePopupWindow, setClassPopupWindow, setTimePopupWindow, setDeletePopupWindow;

    private RecyclerView recyclerView;
    private BaseQuickAdapter<NotificationInfo, BaseViewHolder> notificationAdapter;
    private ArrayList<NotificationInfo> recyclerViewData = new ArrayList<>();

    private DeviceBaseInfo deviceBaseInfo = SpUtils.getBeanFromSp(MyApplication.getAppContext(),SpUtils.SP_DEVICE_INFO);
    private String dev = deviceBaseInfo.getHardware();
    private int[] targetTypes = {R.string.detection_alarm_all, R.string.detection_alarm_animal, R.string.detection_alarm_person};
    private int[] saveDurations = {1,7,30};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection_alarm_list);
        StatusBarUtil.setStatusBarWhiteMode(this);
        initView();
    }

    /**
     * 初始化布局
     */
    private void initView() {

        FLTab1 = findViewById(R.id.fl_alarm_item_1);
        FLTab1.setOnClickListener(this);
        FLTab2 = findViewById(R.id.fl_alarm_item_2);
        FLTab2.setOnClickListener(this);
        FLTab3 = findViewById(R.id.fl_alarm_item_3);
        FLTab3.setOnClickListener(this);
        FLTab4 = findViewById(R.id.fl_alarm_item_4);
        FLTab4.setOnClickListener(this);

        textTab1 = findViewById(R.id.text_content_1);
        if (dev.isEmpty()){
            dev = SpUtils.getString(this, SpUtils.ALARM_NOTIFICATION_DEVICE_NAME, "");
        }
        textTab1.setText(dev);
        textTab2 = findViewById(R.id.text_content_2);
        textTab2.setText(getResources().getString(targetTypes[SpUtils.getInt(this, SpUtils.ALARM_TARGET_TYPE, 0)]));
        textTab3 = findViewById(R.id.text_content_3);
        textTab3.setText(SpUtils.getString(this, SpUtils.ALARM_NOTIFICATION_DATE, DateUtils.getStringDate()));
        textTab4 = findViewById(R.id.text_content_4);

        CommonTitleBar mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setImageShareAndClick(R.mipmap.ic_menu_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.initShowMenuDialog(getSupportFragmentManager(), SpUtils.getBoolean(mActivity,Utils.DEVICE_CONNECT,false));
            }
        });

        initRecyclerView();
    }

    /**
     * 初始化列表
     */
    private void initRecyclerView() {

        // 删除过期n天的记录
        int saveDurationType = SpUtils.getInt(this, SpUtils.ALARM_SAVE_TIME_STRING, 0);
        String date = DateUtils.getStringOverdueDate(saveDurations[saveDurationType]);
        MyApplication.getNotificationsDB().deleteOverdue(date);

        recyclerViewData = (ArrayList<NotificationInfo>) MyApplication.getNotificationsDB().find(dev,
                SpUtils.getInt(this, SpUtils.ALARM_TARGET_TYPE, 0),
                SpUtils.getString(this, SpUtils.ALARM_NOTIFICATION_DATE, DateUtils.getStringDate()));
        if (recyclerViewData == null)return;
        recyclerView = findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.setNestedScrollingEnabled(false);
        notificationAdapter = new BaseQuickAdapter<NotificationInfo, BaseViewHolder>(R.layout.detection_alarm_notification_item, recyclerViewData) {
            @Override
            protected void convert(@NonNull BaseViewHolder baseViewHolder, NotificationInfo notificationInfo) {
                TextView distance = baseViewHolder.getView(R.id.text_distance);
                ImageView imgType = baseViewHolder.getView(R.id.img_class_type);
                TextView time = baseViewHolder.getView(R.id.text_save_time);
                distance.setText(notificationInfo.getTarget_distance() + "m");
                if (notificationInfo.getTarget_type() == 1){
                    imgType.setImageDrawable(getDrawable(R.drawable.ic_alarm_animal));
                }
                else {
                    imgType.setImageDrawable(getDrawable(R.drawable.ic_alarm_person));
                }
                time.setText(notificationInfo.getNotification_time());
            }
        };
        recyclerView.setAdapter(notificationAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_alarm_item_1:
                showDevicePop();
                break;
            case R.id.fl_alarm_item_2:
                showClassPop();
                break;
            case R.id.fl_alarm_item_3:
                showSaveTimePop();
                break;
            case R.id.fl_alarm_item_4:
                showDeletePop();
                break;
            default:
                break;
        }
    }

    /**
     * 侦测报警分类pop
     */
    private void showDevicePop() {
        deviceListData = (ArrayList<AlarmClassInfo>) MyApplication.getNotificationsDB().findDeviceList();
        if (deviceListData == null)return;
        if (setDevicePopupWindow == null) {
            setDevicePopupWindow = new AlarmRecordClassPop(mActivity, deviceListData);
            setDevicePopupWindow.setOnOptionChange(value -> {
                setTextContent(textTab1, value, deviceListData);
                dev = deviceListData.get(value).getContent();
                SpUtils.saveString(this, SpUtils.ALARM_NOTIFICATION_DEVICE_NAME,dev);
                initRecyclerView();
                setDevicePopupWindow.dismiss();
            });
        }
        int right = (int) (ScreenUtils.getScreenWidthPixels(this) * 0.75f);
        int top = (int) getResources().getDimension(R.dimen.common_menu_popup_top_margin);
        showPop(setDevicePopupWindow, textTab1, right, top);
    }

    /**
     * 侦测报警分类pop
     */
    private void showClassPop() {
        classListData.clear();
        AlarmClassInfo info = new AlarmClassInfo();
        AlarmClassInfo info2 = new AlarmClassInfo();
        AlarmClassInfo info3 = new AlarmClassInfo();

        info.setContent(getResources().getString(targetTypes[0]));
        info2.setContent(getResources().getString(targetTypes[1]));
        info3.setContent(getResources().getString(targetTypes[2]));
        classListData.add(info);
        classListData.add(info2);
        classListData.add(info3);
        if (setClassPopupWindow == null) {
            setClassPopupWindow = new AlarmRecordClassPop(mActivity, classListData);
            setClassPopupWindow.setOnOptionChange(value -> {
                setTextContent(textTab2, value, classListData);
                SpUtils.saveInt(this, SpUtils.ALARM_TARGET_TYPE, value);
                initRecyclerView();
                setClassPopupWindow.dismiss();
            });
        }
        int right = (int) (ScreenUtils.getScreenWidthPixels(this) * 0.5f);
        int top = (int) getResources().getDimension(R.dimen.common_menu_popup_top_margin);
        showPop(setClassPopupWindow, textTab2, right, top);
    }

    /**
     * 侦测报警时间pop
     */
    private void showSaveTimePop() {
        timeListData = (ArrayList<AlarmClassInfo>) MyApplication.getNotificationsDB().findDateNotificationList();
        if (timeListData == null)return;
        if (setTimePopupWindow == null) {
            setTimePopupWindow = new AlarmRecordClassPop(mActivity, timeListData);
            setTimePopupWindow.setOnOptionChange(value -> {
                setTextContent(textTab3, value, timeListData);
                SpUtils.saveString(this,SpUtils.ALARM_NOTIFICATION_DATE, timeListData.get(value).getContent());
                initRecyclerView();
                setTimePopupWindow.dismiss();
            });
        }
        int right = (int) (ScreenUtils.getScreenWidthPixels(this) * 0.25f);
        int top = (int) getResources().getDimension(R.dimen.common_menu_popup_top_margin);
        showPop(setTimePopupWindow, textTab3, right, top);
    }

    /**
     * 删除记录pop
     */
    private void showDeletePop() {
        deleteListData.clear();
        AlarmClassInfo info = new AlarmClassInfo();
        AlarmClassInfo info2 = new AlarmClassInfo();
        info.setContent(getResources().getString(R.string.detection_alarm_delete_record));
        deleteListData.add(info);
        info2.setContent(getResources().getString(R.string.detection_alarm_delete_all));
        deleteListData.add(info2);
        if (setDeletePopupWindow == null) {
            setDeletePopupWindow = new AlarmRecordClassPop(mActivity, deleteListData);
            setDeletePopupWindow.setOnOptionChange(value -> {
                setTextContent(textTab4, 0, deleteListData);
                deleteNotificationData(value);
                setDeletePopupWindow.dismiss();
            });
        }
        int right = 0;
        int top = (int) getResources().getDimension(R.dimen.common_menu_popup_top_margin);
        showPop(setDeletePopupWindow, textTab4, right, top);
    }

    /**
     * 删除记录
     * @param value
     */
    private void deleteNotificationData(int value) {
        if (value == 0){
            MyApplication.getNotificationsDB().delete(SpUtils.getString(this, SpUtils.ALARM_NOTIFICATION_DATE, DateUtils.getStringDate()));
        }
        else {
            MyApplication.getNotificationsDB().deleteAllDate();
        }
        initRecyclerView();
    }

    /**
     * 跟新类别
     * @param textTab
     * @param value
     * @param mListData
     */
    private void setTextContent(TextView textTab, int value, ArrayList<AlarmClassInfo> mListData) {
        textTab.setText(mListData.get(value).getContent());
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

    public static void startActivity(Context context) {
        if (context == null) {
            return;
        }
        final Intent intent = new Intent(context, DetectionAlarmListActivity.class);
        context.startActivity(intent);
    }
}
