package com.hq.monitor.ui.searchhelp.devicestart;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hq.monitor.R;
import com.hq.monitor.adapter.QuickBean;


public class DeviceStartOneAdapter extends BaseQuickAdapter<QuickBean, BaseViewHolder> {
    public DeviceStartOneAdapter() {
        super(R.layout.item_device_search_start_one);
    }
    @Override
    protected void convert(@NonNull BaseViewHolder helper, QuickBean item) {
          helper.setImageResource(R.id.ivItemOne,item.getIntOne());
          TextView tvItemOne = helper.getView(R.id.tvItemOne);
          helper.setText(R.id.tvItemOne, tvItemOne.getContext().getString(item.getIntTwo()));
    }
}