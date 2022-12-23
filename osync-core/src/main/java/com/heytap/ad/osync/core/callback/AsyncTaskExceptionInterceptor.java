package com.heytap.ad.osync.core.callback;



public interface AsyncTaskExceptionInterceptor<Param> {
    /**
     * error CallBack
     *
     * @param errorCallback the error com.heytap.ad.osync.callback
     */
    void exception(ErrorCallback<Param> errorCallback);
}
