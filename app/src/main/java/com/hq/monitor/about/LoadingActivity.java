package com.hq.monitor.about;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.hq.base.ui.BaseActivity;
import com.hq.commonwidget.WidgetSelectorBtn;
import com.hq.monitor.BuildConfig;
import com.hq.monitor.R;
import com.hq.monitor.device.dialog.LawDialog;
import com.hq.monitor.device.dialog.OnClickListener;
import com.hq.monitor.device.versiontipdialog.OnClickVersionListener;
import com.hq.monitor.device.versiontipdialog.VersionTipDialog;
import com.hq.monitor.ui.devicescanpre.DeviceScanPreActivity;
import com.hq.monitor.util.Constant;
import com.hq.monitor.util.SpUtils;

import java.util.Locale;

public class LoadingActivity extends BaseActivity implements View.OnClickListener {
    private static final int TOTAL_MILLS = 1500;
    private static final int INTERVAL_MILLS = 1000;

    private WidgetSelectorBtn countDownBtn;
    private CountDownTimer mCountDownTimer;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * Home键返回主界面之后，点击ICON再次唤起应用，跳转到最后一个显示界面
         */
        if (!isTaskRoot()) {
            finish();
            return;
        }

        setContentView(R.layout.activity_loading);

        final AppCompatTextView versionName = findViewById(R.id.version_name);
        versionName.setText(getString(R.string.version_name_placeholder, BuildConfig.VERSION_NAME));
        countDownBtn = findViewById(R.id.count_down_btn);
        countDownBtn.setOnClickListener(this);

//        initForGooglePlay();//国际市场，不需要隐私协议信息

        if (isZh() && SpUtils.getBoolean(mActivity,Constant.START_LAUNCH_FIRST,true)){
            initOtherStoreDialog();  //国内市场，需要添加隐私协议显示
        } else {
            initForGooglePlay();
        }
    }

    private void initForGooglePlay() {
        if (SpUtils.getBoolean(mActivity, Constant.NOT_SHOW_AGAIN,false)){
            initCountDownTimer();
        } else {
            initVersionTipDialog();
        }
    }

    private void initOtherStoreDialog() {
//        String title = "用户隐私协议";
//        String content = "我们非常重视您的个人信息和隐私保护。为了有效保障您的个人权益，在使用“Smart Thermal”服务前，请您务必审慎阅读 《用户隐私协议》 内的所有条款，为向你提供通过APP观看热像仪的实时画面,并远程控制热像仪，我们会使用必要的功能信息。\n 1、为了配合购买的硬件使用并简单控制硬件，我们需要访问网络权限，\n 2、为了方便录制并存储硬件的音视频内容，，我们将获取您的内存，相机权限 \n 3、我们对您的设备文件使用/保护等规则条款，以及您的用户权利条等条款;\n 4、约定我们的限制责任、免责条款;\n\n如果同意上述协议及声明的内容，请点击“同意”开始使用产品和服务。如后再次使用，即表示您已同意《用户隐私协议》。 否则，请退出本应用程序并建议删除卸载本应用";
        String title = getString(R.string.laws_title);
        String content = getString(R.string.laws_content);
        LawDialog mDialog = new LawDialog();
        mDialog.show(getSupportFragmentManager(), false, title, content, new OnClickListener() {
            @Override
            public void onClickOne() {
                SpUtils.saveBoolean(mActivity,Constant.START_LAUNCH_FIRST,false);
//                initCountDownTimer();
                initForGooglePlay();
            }
        });
    }

    private void initVersionTipDialog(){
        VersionTipDialog mDialog = new VersionTipDialog();
        mDialog.show(getSupportFragmentManager(), false, "title", "content", new OnClickVersionListener() {
            @Override
            public void onClickOne() {
                Uri uri = Uri.parse(Constant.mDownLoadUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

            @Override
            public void onClickTwo() {
                if (mDialog != null){
                    mDialog.dismiss();
                }
                initCountDownTimer();
            }

            @Override
            public void onClickThree() {
                SpUtils.saveBoolean(mActivity,Constant.NOT_SHOW_AGAIN,true);
                if (mDialog != null){
                    mDialog.dismiss();
                }
                initCountDownTimer();
            }
        });
    }

    private void initCountDownTimer() {
        mCountDownTimer = new CountDownTimer(TOTAL_MILLS, INTERVAL_MILLS) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (countDownBtn == null) {
                    return;
                }
//                countDownBtn.setVisibility(View.VISIBLE);
                countDownBtn.setText(getString(R.string.count_down_tip,
                        millisUntilFinished / INTERVAL_MILLS + 1));
            }

            @Override
            public void onFinish() {
                nextStep();
            }
        };
        mCountDownTimer.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.count_down_btn) {
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }
            v.setEnabled(false);
            nextStep();
        }
    }

    private void nextStep() {
//        DeviceConnectedActivity.startActivity(mActivity);
//        ConnectDeviceActivity.startActivity(mActivity);
        DeviceScanPreActivity.startActivity(mActivity);
        finish();
    }

    private boolean isZh() {
        Locale locale = getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }
}
