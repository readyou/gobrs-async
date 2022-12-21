package com.heytap.ad.osync.core.threadpool;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.heytap.ad.osync.core.config.OsyncConfig;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class OsyncThreadPoolFactory {

    private OsyncConfig osyncConfig;

    public OsyncThreadPoolFactory(OsyncConfig osyncConfig) {
        this.osyncConfig = osyncConfig;
        this.defaultPool = TtlExecutors.getTtlExecutorService(createDefaultThreadPool());
        this.threadPoolExecutor = defaultPool;
    }

    private final ExecutorService defaultPool;

    private ExecutorService threadPoolExecutor;

    public ExecutorService getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public void setThreadPoolExecutor(ExecutorService threadPoolExecutor) {
        this.threadPoolExecutor = TtlExecutors.getTtlExecutorService(threadPoolExecutor);
    }

    ExecutorService createDefaultThreadPool() {
        OsyncConfig.ThreadPoolProperties threadPoolProperties = osyncConfig.getThreadPoolProperties();
        if (Objects.isNull(threadPoolProperties)) {
            return Executors.newCachedThreadPool();
        }
        return new ThreadPoolExecutor(threadPoolProperties.getCorePoolSize(),
                threadPoolProperties.getMaxPoolSize(), threadPoolProperties.getKeepAliveTime(), threadPoolProperties.getTimeUnit(),
                threadPoolProperties.getWorkQueue(), caseReject(threadPoolProperties.getRejectedExecutionHandler()));
    }

    public static RejectedExecutionHandler caseReject(String rejected) {
        if (rejected == null) {
            return new ThreadPoolExecutor.AbortPolicy();
        }

        RejectedExecutionHandler rejectedExecutionHandler;
        switch (rejected) {
            case "CallerRunsPolicy":
                rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
                break;
            case "AbortPolicy":
                rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
                break;
            case "DiscardPolicy":
                rejectedExecutionHandler = new ThreadPoolExecutor.DiscardPolicy();
                break;
            case "DiscardOldestPolicy":
                rejectedExecutionHandler = new ThreadPoolExecutor.DiscardOldestPolicy();
                break;
            default:
                rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
        }
        return rejectedExecutionHandler;
    }


}
