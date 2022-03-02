package com.hq.monitor.ui.devicescanpre;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hq.base.CommonConst;
import com.hq.base.consts.GlobalConst;
import com.hq.base.dialog.CommonConfirmDialog;
import com.hq.base.ui.BaseActivity;
import com.hq.base.util.Logger;
import com.hq.base.util.ToastUtil;
import com.hq.base.widget.CommonTitleBar;
import com.hq.basebean.device.DeviceBaseInfo;
import com.hq.commonwidget.item_decoration.SpaceItemDecoration;
import com.hq.commonwidget.util.DensityUtil;
import com.hq.monitor.R;
import com.hq.monitor.adapter.DevicePopupListAdapter;
import com.hq.monitor.app.MyApplication;
import com.hq.monitor.device.DeviceScanPresenter;
import com.hq.monitor.device.IDeviceScanView;
import com.hq.monitor.device.menudialog.MenuDialog;
import com.hq.monitor.device.selectdialog.OnSelectClickListener;
import com.hq.monitor.device.selectdialog.SelectDialog;
import com.hq.monitor.device.socket.SocketServerUtil;
import com.hq.monitor.device.widget.DeviceWidget;
import com.hq.monitor.ui.searchhelp.devicesearchhelp.DeviceSearchHelpActivity;
import com.hq.monitor.util.NetWorkUtil;
import com.hq.monitor.util.SpUtils;
import com.hq.monitor.util.Utils;
import com.hq.monitor.view.IOSLoadingView;

import java.util.ArrayList;

/**
 * 设备列表页面
 */
public class DeviceScanPreActivity extends BaseActivity implements View.OnClickListener, IDeviceScanView {

    private static String TAG = "ZeNet=" + DeviceScanPreActivity.class.getSimpleName();

    private CommonTitleBar mTitleBar;
    private BaseQuickAdapter<DeviceBaseInfo, BaseViewHolder> deviceAdapter;
    private RecyclerView deviceRecyclerList;
    private View connectDeviceBtn,scanDeviceBtn;
    private TextView tvDeviceName;
    private RelativeLayout rlDeviceName;
    private DeviceScanPresenter mDeviceScanPresenter;
    private IOSLoadingView loadingOne;
    private View progressLayout;
    private AppCompatTextView scanTip;
    private Boolean isScanning = false;

    ArrayList<DeviceBaseInfo> mDeviceList = new ArrayList<>();

    int positionFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeviceScanPresenter = new DeviceScanPresenter(this);
        setContentView(R.layout.activity_device_scan_pre);
//        StatusBarUtil.setStatusBarWhiteMode(this);
        rlDeviceName = findViewById(R.id.rlDeviceName);
        rlDeviceName.getLayoutParams().width = CommonConst.getCommonWidgetWidth(mActivity);
        rlDeviceName.setOnClickListener(this);
        tvDeviceName = findViewById(R.id.tvDeviceName);
        connectDeviceBtn = findViewById(R.id.connect_device_btn);
        connectDeviceBtn.getLayoutParams().width = CommonConst.getCommonWidgetWidth(mActivity);
        connectDeviceBtn.setOnClickListener(this);
        scanDeviceBtn = findViewById(R.id.scan_device_btn);
        scanDeviceBtn.getLayoutParams().width = CommonConst.getCommonWidgetWidth(mActivity);
        scanDeviceBtn.setOnClickListener(this);
        deviceRecyclerList = findViewById(R.id.device_recycler_list);

        loadingOne = findViewById(R.id.loadingOne);

        progressLayout = findViewById(R.id.progress_layout);
        scanTip = findViewById(R.id.scan_tip);
        initRecyclerList();

        positionFrom = getIntent().getIntExtra(Utils.DEVICE_KEY,-1);

        mTitleBar = findViewById(R.id.title_bar);

