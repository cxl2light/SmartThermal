package com.hq.monitor.device;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.FrameMetrics;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.hq.base.ui.BaseActivity;
import com.hq.base.util.ExceptionToTip;
import com.hq.base.util.Logger;
import com.hq.base.util.ScreenUtils;
import com.hq.base.util.ToastUtil;
import com.hq.basebean.device.DeviceBaseInfo;
import com.hq.basebean.device.DeviceConfig;
import com.hq.commonwidget.WidgetImageTextView;
import com.hq.monitor.R;
import com.hq.monitor.about.DeviceConnectedActivity;
import com.hq.monitor.app.MyApplication;
import com.hq.monitor.device.menudialog.MenuDialog;
import com.hq.monitor.device.popup.ColorAtlaPopupMenu;
import com.hq.monitor.device.receiver.AlarmReceiver;
import com.hq.monitor.device.widget.GraphicsSettingWidget;
import com.hq.monitor.device.widget.SeekBarWidget;
import com.hq.monitor.device.widget.SettingWidget;
import com.hq.monitor.device.widget.ValuePackage;
import com.hq.monitor.jni.ObservableStart;
import com.hq.monitor.media.local.LocalMediaListActivity;
import com.hq.monitor.util.Constant;
import com.hq.monitor.util.RxUtil;
import com.hq.monitor.util.SpUtils;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.widget.IRenderView;
import tv.danmaku.ijk.media.player.widget.TextureRenderView;


public class ControlDeviceIJKActivity extends BaseActivity
        implements View.OnClickListener, SettingWidget.OnSettingClickCallback,
        GraphicsSettingWidget.OnGraphicsSettingClickCallback, IDeviceCtrlView {
    private static final int REQUEST_CODE_WRITE_STORAGE = 0xff01;

    private AppCompatImageView settingImgBtn, graphicsSettingImgBtn,
            distanceMeasurementBtn, rateBtn, palette, takePhotoIndicator,
            takeVideoIndicator, circleTakePhotos;

    private WidgetImageTextView settingImgBtnOne, graphicsSettingImgBtnOne,
            distanceMeasurementBtnOne, rateBtnOne, paletteOne, takePhotoIndicatorOne,
            takeVideoIndicatorOne, circleTakePhotosOne;

    private View leftMenuLayout, rightMenuLayout;
    private RelativeLayout rlOne;
    private SeekBarWidget seekBarFirst, seekBarSecond;
    private PopupWindow preShowingPopup;
    private TextureRenderView mPlayerView;
    private IjkMediaPlayer mIjkMediaPlayer;
    private View mProgressView;
    private Chronometer recordTimer;

    private boolean isFront = true;  //????????????Activity???????????????

    private Handler handler = new Handler();
    private int[] alarmIntervals = {1, 2, 5, 60, 1};

    View viewOne,viewTwo;

    private int commonPopupMenuGap = 0;

    Thread mThread;
    Boolean isDestroy = false;

    String deviceIp = "";

    private DeviceCtrlIJKPresenter mCtrlPresenter;
    private SoundPool mSoundPool;

    private final ValuePackage mZoomValuePackage = new ValuePackage(ValuePackage.TYPE_ZOOM, 10, 60);

    long delay = 2000;
    Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    mCtrlPresenter.getDeviceConfig2();
                    break;
            }
        }
    };

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_control_device_ijk);

        DeviceBaseInfo preSelectedDeviceInfo = SpUtils.getBeanFromSp(this,SpUtils.SP_DEVICE_INFO);
        if (preSelectedDeviceInfo == null) {
            ToastUtil.toast(this.getString(R.string.please_select_connect_device));
            return;
        }

        findView();

        viewOne = findViewById(R.id.viewOne);
        viewTwo = findViewById(R.id.viewTwo);
        ConstraintLayout clVideo = findViewById(R.id.clVideo);
        clVideo.setLayoutParams(ScreenUtils.getVideoLayoutParams(this));

        ImageView ivMenu = findViewById(R.id.ivMenu);
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                initShowMenuDialog();
            }
        });

        final String rtspUrl = "rtsp://" + preSelectedDeviceInfo.getIp() + ":554/mainstream";
        deviceIp = preSelectedDeviceInfo.getIp();
        final String deviceName = preSelectedDeviceInfo.getDevName();
        if (TextUtils.isEmpty(rtspUrl) || TextUtils.isEmpty(deviceIp)) {
            ToastUtil.toast("???????????????IP??????");
            return;
        }
        commonPopupMenuGap = (int) getResources().getDimension(R.dimen.common_menu_popup_gap_no);
        mCtrlPresenter = new DeviceCtrlIJKPresenter(this, deviceIp, rtspUrl, deviceName);
        Log.d("getDeviceCurrent","deviceIp=" + deviceIp + ",rtspUrl=" + rtspUrl + ",deviceName=" + deviceName);
        mCtrlPresenter.getDeviceConfig2();
        mZoomValuePackage.setCurrentValue(mZoomValuePackage.getMin());
        mZoomValuePackage.setMinMaxStr("Z-","Z+");
        //???????????????
        mSoundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM, 5);
        mSoundPool.load(this, R.raw.shutter, 1);

        Log.d("ZeOne=",deviceIp);
