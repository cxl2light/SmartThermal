package com.hq.monitor.device;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hq.base.CommonConst;
import com.hq.base.consts.GlobalConst;
import com.hq.base.dialog.CommonConfirmDialog;
import com.hq.base.ui.BaseActivity;
import com.hq.base.util.StatusBarUtil;
import com.hq.base.util.ToastUtil;
import com.hq.base.widget.CommonTitleBar;
import com.hq.basebean.device.DeviceBaseInfo;
import com.hq.commonwidget.CircleProgress;
import com.hq.commonwidget.item_decoration.SpaceItemDecoration;
import com.hq.commonwidget.util.DensityUtil;
import com.hq.monitor.R;
import com.hq.monitor.device.menudialog.MenuDialog;
import com.hq.monitor.device.socket.SocketServerUtil;
import com.hq.monitor.device.widget.DeviceWidget;
import com.hq.monitor.util.NetWorkUtil;
import com.hq.monitor.view.IOSLoadingView;

import java.util.ArrayList;

/**
 * 设备列表页面
 */
public class DeviceScanListActivity extends BaseActivity implements View.OnClickListener, IDeviceScanView {

    private static String TAG = "ZeNet=" + DeviceScanListActivity.class.getSimpleName();

    private CommonTitleBar mTitleBar;
    private BaseQuickAdapter<DeviceBaseInfo, BaseViewHolder> deviceAdapter;
    private RecyclerView deviceRecyclerList;
    private View connectDeviceBtn;
    private DeviceScanPresenter mDeviceScanPresenter;
    private CircleProgress mProgressBar;
    private IOSLoadingView loadingOne;
    private View progressLayout;
    private AppCompatTextView scanTip;
    private Boolean isScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeviceScanPresenter = new DeviceScanPresenter(this);
        setContentView(R.layout.activity_device_scan_list);
        StatusBarUtil.setStatusBarWhiteMode(this);
        connectDeviceBtn = findViewById(R.id.connect_device_btn);
        connectDeviceBtn.getLayoutParams().width = CommonConst.getCommonWidgetWidth(mActivity);
        connectDeviceBtn.setOnClickListener(this);
        deviceRecyclerList = findViewById(R.id.device_recycler_list);
        mProgressBar = findViewById(R.id.scan_progress_bar);

        loadingOne = findViewById(R.id.loadingOne);

        progressLayout = findViewById(R.id.progress_layout);
        scanTip = findViewById(R.id.scan_tip);
        initRecyclerList();

        mProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"isScanning=" + isScanning);
                if (!isScanning){
                    scanDevice();
                }
            }
        });

        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setImageShareAndClick(R.mipmap.ic_menu_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initShowMenuDialog();
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                scanDevice();
            }
        },2000);
    }

    private void initShowMenuDialog() {
        MenuDialog mDialog = new MenuDialog();
        mDialog.show(getSupportFragmentManager(), true,false, "title", "content");
    }

    private void initRecyclerList() {
        deviceRecyclerList.setLayoutManager(new LinearLayoutManager(mActivity));
        deviceRecyclerList.setNestedScrollingEnabled(false);
        deviceAdapter = new BaseQuickAdapter<DeviceBaseInfo, BaseViewHolder>(-1, null) {

            @NonNull
            @Override
            protected BaseViewHolder onCreateDefViewHolder(@NonNull ViewGroup parent, int viewType) {
                final DeviceWidget widget = new DeviceWidget(mActivity);
                final ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(CommonConst.getCommonWidgetWidth(mActivity),
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
        deviceRecyclerList.addItemDecoration(new SpaceItemDecoration(DensityUtil.dpToPx(mActivity, 10)));
    }

    private void scanDevice() {
        isScanning = true;
        final boolean wifi = NetWorkUtil.isWifiEnabled(mActivity);
        Log.d(TAG,"curIpZero=" + wifi);
        if (wifi) {
            // WiFi情况下
            String curIp = NetWorkUtil.getWifiIp(mActivity.getApplicationContext());
            Log.d(TAG,"curIpOne=" + curIp);
            if (mDeviceScanPresenter.isValidateIp(curIp)) {
                ToastUtil.toast(getString(R.string.tip_cur_ip_addr,curIp));
                mDeviceScanPresenter.scanWifiDevice(curIp);
            } else {
                curIp = NetWorkUtil.getApHostIp();
                Log.d(TAG,"curIpTwo=" + curIp);
                if (mDeviceScanPresenter.isValidateIp(curIp)) {
                    ToastUtil.toast(getString(R.string.tip_cur_ip_addr,curIp));
                    mDeviceScanPresenter.scanWifiDevice(curIp);
                }
            }
        } else {
            final boolean ap = NetWorkUtil.isApOn(mActivity);
            Log.d(TAG,"curIpThree=" + ap);
            if (ap) {
                requestReadPermission();
                return;
            }
            if (GlobalConst.isDebug()) {
                ToastUtil.toast(getString(R.string.tip_open_wi_fi_hotspots));
            }
            final CommonConfirmDialog dialog = new CommonConfirmDialog(mActivity);
            dialog.setContent(getString(R.string.tip_open_net));
            dialog.show();
        }
    }

    private static final int REQUEST_CODE_PERMISSION_READ_STORAGE = 0xff10;

    private void requestReadPermission() {
        final int result = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            mDeviceScanPresenter.scanApDeviceList();
            return;
        }
        mActivity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION_READ_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_CODE_PERMISSION_READ_STORAGE) {
            return;
        }
        mDeviceScanPresenter.scanApDeviceList();
    }

    @Override
    public void showDeviceList(@NonNull ArrayList<DeviceBaseInfo> deviceList) {
        progressLayout.setVisibility(deviceList.isEmpty() ? View.VISIBLE : View.GONE);
        deviceAdapter.setNewInstance(deviceList);
    }

    @Override
    public void showProgress(int progress) {
        mProgressBar.setProgress(progress);
        if (progress == 100){
            loadingOne.setVisibility(View.GONE);
        }
    }

    @Override
    public void scanFinished() {
        if (deviceAdapter.getData().isEmpty()) {
            scanTip.setText(getString(R.string.tip_scan_finished_no_device));
            isScanning = false;
        }
        if (GlobalConst.isDebug()) {
            connectDeviceBtn.setEnabled(true);
        }
    }

    @Override
    public void scanTip(String tip) {
        scanTip.setText(getString(R.string.tip_get_device_ip_exception) + tip);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect_device_btn:
                connectDevice();
                break;
            default:
                break;
        }
    }

    private DeviceBaseInfo preSelectedDeviceInfo;

    private final DeviceWidget.OnSelectListener mOnSelectListener = new DeviceWidget.OnSelectListener() {
        @Override
        public void onSelect(@NonNull View v, @NonNull DeviceBaseInfo data) {
            if (preSelectedDeviceInfo != null) {
                preSelectedDeviceInfo.setSelected(false);
            }
            preSelectedDeviceInfo = data;
            preSelectedDeviceInfo.setSelected(true);
            deviceAdapter.notifyDataSetChanged();
            connectDeviceBtn.setEnabled(true);
        }
    };

    private void connectDevice() {
        if (GlobalConst.isDebug()) {
            final Uri uri = Uri.parse("rtsp://192.168.1.11:554/mainstream");
//            ControlDeviceActivity.startActivity(mActivity, uri.toString(), uri.getHost());
//            ControlDeviceIJKActivity.startActivity(mActivity, uri.toString(), uri.getHost(), null);
            ControlDeviceIJKActivity.startActivity(mActivity);
            return;
        }
        if (preSelectedDeviceInfo == null) {
            ToastUtil.toast(getString(R.string.please_select_connect_device));
            return;
        }
//        ControlDeviceIJKActivity.startActivity(mActivity, "rtsp://" + preSelectedDeviceInfo.getIp() + ":554/mainstream",
//                preSelectedDeviceInfo.getIp(), preSelectedDeviceInfo.getDevName());
        ControlDeviceIJKActivity.startActivity(mActivity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketServerUtil.destroy();
        mDeviceScanPresenter.destroy();
    }

    public static void startActivity(Context context) {
        if (context == null) {
            return;
        }
        context.startActivity(new Intent(context, DeviceScanListActivity.class));
    }

}
