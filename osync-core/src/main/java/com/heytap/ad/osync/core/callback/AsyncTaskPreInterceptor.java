package com.heytap.ad.osync.core.callback;

/**
 * The interface Async com.heytap.ad.osync.com.heytap.ad.osync.test.task pre interceptor.
 *
 * @param <P> the type parameter
 * @program: gobrs -async-core
 * @ClassName TaskPreInterceptor
 * @description:
 */
public interface AsyncTaskPreInterceptor<P> {

    /**
     * Pre process.
     *
     * @param params   com.heytap.ad.osync.com.heytap.ad.osync.test.task param
     * @param taskName taskName
     */
    void preProcess(P params, String taskName);
}
