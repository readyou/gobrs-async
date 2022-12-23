package com.heytap.ad.osync.test.task.timeout;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;
import lombok.extern.slf4j.Slf4j;

/**
 * The type F service.
 *
 * @program: gobrs -async-starter
 * @description:
 */
@Slf4j
@Task
public class CaseTimeoutTaskC extends AsyncTask<Object, Object> {
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
        try {
            System.out.println("CaseTimeoutTaskC Begin");
            Thread.sleep(2000);
            for (int i1 = 0; i1 < i; i1++) {
                i1 += i1;
            }
            System.out.println("CaseTimeoutTaskC Finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
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
