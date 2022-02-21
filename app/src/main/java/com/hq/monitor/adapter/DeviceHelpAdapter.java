package com.hq.monitor.adapter;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hq.monitor.R;


public class DeviceHelpAdapter extends BaseQuickAdapter<QuickBean, BaseViewHolder> {
    public DeviceHelpAdapter() {
        super(R.layout.item_device_help);
    }
    @Override
    protected void convert(@NonNull BaseViewHolder helper, QuickBean item) {
          if (item.getStrOne().equals("2")){
              helper.setGone(R.id.ivItemOne,true);
              helper.setVisible(R.id.llItemOne,true);
//              int sWidth = ScreenUtils.getScreenHeight() * 4 / 5;
//              ImageView ivQrOne = helper.getView(R.id.ivItemQrOne);
//              ImageView ivQrTwo = helper.getView(R.id.ivItemQrTwo);
//              ivQrOne.setLayoutParams(new LinearLayout.LayoutParams(sWidth,sWidth));
//              ivQrTwo.setLayoutParams(new LinearLayout.LayoutParams(sWidth,sWidth));
          } else {
              helper.setVisible(R.id.ivItemOne,true);
              helper.setGone(R.id.llItemOne,true);
          }
          helper.setImageResource(R.id.ivItemOne,item.getIntOne());
    }
}