        if (positionFrom > 0){
            mTitleBar.setIvBackVisibility(View.VISIBLE);
        } else {
            mTitleBar.setIvBackVisibility(View.INVISIBLE);
            mTitleBar.setImageShareAndClick(R.mipmap.ic_menu_white, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initShowMenuDialog();
                }
            });
        }
        mTitleBar.getBackBtnLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTitleBar.setIvBackVisibility(View.GONE);
                connectDeviceBtn.setVisibility(View.GONE);
                scanDeviceBtn.setVisibility(View.VISIBLE);
                rlDeviceName.setVisibility(View.GONE);
                progressLayout.setVisibility(View.GONE);
            }
        });

        if (!isIgnoringBatteryOptimizations()){
            requestIgnoreBatteryOptimizations();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isIgnoringBatteryOptimizations() {
        boolean isIgnoring = false;
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if(powerManager != null) {
            isIgnoring = powerManager.isIgnoringBatteryOptimizations(getPackageName());
        }
        Logger.d(TAG, "isIgnoring=" + isIgnoring);
        return isIgnoring;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestIgnoreBatteryOptimizations() {
        try{
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:"+ getPackageName()));
            startActivityForResult(intent, 200);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && !isIgnoringBatteryOptimizations()){
            if (isHuawei()){
                goHuaweiSetting();
            } else if (isOPPO()){
                goOPPOSetting();
            } else if (isVIVO()){
                goVIVOSetting();
            } else if (isMeizu()){
                goMeizuSetting();
            } else if (isSamsung()){
                goSamsungSetting();
            } else if (isXiaomi()){
                goXiaomiSetting();
            } else if (isLeTV()){
                goLetvSetting();
            } else if (isSmartisan()){
                goSmartisanSetting();
            } else {
                showActivity(getPackageName());
            }
        }
    }

    private void initShowMenuDialog() {
        MenuDialog mDialog = new MenuDialog();
        mDialog.show(getSupportFragmentManager(), true,false, "title", "content");
//        mDialog.show(getSupportFragmentManager(), true,true, "title", "content");
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
//                ToastUtil.toast(getString(R.string.tip_cur_ip_addr,curIp));
                mDeviceScanPresenter.scanWifiDevice(curIp);
            } else {
                curIp = NetWorkUtil.getApHostIp();
                Log.d(TAG,"curIpTwo=" + curIp);
                if (mDeviceScanPresenter.isValidateIp(curIp)) {
//                    ToastUtil.toast(getString(R.string.tip_cur_ip_addr,curIp));
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
//        progressLayout.setVisibility(deviceList.isEmpty() ? View.VISIBLE : View.GONE);
//        deviceAdapter.setNewInstance(deviceList);
        for (DeviceBaseInfo info:deviceList){
            Log.d("ZeOne","=" + info.toString());
        }
        Log.d("ZeOne","=" + deviceList.size());
        //去除重复的
        mDeviceList.clear();
        mDeviceList.addAll(deviceList);
        Log.d("ZeOne","=" + deviceList.size());

        boolean isEmpty = mDeviceList.isEmpty();
        if (!isEmpty){
            preSelectedDeviceInfo = mDeviceList.get(0);

            loadingOne.setVisibility(View.GONE);
            scanDeviceBtn.setVisibility(View.GONE);
            progressLayout.setVisibility(View.GONE);

            connectDeviceBtn.setVisibility(View.VISIBLE);
            mTitleBar.setIvBackVisibility(View.VISIBLE);

            rlDeviceName.setVisibility(View.VISIBLE);
            tvDeviceName.setText(mDeviceList.get(0).getDevName());
        } else {
            rlDeviceName.setVisibility(View.GONE);
            connectDeviceBtn.setVisibility(View.GONE);
            scanDeviceBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showProgress(int progress) {
        if (progress == 100){
//            loadingOne.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SpUtils.saveBoolean(mActivity,Utils.DEVICE_CONNECT,false);
    }

    @Override
    public void scanFinished() {
//        if (deviceAdapter.getData().isEmpty()) {
        if (mDeviceList.isEmpty()) {
            scanTip.setText(getString(R.string.tip_scan_finished_no_device));
            loadingOne.setVisibility(View.GONE);
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
            case R.id.scan_device_btn:
                if (positionFrom > 0){
                    initScanStart();
                } else {
                    initSelectDialog();
                }
                break;

            case R.id.connect_device_btn:
                scanTip.setText("");
                progressLayout.setVisibility(View.VISIBLE);
                loadingOne.setVisibility(View.VISIBLE);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressLayout.setVisibility(View.GONE);
                        SpUtils.saveBean2Sp(mActivity,preSelectedDeviceInfo,SpUtils.SP_DEVICE_INFO);
//                        DeviceConnectedActivity.startActivity(mActivity);
                        Utils.connectDevice(mActivity);
                    }
                },2000);
//                connectDevice();
                break;

            case R.id.rlDeviceName:
                if (mDeviceList.size() < 1){
                    ToastUtil.toast("获取设备信息有误，请返回重试");
                } else {
                    showPopupWindow();
                }
                break;

            default:
                break;
        }
    }

    private void initSelectDialog() {
        String title = getString(R.string.laws_title);
        String content = getString(R.string.select_hint);
        SelectDialog mDialog = new SelectDialog();
        mDialog.show(getSupportFragmentManager(), false, title, content, new OnSelectClickListener() {
            @Override
            public void onClickPositive(DialogFragment dialogFragment) {
                dialogFragment.dismiss();
                DeviceSearchHelpActivity.startActivity(mActivity);
            }

            @Override
            public void onClickNegative(DialogFragment dialogFragment) {
                dialogFragment.dismiss();
                initScanStart();
            }
        });
    }

    private void initScanStart() {
        progressLayout.setVisibility(View.VISIBLE);
        scanTip.setText(getString(R.string.tip_scanning_device));
        loadingOne.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                scanDevice();
            }
        },2000);
    }

    private void showPopupWindow() {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popupwindows_recycleview_base,null);
        PopupWindow popupWindow = new PopupWindow(mActivity);
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(getColor(android.R.color.transparent)));    //要为popWindow设置一个背景才有效
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(rlDeviceName);
        popupView.getLayoutParams().width = CommonConst.getCommonWidgetWidth(mActivity);
        RecyclerView rvPopup = popupView.findViewById(R.id.rv_popup);
        DevicePopupListAdapter mPopAdapter = new DevicePopupListAdapter();
        rvPopup.setLayoutManager(new GridLayoutManager(mActivity, 1, RecyclerView.VERTICAL, false));
        rvPopup.setAdapter(mPopAdapter);
        mPopAdapter.setList(mDeviceList);
        mPopAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                preSelectedDeviceInfo = mDeviceList.get(position);
                tvDeviceName.setText(mDeviceList.get(position).getDevName());
                popupWindow.dismiss();
            }
        });
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
        context.startActivity(new Intent(context, DeviceScanPreActivity.class));
    }

    public static void startActivity(Context context, int position) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, DeviceScanPreActivity.class);
        intent.putExtra(Utils.DEVICE_KEY, position);
        context.startActivity(intent);
    }


     /** * 跳转到指定应用的首页 */
    private void showActivity(@NonNull String packageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        startActivity(intent);
    }
    /** * 跳转到指定应用的指定页面 */
    private void showActivity(@NonNull String packageName, @NonNull String activityDir) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activityDir));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public static boolean isHuawei() {
        if(Build.BRAND == null) {
            return false;
        } else{
            return Build.BRAND.toLowerCase().equals("huawei") || Build.BRAND.toLowerCase().equals("honor");
        }
    }

    private void goHuaweiSetting() {
        try{
            showActivity("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
        } catch(Exception e) {
            try {
                showActivity("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.bootstart.BootStartActivity");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public static boolean isXiaomi() {
        return Build.BRAND != null&& Build.BRAND.toLowerCase().equals("xiaomi");
    }

    private void goXiaomiSetting() {
        try {
            showActivity("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isOPPO() {
        return Build.BRAND != null&& Build.BRAND.toLowerCase().equals("oppo");
    }

    private void goOPPOSetting() {
        try{
            showActivity("com.coloros.phonemanager");
        } catch(Exception e1) {
            try{
                showActivity("com.oppo.safe");
            } catch(Exception e2) {
                try{
                    showActivity("com.coloros.oppoguardelf");
                } catch(Exception e3) {
                    showActivity("com.coloros.safecenter");
                }
            }
        }
    }

    public static boolean isVIVO() {
        return Build.BRAND != null&& Build.BRAND.toLowerCase().equals("vivo");
    }

    private void goVIVOSetting() {
        try {
            showActivity("com.iqoo.secure");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isMeizu() {
        return Build.BRAND != null&& Build.BRAND.toLowerCase().equals("meizu");
    }

    private void goMeizuSetting() {
        try {
            showActivity("com.meizu.safe");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isSamsung() {
        return Build.BRAND != null&& Build.BRAND.toLowerCase().equals("samsung");
    }

    private void goSamsungSetting() {
        try{
            showActivity("com.samsung.android.sm_cn");
        } catch(Exception e) {
            showActivity("com.samsung.android.sm");
        }
    }

    public static boolean isLeTV() {
        return Build.BRAND != null&& Build.BRAND.toLowerCase().equals("letv");
    }

    private void goLetvSetting() {
        try {
            showActivity("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isSmartisan() {
        return Build.BRAND != null&& Build.BRAND.toLowerCase().equals("smartisan");
    }

    private void goSmartisanSetting() {
        try {
            showActivity("com.smartisanos.security");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
