package com.hq.monitor.adapter;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hq.monitor.R;
import com.hq.monitor.app.MyApplication;

public class DeviceStartAdapter extends BaseQuickAdapter<QuickBean, BaseViewHolder> {
    public DeviceStartAdapter() {
        super(R.layout.item_device_start);
    }
    @Override
    protected void convert(@NonNull BaseViewHolder helper, QuickBean item) {
          helper.setImageResource(R.id.ivItemOne,item.getIntOne());
          helper.setText(R.id.tvItemOne,getContext().getString(item.getIntTwo()));

        final Drawable drawable;
        if (item.isCheckOne()){
            drawable = new ColorDrawable(0x000000);
            helper.setTextColor(R.id.tvItemOne, MyApplication.getAppContext().getColor(R.color.white));
        } else {
            drawable = new ColorDrawable(0x88222222);
            helper.setTextColor(R.id.tvItemOne, MyApplication.getAppContext().getColor(R.color.text_white_trans));
        }
        helper.getView(R.id.ivItemOne).setForeground(drawable);
    }
}