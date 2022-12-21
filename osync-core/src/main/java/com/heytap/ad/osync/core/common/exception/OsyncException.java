package com.heytap.ad.osync.core.common.exception;

public class OsyncException extends RuntimeException {

    public OsyncException() {
        super();
    }

    public OsyncException(String message, Throwable cause) {
        super(message, cause);
    }

    public OsyncException(String message) {
        super(message);
    }

    public OsyncException(Throwable cause) {
        super(cause);
    }

}
