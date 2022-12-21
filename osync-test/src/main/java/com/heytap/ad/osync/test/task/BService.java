package com.heytap.ad.osync.test.task;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;
import lombok.extern.slf4j.Slf4j;

/**
 * The type B service.
 *
 * @program: gobrs -async-starter
 * @ClassName BService
 * @description:
 */
@Slf4j
@Task(continueOnError = true)
public class BService extends AsyncTask {


    /**
     * The .
     */
    int i = 10000;

    @Override
    public void prepare(Object o) {
        log.info(this.getName() + " 使用线程---" + Thread.currentThread().getName());
    }

    @Override
    public Object execute(Object o, TaskContext support) {
        System.out.println("BService Begin");
        for (int i1 = 0; i1 < i; i1++) {
            i1 += i1;
        }
        try {
            System.out.println(1 / 0);
        } catch (Exception exception) {
            log.error(getFormattedTraceId(), exception);
        }

        System.out.println("BService Finish");
        return null;
    }

    @Override
    public boolean necessary(Object o, TaskContext support) {
        return true;
    }

    @Override
    public void onSuccess(TaskContext support) {

    }
}
