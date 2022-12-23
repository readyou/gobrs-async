package com.heytap.ad.osync.core.common.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.atomic.AtomicInteger;

import static com.heytap.ad.osync.core.common.def.TaskStatus.TASK_INITIALIZED;

/**
 * task运行时状态
 */
@Data
@Accessors(chain = true)
public class TaskStatus {
    private final AtomicInteger status = new AtomicInteger(TASK_INITIALIZED);

    /**
     * 已重试次数
     */
    private final AtomicInteger retriedCounts = new AtomicInteger(0);

    /**
     * the class type of currentTask
     */
    private final Class<?> taskClass;


    public TaskStatus(Class<?> taskCls) {
        this.taskClass = taskCls;
    }

    /**
     * change task status
     */
    public boolean compareAndSet(int expect, int update) {
        return getStatus().compareAndSet(expect, update);
    }

}
