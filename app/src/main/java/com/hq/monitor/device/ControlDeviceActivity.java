package com.hq.monitor.device;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.DisplayCutout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.hq.base.ui.BaseActivity;
import com.hq.base.util.ScreenUtils;
import com.hq.basebean.device.DeviceConfig;
import com.hq.monitor.R;
import com.hq.monitor.device.popup.ColorAtlaPopupMenu;
import com.hq.monitor.device.widget.GraphicsSettingWidget;
import com.hq.monitor.device.widget.SeekBarWidget;
import com.hq.monitor.device.widget.SettingWidget;
import com.hq.monitor.device.widget.ValuePackage;
import com.hq.monitor.play.PlayFragment;

import static com.hq.monitor.play.PlayFragment.EXTRA_DEVICE_IP;
import static com.hq.monitor.play.PlayFragment.EXTRA_RTSP_URL;

public class ControlDeviceActivity extends BaseActivity
        implements View.OnClickListener, SettingWidget.OnSettingClickCallback,
        GraphicsSettingWidget.OnGraphicsSettingClickCallback, IDeviceCtrlView {
    private static final int REQUEST_CODE_WRITE_STORAGE = 0xff01;

    private AppCompatImageView settingImgBtn, graphicsSettingImgBtn,
            distanceMeasurementBtn, rateBtn, palette, takePhotoIndicator,
            takeVideoIndicator, circleTakePhotos;
    private View leftMenuLayout, rightMenuLayout;
    private SeekBarWidget seekBarFirst, seekBarSecond;
    private PopupWindow preShowingPopup;
    private int commonPopupMenuGap = 0;

    private DeviceCtrlPresenter mCtrlPresenter;

    private final ValuePackage mZoomValuePackage = new ValuePackage(ValuePackage.TYPE_ZOOM, 10, 60);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_device);
        findView();
        final String rtspUrl = getIntent().getStringExtra(EXTRA_RTSP_URL);
        final String deviceIp = getIntent().getStringExtra(EXTRA_DEVICE_IP);
        if (TextUtils.isEmpty(rtspUrl) || TextUtils.isEmpty(deviceIp)) {
            return;
        }
        commonPopupMenuGap = (int) getResources().getDimension(R.dimen.common_menu_popup_gap);
        mCtrlPresenter = new DeviceCtrlPresenter(this, deviceIp, rtspUrl);
        mCtrlPresenter.getDeviceConfig();
        startRtsp();
        mZoomValuePackage.setCurrentValue(mZoomValuePackage.getMin());
    }

    @Override
    protected boolean safeArea() {
        return true;
    }

    private void findView() {
        settingImgBtn = findViewById(R.id.setting_img_btn);
        settingImgBtn.setOnClickListener(this);
        graphicsSettingImgBtn = findViewById(R.id.graphics_setting_img_btn);
        graphicsSettingImgBtn.setOnClickListener(this);
        distanceMeasurementBtn = findViewById(R.id.distance_measurement);
        distanceMeasurementBtn.setOnClickListener(this);
        rateBtn = findViewById(R.id.rate_btn);
        rateBtn.setOnClickListener(this);
        palette = findViewById(R.id.palette);
        palette.setOnClickListener(this);
        leftMenuLayout = findViewById(R.id.left_menu_layout);
        rightMenuLayout = findViewById(R.id.right_menu_layout);
        seekBarFirst = findViewById(R.id.seek_bar_first);
        seekBarFirst.setOnSeekBarChange(onSeekBarChange);
        seekBarSecond = findViewById(R.id.seek_bar_second);
        seekBarSecond.setOnSeekBarChange(onSeekBarChange);
        takePhotoIndicator = findViewById(R.id.take_photo_indicator);
        takePhotoIndicator.setOnClickListener(this);
        changeTintColor(takePhotoIndicator, true);
        takeVideoIndicator = findViewById(R.id.take_video_indicator);
        takeVideoIndicator.setOnClickListener(this);
        circleTakePhotos = findViewById(R.id.circle_take_photos);
        circleTakePhotos.setOnClickListener(this);
        findViewById(R.id.close_icon).setOnClickListener(this);

        final View temp = leftMenuLayout;
        final WindowInsets rootWindowInsets = temp.getRootWindowInsets();
        if (rootWindowInsets != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                final DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
                if (displayCutout != null) {
                    temp.setPadding(temp.getPaddingLeft() + displayCutout.getSafeInsetLeft(),
                            temp.getPaddingTop(), temp.getPaddingRight(), temp.getPaddingBottom());
                }
            }

        }
        temp.post(this::alignSeekBarMargin);

        findViewById(R.id.open_file_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        hideSeekBar();
        switch (v.getId()) {
            case R.id.setting_img_btn:
                showSettingPopup();
                break;
            case R.id.graphics_setting_img_btn:
                // 设置图像相关
                showGraphicsSettingPopup();
                break;
            case R.id.rate_btn:
                onSelectedChange(rateBtn, !rateBtn.isSelected());
                sameRang(rateBtn, mZoomValuePackage);
                break;
            case R.id.distance_measurement:
                // 测距
                changeTintColor(distanceMeasurementBtn, !distanceMeasurementBtn.isSelected());
                if (mCtrlPresenter != null) {
                    mCtrlPresenter.distanceMeasurement(distanceMeasurementBtn.isSelected());
                }
                break;
            case R.id.palette:
                // 调色板
                showColorAtlaPopupMenu();
                break;
            case R.id.take_photo_indicator:
                if (takePhotoIndicator.isSelected()) {
                    return;
                }
                changeTintColor(takeVideoIndicator, false);
                changeTintColor(takePhotoIndicator, true);
                break;
            case R.id.take_video_indicator:
                if (takeVideoIndicator.isSelected()) {
                    return;
                }
                changeTintColor(takePhotoIndicator, false);
                changeTintColor(takeVideoIndicator, true);
                break;
            case R.id.circle_take_photos:
                if (takePhotoIndicator.isSelected()) {
                    takePhoto();
                    return;
                }
                if (takeVideoIndicator.isSelected()) {
                    takeVideo();
                }
                break;
            case R.id.open_file_btn:
                break;
            case R.id.close_icon:
                finish();
                break;
            default:
                break;
        }
    }

    private void startRtsp() {
    }

    @Override
    public void showDeviceConfig(@NonNull DeviceConfig deviceConfig) {
        if (graphicsSettingWidget != null) {
            graphicsSettingWidget.updateValue(deviceConfig);
        }
        if (settingWidget != null) {
            settingWidget.updateValue(deviceConfig);
        }
        if (colorAtlaPopupMenu != null) {
            colorAtlaPopupMenu.updateValue(deviceConfig);
        }
        if (distanceMeasurementBtn != null) {
            distanceMeasurementBtn.setSelected(deviceConfig.getDistanceEn());
        }
        mZoomValuePackage.setCurrentValue(deviceConfig.getZoom());
        seekBarFirst.updateValue();
        seekBarSecond.updateValue();
    }

    private PopupWindow settingPopupWindow;
    private SettingWidget settingWidget;

    /**
     * 弹出设置菜单
     */
    private void showSettingPopup() {
        if (settingPopupWindow == null) {
            settingWidget = new SettingWidget(mActivity);
            settingWidget.setOnSettingClickCallback(this);
            final int paddingH = (int) getResources().getDimension(R.dimen.common_menu_popup_padding_h);
            settingWidget.setPadding(paddingH, settingWidget.getPaddingTop(), paddingH, settingWidget.getPaddingBottom());
            settingPopupWindow = new PopupWindow(settingWidget,
                    (int) getResources().getDimension(R.dimen.common_menu_popup_width),
                    (int) (ScreenUtils.getScreenHeightPixels(mActivity) * 0.8f));
            settingPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.common_round_rect_dark_bg));
            settingPopupWindow.setOutsideTouchable(false);
            settingPopupWindow.setOnDismissListener(() -> {
                onSelectedChange(settingImgBtn, false);
                hideSeekBar();
                if (settingWidget != null) {
                    settingWidget.cancelSelected();
                }
            });
        }
        showPop(settingPopupWindow, settingImgBtn,
                leftMenuLayout.getMeasuredWidth() + commonPopupMenuGap);
    }

    private PopupWindow graphicsSettingPopupWindow;
    private GraphicsSettingWidget graphicsSettingWidget;

    /**
     * 弹出图像设置菜单
     */
    private void showGraphicsSettingPopup() {
        if (graphicsSettingPopupWindow == null) {
            graphicsSettingWidget = new GraphicsSettingWidget(mActivity);
            graphicsSettingWidget.setOnClickCallback(this);
            final int paddingH = (int) getResources().getDimension(R.dimen.common_menu_popup_padding_h);
            graphicsSettingWidget.setPadding(paddingH, graphicsSettingWidget.getPaddingTop(),
                    paddingH, graphicsSettingWidget.getPaddingBottom());
            graphicsSettingPopupWindow = new PopupWindow(graphicsSettingWidget,
                    (int) getResources().getDimension(R.dimen.common_menu_popup_width),
                    (int) (ScreenUtils.getScreenHeightPixels(mActivity) * 0.8f));
            graphicsSettingPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.common_round_rect_dark_bg));
            graphicsSettingPopupWindow.setOutsideTouchable(false);
            graphicsSettingPopupWindow.setOnDismissListener(() -> {
                onSelectedChange(graphicsSettingImgBtn, false);
                hideSeekBar();
                if (graphicsSettingWidget != null) {
                    graphicsSettingWidget.cancelSelected();
                }
            });
        }
        showPop(graphicsSettingPopupWindow, graphicsSettingImgBtn,
                leftMenuLayout.getMeasuredWidth() + commonPopupMenuGap);
    }

    private ColorAtlaPopupMenu colorAtlaPopupMenu;

    private void showColorAtlaPopupMenu() {
        if (colorAtlaPopupMenu == null) {
            colorAtlaPopupMenu = new ColorAtlaPopupMenu(mActivity);
            colorAtlaPopupMenu.setOnPaletteChange(value -> {
                if (mCtrlPresenter != null) {
//                    mCtrlPresenter.setPalette(value);
                    mCtrlPresenter.setPalette2(value);
                }
            });
            colorAtlaPopupMenu.setOnDismissListener(() -> onSelectedChange(palette, false));
        }
        showPop(colorAtlaPopupMenu, palette, ScreenUtils.getScreenWidthPixels(mActivity) - colorAtlaPopupMenu.getWidth()
                - rightMenuLayout.getMeasuredWidth() - commonPopupMenuGap);
    }

    private void showPop(@NonNull PopupWindow popupWindow, @NonNull AppCompatImageView curView, int xOffset) {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
            return;
        }
        if (preShowingPopup != null && preShowingPopup.isShowing()) {
            preShowingPopup.dismiss();
        }
        if (isFinishing() || isDestroyed()) {
            return;
        }
        popupWindow.showAtLocation(curView, Gravity.START | Gravity.CENTER_VERTICAL,
                xOffset, 0);
        preShowingPopup = popupWindow;
        onSelectedChange(curView, !curView.isSelected());
    }

    private AppCompatImageView preSelectedView;

    private void onSelectedChange(AppCompatImageView targetView, boolean selected) {
        if (targetView == null) {
            return;
        }
        if (preSelectedView == targetView && !selected) {
            // 取消选中
            changeTintColor(preSelectedView, selected);
            preSelectedView = null;
            return;
        }
        changeTintColor(preSelectedView, false);
        changeTintColor(targetView, selected);
        if (targetView != settingImgBtn && targetView != graphicsSettingImgBtn) {
            if (settingPopupWindow != null && settingPopupWindow.isShowing()) {
                settingPopupWindow.dismiss();
            }
            if (graphicsSettingPopupWindow != null && graphicsSettingPopupWindow.isShowing()) {
                graphicsSettingPopupWindow.dismiss();
            }
        }
        if (selected) {
            preSelectedView = targetView;
        }
    }

    private void changeTintColor(AppCompatImageView targetView, boolean selected) {
        if (targetView == null) {
            return;
        }
        targetView.setSelected(selected);
        final Drawable drawable = targetView.getDrawable();
        drawable.setTint(getColor(targetView.isSelected() ?
                R.color.tint_color_selected_dark_bg : R.color.tint_color_normal_dark_bg));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (preShowingPopup != null && preShowingPopup.isShowing()) {
            preShowingPopup.dismiss();
        }
        changeTintColor(preSelectedView, false);
        if (mCtrlPresenter != null) {
            mCtrlPresenter.destroy();
        }
    }

    private void sameRang(View v, @NonNull ValuePackage valuePackage) {
        seekBarFirst.setVisibility(v.isSelected() ? View.VISIBLE : View.GONE);
        if (v.isSelected()) {
            seekBarFirst.setLeftRightText("-", "+");
            seekBarFirst.setData(valuePackage);
        }
        seekBarSecond.setVisibility(View.GONE);
    }

    private void hideSeekBar() {
        seekBarFirst.setVisibility(View.GONE);
        seekBarSecond.setVisibility(View.GONE);
    }

    @Override
    public void onAcuityClick(View v, @NonNull ValuePackage valuePackage) {
        sameRang(v, valuePackage);
    }

    @Override
    public void onContrastRatioClick(View v, @NonNull ValuePackage valuePackage) {
        sameRang(v, valuePackage);
    }

    @Override
    public void onBrightnessClick(View v, @NonNull ValuePackage valuePackage) {
        sameRang(v, valuePackage);
    }

    @Override
    public void onNoiseReductionClick(View v, @NonNull ValuePackage valuePackage) {
        sameRang(v, valuePackage);
    }

    @Override
    public void onPolarizationClick(View v, @NonNull ValuePackage valuePackage) {
        sameRang(v, valuePackage);
    }

    @Override
    public void onCoordinateClick(View v, @NonNull ValuePackage xValuePackage, @NonNull ValuePackage yValuePackage) {
        seekBarFirst.setVisibility(v.isSelected() ? View.VISIBLE : View.GONE);
        seekBarSecond.setVisibility(v.isSelected() ? View.VISIBLE : View.GONE);
        if (v.isSelected()) {
            seekBarFirst.setData(xValuePackage);
            seekBarFirst.setLeftRightText("X-", "X+");
            seekBarSecond.setData(yValuePackage);
            seekBarSecond.setLeftRightText("Y-", "Y+");
        }
    }

    @Override
    public void onTrackClick(View v) {
        hideSeekBar();
        mCtrlPresenter.setTrack(v.isSelected());
    }

    @Override
    public void onPipClick(View v) {
        hideSeekBar();
        mCtrlPresenter.setPip(v.isSelected());
    }

    @Override
    public void onGPSClick(View v) {
        hideSeekBar();
        mCtrlPresenter.setGps(v.isSelected());
    }

    private void alignSeekBarMargin() {
        final int leftW = leftMenuLayout.getMeasuredWidth();
        final int rightW = rightMenuLayout.getMeasuredWidth();
        final ViewGroup.MarginLayoutParams firstLp = (ViewGroup.MarginLayoutParams) seekBarFirst.getLayoutParams();
        final ViewGroup.MarginLayoutParams secondLp = (ViewGroup.MarginLayoutParams) seekBarSecond.getLayoutParams();
        int left = firstLp.getMarginStart() + leftW;
        int right = firstLp.getMarginEnd() + rightW;
        if (right > left) {
            firstLp.leftMargin += right - left;
        } else {
            firstLp.rightMargin += left - right;
        }
        seekBarFirst.setLayoutParams(firstLp);

        left = secondLp.getMarginStart() + leftW;
        right = secondLp.getMarginEnd() + rightW;
        if (right > left) {
            secondLp.leftMargin += right - left;
        } else {
            secondLp.rightMargin += left - right;
        }
        seekBarSecond.setLayoutParams(secondLp);
    }

    private void takePhoto() {
        if (circleTakePhotos.isSelected()) {
            return;
        }
        changeTintColor(circleTakePhotos, !circleTakePhotos.isSelected());
        circleTakePhotos.postDelayed(() -> changeTintColor(circleTakePhotos, false), 500);
        mCtrlPresenter.snapshot();
    }

    private void takeVideo() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            changeTintColor(circleTakePhotos, !circleTakePhotos.isSelected());
            if (circleTakePhotos.isSelected()) {
                mCtrlPresenter.recording();
                mCtrlPresenter.startRecording(mActivity.getApplicationContext());
                return;
            }
            mCtrlPresenter.stopRecording();
            return;
        }
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_WRITE_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takeVideo();
        }
    }

    private SeekBarWidget.OnSeekBarChange onSeekBarChange = new SeekBarWidget.OnSeekBarChange() {
        @Override
        public void onValueChange(@NonNull ValuePackage valuePackage) {
            if (mCtrlPresenter != null) {
                mCtrlPresenter.dispatchSeekBarValue(valuePackage);
            }
        }
    };

    public static void startActivity(Context context, String rtspUrl, String ip) {
        if (context == null) {
            return;
        }
        final Intent intent = new Intent(context, ControlDeviceActivity.class);
        intent.putExtra(PlayFragment.EXTRA_RTSP_URL, rtspUrl);
        intent.putExtra(PlayFragment.EXTRA_DEVICE_IP, ip);
        context.startActivity(intent);
    }

}
