package com.hq.monitor.ui.searchhelp.wifistart;

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
import com.hq.monitor.ui.devicescanpre.DeviceScanPreActivity;
import com.hq.monitor.ui.searchhelp.devicestart.DeviceStartOneActivity;

import java.util.ArrayList;

/**
 * 设备列表页面
 */
public class DeviceSearchHelpWifiActivity extends BaseActivity{

    private static String TAG = "ZeNet=" + DeviceSearchHelpWifiActivity.class.getSimpleName();

    private CommonTitleBar mTitleBar;
    private ArrayList mDataList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan_help_device);
//        StatusBarUtil.setStatusBarWhiteMode(this);
        mTitleBar = findViewById(R.id.title_bar);
        initRecyclerList();

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
        mDataList.add(new QuickBean(1,R.drawable.help_iphone,R.string.help_phone_setting));
        mDataList.add(new QuickBean(2,R.drawable.help_device,R.string.help_device_setting));
        mDataList.add(new QuickBean(3,R.drawable.help_search,R.string.help_search_device));
        //创建adapter
        rvOne.setLayoutManager(new GridLayoutManager(mActivity, 3));
        DeviceSearchHelpWifiAdapter adapterOne = new DeviceSearchHelpWifiAdapter();
        rvOne.setAdapter(adapterOne);
        rvOne.addItemDecoration(new SpaceItemDecoration(0,0,0,0));
        adapterOne.setList(mDataList);
        adapterOne.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                switch (position){
                    case 0:
                    case 1:
                        DeviceStartOneActivity.startActivity(DeviceSearchHelpWifiActivity.this,position + 20);
                        break;
                    case 2:
                        DeviceScanPreActivity.startActivity(DeviceSearchHelpWifiActivity.this,position);
                        break;
                }
            }
        });
    }

    public static void startActivity(Context context) {
        if (context == null) {
            return;
        }
        context.startActivity(new Intent(context, DeviceSearchHelpWifiActivity.class));
    }

}
