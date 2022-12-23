package com.heytap.ad.osync.test.task.interrupt;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;
import lombok.SneakyThrows;

/**
 * The type B service.
 *
 * @program: gobrs -async-starter
 * @ClassName BService
 * @description:
 */
@Task
public class InterruptTaskD extends AsyncTask {


    /**
     * The .
     */
    int i = 10000;

    @Override
    public void prepare(Object o) {

    }

    @SneakyThrows
    @Override
    public Object execute(Object o, TaskContext support) {
        System.out.println("InterruptTaskD Begin");
        for (int i1 = 0; i1 < i; i1++) {
            i1 += i1;
        }
        Thread.sleep(20000);
        System.out.println("InterruptTaskD Finish");
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
