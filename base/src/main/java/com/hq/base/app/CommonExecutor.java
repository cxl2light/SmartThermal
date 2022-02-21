package com.hq.base.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2020/5/10
 * author :
 * desc :
 */
public class CommonExecutor {

    private volatile static ExecutorService mExecutorService;
    private final static int MAXIMUM_POOL_SIZE = Integer.MAX_VALUE;

    public static ExecutorService getExecutorService() {
        if (mExecutorService == null) {
            synchronized (CommonExecutor.class) {
                if (mExecutorService == null) {
                    mExecutorService = new ThreadPoolExecutor(1,
                            MAXIMUM_POOL_SIZE, 30, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
                            new ThreadFactory() {
                                @Override
                                public Thread newThread(Runnable r) {
                                    final Thread result = new Thread(r, "Common Executor");
                                    result.setDaemon(false);
                                    return result;
                                }
                            });
                }
            }
        }
        return mExecutorService;
    }


}
