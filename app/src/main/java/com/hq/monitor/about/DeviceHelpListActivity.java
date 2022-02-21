package com.hq.monitor.about;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hq.base.ui.BaseActivity;
import com.hq.base.util.StatusBarUtil;
import com.hq.monitor.R;
import com.hq.monitor.media.MediaCenter;

import java.util.ArrayList;
import java.util.List;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class DeviceHelpListActivity extends BaseActivity {
    private static final String EXTRA_KEY_URI = "extra_key_uri";

    private final List<Fragment> fragmentList = new ArrayList<>(10);
    private final List<String> pageTitleList = new ArrayList<>(10);

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicehelp_list);

        StatusBarUtil.setStatusBarWhiteMode(this);

        mViewPager = findViewById(R.id.view_pager);
        final TabLayout mTabLayout = findViewById(R.id.tab_layout);
        pageTitleList.add(getString(R.string.help_device_wifi));
        pageTitleList.add(getString(R.string.help_phone_wifi));
        pageTitleList.add(getString(R.string.help_same_wifi));
        final String uri = getIntent().getStringExtra(EXTRA_KEY_URI);
        fragmentList.add(DeviceHelpListOneFragment.getInstance(1));
        fragmentList.add(DeviceHelpListOneFragment.getInstance(2));
        fragmentList.add(DeviceHelpListOneFragment.getInstance(3));
        initPagerAdapter();
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initPagerAdapter() {
        final FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(),
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return pageTitleList.get(position);
            }
        };
        mViewPager.setAdapter(pagerAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaCenter.getInstance().destroy();
    }

    public static void startActivity(Context context) {
        if (context == null) {
            return;
        }
        context.startActivity(new Intent(context, DeviceHelpListActivity.class));
    }

}
