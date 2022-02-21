package com.hq.monitor.adapter;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hq.monitor.R;


public class MenuAdapter extends BaseQuickAdapter<QuickBean, BaseViewHolder> {

    public MenuAdapter() {
        super(R.layout.item_menu);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, QuickBean item) {
          helper.setImageResource(R.id.ivItemOne,item.getIntOne());
          helper.setText(R.id.tvItemOne, getContext().getString(item.getIntTwo()));
          if (item.isCheckOne()){
              helper.setBackgroundResource(R.id.llOne,R.drawable.bg_menu_select);
          } else {
              helper.setBackgroundResource(R.id.llOne,R.drawable.bg_menu_unselect);
          }

        final Drawable drawable;
        if (item.isCheckTwo()){
            drawable = new ColorDrawable(0xFFFFFFF);
        } else {
            drawable = new ColorDrawable(0x88222222);
        }
        ((LinearLayoutCompat)helper.getView(R.id.llOne)).setForeground(drawable);
    }
}