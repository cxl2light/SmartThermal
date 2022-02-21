package com.hq.monitor.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hq.base.CommonConst;
import com.hq.base.ui.BaseActivity;
import com.hq.base.util.ScreenUtils;
import com.hq.base.util.StatusBarUtil;
import com.hq.base.util.ToastUtil;
import com.hq.basebean.device.DeviceBaseInfo;
import com.hq.commonwidget.WidgetSelectorBtn;
import com.hq.commonwidget.item_decoration.SpaceItemDecoration;
import com.hq.commonwidget.util.DensityUtil;
import com.hq.monitor.about.AboutDeviceActivity;
import com.hq.monitor.R;
import com.hq.monitor.RtspUrlInputActivity;
import com.hq.monitor.device.menudialog.MenuDialog;
import com.hq.monitor.device.selectdialog.OnSelectClickListener;
import com.hq.monitor.device.selectdialog.SelectDialog;
import com.hq.monitor.device.widget.DeviceWidget;

public class ConnectDeviceActivity extends BaseActivity implements View.OnClickListener {
    private static final int MILLIS_IN_FUTURE = 8000;
    private static final int COUNTDOWN_INTERVAL = 500;

    private View connectDeviceBtn, deviceListLabelLayout, scanProgressBar;
    private WidgetSelectorBtn scanDeviceBtn;
    private RecyclerView deviceRecyclerList;
    private BaseQuickAdapter<DeviceBaseInfo, BaseViewHolder> deviceAdapter;
    ImageView ivMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_device);

        StatusBarUtil.setStatusBarWhiteMode(this);

        final ViewGroup rootLayout = findViewById(R.id.root_layout);
        rootLayout.setPadding(rootLayout.getPaddingLeft(), ScreenUtils.getStatusHeight(),
                rootLayout.getPaddingRight(), rootLayout.getPaddingBottom());
        final View aboutTextView = findViewById(R.id.about);
        aboutTextView.setOnClickListener(this);
        connectDeviceBtn = findViewById(R.id.connect_device_btn);
        connectDeviceBtn.setOnClickListener(this);
        scanDeviceBtn = findViewById(R.id.scan_device_btn);
        scanDeviceBtn.getLayoutParams().width = CommonConst.getCommonWidgetWidth(mActivity);
        scanDeviceBtn.setOnClickListener(this);
        deviceRecyclerList = findViewById(R.id.device_recycler_list);
        deviceListLabelLayout = findViewById(R.id.device_list_label_layout);
        scanProgressBar = findViewById(R.id.scan_progress_bar);
        ivMenu = findViewById(R.id.ivMenu);
        ivMenu.setOnClickListener(this);
        initRecyclerList();

        findViewById(R.id.tvText).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                finish();
                return false;
            }
        });
    }

    private void initRecyclerList() {
        deviceRecyclerList.setLayoutManager(new LinearLayoutManager(mActivity));
        deviceRecyclerList.setNestedScrollingEnabled(false);
        deviceAdapter = new BaseQuickAdapter<DeviceBaseInfo, BaseViewHolder>(-1, null) {

            @NonNull
            @Override
            protected BaseViewHolder onCreateDefViewHolder(@NonNull ViewGroup parent, int viewType) {
                final DeviceWidget widget = new DeviceWidget(mActivity);
                final ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                widget.setLayoutParams(layoutParams);
                widget.setOnSelectListener(mOnSelectListener);
                return createBaseViewHolder(widget);
            }

            @Override
            protected void convert(@NonNull BaseViewHolder holder, DeviceBaseInfo d) {
                final DeviceWidget textView = (DeviceWidget) holder.itemView;
                textView.setData(d);
            }
        };
        deviceRecyclerList.setAdapter(deviceAdapter);
        deviceRecyclerList.addItemDecoration(new SpaceItemDecoration(DensityUtil.dpToPx(mActivity,10)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivMenu:
                initShowMenuDialog();
                break;
            case R.id.about:
                AboutDeviceActivity.startActivity(mActivity);
                break;
            case R.id.scan_device_btn:
//                DeviceScanListActivity.startActivity(mActivity);
                  initDialog();

//                scanDevice();

                break;
            case R.id.connect_device_btn:
                connectDevice();
                break;
            default:
                break;
        }
    }

    private void initShowMenuDialog() {
        MenuDialog mDialog = new MenuDialog();
        mDialog.show(getSupportFragmentManager(), true,false, "title", "content");
    }

    private void initDialog() {
//        String title = "用户隐私协议";
//        String content = "我们非常重视您的个人信息和隐私保护。为了有效保障您的个人权益，在使用“Smart Thermal”服务前，请您务必审慎阅读 《用户隐私协议》 内的所有条款，为向你提供通过APP观看热像仪的实时画面,并远程控制热像仪，我们会使用必要的功能信息。\n 1、为了配合购买的硬件使用并简单控制硬件，我们需要访问网络权限，\n 2、为了方便录制并存储硬件的音视频内容，，我们将获取您的内存，相机权限 \n 3、我们对您的设备文件使用/保护等规则条款，以及您的用户权利条等条款;\n 4、约定我们的限制责任、免责条款;\n\n如果同意上述协议及声明的内容，请点击“同意”开始使用产品和服务。如后再次使用，即表示您已同意《用户隐私协议》。 否则，请退出本应用程序并建议删除卸载本应用";
        String title = getString(R.string.laws_title);
        String content = getString(R.string.laws_content);
        content = getString(R.string.select_hint);
        SelectDialog mDialog = new SelectDialog();
        mDialog.show(getSupportFragmentManager(), false, title, content, new OnSelectClickListener() {
            @Override
            public void onClickPositive(DialogFragment dialogFragment) {
                dialogFragment.dismiss();
//                ConnectHelpActivity.startActivity(mActivity);
            }

            @Override
            public void onClickNegative(DialogFragment dialogFragment) {
                dialogFragment.dismiss();
                DeviceScanListActivity.startActivity(mActivity);
            }
        });
    }
    private CountDownTimer mCountDownTimer;

    private void scanDevice() {
        if (mCountDownTimer != null) {
            scanDeviceBtn.setText(R.string.scan_device);
            mCountDownTimer.cancel();
            mCountDownTimer = null;
            return;
        }
        deviceListLabelLayout.setVisibility(View.VISIBLE);
        mCountDownTimer = new CountDownTimer(MILLIS_IN_FUTURE, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
//                deviceAdapter.addData(new DeviceBaseInfo("设备: " + (millisUntilFinished / 1000 + 1)));
            }

            @Override
            public void onFinish() {
                if (scanProgressBar == null) {
                    return;
                }
                scanProgressBar.setVisibility(View.GONE);
                scanDeviceBtn.setText(R.string.scan_device);
                mCountDownTimer = null;
            }
        };
        mCountDownTimer.start();
        scanDeviceBtn.setText(R.string.scan_device_stop);
    }

    private void connectDevice() {
        if (preSelectedDeviceBaseInfo == null) {
            ToastUtil.toast(getString(R.string.please_select_connect_device));
            return;
        }
        RtspUrlInputActivity.startActivity(mActivity);
//        ControlDeviceActivity.startActivity(mActivity);
    }

    private DeviceBaseInfo preSelectedDeviceBaseInfo;

    private final DeviceWidget.OnSelectListener mOnSelectListener = new DeviceWidget.OnSelectListener() {
        @Override
        public void onSelect(@NonNull View v, @NonNull DeviceBaseInfo data) {
            if (preSelectedDeviceBaseInfo != null) {
                preSelectedDeviceBaseInfo.setSelected(false);
            }
            preSelectedDeviceBaseInfo = data;
            preSelectedDeviceBaseInfo.setSelected(true);
            deviceAdapter.notifyDataSetChanged();
            connectDeviceBtn.setEnabled(true);
        }
    };

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    public static void startActivity(Context context) {
        if (context == null) {
            return;
        }
        final Intent intent = new Intent(context, ConnectDeviceActivity.class);
        context.startActivity(intent);
    }

    /*static {
        System.loadLibrary("native-lib");
    }

    public native String stringFromJNI();*/

}
