package com.hq.monitor.app;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.PowerManager;
import android.util.DisplayMetrics;

import androidx.annotation.RequiresApi;

import com.hq.base.BuildConfig;
import com.hq.base.app.CrashHandler;
import com.hq.base.app.CustomerActivityLifecycleCallbacks;
import com.hq.base.consts.GlobalConst;
import com.hq.base.util.Logger;
import com.hq.base.util.ToastUtil;
import com.hq.monitor.db.NotificationsDB;

import java.util.Locale;

/**
 * Created on 2020/4/4.
 * author :
 * desc :
 */
public class MyApplication extends Application {
    private static Context appContext;
    private static NotificationsDB notificationsDB;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler.getInstance().init(getApplicationContext());

        registerActivityLifecycleCallbacks(CustomerActivityLifecycleCallbacks.getInstance());
        ToastUtil.init(this);
//        BuglyUtil.initBugly(this);
        GlobalConst.setDebug(BuildConfig.DEBUG);
        Logger.init(BuildConfig.DEBUG);
        appContext = getApplicationContext();
        notificationsDB = new NotificationsDB(appContext);

//        DeviceOSUtil.setLocalEnglish(this);
//        setLocal();
    }

    private void setLocal() {
        Resources resources = appContext.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        // 应用用户选择语言
        config.locale = Locale.ENGLISH;
        resources.updateConfiguration(config, dm);
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static NotificationsDB getNotificationsDB(){return notificationsDB;}
}
