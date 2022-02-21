package com.hq.base.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hq.base.dialog.CommonProgressDialog;
import com.hq.base.util.StatusBarUtil;
import com.hq.base.util.ActivityCollector;

/**
 * Created on 2020/4/4.
 * author :
 * desc :
 */
public class BaseActivity extends AppCompatActivity {

    protected AppCompatActivity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        initBase();
//        DeviceOSUtil.setLocalEnglish(this);
        //在活动管理器添加当前Activity
        ActivityCollector.addActivity(this);
    }

    protected void initBase() {
        // 如果使用沉浸式状态栏
        if (isTranslucentNavigation()) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            StatusBarUtil.init(mActivity);
        }
        if (safeArea()) {
            final WindowManager.LayoutParams lp = getWindow().getAttributes();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
                getWindow().setAttributes(lp);
            }
        }
    }

    protected boolean safeArea() {
        return false;
    }

    private boolean isTranslucentNavigation() {
        return true;
    }

    private CommonProgressDialog mProgressDialog;

    public void showProgressDialog() {
        showProgressDialog(true);
    }

    public void showProgressDialog(boolean cancel) {
        if (mProgressDialog == null) {
            mProgressDialog = new CommonProgressDialog(mActivity);
        }
        mProgressDialog.setCancelable(cancel);
        mProgressDialog.show();
    }

    public void closeProgressDialog() {
        if (mProgressDialog == null || !mProgressDialog.isShowing()) {
            return;
        }
        mProgressDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        //从activity的活动管理器清除
        ActivityCollector.removeActivity(this);
        super.onDestroy();
    }
}
