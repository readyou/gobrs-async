package com.heytap.ad.osync.test.task.retry;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * The type Case retry task b.
 *
 * @program: gobrs -async
 * @ClassName GobrsTaskA
 * @description:
 */
@Slf4j
@Task(retryCount = 2, timeoutInMilliseconds = 3000)
public class CaseRetryTaskB extends AsyncTask {

    @Override
    public void prepare(Object o) {
        log.info(this.getName() + " 使用线程---" + Thread.currentThread().getName());
    }

    @SneakyThrows
    @Override
    public Object execute(Object o, TaskContext support) {
        System.out.println("CaseRetryTaskB Begin");
        Thread.sleep(800);
        System.out.println(1 / 0);
        System.out.println("CaseRetryTaskB End");
        return "AResult";
    }
}
