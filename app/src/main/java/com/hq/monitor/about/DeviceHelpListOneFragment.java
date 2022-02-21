package com.hq.monitor.about;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.hq.base.ui.BaseFragment;
import com.hq.monitor.R;
import com.hq.monitor.adapter.DeviceHelpAdapter;
import com.hq.monitor.adapter.QuickBean;
import com.hq.monitor.banner.VerticalIndicatorView;
import com.to.aboomy.pager2banner.Banner;

import java.util.ArrayList;

/**
 * Created on 2020/5/24
 * author :
 * desc :
 */
public class DeviceHelpListOneFragment extends BaseFragment {
    private static final String EXTRA_KEY_POSITION = "extra_key_position";

    private Banner banner;
    private ArrayList mDataList = new ArrayList();

    int mFrom = 1;

    public static DeviceHelpListOneFragment getInstance(@NonNull int position) {
        final Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_KEY_POSITION, position);
        final DeviceHelpListOneFragment fragment = new DeviceHelpListOneFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help_list_one, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        banner = view.findViewById(R.id.banner);

        mFrom = getArguments().getInt(EXTRA_KEY_POSITION,1);
        initRecyclerList();
    }

    private void initRecyclerList() {
        mDataList.clear();
        if (mFrom == 1){
            mDataList.add(new QuickBean(R.drawable.ic_connect_device_hot_1,""));
            mDataList.add(new QuickBean(R.drawable.ic_connect_device_hot_2,""));
            mDataList.add(new QuickBean(R.drawable.ic_connect_device_hot_3,""));
        } else if (mFrom == 2){
            mDataList.add(new QuickBean(R.drawable.ic_connect_phone_wifi_1,""));
            mDataList.add(new QuickBean(R.drawable.ic_connect_phone_wifi_2,""));
            mDataList.add(new QuickBean(R.drawable.ic_connect_phone_wifi_3,""));
        } else {
            mDataList.add(new QuickBean(R.drawable.ic_connect_samewifi_1,""));
            mDataList.add(new QuickBean(R.drawable.ic_connect_samewifi_2,""));
            mDataList.add(new QuickBean(R.drawable.ic_connect_samewifi_3,""));
        }

        banner.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        //使用内置Indicator
        VerticalIndicatorView indicator = new VerticalIndicatorView(getContext())
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
}
