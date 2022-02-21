package com.hq.monitor.about;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.hq.base.ui.BaseActivity;
import com.hq.base.util.ActivityCollector;
import com.hq.base.util.StatusBarUtil;
import com.hq.base.util.ToastUtil;
import com.hq.base.widget.CommonTitleBar;
import com.hq.basebean.device.DeviceBaseInfo;
import com.hq.monitor.R;
import com.hq.monitor.adapter.QuickBean;
import com.hq.monitor.jni.ObservableStart;
import com.hq.monitor.jni.ParamsJni;
import com.hq.monitor.net.ApiManager;
import com.hq.monitor.net.download.DownloadCallback;
import com.hq.monitor.net.download.RetrofitFactory;
import com.hq.monitor.net.download.RxNet;
import com.hq.monitor.ui.dialog.versionupdate.OnUpdateClickListener;
import com.hq.monitor.ui.dialog.versionupdate.VersionUpdateDialog;
import com.hq.monitor.util.RxBus;
import com.hq.monitor.util.RxUtil;
import com.hq.monitor.util.SpUtils;
import com.hq.monitor.util.StringRegUtils;
import com.hq.monitor.util.Utils;

import java.io.File;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class AboutDeviceActivity extends BaseActivity {

    VersionUpdateDialog mDialog;
    String mUrl = "";
    String mName = "";
    DeviceBaseInfo info;
    private Disposable mDownloadTask;
    AppCompatTextView tvFour;
    Boolean updateIsReady = false;
    String mFilePath = "";
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        info = SpUtils.getBeanFromSp(mActivity,SpUtils.SP_DEVICE_INFO);

        String dev = info.getHardware();
        if (dev != null && dev.toLowerCase().contains("ares")){
            setContentView(R.layout.activity_about_device_aim);
        }
        else {
            setContentView(R.layout.activity_about_device);
        }

        StatusBarUtil.setStatusBarWhiteMode(this);



        final AppCompatTextView tvOne = findViewById(R.id.tvOne);
        tvOne.setText(getString(R.string.about_product_sn) + info.getHardware());

        final AppCompatTextView tvTwo = findViewById(R.id.tvTwo);
        tvTwo.setText(getString(R.string.about_current_version)+ info.getSoftVer());

        Log.d("ZeOne",info.toString());

        final AppCompatTextView tvThree = findViewById(R.id.tvThree);
        tvThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductIntroduceActivity.startActivity(AboutDeviceActivity.this);
            }
        });

        mDialog = new VersionUpdateDialog();

        tvFour = findViewById(R.id.tvFour);
        tvFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestWritePermission();
            }
        });

        CommonTitleBar mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setImageShareAndClick(R.mipmap.ic_menu_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.initShowMenuDialog(getSupportFragmentManager(),SpUtils.getBoolean(mActivity,Utils.DEVICE_CONNECT,false));
            }
        });

        initObserve();
    }

    private void initObserve() {
        disposable = RxBus.getInstance().toObservable(QuickBean.class).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<QuickBean>() {
            @Override
            public void accept(QuickBean mBean) throws Exception {
                int channelNo = mBean.getIntOne();
                int nUpgrade = mBean.getIntTwo();
                // nUpgrade=0 成功;  非0 失败
                Log.d("ZeUpgrade", "channel=" + channelNo + ",nUpgrade=" + nUpgrade);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (nUpgrade == 0){
                            Log.d("ZeUpgrade", "channel=" + channelNo + ",nUpgrade=" + nUpgrade);
                            mDialog.setOneButton(R.string.about_update_success);
                        } else {
                            mDialog.dismissDialog();
                            ToastUtil.toast(getString(R.string.error_update));
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        RxUtil.apply(ObservableStart.getObserveStart().initBase()).subscribe();
    }

    private void initUpdate() {
        if (updateIsReady){
            initTransDialog();
            initFileTrans();
        } else {
            initUpdateDialog();
//              Utils.GoStore(AboutDeviceActivity.this);
            String url = "https://qinkung1.oss-us-west-1.aliyuncs.com/telescope.ini";
            RxUtil.apply(ApiManager.getDeviceApis().checkVersion())
                    .subscribe(new Consumer<ResponseBody>() {
                        @Override
                        public void accept(ResponseBody versionText) {
                            try {
                                String mVersionText = versionText.string().toString();
                                Log.d("ZeOne",mVersionText);
                                List<String> mDeviceList = StringRegUtils.extractMessageByRegular(mVersionText,"(?<=\\[).*?(?=\\])");
                                for (int i = 0; i < mDeviceList.size();i++){
                                    if (mDeviceList.get(i).equals(info.getHardware())){
//                                    if (mDeviceList.get(i).equals("Cyclops315")){
                                        String[] mSpliteArr = mVersionText.split("version=V");
                                        for (int m = 0; m < mSpliteArr.length; m++){
                                            Log.d("ZeSplit=","m=" + m + ",Str=" + mSpliteArr[m]);
                                        }
                                        Log.d("ZeOne",mSpliteArr[i + 1]);
                                        String[] mOneArr = mSpliteArr[i + 1].split("url=");
                                        Log.d("ZeOne","i=" + i + "==" + mOneArr[0] + "==,==" + mOneArr[1] + "==");
                                        String textCur = info.getSoftVer().split(" ")[0].replace("V","").replace(".","");
                                        String textVer = mOneArr[0].replace(".","").replaceAll("\r?\n","");
                                        Log.d("ZeOne","==" + textCur + "=,=" + textVer + "==");
                                        int versionCur = Integer.parseInt(textCur);
                                        int version = Integer.parseInt(textVer);
                                        Log.d("ZeOne","i=" + i + ",version=" + version + ",versionCur=" + versionCur + "," + (version > versionCur) + "," + mOneArr[1]);
                                        if (version > versionCur){
//                                        if (version <= versionCur){
                                            mUrl = mOneArr[1].split("\n")[0];
//                                            mUrl = "https://misheoss.oss-cn-hangzhou.aliyuncs.com/T400_ThermEye325-ARM-Main_En-V2.0.4-20210923%281%29.pkg";
                                            String[] mNameArr = mUrl.split("/");
                                            mName = mNameArr[mNameArr.length - 1];
                                            ToastUtil.toast(getString(R.string.new_version_detected));
                                            mDialog.setGotNewVersion("V" + mOneArr[0]);
                                            return;
                                        }
                                    }
                                }
//                                 ToastUtil.toast(R.string.new_version_latest);
                                Toast.makeText(mActivity,R.string.new_version_latest,Toast.LENGTH_SHORT).show();
                                 tvFour.setText(R.string.new_version_latest);
                                 mDialog.dismissDialog();
                            } catch (Exception e){
                                mDialog.dismissDialog();
                                ToastUtil.toast(e.toString());
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.d("ZeError","throwable:" + throwable.toString());
                            ToastUtil.toast(getString(R.string.error_network));
                            mDialog.dismissDialog();
                        }
                    });
        }

    }

    private void initUpdateDialog() {
        mDialog.show(getSupportFragmentManager(),false,R.string.about_checking_update, new OnUpdateClickListener() {
            @Override
            public void onClickPositive(DialogFragment dialogFragment) {
                initDownLoad();
            }

            @Override
            public void onClickNegative(DialogFragment dialogFragment) {

            }
        });
    }

    private void initTransDialog() {
        mDialog = new VersionUpdateDialog();
        mDialog.show(getSupportFragmentManager(),false,R.string.about_update_pushing,1, new OnUpdateClickListener() {
            @Override
            public void onClickPositive(DialogFragment dialogFragment) {

            }

            @Override
            public void onClickNegative(DialogFragment dialogFragment) {
                  initDeviceRestart();
            }
        });
        mDialog.setProgressing(R.string.about_update_pushing,0);
    }

    private void initDeviceRestart() {
        ObservableStart.getObserveStart().initDeviceReboot().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.d("ZeOne=","setResult=" + integer);
                if (integer == 1){
                    ActivityCollector.finishAll();
                    startActivity(new Intent(mActivity, LoadingActivity.class));
                    RxUtil.apply(ObservableStart.getObserveStart().initDisLink()).subscribe();
                }
            }
        });
    }

    private void initFileTrans() {
        ObservableStart.getObserveStart().initUpgradeFile(mFilePath, new ParamsJni.FunUpgradeFile() {
            @Override
            public void onUpgradeFile(int channelNo, float nProgress, int endFlag) {
                Log.d("ZeFile=", "channel:" + channelNo + ",nProgress:" + nProgress * 100  + "%,endFlag:" + endFlag);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDialog.setProgressing(R.string.about_update_pushing,(int)(nProgress * 100));

                        if (endFlag == 1){
                            mDialog.setLoading(R.string.about_update_upgrading);
//                            mDialog.setOneButton(R.string.about_update_success);
                        }
                    }
                });
            }
        }) .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                if (integer == 0){
                    ToastUtil.toast(getString(R.string.error_update));
                    mDialog.dismissDialog();
                }
            }
        });
    }

    private void initDownLoad() {
        String path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + mName;
        RxNet.download(mUrl, path, new DownloadCallback() {
            @Override
            public void onStart(Disposable d) {
                mDownloadTask = d;
                mDialog.setProgressing(R.string.about_update_downloading,0);
            }

            @Override
            public void onProgress(long totalByte, long currentByte, int progress) {
                mDialog.setProgressing(R.string.about_update_downloading,progress);
            }

            @Override
            public void onFinish(File file) {
                mDialog.dismissDialog();
                updateIsReady = true;
                tvFour.setText(R.string.about_update_push);
                mFilePath = file.getAbsolutePath();
            }

            @Override
            public void onError(String msg) {
                ToastUtil.toast(msg);
                RetrofitFactory.cancel(mDownloadTask);
                mDialog.dismissDialog();
            }
        });
    }

    private static final int REQUEST_CODE_PERMISSION_READ_STORAGE = 0xff10;

    private void requestWritePermission() {
        final int result = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            initUpdate();
            return;
        }
        mActivity.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_READ_STORAGE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitFactory.cancel(mDownloadTask);
        if(disposable!=null&&!disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_CODE_PERMISSION_READ_STORAGE) {
            return;
        }
        initUpdate();
    }

    public static void startActivity(Context context) {
        if (context == null) {
            return;
        }
        final Intent intent = new Intent(context, AboutDeviceActivity.class);
        context.startActivity(intent);
    }

}
