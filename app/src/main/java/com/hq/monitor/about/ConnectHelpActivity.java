package com.hq.monitor.about;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.viewpager2.widget.ViewPager2;

import com.hq.base.ui.BaseActivity;
import com.hq.base.util.StatusBarUtil;
import com.hq.monitor.R;
import com.hq.monitor.adapter.ConnectHelpAdapter;
import com.hq.monitor.adapter.QuickBean;
import com.hq.monitor.banner.VerticalIndicatorView;
import com.to.aboomy.pager2banner.Banner;

import java.util.ArrayList;

public class ConnectHelpActivity extends BaseActivity {

    ArrayList mDataList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_help);

        StatusBarUtil.setStatusBarWhiteMode(this);
        initView();
    }

    private void initView() {
        mDataList.clear();
        mDataList.add(new QuickBean("怎么连接设备","方法一：设备释放热点，手机连接"));
        mDataList.add(new QuickBean("怎么连接设备","方法二：手机释放热点，设备连接"));
        mDataList.add(new QuickBean("怎么连接设备","方法三：手机设备连接同一个网络"));
        mDataList.add(new QuickBean("怎么连接设备4","方法三：手机设备连接同一个网络"));
        mDataList.add(new QuickBean("怎么连接设备5","方法三：手机设备连接同一个网络"));
        mDataList.add(new QuickBean("怎么连接设备6","方法三：手机设备连接同一个网络"));
        mDataList.add(new QuickBean("怎么连接设备7","方法三：手机设备连接同一个网络"));
        mDataList.add(new QuickBean("怎么连接设备8","方法三：手机设备连接同一个网络"));
        mDataList.add(new QuickBean("怎么连接设备9","方法三：手机设备连接同一个网络"));
        mDataList.add(new QuickBean("怎么连接设备10","方法三：手机设备连接同一个网络"));

        Banner banner = findViewById(R.id.banner);
        banner.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        //使用内置Indicator
        VerticalIndicatorView indicator = new VerticalIndicatorView(this)
                .setIndicatorColor(Color.DKGRAY)
                .setIndicatorSelectorColor(Color.WHITE);

        //创建adapter
        ConnectHelpAdapter adapter = new ConnectHelpAdapter();
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
        final Intent intent = new Intent(context, ConnectHelpActivity.class);
        context.startActivity(intent);
    }

}
