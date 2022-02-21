package com.hq.monitor.about;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

import com.hq.base.ui.BaseActivity;
import com.hq.base.util.StatusBarUtil;
import com.hq.base.widget.CommonTitleBar;
import com.hq.basebean.device.DeviceBaseInfo;
import com.hq.monitor.R;
import com.hq.monitor.adapter.DeviceHelpAdapter;
import com.hq.monitor.adapter.QuickBean;
import com.hq.monitor.banner.VerticalIndicatorView;
import com.hq.monitor.util.SpUtils;
import com.hq.monitor.util.Utils;
import com.to.aboomy.pager2banner.Banner;

import java.util.ArrayList;

public class ProductIntroduceActivity extends BaseActivity {

    private ArrayList mDataList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_introduce);

        StatusBarUtil.setStatusBarWhiteMode(this);

        initBanner();
        initMenu();
    }

    private void initMenu() {
       CommonTitleBar mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setImageShareAndClick(R.mipmap.ic_menu_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.initShowMenuDialog(getSupportFragmentManager(), SpUtils.getBoolean(mActivity,Utils.DEVICE_CONNECT,false));
            }
        });
    }

    private void initBanner() {
        mDataList.clear();

        DeviceBaseInfo info = SpUtils.getBeanFromSp(mActivity,SpUtils.SP_DEVICE_INFO);
        String dev = info.getHardware();
        if (dev != null && dev.toLowerCase().contains("ares")){
            mDataList.add(new QuickBean(R.drawable.ic_aim_product_01,""));
            mDataList.add(new QuickBean(R.drawable.ic_aim_product_02,""));
            mDataList.add(new QuickBean(R.drawable.ic_aim_product_03,""));
            mDataList.add(new QuickBean(R.drawable.ic_aim_product_04,""));
        }
        else {
            mDataList.add(new QuickBean(R.drawable.ic_product_01,""));
            mDataList.add(new QuickBean(R.drawable.ic_product_02,""));
            mDataList.add(new QuickBean(R.drawable.ic_product_03,""));
            mDataList.add(new QuickBean(R.drawable.ic_product_04,""));
            mDataList.add(new QuickBean(R.drawable.ic_product_05,""));
            mDataList.add(new QuickBean(R.drawable.ic_product_05,"2"));
        }

        Banner banner = findViewById(R.id.banner);
        banner.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        //使用内置Indicator
        VerticalIndicatorView indicator = new VerticalIndicatorView(this)
                .setIndicatorColor(Color.DKGRAY)
                .setIndicatorSelectorColor(Color.WHITE);

        //创建adapter
        DeviceHelpAdapter adapter = new DeviceHelpAdapter();
        adapter.setList(mDataList);

        //传入RecyclerView.Adapter 即可实现无限轮播
        banner.setIndicator(indicator)
                .setAutoTurningTime(3000)
                .setAdapter(adapter);
    }

    public static void startActivity(Context context) {
        if (context == null) {
            return;
        }
        final Intent intent = new Intent(context, ProductIntroduceActivity.class);
        context.startActivity(intent);
    }

}
