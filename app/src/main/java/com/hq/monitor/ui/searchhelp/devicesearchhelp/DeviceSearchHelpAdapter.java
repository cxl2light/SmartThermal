package com.hq.monitor.ui.searchhelp.devicesearchhelp;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hq.monitor.R;
import com.hq.monitor.adapter.QuickBean;

public class DeviceSearchHelpAdapter extends BaseQuickAdapter<QuickBean, BaseViewHolder> {
    public DeviceSearchHelpAdapter() {
        super(R.layout.item_device_search_help);
    }
    @Override
    protected void convert(@NonNull BaseViewHolder helper, QuickBean item) {
          helper.setImageResource(R.id.ivItemOne,item.getIntOne());
          helper.setText(R.id.tvItemOne,getContext().getString(item.getIntTwo()));
          helper.setText(R.id.tvItemTwo,getContext().getString(item.getIntThree()));
    }
}