package com.hq.monitor.ui.searchhelp.devicesearchhelp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.hq.base.ui.BaseActivity;
import com.hq.base.widget.CommonTitleBar;
import com.hq.commonwidget.item_decoration.SpaceItemDecoration;
import com.hq.monitor.R;
import com.hq.monitor.adapter.QuickBean;
import com.hq.monitor.device.menudialog.MenuDialog;
import com.hq.monitor.ui.searchhelp.devicestart.DeviceSearchHelpDeviceActivity;
import com.hq.monitor.ui.searchhelp.mobilestart.DeviceSearchHelpMobileActivity;
import com.hq.monitor.ui.searchhelp.wifistart.DeviceSearchHelpWifiActivity;

import java.util.ArrayList;

/**
 * 设备列表页面
 */
public class DeviceSearchHelpActivity extends BaseActivity{

    private static String TAG = "ZeNet=" + DeviceSearchHelpActivity.class.getSimpleName();

    private CommonTitleBar mTitleBar;
    private ArrayList mDataList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan_help);
//        StatusBarUtil.setStatusBarWhiteMode(this);
        initRecyclerList();

        mTitleBar = findViewById(R.id.title_bar);
//        mTitleBar.setImageShareAndClick(R.mipmap.ic_menu_white, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                initShowMenuDialog();
//            }
//        });
    }

    private void initShowMenuDialog() {
        MenuDialog mDialog = new MenuDialog();
        mDialog.show(getSupportFragmentManager(), true,false, "title", "content");
    }

    private void initRecyclerList() {
        RecyclerView rvOne = findViewById(R.id.rvOne);
        mDataList.clear();
        mDataList.add(new QuickBean(R.drawable.by_phone_v2,R.string.help_device_wifi,R.string.base_enter));
        mDataList.add(new QuickBean(R.drawable.by_device_v3,R.string.help_phone_wifi,R.string.base_enter));
        mDataList.add(new QuickBean(R.drawable.by_wifi_v2,R.string.help_same_wifi,R.string.base_enter));
        //创建adapter
        rvOne.setLayoutManager(new GridLayoutManager(mActivity, 1));
        DeviceSearchHelpAdapter adapterOne = new DeviceSearchHelpAdapter();
        rvOne.setAdapter(adapterOne);
        rvOne.addItemDecoration(new SpaceItemDecoration(20,0,0,200));
        adapterOne.setList(mDataList);
        adapterOne.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                switch (position){
                    case 0:
                        DeviceSearchHelpDeviceActivity.startActivity(DeviceSearchHelpActivity.this);
                        break;
                    case 1:
                        DeviceSearchHelpMobileActivity.startActivity(DeviceSearchHelpActivity.this);
                        break;
                    case 2:
                        DeviceSearchHelpWifiActivity.startActivity(DeviceSearchHelpActivity.this);
                        break;
                }
            }
        });
    }

    public static void startActivity(Context context) {
        if (context == null) {
            return;
        }
        context.startActivity(new Intent(context, DeviceSearchHelpActivity.class));
    }

}
