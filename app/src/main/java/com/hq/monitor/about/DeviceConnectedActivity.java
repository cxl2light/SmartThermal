package com.hq.monitor.about;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.hq.base.CommonConst;
import com.hq.base.ui.BaseActivity;
import com.hq.base.util.StatusBarUtil;
import com.hq.base.widget.CommonTitleBar;
import com.hq.basebean.device.DeviceBaseInfo;
import com.hq.commonwidget.WidgetSelectorBtn;
import com.hq.commonwidget.item_decoration.SpaceItemDecoration;
import com.hq.monitor.R;
import com.hq.monitor.adapter.DeviceStartAdapter;
import com.hq.monitor.adapter.QuickBean;
import com.hq.monitor.device.alarm.DetectionAlarmActivity;
import com.hq.monitor.device.menudialog.MenuDialog;
import com.hq.monitor.util.SpUtils;
import com.hq.monitor.util.Utils;

import java.util.ArrayList;

public class DeviceConnectedActivity extends BaseActivity implements View.OnClickListener{

    private static String TAG = "ZeNet=" + DeviceConnectedActivity.class.getSimpleName();

    private CommonTitleBar mTitleBar;
    private RecyclerView rvOne;
    private WidgetSelectorBtn btnDevice;
    ArrayList mDataList = new ArrayList();

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_connected);
        StatusBarUtil.setStatusBarWhiteMode(this);
        btnDevice = findViewById(R.id.btnDevice);
        btnDevice.getLayoutParams().width = CommonConst.getCommonWidgetWidth(mActivity);
        btnDevice.setOnClickListener(this);

        rvOne = findViewById(R.id.rvOne);

        initRecyclerList();

        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setImageShareAndClick(R.mipmap.ic_menu_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initShowMenuDialog();
            }
        });

//      DeviceBaseInfo deviceBaseInfo = (DeviceBaseInfo)getIntent().getSerializableExtra(Utils.DEVICE_INFO);
        DeviceBaseInfo deviceBaseInfo = (DeviceBaseInfo) SpUtils.getBeanFromSp(mActivity,SpUtils.SP_DEVICE_INFO);
        btnDevice.setText(deviceBaseInfo.getDevName());

        SpUtils.saveBoolean(mActivity,Utils.DEVICE_CONNECT,true);
    }

    private void initShowMenuDialog() {
        MenuDialog mDialog = new MenuDialog();
        mDialog.show(getSupportFragmentManager(), true,true, "title", "content");
    }

    private void initRecyclerList() {
        mDataList.clear();
        mDataList.add(new QuickBean(true,R.mipmap.icon_start_device,R.string.device_management));
        mDataList.add(new QuickBean(true,R.mipmap.icon_start_video,R.string.realtime_vision));
        mDataList.add(new QuickBean(true,R.mipmap.icon_aim_alarm,R.string.detection_alarm));
        //创建adapter
        rvOne.setLayoutManager(new GridLayoutManager(mActivity, 3));
        DeviceStartAdapter adapterOne = new DeviceStartAdapter();
        rvOne.setAdapter(adapterOne);
        rvOne.addItemDecoration(new SpaceItemDecoration(0,0,120,120));
        adapterOne.setList(mDataList);
        adapterOne.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (position == 0){
                    AboutDeviceActivity.startActivity(mActivity);
                } else if (position == 1){
                    Utils.connectDevice(mActivity);
                } else {
                    DetectionAlarmActivity.startActivity(mActivity);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDevice:
//                ConnectDeviceActivity.startActivity(mActivity);
                break;
            default:
                break;
        }
    }

    public static void startActivity(Context context, DeviceBaseInfo deviceBaseInfo) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, DeviceConnectedActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Utils.DEVICE_INFO, deviceBaseInfo);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void startActivity(Context context) {
        if (context == null) {
            return;
        }
//        context.startActivity(new Intent(context, DeviceConnectedActivity.class));
    }

}
