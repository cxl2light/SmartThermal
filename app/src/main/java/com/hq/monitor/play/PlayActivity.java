package com.hq.monitor.play;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hq.base.ui.BaseActivity;
import com.hq.base.widget.CommonTitleBar;
import com.hq.monitor.R;

/**
 * Created on 2020/4/5.
 * author :
 * desc :
 */
public class PlayActivity extends BaseActivity {
    private static final String EXTRA_TITLE = "extra_title";


    private CommonTitleBar titleBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle(getIntent().getStringExtra(EXTRA_TITLE));
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                PlayFragment.getInstance(getIntent().getExtras())).commit();
    }

    public static void startActivity(@NonNull Context context, @Nullable String title,
                                     @NonNull String rtspUrl) {
        final Intent intent = new Intent(context, PlayActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(PlayFragment.EXTRA_RTSP_URL, rtspUrl);
        context.startActivity(intent);
    }
}
