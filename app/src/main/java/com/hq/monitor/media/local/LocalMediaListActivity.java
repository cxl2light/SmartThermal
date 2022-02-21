package com.hq.monitor.media.local;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.hq.base.ui.BaseActivity;
import com.hq.base.util.StatusBarUtil;
import com.hq.monitor.R;
import com.hq.monitor.media.MediaCenter;
import com.hq.monitor.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class LocalMediaListActivity extends BaseActivity {
    private static final String EXTRA_KEY_URI = "extra_key_uri";

    private final List<Fragment> fragmentList = new ArrayList<>(10);
    private final List<String> pageTitleList = new ArrayList<>(10);

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_list);

        StatusBarUtil.setStatusBarWhiteMode(this);

        mViewPager = findViewById(R.id.view_pager);
        final TabLayout mTabLayout = findViewById(R.id.tab_layout);
        pageTitleList.add(getString(R.string.str_picture));
        pageTitleList.add(getString(R.string.str_video));
        final String uri = getIntent().getStringExtra(EXTRA_KEY_URI);
        fragmentList.add(LocalPictureListFragment.getInstance(uri));
        fragmentList.add(LocalVideoListFragment.getInstance(uri));
        initPagerAdapter();
        mTabLayout.setupWithViewPager(mViewPager);

        LinearLayout linearLayout = (LinearLayout) mTabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,R.drawable.shape_line));
        linearLayout.setDividerPadding(Utils.dp2px(4));
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

    public static void startActivity(Context context, String uri) {
        if (TextUtils.isEmpty(uri)) {
            return;
        }
        final Intent intent = new Intent(context, LocalMediaListActivity.class);
        intent.putExtra(EXTRA_KEY_URI, uri);
        context.startActivity(intent);
    }

}
