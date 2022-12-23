package com.heytap.ad.osync.test.task.performance;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;
import lombok.SneakyThrows;

/**
 * @program: performance-compare
 * @ClassName TaskA
 * @description:
 **/
@Task
public class TaskF extends AsyncTask {

    @SneakyThrows
    @Override
    public Object execute(Object o, TaskContext support) {
//        System.out.println("使用" + Thread.currentThread().getName());
        PUtil.sleep();
//        System.out.println("TaskF");
        return null;
    }
}
