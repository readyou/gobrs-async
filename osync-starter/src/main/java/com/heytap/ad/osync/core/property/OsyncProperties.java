package com.heytap.ad.osync.core.property;


import com.heytap.ad.osync.core.config.OsyncConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.heytap.ad.osync.core.common.def.DefaultConfig.*;

@ConfigurationProperties(prefix = OsyncProperties.PREFIX, ignoreInvalidFields = false)
@PropertySource(value = {"classpath:config/osync.yaml", "classpath:config/osync.yml", "classpath:config/osync.properties"}, ignoreResourceNotFound = false, factory = OsyncPropertySourceFactory.class)
@Component
@Data
public class OsyncProperties {

    /**
     * The constant PREFIX.
     */
    public static final String PREFIX = OsyncConfig.PREFIX;


    private boolean enable;
    /**
     * Task rules
     */
    private List<RuleConfig> rules = new ArrayList<>();

    /**
     * Task separator
     */
    private String split = ";";

    /**
     * Next com.heytap.ad.osync.com.heytap.ad.osync.test.task
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
    private long timeout = 3000;

    private boolean relyDepend = false;


    @Data
    public static class ThreadPoolProperties {
        /**
         * number of core threads
         */
        private Integer corePoolSize = calculateCoreNum();

        /**
         * maximum number of threads
         */
        private Integer maxPoolSize = corePoolSize + (corePoolSize >> 1);

        /**
         * thread survival time
         */
        private Long keepAliveTime = KEEP_ALIVE_TIME_MS;

        /**
         * thread survival time unit
         */
        private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

        /**
         * Thread execution timeout
         */
        private Long executeTimeOut = EXECUTE_TIMEOUT_MS;

        /**
         * queue maximum capacity
         */
        private Integer capacity = THREAD_POOL_QUEUE_SIZE;


        /**
         * blocking queue
         */
        private BlockingQueue workQueue = new LinkedBlockingQueue(capacity);

        /**
         * Reject com.heytap.ad.osync.com.heytap.ad.osync.test.task policy when thread pool com.heytap.ad.osync.com.heytap.ad.osync.test.task is full
         */
        private String rejectedExecutionHandler = "AbortPolicy";

        /**
         * thread name prefix
         */
        private String threadNamePrefix;


        /**
         * Allow core threads to time out
         */
        private Boolean allowCoreThreadTimeOut = false;

        private Integer calculateCoreNum() {
            int cpuCoreNum = Runtime.getRuntime().availableProcessors();
            return new BigDecimal(cpuCoreNum).divide(new BigDecimal("0.2")).intValue();
        }

    }
}