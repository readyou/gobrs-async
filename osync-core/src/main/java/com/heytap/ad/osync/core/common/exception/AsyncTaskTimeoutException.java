package com.heytap.ad.osync.core.common.exception;


public class AsyncTaskTimeoutException extends OsyncException {

    public AsyncTaskTimeoutException() {
        super();
    }

    public AsyncTaskTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public AsyncTaskTimeoutException(String message) {
        super(message);
    }

    public AsyncTaskTimeoutException(Throwable cause) {
        super(cause);
    }

}
