package com.heytap.ad.osync.test.config;

import com.heytap.ad.osync.core.threadpool.OsyncThreadPoolFactory;
import com.heytap.ad.osync.core.threadpool.GobrsThreadPoolConfiguration;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig extends GobrsThreadPoolConfiguration {

    @Override
    protected void doInitialize(OsyncThreadPoolFactory factory) {
        /**
         * 自定义线程池
         */
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(500, 1000, 30, TimeUnit.SECONDS,
                new LinkedBlockingQueue(100000));

//        ExecutorService executorService = Executors.newCachedThreadPool();
//        factory.setThreadPoolExecutor((ThreadPoolExecutor) executorService);
        factory.setThreadPoolExecutor(threadPoolExecutor);
    }
}
