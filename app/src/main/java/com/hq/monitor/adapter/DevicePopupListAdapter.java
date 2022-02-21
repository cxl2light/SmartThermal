package com.hq.monitor.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hq.basebean.device.DeviceBaseInfo;
import com.hq.monitor.R;


public class DevicePopupListAdapter extends BaseQuickAdapter<DeviceBaseInfo, BaseViewHolder> {
    public DevicePopupListAdapter() {
        super(R.layout.item_device_popup);
    }
    @Override
    protected void convert(@NonNull BaseViewHolder helper, DeviceBaseInfo item) {
          helper.setText(R.id.tvItemOne,item.getDevName());
    }
}