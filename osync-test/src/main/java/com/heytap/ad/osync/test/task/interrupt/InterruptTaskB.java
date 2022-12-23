package com.heytap.ad.osync.test.task.interrupt;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;

/**
 * The type D service.
 *
 * @program: gobrs -async-starter
 * @ClassName DService
 * @description:
 */
@Task
public class InterruptTaskB extends AsyncTask<Object, Object> {

    /**
     * The .
     */
    int i = 10000;

    @Override
    public void prepare(Object o) {

    }

    @Override
    public Object execute(Object o, TaskContext support) {
        try {
            System.out.println("InterruptTaskB Begin");
            Thread.sleep(200);
            for (int i1 = 0; i1 < i; i1++) {
                i1 += i1;
            }
            System.out.println("InterruptTaskB Finish");

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
