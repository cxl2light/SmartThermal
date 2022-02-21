package com.hq.monitor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.hq.base.ui.BaseActivity;
import com.hq.base.util.ToastUtil;
import com.hq.monitor.device.ControlDeviceActivity;
import com.hq.monitor.play.GSYManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RtspUrlInputActivity extends BaseActivity implements View.OnClickListener {
    private static final int MIN_RTSP_URL_LENGTH = 12;
    private static final String SP_FILE_NAME = "rtsp_file";
    private static final String SP_KEY_RTSP_URL = "rtsp_url";

    private AppCompatEditText editText;
    private RecyclerView rtspRecycler;

    private final List<String> mRtspDataList = new ArrayList<>(20);
    private BaseQuickAdapter<String, BaseViewHolder> mRtspAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtsp_url_input);
        editText = findViewById(R.id.edit_tv);
        rtspRecycler = findViewById(R.id.rtsp_recycler);
        initRecycler();
        findViewById(R.id.confirm_btn).setOnClickListener(this);

        getStringSet();
        GSYManager.init();
    }

    @Override
    protected boolean safeArea() {
        return true;
    }

    private void initRecycler() {
        rtspRecycler.setLayoutManager(new LinearLayoutManager(mActivity));
        rtspRecycler.setNestedScrollingEnabled(false);
        rtspRecycler.setHasFixedSize(true);
        mRtspAdapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_rtsp_url) {

            @Override
            protected void convert(@NonNull BaseViewHolder holder, String s) {
                holder.setText(R.id.item_text, s);
            }
        };
        mRtspAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                if (position >= 0 && position < mRtspDataList.size()) {
                    toPlay(mRtspDataList.get(position));
                }
            }
        });
        rtspRecycler.setAdapter(mRtspAdapter);
        mRtspAdapter.setNewInstance(mRtspDataList);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.confirm_btn:
                final Editable editable = editText.getText();
                final String rtspUrl = editable == null ? "" : editable.toString();
                if (TextUtils.isEmpty(rtspUrl) || rtspUrl.length() <= MIN_RTSP_URL_LENGTH) {
                    ToastUtil.toast("请输入完整的Rtsp地址");
                    return;
                }
                mRtspDataList.add(0, editable.toString());
                mRtspAdapter.notifyItemInserted(0);
                toPlay(editable.toString());
                break;
        }
    }

    private void toPlay(String rtspUrl) {
//        PlayActivity.startActivity(mActivity, null, rtspUrl);
        final Uri uri = Uri.parse(rtspUrl);
        ControlDeviceActivity.startActivity(mActivity, rtspUrl, uri.getHost());
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveStringSet();
    }

    private void getStringSet() {
        final SharedPreferences sharedPreferences = mActivity.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        mRtspDataList.addAll(sharedPreferences.getStringSet(SP_KEY_RTSP_URL, new HashSet<String>()));
    }

    private void saveStringSet() {
        final SharedPreferences sharedPreferences = mActivity.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putStringSet(SP_KEY_RTSP_URL, new HashSet<>(mRtspDataList)).apply();
    }

    public static void startActivity(Context context) {
        final Intent intent = new Intent(context, RtspUrlInputActivity.class);
        context.startActivity(intent);
    }
}
