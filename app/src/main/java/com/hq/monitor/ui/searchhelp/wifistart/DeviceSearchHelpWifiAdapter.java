package com.hq.monitor.ui.searchhelp.wifistart;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hq.monitor.R;
import com.hq.monitor.adapter.QuickBean;

public class DeviceSearchHelpWifiAdapter extends BaseQuickAdapter<QuickBean, BaseViewHolder> {
    public DeviceSearchHelpWifiAdapter() {
        super(R.layout.item_device_search_help_device);
    }
    @Override
    protected void convert(@NonNull BaseViewHolder helper, QuickBean item) {
        helper.setText(R.id.tvItemZero,"" + item.getIntOne());
        helper.setImageResource(R.id.ivItemOne,item.getIntTwo());
        helper.setText(R.id.tvItemOne,getContext().getString(item.getIntThree()));
    }
}