package com.slwb.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 王鑫
 */
public class NetworkThreadPoolExecutor {
    /**
     * 线程池的最大并发量
     */
    public static final int CORE_POOL_SIZE = 10;
    /**
     * 线程池的最大容量，超出该容量将抛出异常
     */
    public static final int MAX_POOL_SIZE = 100;
    /**
     * 线程等待时间，以秒为单位
     */
    public static final int KEEP_ALIVE_TIME = 30;

    private NetworkThreadPoolExecutor() {
    }

    private static class ExecutorHolder {
        private static final ThreadPoolExecutor INSTANCE = new ThreadPoolExecutor(
                CORE_POOL_SIZE,    /* 线程池的最大并发量 */
                MAX_POOL_SIZE,     /* 线程池的最大容量，超出该容量将抛出异常 */
                KEEP_ALIVE_TIME,   /* 线程等待时间，以秒为单位 */
                TimeUnit.SECONDS,  /* 计时单位 */
                new LinkedBlockingQueue<Runnable>());
    }

    public static ThreadPoolExecutor getInstance() {
        return ExecutorHolder.INSTANCE;
    }
}
