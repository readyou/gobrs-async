package com.heytap.ad.osync.core;

import com.heytap.ad.osync.core.common.domain.AsyncParamSupply;
import com.heytap.ad.osync.core.common.domain.AsyncResult;
import com.heytap.ad.osync.core.common.exception.NotFoundRuleException;
import com.heytap.ad.osync.core.task.AsyncTask;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Osync作为一个统一的入口，使用TaskAdj和TaskTrigger进行相关操作，对外暴露简单易用的接口——go.
 */
public class Osync {

    private Map<String, TaskAdj> taskAdjMap = new ConcurrentHashMap<>();

    private Map<String, TaskTrigger> triggerMap = new ConcurrentHashMap<>();


    public TaskGroup begin(String ruleName, List<AsyncTask> asyncTasks, boolean reload) {
        TaskAdj taskAdj = taskAdjMap.computeIfAbsent(ruleName, v -> new TaskAdj());
        if (reload) {
            taskAdj = new TaskAdj();
            taskAdjMap.put(ruleName, taskAdj);
        }
        return taskAdj.begin(asyncTasks);
    }


    public TaskGroup after(String ruleName, AsyncTask... tasks) {
        TaskAdj taskAdj = taskAdjMap.get(ruleName);
        if (taskAdj == null) {
            throw new RuntimeException("taskAdj not exist: " + ruleName);
        }
        return taskAdj.after(tasks);
    }


    public AsyncResult go(String ruleName, AsyncParamSupply param, long timeout) {
        TaskTrigger taskTrigger = triggerMap.get(ruleName);
        if (taskTrigger == null) {
            throw new NotFoundRuleException("Gobrs Rule Name Is Error!!!");
        }
        return taskTrigger.trigger(param, timeout).load();
    }

    public AsyncResult go(String taskName, AsyncParamSupply param) {
        return go(taskName, param, 0L);
    }


    public synchronized void initTrigger(String ruleName) {
        initTrigger(ruleName, false);
    }

    public synchronized void initTrigger(String ruleName, boolean reload) {
        if (!triggerMap.containsKey(ruleName) || reload) {
            TaskTrigger tr = new TaskTrigger(ruleName, taskAdjMap.get(ruleName));
            triggerMap.put(ruleName, tr);
        }
    }

}

