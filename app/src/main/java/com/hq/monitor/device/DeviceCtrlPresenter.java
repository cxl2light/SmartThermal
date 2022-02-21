package com.hq.monitor.device;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.hq.base.consts.GlobalConst;
import com.hq.base.util.ExceptionToTip;
import com.hq.base.util.Logger;
import com.hq.base.util.ToastUtil;
import com.hq.basebean.EmptyResponse;
import com.hq.basebean.device.DeviceConfig;
import com.hq.monitor.ExecutorServiceUtil;
import com.hq.monitor.app.MyApplication;
import com.hq.monitor.config.GlobalConfig;
import com.hq.monitor.device.widget.ValuePackage;
import com.hq.monitor.jni.ObservableStart;
import com.hq.monitor.net.ApiManager;
import com.hq.monitor.net.api.DeviceCtrlApi;
import com.hq.monitor.util.RxUtil;
import com.hq.monitor.util.SpUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created on 2020/12/2
 * author :
 * desc : 设备kongzhi
 */
public class DeviceCtrlPresenter {
    private static final String TAG = "DeviceCtrlPresenter";

    private static final int ENABLE_VALUE = 1;
    private static final int DISABLE_VALUE = 0;

    private final CompositeDisposable mDisposableSet = new CompositeDisposable();
    private final String mDeviceIp;
    private final String mRtspUrl;
    private final String mDeviceName;

    private DeviceConfig mDeviceConfig;
    @NonNull
    private IDeviceCtrlView mCtrlView;

    public DeviceCtrlPresenter(@NonNull IDeviceCtrlView ctrlView, String deviceIp, String rtspUrl) {
        this(ctrlView, deviceIp, rtspUrl, null);
    }

    public DeviceCtrlPresenter(@NotNull IDeviceCtrlView ctrlView, String deviceIp, String rtspUrl, String deviceName) {
        mCtrlView = ctrlView;
        this.mDeviceIp = deviceIp;
        this.mRtspUrl = rtspUrl;
        mDeviceName = deviceName;
    }

    public String getRtspUrl() {
//        if (GlobalConst.isDebug()) {
//            return "rtsp://192.168.100.108:8556/channel=0";
//        }
        return mRtspUrl;
    }

    private Disposable configDisposable;

