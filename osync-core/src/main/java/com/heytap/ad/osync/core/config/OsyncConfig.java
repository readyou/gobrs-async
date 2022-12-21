package com.heytap.ad.osync.core.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.heytap.ad.osync.core.common.def.DefaultConfig.*;


@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class OsyncConfig {

    public static final String PREFIX = "osync.config";

    private boolean enable;

    private List<OsyncRule> rules;

    /**
     * Task separator
     */
    private String split = ";";

    /**
     * Next task
     */
    private String point = "->";


    private ThreadPoolProperties threadPoolProperties;

    /**
     * 超时时间监听时间
     */
    private Integer timeoutCoreSize;


    /**
     * Whether global parameter dataContext mode  Parameter context
     */
    private boolean paramContext = true;


    /**
     * Default timeout
     *
     * @return
     */
    private long timeout = 10000;


    private boolean relyDepend = false;

    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ThreadPoolProperties {
        private Integer corePoolSize = calculateCoreNum();

        private Integer maxPoolSize = corePoolSize + (corePoolSize >> 1);

        private Long keepAliveTime = KEEP_ALIVE_TIME_MS;

        private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

        private Long executeTimeOut = EXECUTE_TIMEOUT_MS;

        private Integer capacity = THREAD_POOL_QUEUE_SIZE;

        private BlockingQueue workQueue = new LinkedBlockingQueue(capacity);

        private String rejectedExecutionHandler = "AbortPolicy";

        private String threadNamePrefix = "osync";

        private Boolean allowCoreThreadTimeOut = false;

        private Integer calculateCoreNum() {
            int cpuCoreNum = Runtime.getRuntime().availableProcessors();
            return new BigDecimal(cpuCoreNum).divide(new BigDecimal("0.2")).intValue();
        }

    }
}
