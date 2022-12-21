package com.heytap.ad.osync.core;

import com.heytap.ad.osync.core.log.LogWrapper;
import com.heytap.ad.osync.core.common.domain.TaskResult;
import com.heytap.ad.osync.core.common.domain.TaskStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

@Data
@Accessors(chain = true)
public class TaskContext {

    /**
     * 任务加载器
     */
    public TaskLoader taskLoader;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 执行线程池
     */
    public ExecutorService executorService;

    /**
     * 日志封装
     */
    private volatile LogWrapper logWrapper;


    /**
     * 任务参数封装
     */
    private Object param;

    private Map<Class, TaskResult> resultMap = new ConcurrentHashMap<>();

    private Map<Class, TaskStatus> taskStatus = new ConcurrentHashMap<>();

    public TaskStatus getStatus(Class clazz) {
        return taskStatus.computeIfAbsent(clazz, TaskStatus::new);
    }

}
