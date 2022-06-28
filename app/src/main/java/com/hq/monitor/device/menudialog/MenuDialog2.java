package com.hq.monitor.device.menudialog;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.hq.monitor.MapsGpsActivity;
import com.hq.monitor.R;
import com.hq.monitor.about.AboutAppActivity;
import com.hq.monitor.about.AboutDeviceActivity;
import com.hq.monitor.adapter.MenuAdapter;
import com.hq.monitor.adapter.QuickBean;
import com.hq.monitor.device.alarm.DetectionAlarmActivity;
import com.hq.monitor.device.dialog.OnClickListener;
import com.hq.monitor.ui.searchhelp.devicesearchhelp.DeviceSearchHelpActivity;
import com.hq.monitor.util.Utils;

import java.util.ArrayList;

public class MenuDialog extends DialogFragment {

    View dialogView;
    String mTitle;
    String mContent;
    OnClickListener listener;
    RecyclerView rvOne;
    int positionSelect = 1;

    ArrayList mDataList = new ArrayList();
    boolean isClickable = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dialogView = inflater.inflate(R.layout.dialog_fragment_menu, null);
        rvOne = dialogView.findViewById(R.id.rvOne);
        return dialogView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initAdapter();
    }

    private void initAdapter() {
        mDataList.clear();
        mDataList.add(new QuickBean(false,true,R.mipmap.ic_menu_back,R.string.menu_return_to_previous));
        mDataList.add(new QuickBean(false,true,R.mipmap.ic_menu_help,R.string.menu_connection_guide));
//        mDataList.add(new QuickBean(false,R.mipmap.ic_menu_setting,"直播设置"));
        mDataList.add(new QuickBean(false,isClickable,R.mipmap.ic_menu_devices,R.string.menu_device_management));
        mDataList.add(new QuickBean(false,isClickable,R.mipmap.ic_menu_videocast,R.string.menu_realtime_vision));
        mDataList.add(new QuickBean(false,isClickable,R.mipmap.ic_menu_alarm,R.string.detection_alarm));
        mDataList.add(new QuickBean(false,true,R.mipmap.ic_menu_gps_path,R.string.menu_google_gps_path));
        mDataList.add(new QuickBean(false,true,R.mipmap.ic_menu_about,R.string.menu_about));
        //创建adapter
        MenuAdapter adapterOne = new MenuAdapter();
        rvOne.setAdapter(adapterOne);
        adapterOne.setList(mDataList);
        adapterOne.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (!isClickable && (position == 2 || position == 3 || position == 4)){
                    return;
                }
//                if (position != positionSelect){
//                    adapterOne.getData().get(position).setCheckOne(true);
//                    adapterOne.notifyItemChanged(position);
//                    adapterOne.getData().get(positionSelect).setCheckOne(false);
//                    adapterOne.notifyItemChanged(positionSelect);
//                    positionSelect = position;
//                }
//                ToastUtil.toast("点击了=" + position + "=," + adapterOne.getData().get(position).getStrOne());
                switch (position){
                    case 0 :
                        dismiss() ;
//                        if (getActivity() != null && !(getActivity() instanceof DeviceScanPreActivity))
                        if (getActivity() != null)
                        getActivity().finish();
                        break;
                    case 1 :
                        dismiss() ;
//                        ConnectHelpActivity.startActivity(getContext());
                        DeviceSearchHelpActivity.startActivity(getContext());
                        break;
                    case 2 :
                        dismiss() ;
                        AboutDeviceActivity.startActivity(getContext());
                        break;
                    case 3 :
                        dismiss() ;
                        Utils.connectDevice(getActivity());
                        break;
                    case 4 :
                        dismiss() ;
                        DetectionAlarmActivity.startActivity(getContext());
                        break;
                    case 5 :
                        dismiss() ;
                        MapsGpsActivity.startActivity(getContext());
                        break;
                    case 6 :
                        dismiss() ;
                        AboutAppActivity.startActivity(getContext());
                        break;
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        //获取DisplayMetrics
        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        getDialog().getWindow().setLayout((int) (dm.widthPixels * 0.3), WindowManager.LayoutParams.MATCH_PARENT);
        /**
         * https://blog.csdn.net/u011183394/article/details/51445202?utm_source=blogxgwz9
         */
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getDialog().getWindow().setGravity(Gravity.TOP|Gravity.LEFT);

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        getDialog().getWindow().getDecorView().setSystemUiVisibility(uiOptions);

//        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        // 前2 个flag设置dialog 显示到状态栏    第三个设置点击dialog以外的蒙层 不抢夺焦点  响应点击事件
//        lp.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
    }

    public void show(FragmentManager fragmentManager, Boolean isCancelable, Boolean isClickable, String title, String content){
        setCancelable(isCancelable);
        mTitle = title;
        mContent = content;
        this.isClickable = isClickable;
        this.listener = listener;
        show(fragmentManager,"mDialogFragment");
    }
}
