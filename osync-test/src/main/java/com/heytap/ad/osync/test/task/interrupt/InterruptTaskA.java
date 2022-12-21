package com.heytap.ad.osync.test.task.interrupt;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;

/**
 * The type A service.
 *
 * @program: gobrs -async-starter
 * @ClassName AService
 * @description:
 */
@Task(continueOnError = true)
public class InterruptTaskA extends AsyncTask {

    /**
     * The .
     */
    int i = 10000;

    @Override
    public void prepare(Object o) {


    }

    @Override
    public String execute(Object o, TaskContext support) {

        try {
            System.out.println("InterruptTaskA Begin");
            Thread.sleep(10);
            for (int i1 = 0; i1 < i; i1++) {
                i1 += i1;
            }
            System.out.println(1 / 0);
            System.out.println("InterruptTaskA Finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
