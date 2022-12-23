package com.heytap.ad.osync.test.task.retry;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: gobrs-async
 * @ClassName GobrsTaskA
 * @description:
 **/
@Slf4j
@Task
public class CaseRetryTaskC extends AsyncTask {

    @Override
    public void prepare(Object o) {
        log.info(this.getName() + " 使用线程---" + Thread.currentThread().getName());
    }
    @Override
    public Object execute(Object o, TaskContext support) {
        System.out.println("CaseRetryTaskC Begin");
        System.out.println("CaseRetryTaskC Finish");
        return "AResult";
    }
}
