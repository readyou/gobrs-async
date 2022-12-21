package com.heytap.ad.osync.test.task;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;
import lombok.extern.slf4j.Slf4j;

/**
 * The type G service.
 *
 * @program: gobrs -async-starter
 * @ClassName EService
 * @description:
 */
@Slf4j
@Task(rollback = true)
public class GService extends AsyncTask {
    /**
     * The .
     */
    int i  = 10000;
    @Override
    public void prepare(Object o) {
        log.info(this.getName() + " 使用线程---" + Thread.currentThread().getName());
    }

    @Override
    public Object execute(Object o, TaskContext support) {
        try {
            System.out.println("GService Begin");
            Thread.sleep(100);
            System.out.println("GService Finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i1 = 0; i1 < i; i1++) {
            i1 += i1;
        }

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
