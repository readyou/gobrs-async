package com.heytap.ad.osync.test.task.timeout;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * The type A service.
 *
 * @program: gobrs -async-starter
 * @description:
 */
@Slf4j
@Task(continueOnError = true, timeoutInMilliseconds = 300)
public class CaseTimeoutTaskA extends AsyncTask {

    /**
     * The .
     */
    int i = 10000;

    @Override
    public void prepare(Object o) {
        log.info(this.getName() + " 使用线程---" + Thread.currentThread().getName());
    }

    @SneakyThrows
    @Override
    public String execute(Object o, TaskContext support) {

        System.out.println("CaseTimeoutTaskA Begin");
        Thread.sleep(400);
        for (int i1 = 0; i1 < i; i1++) {
            i1 += i1;
        }
        System.out.println("CaseTimeoutTaskA Finish");
        return "result";
    }

    @Override
    public boolean necessary(Object o, TaskContext support) {
        return true;
    }


    @Override
    public void onSuccess(TaskContext support) {

    }

}