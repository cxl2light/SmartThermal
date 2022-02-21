package com.hq.base.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created on 2020/4/4.
 * author :
 * desc :
 */
public class CustomerActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private static volatile CustomerActivityLifecycleCallbacks instance;

    private CustomerActivityLifecycleCallbacks() {

    }

    public static CustomerActivityLifecycleCallbacks getInstance() {
        if (instance == null) {
            synchronized (CustomerActivityLifecycleCallbacks.class) {
                if (instance == null) {
                    instance = new CustomerActivityLifecycleCallbacks();
                }
            }
        }
        return instance;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        ActivityStack.getInstance().pushActivity(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        ActivityStack.getInstance().removeActivity(activity);
    }
}