//        RxUtil.apply(ObservableStart.getObserveStart(deviceIp,this).initBase()).subscribe();

        if (mThread == null){
            mThread = new Thread(new MyRunnable());
            mThread.start();
        }

        initAlarmNotification();

        startActivity(new Intent(this, DeviceConnectedActivity.class));
    }

    /**
     * ??????????????????
     */
    private void initAlarmNotification() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mCtrlPresenter.getAnalyseResult();
                int second = alarmIntervals[SpUtils.getInt(MyApplication.getAppContext(), SpUtils.ALARM_INTERVAL_STRING, 1)];
                handler.postDelayed(this, second*1000);
            }
        };

        handler.postDelayed(runnable, 1000);

    }

    private void initShowMenuDialog() {
        MenuDialog mDialog = new MenuDialog();
        mDialog.show(getSupportFragmentManager(), true,true, "title", "content");
    }

    private void initPlayerView() {
        mPlayerView.removeRenderCallback(renderCallback);
        mPlayerView.setOnClickListener(this);
        mPlayerView.addRenderCallback(renderCallback);
    }

    private final IRenderView.IRenderCallback renderCallback = new IRenderView.IRenderCallback() {
        @Override
        public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
            holder.bindToMediaPlayer(mIjkMediaPlayer);
            //??????????????????
            try {
                mIjkMediaPlayer.prepareAsync();
            } catch (Throwable e) {
                ToastUtil.toast(ExceptionToTip.toTip(e));
            }
        }

        @Override
        public void onSurfaceChanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void onSurfaceDestroyed(@NonNull IRenderView.ISurfaceHolder holder) {

        }
    };

    private void initPlayer() {
        Log.d("ZePlayer","mIjkMediaPlayer=" + mIjkMediaPlayer);
        mIjkMediaPlayer = new IjkMediaPlayer();
        final IjkMediaPlayer mediaPlayer = mIjkMediaPlayer;

        setPlayerParams(mediaPlayer);
        //mediaPlayer????????????-------??????,onPrepared
        mediaPlayer.setOnPreparedListener(mp -> {
            mProgressView.setVisibility(View.GONE);
            mp.start();
        });
        //MediaPlayer??????---------??????,onCompletion
        mediaPlayer.setOnCompletionListener(mp -> mProgressView.setVisibility(View.GONE));

        try {
            mediaPlayer.setDataSource(mCtrlPresenter.getRtspUrl());
        } catch (Throwable e) {
            ToastUtil.toast(ExceptionToTip.toTip(e));
        }
        initPlayerView();
    }

    @Override
    protected boolean safeArea() {
        return true;
    }

    private void findView() {
        mPlayerView = findViewById(R.id.player_texture_view);
        settingImgBtn = findViewById(R.id.setting_img_btn);
        settingImgBtn.setOnClickListener(this);
        graphicsSettingImgBtn = findViewById(R.id.graphics_setting_img_btn);
        graphicsSettingImgBtn.setOnClickListener(this);
        distanceMeasurementBtn = findViewById(R.id.distance_measurement);
        distanceMeasurementBtn.setOnClickListener(this);

        distanceMeasurementBtnOne = findViewById(R.id.distance_measurement_one);
        distanceMeasurementBtnOne.setOnClickListener(this);
        rateBtnOne = findViewById(R.id.rate_btn_one);
        rateBtnOne.setOnClickListener(this);
        graphicsSettingImgBtnOne = findViewById(R.id.graphics_setting_img_btn_one);
        graphicsSettingImgBtnOne.setOnClickListener(this);
        settingImgBtnOne = findViewById(R.id.setting_img_btn_one);
        settingImgBtnOne.setOnClickListener(this);

        paletteOne = findViewById(R.id.palette_one);
        paletteOne.setOnClickListener(this);
        paletteOne.setSelected(false);

        rateBtn = findViewById(R.id.rate_btn);
        rateBtn.setOnClickListener(this);
        palette = findViewById(R.id.palette);
        palette.setOnClickListener(this);
        leftMenuLayout = findViewById(R.id.left_menu_layout);
        rightMenuLayout = findViewById(R.id.right_menu_layout);
        rightMenuLayout.setOnClickListener(this);

        rlOne = findViewById(R.id.rlOne);

        seekBarFirst = findViewById(R.id.seek_bar_first);
        seekBarFirst.setOnSeekBarChange(onSeekBarChange);
        seekBarSecond = findViewById(R.id.seek_bar_second);
        seekBarSecond.setOnSeekBarChange(onSeekBarChange);
        takePhotoIndicator = findViewById(R.id.take_photo_indicator);
        takePhotoIndicator.setOnClickListener(this);
        changeTintColor(takePhotoIndicator, true);
        takeVideoIndicator = findViewById(R.id.take_video_indicator);
        takeVideoIndicator.setOnClickListener(this);
        changeTintColor(takeVideoIndicator, false);
        circleTakePhotos = findViewById(R.id.circle_take_photos);
        circleTakePhotos.setOnClickListener(this);
        findViewById(R.id.close_icon).setOnClickListener(this);
        mProgressView = findViewById(R.id.progress_view);
        recordTimer = findViewById(R.id.record_timer);

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
        findViewById(R.id.open_file_btn_one).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        hideSeekBar();
        switch (v.getId()) {
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
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_STORAGE);
                break;
            case R.id.open_file_btn:
            case R.id.open_file_btn_one:
                LocalMediaListActivity.startActivity(mActivity, mCtrlPresenter.getDirPath(getApplicationContext()));
                break;
            case R.id.close_icon:
                release();
                finish();
                break;
            case R.id.right_menu_layout:
            case R.id.player_texture_view:
                if (preShowingPopup != null && preShowingPopup.isShowing()) {
                    preShowingPopup.dismiss();
                }
                changeTintColor(preSelectedViewOne, false);
                break;


            case R.id.distance_measurement_one:
                // ??????
                changeTintColor(distanceMeasurementBtnOne, !distanceMeasurementBtnOne.isSelected());
                if (mCtrlPresenter != null) {
//                    mCtrlPresenter.distanceMeasurement(distanceMeasurementBtnOne.isSelected());
                    mCtrlPresenter.distanceMeasurement2(distanceMeasurementBtnOne.isSelected());
                }
                break;
            case R.id.rate_btn_one:
                Log.d("getCurrentValue",mZoomValuePackage.getCurrentValue() + ",getMin???" + mZoomValuePackage.getMin() + ",getMax=" + mZoomValuePackage.getMax());
                onSelectedChange(rateBtnOne, !rateBtnOne.isSelected());
                sameRang(rateBtnOne, mZoomValuePackage);
                break;
            case R.id.graphics_setting_img_btn_one:
                // ??????????????????
                showGraphicsSettingPopup();
                break;
            case R.id.setting_img_btn_one:
                showSettingPopup();
                break;
            case R.id.palette_one:
                // ?????????
                showColorAtlaPopupMenu();
                break;

            default:
                break;
        }
    }

    @Override
    public void showDeviceConfig(@NonNull DeviceConfig deviceConfig) {
        mZoomValuePackage.setCurrentValue(deviceConfig.getZoom());
        if (!isFront){  //????????????Activity???????????????????????????????????????
            return;
        }
        if (graphicsSettingWidget != null) {
            graphicsSettingWidget.updateValue(deviceConfig);
        }
        if (settingWidget != null) {
            settingWidget.updateValue(deviceConfig);
        }
        if (colorAtlaPopupMenu != null) {
            colorAtlaPopupMenu.updateValue(deviceConfig);
        }
        changeTintColor(distanceMeasurementBtnOne, deviceConfig.getDistanceEn());
        seekBarFirst.updateValue();
        seekBarSecond.updateValue();
    }

    private PopupWindow settingPopupWindow;
    private SettingWidget settingWidget;

    /**
     * ??????????????????
     */
    private void showSettingPopup() {
        if (settingPopupWindow == null) {
            settingWidget = new SettingWidget(mActivity);
//            settingWidget.updateValue(mCtrlPresenter.deviceConfig());
            settingWidget.setOnSettingClickCallback(this);
            final int paddingH = (int) getResources().getDimension(R.dimen.common_menu_popup_padding_h);
//            settingWidget.setPadding(paddingH, settingWidget.getPaddingTop(), paddingH, settingWidget.getPaddingBottom());
            settingPopupWindow = new PopupWindow(settingWidget,
                    (int) getResources().getDimension(R.dimen.common_menu_popup_width),
                    (int) (ScreenUtils.getScreenHeightPixels(mActivity) * 1f));
            settingPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.shape_gray_tran_bg));
            settingPopupWindow.setOutsideTouchable(false);
            settingPopupWindow.setOnDismissListener(() -> {
                onSelectedChange(settingImgBtnOne, false);
                hideSeekBar();
                if (settingWidget != null) {
                    settingWidget.cancelSelected();
                }
            });
        }
        showPop(settingPopupWindow, settingImgBtnOne,
                leftMenuLayout.getMeasuredWidth() + commonPopupMenuGap + viewOne.getWidth());
    }

    private PopupWindow graphicsSettingPopupWindow;
    private GraphicsSettingWidget graphicsSettingWidget;

    /**
     * ????????????????????????
     */
    private void showGraphicsSettingPopup() {
        if (graphicsSettingPopupWindow == null) {
            graphicsSettingWidget = new GraphicsSettingWidget(mActivity);
//            graphicsSettingWidget.updateValue(mCtrlPresenter.deviceConfig());
            graphicsSettingWidget.setOnClickCallback(this);
            final int paddingH = (int) getResources().getDimension(R.dimen.common_menu_popup_padding_h);
//            graphicsSettingWidget.setPadding(paddingH, graphicsSettingWidget.getPaddingTop(), paddingH, graphicsSettingWidget.getPaddingBottom());
            graphicsSettingPopupWindow = new PopupWindow(graphicsSettingWidget,
                    (int) getResources().getDimension(R.dimen.common_menu_popup_width),
                    (int) (ScreenUtils.getScreenHeightPixels(mActivity) * 1f));
            graphicsSettingPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.shape_gray_tran_bg));
            graphicsSettingPopupWindow.setOutsideTouchable(false);
            graphicsSettingPopupWindow.setOnDismissListener(() -> {
                onSelectedChange(graphicsSettingImgBtnOne, false);
                hideSeekBar();
                if (graphicsSettingWidget != null) {
                    graphicsSettingWidget.cancelSelected();
                }
            });
        }
        showPop(graphicsSettingPopupWindow, graphicsSettingImgBtnOne,
                leftMenuLayout.getMeasuredWidth() + commonPopupMenuGap + viewOne.getWidth());
    }

    private ColorAtlaPopupMenu colorAtlaPopupMenu;

    private void showColorAtlaPopupMenu() {
        if (colorAtlaPopupMenu == null) {
            colorAtlaPopupMenu = new ColorAtlaPopupMenu(mActivity);
//            colorAtlaPopupMenu.updateValue(mCtrlPresenter.deviceConfig());
            colorAtlaPopupMenu.setOutsideTouchable(false);
            colorAtlaPopupMenu.setBackgroundDrawable(ContextCompat.getDrawable(mActivity, R.drawable.shape_gray_tran_bg));
            colorAtlaPopupMenu.setOnPaletteChange(value -> {
                if (mCtrlPresenter != null) {
//                    mCtrlPresenter.setPalette(value);
                    mCtrlPresenter.setPalette2(value);
                }
            });
            colorAtlaPopupMenu.setOnDismissListener(() -> onSelectedChange(paletteOne, false));
        }
//        showPop(colorAtlaPopupMenu, palette, ScreenUtils.getScreenWidthPixels(mActivity) - colorAtlaPopupMenu.getWidth()
//                - rightMenuLayout.getMeasuredWidth() - commonPopupMenuGap - viewTwo.getWidth());
        //??????, ???????????????????????????????????????????????????????????????
        showPop(colorAtlaPopupMenu, paletteOne,
                viewOne.getWidth() + ScreenUtils.getViewWidth(this) + leftMenuLayout.getWidth() - colorAtlaPopupMenu.getWidth());
    }

    private void showPop(@NonNull PopupWindow popupWindow, @NonNull WidgetImageTextView curView, int xOffset) {
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

//    private AppCompatImageView preSelectedView;
    private WidgetImageTextView preSelectedViewOne;

//    private void onSelectedChange(AppCompatImageView targetView, boolean selected) {
//        if (targetView == null) {
//            return;
//        }
//        if (preSelectedView == targetView && !selected) {
//            // ????????????
//            changeTintColor(preSelectedView, selected);
//            preSelectedView = null;
//            return;
//        }
//        changeTintColor(preSelectedView, false);
//        changeTintColor(targetView, selected);
//        if (targetView != settingImgBtn && targetView != graphicsSettingImgBtn && targetView != palette) {
//            if (preShowingPopup != null && preShowingPopup.isShowing()) {
//                preShowingPopup.dismiss();
//            }
//        }
//        if (selected) {
//            preSelectedView = targetView;
//        }
//    }

    private void onSelectedChange(WidgetImageTextView targetView, boolean selected) {
        if (targetView == null) {
            return;
        }
        if (preSelectedViewOne == targetView && !selected) {
            // ????????????
            changeTintColor(preSelectedViewOne, selected);
            preSelectedViewOne = null;
            return;
        }
        changeTintColor(preSelectedViewOne, false);
        changeTintColor(targetView, selected);
        if (targetView != settingImgBtnOne && targetView != graphicsSettingImgBtnOne && targetView != paletteOne) {
            if (preShowingPopup != null && preShowingPopup.isShowing()) {
                preShowingPopup.dismiss();
            }
        }
        if (selected) {
            preSelectedViewOne = targetView;
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

//    private void toggleSelected(WidgetImageTextView widgetImageTextView) {
//        if (widgetImageTextView == null) {
//            return;
//        }
//        if (mPreSelected == widgetImageTextView && widgetImageTextView.isSelected()) {
//            changeTintColor(widgetImageTextView, !widgetImageTextView.isSelected());
//            mPreSelected = null;
//            return;
//        }
//        changeTintColor(mPreSelected, false);
//        changeTintColor(widgetImageTextView, !widgetImageTextView.isSelected());
//        mPreSelected = widgetImageTextView;
//    }
//
    private void changeTintColor(WidgetImageTextView widgetImageTextView, boolean selected) {
        if (widgetImageTextView == null) {
            return;
        }
        widgetImageTextView.setSelected(selected);
        widgetImageTextView.getTextView().setSelected(selected);
        widgetImageTextView.getImageView().setSelected(selected);
        final Drawable drawable = widgetImageTextView.getImageView().getDrawable();
        if (drawable == null) {
            return;
        }
        drawable.setTint(getColor(selected ?
                R.color.tint_color_selected_dark_bg : R.color.tint_color_normal_dark_bg));
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFront = true;
        RxUtil.apply(ObservableStart.getObserveStart(deviceIp,this).initBase()).subscribe();
        if (mIjkMediaPlayer == null) {
            initPlayer();
        } else {
//            mIjkMediaPlayer.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isFront = false;
//        mIjkMediaPlayer.pause();
//        release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecordVideo();
        release();
        if (preShowingPopup != null && preShowingPopup.isShowing()) {
            preShowingPopup.dismiss();
        }
        changeTintColor(preSelectedViewOne, false);
        if (mCtrlPresenter != null) {
            mCtrlPresenter.destroy();
        }
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
        }
        isDestroy = true;
    }

    private void release() {
        if (null == mIjkMediaPlayer) {
            return;
        }
        try {
            mIjkMediaPlayer.stop();
            mIjkMediaPlayer.release();
            mIjkMediaPlayer = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sameRang(View v, @NonNull ValuePackage valuePackage) {
        seekBarFirst.setVisibility(v.isSelected() ? View.VISIBLE : View.GONE);
        rlOne.setBackgroundColor(getColor(android.R.color.transparent));
        if (v.isSelected()) {
//            seekBarFirst.setLeftRightText("-", "+");
            seekBarFirst.setData(valuePackage);
            if (v.getId() == R.id.rate_btn_one){
                rlOne.setBackgroundColor(getColor(R.color.gray_trans_one));
            }
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
        rlOne.setBackgroundColor(getColor(android.R.color.transparent));
        seekBarFirst.setVisibility(v.isSelected() ? View.VISIBLE : View.GONE);
        seekBarSecond.setVisibility(v.isSelected() ? View.VISIBLE : View.GONE);
        if (v.isSelected()) {
            seekBarFirst.setData(xValuePackage);
//            seekBarFirst.setLeftRightText("X-", "X+");
            seekBarSecond.setData(yValuePackage);
//            seekBarSecond.setLeftRightText("Y+", "Y-");
        }
    }

    @Override
    public void onTrackClick(View v) {
        hideSeekBar();
//        mCtrlPresenter.setTrack(v.isSelected());
        mCtrlPresenter.setTrack2(v.isSelected());
    }

    @Override
    public void onPipClick(View v) {
        hideSeekBar();
        mCtrlPresenter.setPip(v.isSelected());
    }

    @Override
    public void onGPSClick(View v) {
        hideSeekBar();
//        mCtrlPresenter.setGps(v.isSelected());
        mCtrlPresenter.setGps2(v.isSelected());
    }

    private void alignSeekBarMargin() {
//        final int leftW = leftMenuLayout.getMeasuredWidth();
//        final int rightW = rightMenuLayout.getMeasuredWidth();
//        final ViewGroup.MarginLayoutParams firstLp = (ViewGroup.MarginLayoutParams) seekBarFirst.getLayoutParams();
//        final ViewGroup.MarginLayoutParams secondLp = (ViewGroup.MarginLayoutParams) seekBarSecond.getLayoutParams();
//        int left = firstLp.getMarginStart() + leftW;
//        int right = firstLp.getMarginEnd() + rightW;
//        if (right > left) {
//            firstLp.leftMargin += right - left;
//        } else {
//            firstLp.rightMargin += left - right;
//        }
//        seekBarFirst.setLayoutParams(firstLp);
//
//        left = secondLp.getMarginStart() + leftW;
//        right = secondLp.getMarginEnd() + rightW;
//        if (right > left) {
//            secondLp.leftMargin += right - left;
//        } else {
//            secondLp.rightMargin += left - right;
//        }
//        seekBarSecond.setLayoutParams(secondLp);
    }

    private void takePhoto() {
        if (circleTakePhotos.isSelected()) {
            return;
        }
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
//            mSoundPool.play(1, 1, 1, 0, 0, 1);
            changeTintColor(circleTakePhotos, !circleTakePhotos.isSelected());
            circleTakePhotos.postDelayed(() -> changeTintColor(circleTakePhotos, false), 500);
            final boolean result = mCtrlPresenter.takePicture(this, mPlayerView.getBitmap());
            if (result) {
                Toast.makeText(this, getString(R.string.tip_save_success), Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    private void takeVideo() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            changeTintColor(circleTakePhotos, !circleTakePhotos.isSelected());
            if (circleTakePhotos.isSelected()) {
                startRecordVideo();
                return;
            }
            stopRecordVideo();
            return;
        }
//        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_WRITE_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initPhotoOrVideo();
        } else {
            ToastUtil.toast("Please Allow The Permissions!");
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_STORAGE);
        }
    }

    private void initPhotoOrVideo() {
        if (!mIjkMediaPlayer.isPlaying()) {
            return;
        }
        if (takePhotoIndicator.isSelected()) {
            takePhoto();
            return;
        }
        if (takeVideoIndicator.isSelected()) {
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

    //??????????????????
    private void startRecordVideo() {
        recordTimer.setVisibility(View.VISIBLE);
        recordTimer.setBase(SystemClock.elapsedRealtime());//???????????????
        final int hour = (int) ((SystemClock.elapsedRealtime() - recordTimer.getBase()) / 1000 / 60);
        recordTimer.setFormat("0" + hour + ":%s");
        recordTimer.start();
        mCtrlPresenter.startRecording(mActivity);
    }

    //??????????????????
    private void stopRecordVideo() {
        mCtrlPresenter.stopRecording();
        recordTimer.stop();
        recordTimer.setVisibility(View.GONE);
    }

    public static void startActivity(Context context) {
        if (context == null) {
            return;
        }
        final Intent intent = new Intent(context, ControlDeviceIJKActivity.class);
        context.startActivity(intent);
    }

    //https://blog.csdn.net/u013270727/article/details/83900062
    private void setPlayerParams(IjkMediaPlayer ijkMediaPlayer){
        // ???????????? 1????????? O:??????
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", 1);
        // ?????????????????????????????? 1,????????????????????????
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1);
        /**
         * ???????????????????????????
         */
        // ?????????rtsp????????????????????????tcp(????????????udp)
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "rtsp_transport", "tcp");
        // ???????????????????????????????????? ???100??????????????????????????????
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 50L);
        // ???????????????packet????????????io?????????
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1L);
        // ??????????????????????????????
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
        // ?????????????????????????????????????????????????????????0 ???
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "fast", 1);
        // ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering",  0);
        // ????????????
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
        // ?????????????????????
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 0);
        // ??????????????????,??????kb
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "max-buffer-size", 0);
        // ??????????????????2
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "min-frames", 2);
        // ??????????????????
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,  "max_cached_duration", 3); //300
        // ???????????????????????????
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,  "infbuf", 1);
        // ???????????????rtmp???????????????1s???
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "nobuffer");
        // ??????????????????Size????????????1M, ??????????????????????????????
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 100); //1024L)
        // ??????????????????
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,"reconnect",5);
        //
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        // ??????????????????????????????: 0?????????????????????????????????????????????48?????????????????????????????????????????????
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48L);
        // ????????? ??????
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_frame", 0);
        // ?????????????????????????????????????????????????????????????????????
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);

    }

    public class MyRunnable implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (!isDestroy) {
                try {
                    Message message = new Message();
                    message.what = 1;
                    mHandler.sendMessage(message);// ????????????
                    Thread.sleep(delay);// ????????????10??????????????????
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
}
