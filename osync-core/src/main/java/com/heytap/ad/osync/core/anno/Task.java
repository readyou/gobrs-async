package com.heytap.ad.osync.core.anno;

import com.heytap.ad.osync.core.common.def.DefaultConfig;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Task {
    /**
     * 也用做task的名称
     *
     * @return
     */
    String value() default "";

    String desc() default "";

    /**
     * Transaction com.heytap.ad.osync.com.heytap.ad.osync.test.task
     *
     * @return boolean boolean
     */
    boolean rollback() default false;

    boolean continueOnError() default false;

    /**
     * Retry times
     *
     * @return int int
     */
    int retryCount() default DefaultConfig.RETRY_COUNT;

    int timeoutInMilliseconds() default DefaultConfig.TASK_TIME_OUT;

}
