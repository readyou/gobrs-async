package com.heytap.ad.osync.test.inteceptor;

import com.heytap.ad.osync.core.callback.AsyncTaskExceptionInterceptor;
import com.heytap.ad.osync.core.callback.ErrorCallback;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class OsyncExceptionInterceptor implements AsyncTaskExceptionInterceptor {


    @SneakyThrows
    @Override
    public void exception(ErrorCallback errorCallback) {
//        log.error("Execute global interceptor  error", errorCallback.getThrowable());
    }
}
