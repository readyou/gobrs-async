package com.heytap.ad.osync.test.task.performance;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;
import lombok.SneakyThrows;

/**
 * @program: performance-compare
 * @ClassName TaskB
 * @description:
 **/
@Task
public class TaskB extends AsyncTask {

    @SneakyThrows
    @Override
    public Object execute(Object o, TaskContext support) {
//        System.out.println("使用" + Thread.currentThread().getName());
//        System.out.println("TaskB");
        PUtil.sleep();
        return null;
    }
}
