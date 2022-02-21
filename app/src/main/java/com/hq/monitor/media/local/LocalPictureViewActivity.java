package com.hq.monitor.media.local;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.hq.base.dialog.CommonConfirmDialog;
import com.hq.base.ui.BaseActivity;
import com.hq.base.util.ShareUtils;
import com.hq.base.util.StatusBarUtil;
import com.hq.base.util.ToastUtil;
import com.hq.base.widget.CommonTitleBar;
import com.hq.monitor.R;
import com.hq.monitor.device.selectdialog.OnSelectClickListener;
import com.hq.monitor.device.selectdialog.SelectDialog;
import com.hq.monitor.media.MediaCenter;

import java.io.File;
import java.util.LinkedList;

public class LocalPictureViewActivity extends BaseActivity {
    private static final String EXTRA_KEY_POSITION = "extra_key_position";
    private ViewPager mViewPager;
    private CommonTitleBar mTitleBar;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);

        StatusBarUtil.setStatusBarWhiteMode(this);

        mViewPager = findViewById(R.id.view_pager);
        ImageView ivOne = findViewById(R.id.ivOne);
        mTitleBar = findViewById(R.id.title_bar);
        initTitleBar();
        position = getIntent().getIntExtra(EXTRA_KEY_POSITION, -1);
        if (position < 0 || position >= MediaCenter.getInstance().getLocalImageListSize()) {
            return;
        }
//        initPagerAdapter();
//        mViewPager.setCurrentItem(position);
//        setTitle(mViewPager.getCurrentItem());

        setImageView(ivOne,position);
    }

    private void setImageView(ImageView imageView,int position) {
        imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.MATCH_PARENT));

        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        final File model = MediaCenter.getInstance().getLocalImage(position);
        imageView.setImageResource(R.drawable.common_default_image);
        if (model != null && model.exists()) {
            Glide.with(mActivity).load(model).placeholder(R.drawable.common_default_image).into(imageView);
        }
    }

    private PagerAdapter mPagerAdapter;

    private void initPagerAdapter() {
        mPagerAdapter = new PagerAdapter() {

            private final LinkedList<AppCompatImageView> viewCache = new LinkedList<>();

            @Override
            public int getCount() {
                return MediaCenter.getInstance().getLocalImageListSize();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                return POSITION_NONE;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                AppCompatImageView imageView = viewCache.poll();
                if (imageView == null) {
                    imageView = new AppCompatImageView(mActivity);
                }

                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                final File model = MediaCenter.getInstance().getLocalImage(position);
                imageView.setImageResource(R.drawable.common_default_image);
                if (model != null && model.exists()) {
                    Glide.with(mActivity).load(model).placeholder(R.drawable.common_default_image).into(imageView);
                }
                container.addView(imageView);
                return imageView;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                final AppCompatImageView imageView = (AppCompatImageView) object;
                container.removeView(imageView);
                imageView.setTag(null);
                viewCache.add(imageView);
            }

            @Override
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                setTitle(mViewPager.getCurrentItem());
            }
        };
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setTitle(position);
            }
        });
    }

    private void initTitleBar() {
//        final int position = mViewPager.getCurrentItem();
        final File model = MediaCenter.getInstance().getLocalImage(position);

//        mTitleBar.setMenu(getString(R.string.operate_delete));
//        mTitleBar.setMenuClick(v -> showConfirmDialog(model,position));

//        mTitleBar.setImageDeleteClick(v -> showConfirmDialog(model,position));
        mTitleBar.setImageDeleteClick(v -> {
            if (model == null) {
                return;
            }
            mDelPosition = position;
            initSelectDialog(getString(R.string.delete_tip_placeholder, model.getName()));
        });

//        mTitleBar.setShareTv(getString(R.string.operate_share));
//        mTitleBar.setMenuShareClick(v -> ShareUtils.shareImage(model,this));

        mTitleBar.setImageShareClick(v -> ShareUtils.shareImage(model,this));
    }

    public void setTitle(int position) {
        final File model = MediaCenter.getInstance().getLocalImage(position);
        mTitleBar.setTitle("");
        if (model != null) {
            mTitleBar.setTitle(model.getName());
        }
    }

    private CommonConfirmDialog mCommonConfirmDialog;
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

    private void showConfirmDialog(File model,int position) {
        if (model == null) {
            return;
        }
        mDelPosition = position;
        if (mCommonConfirmDialog == null) {
            mCommonConfirmDialog = new CommonConfirmDialog(this);
            mCommonConfirmDialog.setConfirmListener(v -> {
                delete();
            });
        }
        mCommonConfirmDialog.setContent(getString(R.string.delete_tip_placeholder, model.getName()));
        mCommonConfirmDialog.show();
    }

    private void delete() {
        MediaCenter.getInstance().deleteImgFile(mDelPosition);
        ToastUtil.toast(getString(R.string.tip_delete_success));

        finish();

        mDelPosition = -1;
        if (mPagerAdapter != null) {
            mPagerAdapter.notifyDataSetChanged();
        }
    }

    public static void startActivity(Context context, int position) {
        final Intent intent = new Intent(context, LocalPictureViewActivity.class);
        intent.putExtra(EXTRA_KEY_POSITION, position);
        context.startActivity(intent);
    }
}
