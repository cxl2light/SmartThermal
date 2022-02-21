package com.hq.monitor.media.local;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;
import com.hq.base.ui.BaseActivity;
import com.hq.base.util.ShareUtils;
import com.hq.base.util.StatusBarUtil;
import com.hq.base.util.ToastUtil;
import com.hq.base.widget.CommonTitleBar;
import com.hq.monitor.R;
import com.hq.monitor.device.selectdialog.OnSelectClickListener;
import com.hq.monitor.device.selectdialog.SelectDialog;
import com.hq.monitor.media.MediaCenter;
import com.to.aboomy.pager2banner.Banner;

import java.io.File;

public class LocalPictureBannerActivity extends BaseActivity {
    private static final String EXTRA_KEY_POSITION = "extra_key_position";
    private CommonTitleBar mTitleBar;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_banner);

        StatusBarUtil.setStatusBarWhiteMode(this);

        mTitleBar = findViewById(R.id.title_bar);
        initTitleBar();
        position = getIntent().getIntExtra(EXTRA_KEY_POSITION, -1);
        if (position < 0 || position >= MediaCenter.getInstance().getLocalImageListSize()) {
            return;
        }
        initBanner();
    }

    private void initBanner() {
        int mFromPosition = getIntent().getIntExtra(EXTRA_KEY_POSITION,0);

        TextView tvTitleNum = findViewById(R.id.tvTitleNum);
        tvTitleNum.setText( mFromPosition + 1 + "/" + MediaCenter.getInstance().getLocalImageListSize());

        Banner banner = findViewById(R.id.banner);
        banner.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        //使用内置Indicator
//        VerticalIndicatorView indicator = new VerticalIndicatorView(this)
//                .setIndicatorColor(Color.DKGRAY)
//                .setIndicatorSelectorColor(Color.WHITE);

        //创建adapter
        LocalPictureBannerAdapter adapter = new LocalPictureBannerAdapter();
        adapter.setList(MediaCenter.getInstance().getLocalImageList());

        banner.setAdapter(adapter);
        adapter.setNewInstance(MediaCenter.getInstance().getLocalImageList());

        banner.setOuterPageChangeListener(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                Log.d("ZeOne:", "onPageScrolled=" + position);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d("ZeOne:", "onPageSelected=" + position);
                tvTitleNum.setText( position + 1 + "/" + MediaCenter.getInstance().getLocalImageListSize());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                Log.d("ZeOne:", "onPageScrollStateChanged=" + state);
            }
        });

        banner.setCurrentItem(mFromPosition);
        banner.setAutoPlay(false);

        //传入RecyclerView.Adapter 即可实现无限轮播
//        banner.setIndicator(indicator)
//                .setAutoTurningTime(3000)
//                .setAdapter(adapter);
    }

    private void initTitleBar() {
        final File model = MediaCenter.getInstance().getLocalImage(position);
        mTitleBar.setImageDeleteClick(v -> {
            if (model == null) {
                return;
            }
            mDelPosition = position;
            initSelectDialog(getString(R.string.delete_tip_placeholder, model.getName()));
        });
        mTitleBar.setImageShareClick(v -> ShareUtils.shareImage(model,this));
    }

    public void setTitle(int position) {
        final File model = MediaCenter.getInstance().getLocalImage(position);
        mTitleBar.setTitle("");
        if (model != null) {
            mTitleBar.setTitle(model.getName());
        }
    }

    private int mDelPosition = -1;

    private void initSelectDialog(String content){
        SelectDialog mDialog = new SelectDialog();
        mDialog.show(getSupportFragmentManager(), false, "title", "", new OnSelectClickListener() {
            @Override
            public void onClickPositive(DialogFragment dialogFragment) {
                dialogFragment.dismiss();
                delete();
            }

            @Override
            public void onClickNegative(DialogFragment dialogFragment) {
                dialogFragment.dismiss();
            }
        });
    }

    private void delete() {
        MediaCenter.getInstance().deleteImgFile(mDelPosition);
        ToastUtil.toast(getString(R.string.tip_delete_success));

        finish();
    }

    public static void startActivity(Context context, int position) {
        final Intent intent = new Intent(context, LocalPictureBannerActivity.class);
        intent.putExtra(EXTRA_KEY_POSITION, position);
        context.startActivity(intent);
    }
}
