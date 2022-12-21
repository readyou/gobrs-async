package com.heytap.ad.osync.core.callback;


public interface AsyncTaskPostInterceptor<P> {

   default void postProcess(P result,  String taskName){};

}
