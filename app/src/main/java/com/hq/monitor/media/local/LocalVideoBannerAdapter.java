package com.hq.monitor.media.local;

import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hq.monitor.R;
import com.hq.monitor.app.MyApplication;

import java.io.File;

public class LocalVideoBannerAdapter extends BaseQuickAdapter<File, BaseViewHolder> {
    public LocalVideoBannerAdapter() {
        super(R.layout.item_video_banner);
    }
    @Override
    protected void convert(@NonNull BaseViewHolder helper, File item) {
        helper.setImageResource(R.id.video_thumbnail,R.drawable.common_default_image);
        if (item != null && item.exists()) {
            Glide.with(MyApplication.getAppContext()).load(item).placeholder(R.drawable.common_default_image).into((ImageView) helper.getView(R.id.video_thumbnail));
        }
    }
}