package com.hq.monitor.util;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.TypedValue;

import androidx.fragment.app.FragmentManager;

import com.hq.base.util.ToastUtil;
import com.hq.basebean.device.DeviceBaseInfo;
import com.hq.monitor.R;
import com.hq.monitor.device.ControlDeviceIJKActivity;
import com.hq.monitor.device.menudialog.MenuDialog;

import java.lang.reflect.Method;

import static com.arthenica.mobileffmpeg.Config.getPackageName;

public class Utils {

    public static final String DEVICE_INFO = "device_info";
    public static final String DEVICE_KEY = "device_key";
    public static final String DEVICE_CONNECT = "device_connect";

    public static void GoStore(Context context){
        //存在手机里没安装应用市场的情况，跳转会包异常，做一个接收判断

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + getPackageName()));

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            //可以接收
            context.startActivity(intent);
        } else {
            //没有应用市场，我们通过浏览器跳转到Google Play
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));

            //这里存在一个极端情况就是有些用户浏览器也没有，再判断一次
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                //有浏览器
                context.startActivity(intent);
            }
        }
    }

    public static void connectDevice(Context context){
        ControlDeviceIJKActivity.startActivity(context);
    }

    public static void initShowMenuDialog(FragmentManager fragmentManager,boolean isClickable) {
        MenuDialog mDialog = new MenuDialog();
        mDialog.show(fragmentManager, true,isClickable, "title", "content");
    }

    public static int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    public static String packageName(Context context) {
        PackageManager manager = context.getPackageManager();
        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return name;
    }

    public static void translucentActivity(Activity activity) {

        try {
            activity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            activity.getWindow().getDecorView().setBackground(null);
            Method activityOptions = Activity.class.getDeclaredMethod("getActivityOptions");
            activityOptions.setAccessible(true);
            Object options = activityOptions.invoke(activity);

            Class<?>[] classes = Activity.class.getDeclaredClasses();
            Class<?> aClass = null;
            for (Class clazz : classes) {
                if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                    aClass = clazz;
                }
            }
            Method method = Activity.class.getDeclaredMethod("convertToTranslucent",
                    aClass, ActivityOptions.class);
            method.setAccessible(true);
            method.invoke(activity, null, options);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}
