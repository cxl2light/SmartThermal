package com.hq.monitor.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hq.monitor.R;


public class ConnectHelpAdapter extends BaseQuickAdapter<QuickBean, BaseViewHolder> {
    public ConnectHelpAdapter() {
        super(R.layout.item_connect_help);
    }
    @Override
    protected void convert(@NonNull BaseViewHolder helper, QuickBean item) {
          helper.setText(R.id.tvItemOne,item.getStrOne());
          helper.setText(R.id.tvItemTwo,item.getStrTwo());
    }
}