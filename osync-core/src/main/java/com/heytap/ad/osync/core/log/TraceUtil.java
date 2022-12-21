package com.heytap.ad.osync.core.log;

import com.heytap.ad.osync.core.threadpool.OsyncThreadLocal;

/**
 * The type Trace com.heytap.ad.osync.util.
 *
 * @program: gobrs -async
 * @ClassName TraceUtil
 * @description:
 */
public class TraceUtil {

    /**
     * The constant gobrsThreadLocal.
     */
    public static OsyncThreadLocal osyncThreadLocal = new OsyncThreadLocal();

    /**
     * Get object.
     *
     * @return the object
     */
    public static Object get() {
        return osyncThreadLocal.get();
    }

    /**
     * Set.
     *
     * @param traceId the trace id
     */
    public static void set(String traceId) {
        osyncThreadLocal.set(traceId);
    }

}
