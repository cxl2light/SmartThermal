package com.hq.monitor.about;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.hq.base.ui.BaseActivity;
import com.hq.base.util.StatusBarUtil;
import com.hq.base.widget.CommonTitleBar;
import com.hq.monitor.R;
import com.hq.monitor.device.menudialog.MenuDialog;
import com.hq.monitor.util.SpUtils;
import com.hq.monitor.util.Utils;

public class AboutAppActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        StatusBarUtil.setStatusBarWhiteMode(this);

        final AppCompatTextView tvOne = findViewById(R.id.tvOne);
        tvOne.setText(getString(R.string.app_name) + "    V" + Utils.packageName(this)
              + "\n\n"  + getString(R.string.app_about));
        CommonTitleBar mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setImageShareAndClick(R.mipmap.ic_menu_white, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.initShowMenuDialog(getSupportFragmentManager(),SpUtils.getBoolean(mActivity,Utils.DEVICE_CONNECT,false));
            }
        });

        final AppCompatTextView tvTwo = findViewById(R.id.tvTwo);
        tvTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://qinkung.com/index/index/protocol");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void initShowMenuDialog() {
        MenuDialog mDialog = new MenuDialog();
        mDialog.show(getSupportFragmentManager(), true,true, "title", "content");
    }

    public static void startActivity(Context context) {
        if (context == null) {
            return;
        }
        final Intent intent = new Intent(context, AboutAppActivity.class);
        context.startActivity(intent);
    }

}
