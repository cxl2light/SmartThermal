package com.hq.monitor.jni;


import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hq.base.util.Logger;
import com.hq.basebean.device.AiObjInfo;
import com.hq.basebean.device.DeviceBaseInfo;
import com.hq.basebean.device.DeviceConfig;
import com.hq.monitor.adapter.QuickBean;
import com.hq.monitor.app.MyApplication;
import com.hq.monitor.db.NotificationInfo;
import com.hq.monitor.device.IDeviceCtrlView;
import com.hq.monitor.device.receiver.AlarmReceiver;
import com.hq.monitor.util.DateUtils;
import com.hq.monitor.util.RxBus;
import com.hq.monitor.util.RxUtil;
import com.hq.monitor.util.SpUtils;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;

public class ObservableStart {

    private static String mIp = "";
    ParamsJni paramsJni = new ParamsJni();

    private static IDeviceCtrlView mCtrlView;
    private DeviceConfig mDeviceConfig;
    private List<AiObjInfo> listAiObjInfo;
    private int TYPE_PERSON = 15;

     public Observable<Integer> observableStart(int start){
        return Observable.create(new ObservableOnSubscribe<Integer>() {
             @Override
             public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                 paramsJni.initDevice(start);
//                 new Thread(new Runnable() {
//                     @Override
//                     public void run() {
//                         Log.d(TAG,"CHANNEL=" + paramsJni.initDevice(6));
//                     }
//                 }).start();
             }
         });
     }

    private static volatile ObservableStart observeStart;

    public static ObservableStart getObserveStart() {

        if (TextUtils.isEmpty(mIp)){
            DeviceBaseInfo deviceBaseInfo = (DeviceBaseInfo)SpUtils.getBeanFromSp(MyApplication.getAppContext(),SpUtils.SP_DEVICE_INFO);
            if (deviceBaseInfo != null){
                mIp = deviceBaseInfo.getIp();
                Log.d("ZeIp=","start=" + mIp);
            }
        }

        if (observeStart == null) {
            synchronized (ObservableStart.class) {
                if (observeStart == null) {
                    observeStart = new ObservableStart();
                }
            }
        }
        return observeStart;
    }

    public static ObservableStart getObserveStart(String ip, IDeviceCtrlView ctrlView) {
        mIp = ip;
        mCtrlView = ctrlView;
        if (observeStart == null) {
            synchronized (ObservableStart.class) {
                if (observeStart == null) {
                    observeStart = new ObservableStart();
                }
            }
        }
        return observeStart;
    }

    /**
     * 创建通道，注册回调方法
     * @return
     */
    public Observable<Integer> initBase(){
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
//                mIp = "天马行空";
                Log.d("ZeOneBase1","mIp=" + mIp);
                paramsJni.initDeviceChannel(mIp, new ParamsJni.FunGetJsonDevConfig() {
                    @Override
                    public void onDeviceConfig(int channel, String json) {
                        Log.d("ZeTwo", "channel=" + channel + ",mIp=" + mIp + ",json" + json);
                        try {
                            mDeviceConfig = new Gson().fromJson(json, DeviceConfig.class);
                        } catch (Exception e) {
                            Log.d("ZeErrorJson", "channel=" + channel + ",json" + json);
                            return;
                        }
                        if (mCtrlView != null) {
                            mCtrlView.showDeviceConfig(mDeviceConfig);
                        }
                    }
                }, new ParamsJni.FunUpgrade() {
                    @Override
                    public void onUpgrade(int channelNo, int nUpgrade) {
                        Log.d("ZeUpgrade", "channel=" + channelNo + ",nUpgrade" + nUpgrade);
                        RxBus.getInstance().post(new QuickBean(channelNo, nUpgrade));
                    }
                }, new ParamsJni.FunGetJsonAnalyseResult() {
                    @Override
                    public void onAnalyseResult(int channelNo, String pJson) {
                        Log.d("ZeTwo=", "channelNo=" + channelNo + ",mIp=" + mIp + ",pJson" + pJson);
                        try {
                            listAiObjInfo = new Gson().fromJson(pJson, new TypeToken<List<AiObjInfo>>(){}.getType());
                            if (mDeviceConfig != null && mDeviceConfig.getDistanceEn()){
                                sendNotificationBroadcast(listAiObjInfo);
                            }
                        } catch (Exception e) {
                            Log.d("ZeErrorJson", "channelNo=" + channelNo + ",pJson" + pJson);
                            return;
                        }
                    }
                });
            }
        });
    }

    /**
     * 侦测到目标发送通知
     * @param objInfo
     */
    private void sendNotificationBroadcast(List<AiObjInfo> objInfo) {
        String title = "Find target";
        String content = "";
        for (int i = 0; i < objInfo.size(); i++){
            AiObjInfo aiObjInfo = objInfo.get(i);
            int type = aiObjInfo.getU16AIType();
            if (type > 0){
                insertNotificationData(aiObjInfo);
                if (type == TYPE_PERSON){
                    content = content + "PERSON distance:" + aiObjInfo.getU16Dist() + "m; ";
                }
                else {
                    content = content + "ANIMAL distance:" + aiObjInfo.getU16Dist() + "m; ";
                }

            }
        }

        if (!content.isEmpty()){
            Intent intent = new Intent(MyApplication.getAppContext(), AlarmReceiver.class);
            intent.putExtra("notification_title", title);
            intent.putExtra("notification_content", content);
            intent.setAction("NOTIFICATION_ACTION");
            MyApplication.getAppContext().sendBroadcast(intent);
        }

    }

    /**
     * 添加通知记录
     * @param aiObjInfo
     */
    private void insertNotificationData(AiObjInfo aiObjInfo) {
        NotificationInfo notificationInfo = new NotificationInfo();
        DeviceBaseInfo info = SpUtils.getBeanFromSp(MyApplication.getAppContext(),SpUtils.SP_DEVICE_INFO);
        String dev = info.getHardware();

        notificationInfo.setDevice_name(dev);
        notificationInfo.setTarget_distance(aiObjInfo.getU16Dist());
        notificationInfo.setTarget_type(aiObjInfo.getU16AIType()==TYPE_PERSON?2:1);
        notificationInfo.setNotification_date(DateUtils.getStringDate());
        notificationInfo.setNotification_time(DateUtils.getTimeShort());
        notificationInfo.setMarks("");

        long re = MyApplication.getNotificationsDB().insert(notificationInfo);
        if (re == -1){
            Logger.e("NotificationDB",  "insert failed");
        }
        else {
            Logger.d("NotificationDB",  "insert success " + re);
        }
    }

    /**
     * 获取设备控制实时数据
     * @return
     */
    public Observable<Integer> initGetDeviceConfig(){
        RxUtil.apply(ObservableStart.getObserveStart().initBase()).subscribe();
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int setResult = paramsJni.initGetDeviceConfig();
                Log.d("ZeOne=","initSetValue=" + "value" + ",setResult=" + setResult);
            }
        });
    }

    /**
     * 获取侦测报警实时数据
     * @return
     */
    public Observable<Integer> initGetAnalyseResult(){
        RxUtil.apply(ObservableStart.getObserveStart().initBase()).subscribe();
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int setResult = paramsJni.initGetAnalyseResult();
                Log.d("ZeOne=","initSetValue=" + "value" + ",initGetAnalyseResult=" + setResult);
            }
        });
    }

    /**
     * 上传升级文件
     * @param filePath
     * @return
     */
    public Observable<Integer> initUpgradeFile(String filePath, ParamsJni.FunUpgradeFile funUpgradeFile){
        Log.d("ZeIp=","mIp=" + mIp);
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int setResult = paramsJni.initUpgradeFile(filePath,funUpgradeFile);
                Log.d("ZeOne=","filePath=" + filePath + ",setResult=" + setResult);
                emitter.onNext(setResult);
            }
        });
    }

    //测试JNI读写文件
    public Observable<Integer> initUpgradeFile2(String filePath,ParamsJni.FunUpgradeFile funUpgradeFile){
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int setResult = paramsJni.initUpgradeFile2(filePath,filePath + "1606");
                Log.d("ZeOne=","filePath=" + filePath + ",setResult=" + setResult);
            }
        });
    }

    /**
     * 重启设备
     * @return
     */
    public Observable<Integer> initDeviceReboot(){
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int setResult = paramsJni.initReboot();
                Log.d("ZeOne=","setResult=" + setResult);
                emitter.onNext(setResult);
            }
        });
    }

    /**
     * 断开连接
     * @return
     */
    public Observable<Integer> initDisLink(){
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int setResult = paramsJni.initDisLink();
                Log.d("ZeOne=","setResult=" + setResult);
                emitter.onNext(setResult);
            }
        });
    }

    /**
     * 设置坐标X轴
     * @param value
     * @return
     */
    public Observable<Integer> setX(int value){
        initBase();
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int setResult = paramsJni.initSetX(value);
                Log.d("ZeOne=","initSetValue=" + value + ",setResult=" + setResult);
            }
        });
    }

    /**
     * setY
     * 设置坐标Y轴
     * @param value
     * @return
     */
    public Observable<Integer> setY(int value){
        initBase();
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int setResult = paramsJni.initSetY(value);
                Log.d("ZeOne=","initSetValue=" + value + ",setResult=" + setResult);
            }
        });
    }

    /**
     * 设置锐度
     * @param value
     * @return
     */
    public Observable<Integer> setSharpness(int value){
        initBase();
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int setResult = paramsJni.initSetSharpness(value);
                Log.d("ZeOne=","initSetValue=" + value + ",setResult=" + setResult);
            }
        });
    }

    /**
     * 设置对比度
     * @param value
     * @return
     */
    public Observable<Integer> setContrast(int value){
        initBase();
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int setResult = paramsJni.initSetContrast(value);
                Log.d("ZeOne=","initSetValue=" + value + ",setResult=" + setResult);
            }
        });
    }

    /**
     * 设置明亮度
     * setBrightness
     * @param value
     * @return
     */
    public Observable<Integer> setBrightness(int value){
        initBase();
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int setResult = paramsJni.initSetBrightness(value);
                Log.d("ZeOne=","initSetValue=" + value + ",setResult=" + setResult);
            }
        });
    }

    /**
     * 设置降噪
     * setNoiseReduction
     * @param value
     * @return
     */
    public Observable<Integer> setNoiseReduction(int value){
        initBase();
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int setResult = paramsJni.initSetNoiseReduction(value);
                Log.d("ZeOne=","initSetValue=" + value + ",setResult=" + setResult);
            }
        });
    }

    /**
     * 设置分划
     * setReticle
     * @param value
     * @return
     */
    public Observable<Integer> setReticle(int value){
        initBase();
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int setResult = paramsJni.initSetReticle(value);
                Log.d("ZeOne=","initSetValue=" + value + ",setResult=" + setResult);
            }
        });
    }

    /**
     * setZoom
     * @param value
     * @return
     */
    public Observable<Integer> setZoom(int value){
        initBase();
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int setResult = paramsJni.initSetZoom(value);
                Log.d("ZeOne=","initSetValue=" + value + ",setResult=" + setResult);
            }
        });
    }

    /**
     * 设置调色板
     * @param value
     * @return
     */
    public Observable<Integer> setPalette(int value){
        initBase();
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int setResult = paramsJni.initSetPalette(value);
                Log.d("ZeOne=","initSetValue=" + value + ",setResult=" + setResult);
            }
        });
    }

    /**
     * setDistanceMeasurement
     * @param value
     * @return
     */
    public Observable<Integer> setDistanceMeasurement(int value){
        initBase();
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int setResult = paramsJni.initSetDistanceMeasurement(value);
                Log.d("ZeOne=","initSetValue=" + value + ",setResult=" + setResult);
            }
        });
    }

    /**
     * setTrack
     * @param value
     * @return
     */
    public Observable<Integer> setTrack(int value){
        initBase();
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int setResult = paramsJni.initSetTrack(value);
                Log.d("ZeOne=","initSetValue=" + value + ",setResult=" + setResult);
            }
        });
    }

    /**
     * setTrack
     * @param value
     * @return
     */
    public Observable<Integer> setPip(int value){
        initBase();
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int setResult = paramsJni.initSetPip(value);
                Log.d("ZeOne=","initSetValue=" + value + ",setResult=" + setResult);
            }
        });
    }

    public Observable<Integer> setGPS(int value){
        initBase();
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Exception {
                int setResult = paramsJni.initSetGPS(value);
                Log.d("ZeOne=","initSetValue=" + value + ",setResult=" + setResult);
            }
        });
    }
}
