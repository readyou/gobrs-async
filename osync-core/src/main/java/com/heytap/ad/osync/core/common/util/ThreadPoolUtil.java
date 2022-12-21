package com.heytap.ad.osync.core.common.util;

import java.math.BigDecimal;

public class ThreadPoolUtil {

    public static Integer calculateCoreNum() {
        int cpuCoreNum = Runtime.getRuntime().availableProcessors();
        return new BigDecimal(cpuCoreNum).divide(new BigDecimal("0.2")).intValue();
    }

}
