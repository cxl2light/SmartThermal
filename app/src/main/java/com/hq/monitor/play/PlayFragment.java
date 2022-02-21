package com.hq.monitor.play;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.hq.base.ui.BaseFragment;

/**
 * Created on 2020/4/5.
 * author :
 * desc :
 */
public class PlayFragment extends BaseFragment {
    public static final String EXTRA_RTSP_URL = "extra_rtsp_url";
    public static final String EXTRA_DEVICE_IP = "extra_device_ip";
    public static final String EXTRA_DEVICE_NAME = "extra_device_name";

    static PlayFragment getInstance(@Nullable Bundle bundle) {
        final PlayFragment fragment = new PlayFragment();
        fragment.setArguments(bundle);
        return fragment;
    }
}
