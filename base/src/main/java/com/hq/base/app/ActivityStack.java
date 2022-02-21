package com.hq.base.app;

import android.app.Activity;

import androidx.annotation.Nullable;

import java.util.Iterator;
import java.util.Stack;

/**
 * Created on 2020/4/4.
 * author :
 * desc :
 */
public class ActivityStack {
    private final Stack<Activity> activityStack;

    private static volatile ActivityStack instance;

    private ActivityStack() {
        activityStack = new Stack<>();
    }

    public static ActivityStack getInstance() {
        if (instance == null) {
            synchronized (ActivityStack.class) {
                if (instance == null) {
                    instance = new ActivityStack();
                }
            }
        }
        return instance;
    }

    public synchronized void pushActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        activityStack.push(activity);
    }

    public synchronized void removeActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        activityStack.remove(activity);
    }

    /**
     * 弹出所有的activity，除了指定的activity
     */
    public synchronized void popAllActivityExcept(@Nullable Class<? extends Activity> activityCls) {
        final Iterator<Activity> iterator = activityStack.iterator();
        Activity activity;
        while (iterator.hasNext()) {
            activity = iterator.next();
            if (activity == null || activity.getClass() != activityCls) {
                iterator.remove();
            }
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }

}
