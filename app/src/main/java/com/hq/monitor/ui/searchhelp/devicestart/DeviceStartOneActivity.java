package com.hq.monitor.ui.searchhelp.devicestart;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.viewpager2.widget.ViewPager2;

import com.hq.base.ui.BaseActivity;
import com.hq.base.widget.CommonTitleBar;
import com.hq.monitor.R;
import com.hq.monitor.adapter.QuickBean;
import com.hq.monitor.banner.VerticalIndicatorView;
import com.hq.monitor.device.menudialog.MenuDialog;
import com.hq.monitor.util.Utils;
import com.to.aboomy.pager2banner.Banner;

import java.util.ArrayList;

/**
 * 设备列表页面
 */
public class DeviceStartOneActivity extends BaseActivity{

    private static String TAG = "ZeNet=" + DeviceStartOneActivity.class.getSimpleName();

    private CommonTitleBar mTitleBar;
    private ArrayList mDataList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_search_banner);
//        StatusBarUtil.setStatusBarWhiteMode(this);
        mTitleBar = findViewById(R.id.title_bar);
        initBanner();

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

    private void initBanner() {
        mDataList.clear();
        int position =  getIntent().getIntExtra(Utils.DEVICE_KEY,0);
        if (position == 0){
            mDataList.add(new QuickBean(R.drawable.device_hotspot_device_setting1,R.string.help_search_device_1,0));
            mDataList.add(new QuickBean(R.drawable.device_hotspot_device_setting2,R.string.help_search_device_2,0));
            mDataList.add(new QuickBean(R.drawable.device_hotspot_device_setting3,R.string.help_search_device_3,0));
        } else if (position == 1){
            mDataList.add(new QuickBean(R.drawable.device_hotspot_device_setting4,R.string.help_search_device_4,0));
            mDataList.add(new QuickBean(R.drawable.device_hotspot_device_setting5,R.string.help_search_device_5,0));
            mDataList.add(new QuickBean(R.drawable.device_hotspot_device_setting6,R.string.help_search_device_6,0));
        } else if (position == 10){
            mDataList.add(new QuickBean(R.drawable.mobile_hotspot_device_settings1,R.string.help_search_mobile_1,0));
            mDataList.add(new QuickBean(R.drawable.mobile_hotspot_device_settings2,R.string.help_search_mobile_2,0));
            mDataList.add(new QuickBean(R.drawable.mobile_hotspot_device_settings3,R.string.help_search_mobile_3,0));
        } else if (position == 11){
            mDataList.add(new QuickBean(R.drawable.mobile_hotspot_device_settings4,R.string.help_search_mobile_4,0));
            mDataList.add(new QuickBean(R.drawable.mobile_hotspot_device_settings5,R.string.help_search_mobile_5,0));
            mDataList.add(new QuickBean(R.drawable.mobile_hotspot_device_settings6,R.string.help_search_mobile_6,0));
        } else if (position == 20){
            mDataList.add(new QuickBean(R.drawable.wifi_device_settins1,R.string.help_search_wifi_1,0));
            mDataList.add(new QuickBean(R.drawable.wifi_device_settins2,R.string.help_search_wifi_2,0));
            mDataList.add(new QuickBean(R.drawable.wifi_device_settins3,R.string.help_search_wifi_3,0));
        } else if (position == 21){
            mDataList.add(new QuickBean(R.drawable.wifi_device_settins4,R.string.help_search_wifi_4,0));
            mDataList.add(new QuickBean(R.drawable.wifi_device_settins5,R.string.help_search_wifi_5,0));
            mDataList.add(new QuickBean(R.drawable.wifi_device_settins6,R.string.help_search_wifi_6,0));
        }

        //创建adapter
        DeviceStartOneAdapter adapter = new DeviceStartOneAdapter();
        adapter.setList(mDataList);

        Banner banner = findViewById(R.id.banner);
        banner.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        //使用内置Indicator
        VerticalIndicatorView indicator = new VerticalIndicatorView(this)
                .setIndicatorColor(Color.DKGRAY)
                .setIndicatorSelectorColor(Color.WHITE);

        //传入RecyclerView.Adapter 即可实现无限轮播
        banner.setIndicator(indicator)
                .setAutoTurningTime(3000)
                .setAdapter(adapter);
    }

    public static void startActivity(Context context) {
        if (context == null) {
            return;
        }
        context.startActivity(new Intent(context, DeviceStartOneActivity.class));
    }

    public static void startActivity(Context context, int position) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, DeviceStartOneActivity.class);
        intent.putExtra(Utils.DEVICE_KEY, position);
        context.startActivity(intent);
    }

}
