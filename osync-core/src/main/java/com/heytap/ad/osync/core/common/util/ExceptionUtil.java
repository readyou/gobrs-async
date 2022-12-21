package com.heytap.ad.osync.core.common.util;

public class ExceptionUtil {

    public static boolean isInterruptedException(Exception exception) {
        return exception instanceof InterruptedException;
    }

}
