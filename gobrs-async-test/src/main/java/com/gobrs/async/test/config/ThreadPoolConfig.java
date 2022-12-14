package com.gobrs.async.test.config;

import com.gobrs.async.core.threadpool.GobrsAsyncThreadPoolFactory;
import com.gobrs.async.core.threadpool.GobrsThreadPoolConfiguration;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * The type Thread pool com.gobrs.async.config.
 *
 * @program: gobrs -async
 * @ClassName ThreadPoolConfig
 * @description: Custom thread pool configuration
 * @author: sizegang
 * @create: 2022 -02-20 00:34
 * @Version 1.0
 */
@Configuration
public class ThreadPoolConfig extends GobrsThreadPoolConfiguration {

    @Override
    protected void doInitialize(GobrsAsyncThreadPoolFactory factory) {
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
