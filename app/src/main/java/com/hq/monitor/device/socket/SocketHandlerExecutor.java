package com.hq.monitor.device.socket;

import com.hq.base.app.CommonExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2020/6/8
 * author :
 * desc :
 */
public class SocketHandlerExecutor {

    private volatile static ExecutorService mExecutorService;
    private final static int MAXIMUM_POOL_SIZE = Integer.MAX_VALUE;

    public static ExecutorService getExecutorService() {
        if (mExecutorService == null) {
            synchronized (CommonExecutor.class) {
                if (mExecutorService == null) {
                    mExecutorService = new ThreadPoolExecutor(0,
                            MAXIMUM_POOL_SIZE, 30, TimeUnit.SECONDS, new SynchronousQueue<>(),
                            r -> {
                                final Thread result = new Thread(r, "SocketHandler Executor");
                                result.setDaemon(false);
                                return result;
                            });
                }
            }
        }
        return mExecutorService;
    }

}