    /**
     * 获取配置参数
     */
    public void getDeviceConfig() {
        dispose(configDisposable);
        configDisposable = RxUtil.apply(getCtrlApi()
                .getDeviceConfig())
                .subscribe(deviceConfig -> {
                    mDeviceConfig = deviceConfig;
                    mCtrlView.showDeviceConfig(deviceConfig);
                }, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(configDisposable);
    }

    private Disposable configDisposable2;
    /**
     * 获取配置参数
     */
    public void getDeviceConfig2() {
        dispose(configDisposable2);
        configDisposable2 = RxUtil.apply(getTcpObserve().initGetDeviceConfig())
                .subscribe(deviceConfig -> {
                      Log.d("ZeOne","deviceConfig=" + deviceConfig);
//                    mDeviceConfig = deviceConfig;
//                    mCtrlView.showDeviceConfig(deviceConfig);
                }, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(configDisposable2);
    }

    @Nullable
    public DeviceConfig deviceConfig() {
        return mDeviceConfig;
    }

    private Disposable analyseResultDisposable;
    /**
     * 获取AI分析数据
     */
    public void getAnalyseResult() {
        boolean isOpen = SpUtils.getBoolean(MyApplication.getAppContext(), SpUtils.ALARM_NOTIFICATION_SWITCH, false);
        if (isOpen){
            dispose(analyseResultDisposable);
            analyseResultDisposable = RxUtil.apply(getTcpObserve().initGetAnalyseResult())
                    .subscribe(aiObjListInfo -> {
                        Log.d("ZeOne","analyseResultDisposable=" + aiObjListInfo);
                    }, throwable -> {
                        ToastUtil.toast(ExceptionToTip.toTip(throwable));
                    });
            mDisposableSet.add(analyseResultDisposable);
        }
    }

//    private Disposable configDisposableFile;
//    /**
//     * 上传文件
//     */
//    public void upgradeFile(String filePath, ParamsJni.FunUpgradeFile funUpgradeFile) {
//        dispose(configDisposableFile);
//        configDisposableFile = RxUtil.apply(getTcpObserve().initUpgradeFile(filePath,funUpgradeFile))
//                .subscribe(emptyResponseConsumer2, throwable -> {
//                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
//                });
//        mDisposableSet.add(configDisposableFile);
//    }


    private Disposable paletteDisposable;
    /**
     * 设置调色板
     */
    public void setPalette(int palette) {
        dispose(paletteDisposable);
        paletteDisposable = RxUtil.apply(getCtrlApi()
                .setPalette(palette))
                .subscribe(emptyResponseConsumer, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(paletteDisposable);
    }

    private Disposable paletteDisposable2;
    /**
     * 设置调色板
     */
    public void setPalette2(int palette) {
        dispose(paletteDisposable2);
        paletteDisposable2 = RxUtil.apply(getTcpObserve()
                .setPalette(palette))
                .subscribe(emptyResponseConsumer2, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(paletteDisposable2);
    }

    private Disposable distanceDisposable;
    /**
     * 测距
     */
    public void distanceMeasurement(boolean enable) {
        dispose(distanceDisposable);
        distanceDisposable = RxUtil.apply(getCtrlApi()
                .setDistanceMeasurement(enable ? ENABLE_VALUE : DISABLE_VALUE))
                .subscribe(emptyResponseConsumer, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(distanceDisposable);
    }

    private Disposable distanceDisposable2;
    /**
     * 测距
     */
    public void distanceMeasurement2(boolean enable) {
        dispose(distanceDisposable2);
        distanceDisposable2 = RxUtil.apply(getTcpObserve()
                .setDistanceMeasurement(enable ? ENABLE_VALUE : DISABLE_VALUE))
                .subscribe(emptyResponseConsumer2, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(distanceDisposable2);
    }

    public void dispatchSeekBarValue(@NonNull ValuePackage valuePackage) {
        switch (valuePackage.getType()) {
            case ValuePackage.TYPE_ACUITY:
                // 锐度
//                setSharpness(valuePackage.getCurrentValue());
                setSharpness2(valuePackage.getCurrentValue());
                break;
            case ValuePackage.TYPE_CONTRAST_RATIO:
                // 对比度
//                setContrast(valuePackage.getCurrentValue());
                setContrast2(valuePackage.getCurrentValue());
                break;
            case ValuePackage.TYPE_BRIGHTNESS:
                // 亮度
//                setBrightness(valuePackage.getCurrentValue());
                setBrightness2(valuePackage.getCurrentValue());
                break;
            case ValuePackage.TYPE_NOISE_REDUCTION:
                // 降噪
//                setNoiseReduction(valuePackage.getCurrentValue());
                setNoiseReduction2(valuePackage.getCurrentValue());
                break;
            case ValuePackage.TYPE_POLARIZATION:
                // 分划
//                setReticle(valuePackage.getCurrentValue());
                setReticle2(valuePackage.getCurrentValue());
                break;
            case ValuePackage.TYPE_X:
                // x
//                setX(valuePackage.getCurrentValue());
                setX2(valuePackage.getCurrentValue());
                break;
            case ValuePackage.TYPE_Y:
                // y
//                setY(valuePackage.getCurrentValue());
                setY2(valuePackage.getCurrentValue());
                break;
            case ValuePackage.TYPE_ZOOM:
                // zoom
                Log.d("TYPE_ZOOM","getCurrentValue=" + valuePackage.getCurrentValue());
//                setZoom(valuePackage.getCurrentValue());
                setZoom2(valuePackage.getCurrentValue());
                break;
            default:
                break;
        }
    }

    private Disposable sharpnessDisposable;

    /**
     * 设置锐度
     */
    public void setSharpness(int value) {
        dispose(sharpnessDisposable);
        sharpnessDisposable = RxUtil.apply(getCtrlApi()
                .setSharpness(value))
                .subscribe(emptyResponseConsumer, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(sharpnessDisposable);
    }

    private Disposable sharpnessDisposable2;
    /**
     * 设置锐度
     */
    public void setSharpness2(int value) {
        dispose(sharpnessDisposable2);
        sharpnessDisposable2 = RxUtil.apply(getTcpObserve()
                .setSharpness(value))
                .subscribe(emptyResponseConsumer2, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(sharpnessDisposable2);
    }

    private Disposable contrastDisposable;
    /**
     * 设置对比度
     */
    public void setContrast(int value) {
        dispose(contrastDisposable);
        contrastDisposable = RxUtil.apply(getCtrlApi()
                .setContrast(value))
                .subscribe(emptyResponseConsumer, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(contrastDisposable);
    }

    private Disposable contrastDisposable2;

    /**
     * 设置对比度
     */
    public void setContrast2(int value) {
        dispose(contrastDisposable2);
        contrastDisposable2 = RxUtil.apply(getTcpObserve()
                .setContrast(value))
                .subscribe(emptyResponseConsumer2, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(contrastDisposable2);
    }

    private Disposable brightnessDisposable;

    /**
     * 设置明亮度
     */
    public void setBrightness(int value) {
        dispose(brightnessDisposable);
        brightnessDisposable = RxUtil.apply(getCtrlApi()
                .setBrightness(value))
                .subscribe(emptyResponseConsumer, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(brightnessDisposable);
    }

    private Disposable brightnessDisposable2;

    /**
     * 设置明亮度
     */
    public void setBrightness2(int value) {
        dispose(brightnessDisposable2);
        brightnessDisposable2 = RxUtil.apply(getTcpObserve()
                .setBrightness(value))
                .subscribe(emptyResponseConsumer2, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(brightnessDisposable2);
    }

    private Disposable noiseReductionDisposable;

    /**
     * 设置降噪
     */
    public void setNoiseReduction(int value) {
        dispose(noiseReductionDisposable);
        noiseReductionDisposable = RxUtil.apply(getCtrlApi()
                .setNoiseReduction(value))
                .subscribe(emptyResponseConsumer, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(noiseReductionDisposable);
    }

    private Disposable noiseReductionDisposable2;

    /**
     * 设置降噪
     */
    public void setNoiseReduction2(int value) {
        dispose(noiseReductionDisposable2);
        noiseReductionDisposable2 = RxUtil.apply(getTcpObserve()
                .setNoiseReduction(value))
                .subscribe(emptyResponseConsumer2, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(noiseReductionDisposable2);
    }

    private Disposable reticleDisposable;
    /**
     * 设置分划
     */
    public void setReticle(int value) {
        dispose(reticleDisposable);
        reticleDisposable = RxUtil.apply(getCtrlApi()
                .setReticle(value))
                .subscribe(emptyResponseConsumer, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(reticleDisposable);
    }

    private Disposable reticleDisposable2;
    /**
     * 设置分划
     */
    public void setReticle2(int value) {
        dispose(reticleDisposable2);
        reticleDisposable2 = RxUtil.apply(getTcpObserve()
                .setReticle(value))
                .subscribe(emptyResponseConsumer2, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(reticleDisposable2);
    }

    private Disposable xDisposable;

    /**
     * 设置十字光标X坐标
     */
    public void setX(int value) {
        dispose(xDisposable);
        xDisposable = RxUtil.apply(getCtrlApi()
                .setX(value))
                .subscribe(emptyResponseConsumer, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(xDisposable);
    }

    private Disposable x2Disposable;
    /**
     * 设置十字光标X坐标
     */
    public void setX2(int value) {
        dispose(x2Disposable);
        x2Disposable = RxUtil.apply(getTcpObserve().setX(value))
                .subscribe(emptyResponseConsumer2, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(x2Disposable);
    }

    private Disposable yDisposable;

    /**
     * 设置十字光标Y坐标
     */
    public void setY(int value) {
        dispose(yDisposable);
        yDisposable = RxUtil.apply(getCtrlApi()
                .setY(value))
                .subscribe(emptyResponseConsumer, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(yDisposable);
    }

    private Disposable yDisposable2;
    /**
     * 设置十字光标Y坐标
     */
    public void setY2(int value) {
        dispose(yDisposable2);
        yDisposable2 = RxUtil.apply(getTcpObserve()
                .setY(value))
                .subscribe(emptyResponseConsumer2, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(yDisposable2);
    }

    private Disposable zoomDisposable;

    /**
     * zoom
     */
    public void setZoom(int value) {
        Log.d("setZoom","getCurrentValue=" + value);
        dispose(zoomDisposable);
        zoomDisposable = RxUtil.apply(getCtrlApi()
                .setZoom(value))
                .subscribe(emptyResponseConsumer, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(zoomDisposable);
    }

    private Disposable zoomDisposable2;
    /**
     * zoom
     */
    public void setZoom2(int value) {
        Log.d("setZoom","getCurrentValue=" + value);
        dispose(zoomDisposable2);
        zoomDisposable2 = RxUtil.apply(getTcpObserve()
                .setZoom(value))
                .subscribe(emptyResponseConsumer2, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(zoomDisposable2);
    }

    private Disposable trackDisposable;
    /**
     * track
     */
    public void setTrack(boolean track) {
        dispose(trackDisposable);
        trackDisposable = RxUtil.apply(getCtrlApi()
                .setTrack(track ? 1 : 0))
                .subscribe(emptyResponseConsumer, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(trackDisposable);
    }

    private Disposable trackDisposable2;
    /**
     * track
     */
    public void setTrack2(boolean track) {
        dispose(trackDisposable2);
        trackDisposable2 = RxUtil.apply(getTcpObserve()
                .setTrack(track ? 1 : 0))
                .subscribe(emptyResponseConsumer2, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(trackDisposable2);
    }

    private Disposable pipDisposable;
    /**
     * PIP
     */
    public void setPip(boolean track) {
        dispose(pipDisposable);
        pipDisposable = RxUtil.apply(getTcpObserve()
                .setPip(track ? 1 : 0))
                .subscribe(emptyResponseConsumer2, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(pipDisposable);
    }

    private Disposable gpsDisposable;
    /**
     * Gps
     */
    public void setGps(boolean gps) {
        dispose(gpsDisposable);
        gpsDisposable = RxUtil.apply(getCtrlApi()
                .setGPS(gps ? 1 : 0))
                .subscribe(emptyResponseConsumer, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(gpsDisposable);
    }

    private Disposable gpsDisposable2;
    /**
     * Gps
     */
    public void setGps2(boolean gps) {
        dispose(gpsDisposable2);
        gpsDisposable2 = RxUtil.apply(getTcpObserve()
                .setGPS(gps ? 1 : 0))
                .subscribe(emptyResponseConsumer2, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(gpsDisposable2);
    }

    private Disposable snapshotDisposable;

    /**
     * snapshot
     */
    public void snapshot() {
        dispose(snapshotDisposable);
        snapshotDisposable = RxUtil.apply(getCtrlApi()
                .snapshot())
                .subscribe(emptyResponseConsumer, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(snapshotDisposable);
    }

    private Disposable recordingDisposable;

    /**
     * snapshot
     */
    public void recording() {
        dispose(recordingDisposable);
        recordingDisposable = RxUtil.apply(getCtrlApi()
                .recording())
                .subscribe(emptyResponseConsumer, throwable -> {
                    ToastUtil.toast(ExceptionToTip.toTip(throwable));
                });
        mDisposableSet.add(recordingDisposable);
    }

    private final Consumer<EmptyResponse> emptyResponseConsumer = emptyResponse -> getDeviceConfig();

    private final Consumer<Integer> emptyResponseConsumer2 = emptyResponse -> getDeviceConfig();

    private volatile boolean startRecording = false;

    /**
     * ffmpeg -loglevel debug \
     * -i rtsp://user:pass@10.0.0.100:554//h264Preview_01_main \
     * -c copy -map 0 -c:a aac \
     * -f segment -segment_time 300 \
     * -segment_format mp4 /path/to/capture-%03d.mp4
     * <p>
     * <p>
     * """ffmpeg -i rtsp://mpv.cdn3.bigCDN.com:554/bigCDN/mp4:bigbuckbunnyiphone_400.mp4 -acodec copy -vcodec copy {0}""".format(filename)
     */
    public void startRecording(Context context) {
        if (startRecording) {
            return;
        }
        try {
            final String filePath = generateFile(context, false);
//            ToastUtil.toast("开始录制！");
            ExecutorServiceUtil.getExecutorService().execute(() -> {
                if (startRecording) {
                    return;
                }
                startRecording = true;
                /*final int result = FFmpeg.execute("-i " + getRtspUrl() +
                        " -acodec copy -bsf:a aac_adtstoasc -vcodec copy " + filePath);*/
                /*final int result = FFmpeg.execute("-i " + getRtspUrl() +
                        " -c copy -map 0 -c:a aac -f segment -segment_time 300 -segment_format mp4 " + filePath);*/
                final int result = FFmpeg.execute(" -i " + getRtspUrl() + " -acodec copy -vcodec copy " + filePath);
                Logger.d(TAG, String.valueOf(result));
                refreshGallery(filePath);
            });
        } catch (Exception e) {
            ToastUtil.toast("文件创建失败：" + ExceptionToTip.toTip(e));
        }
    }

    public void refreshGallery(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        final File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        final Intent intent;
        if (file.isDirectory()) {
            intent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
            intent.setClassName("com.android.providers.media", "com.android.providers.media.MediaScannerReceiver");
        } else {
            intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        }
        intent.setData(Uri.fromFile(file));
        MyApplication.getAppContext().sendBroadcast(intent);
    }

    public void stopRecording() {
        if (!startRecording) {
            return;
        }
        FFmpeg.cancel();
        startRecording = false;
//        ToastUtil.toast("停止录制！");
    }

    protected String generateFile(Context context, boolean picture) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss", Locale.CHINA);
        final String date = format.format(new Date());
        final File file = new File(getDirPath(context),
                date + (picture ? GlobalConfig.PICTURE_POSTFIX : GlobalConfig.VIDEO_POSTFIX));
        return file.getAbsolutePath();
    }

    public String getDirPath(Context context) {
        return GlobalConfig.getRootSavePath(context, mDeviceName);
    }

    private void dispose(Disposable d) {
        if (d != null) {
            d.dispose();
        }
    }

    private DeviceCtrlApi getCtrlApi() {
        return ApiManager.getDeviceCtrlApi(mDeviceIp);
    }

    private ObservableStart getTcpObserve() {
        return ObservableStart.getObserveStart(mDeviceIp,mCtrlView);
    }

    public void destroy() {
        mDisposableSet.dispose();
        ApiManager.release();
    }

}
