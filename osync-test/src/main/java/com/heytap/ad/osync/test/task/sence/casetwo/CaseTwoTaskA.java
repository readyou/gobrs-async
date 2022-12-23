package com.heytap.ad.osync.test.task.sence.casetwo;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;

/**
 * @program: gobrs-async
 * @ClassName GobrsTaskA
 * @description:
 **/
@Task(value = "caseTwoTaskA")
public class CaseTwoTaskA extends AsyncTask {

    @Override
    public Object execute(Object o, TaskContext support) {
        System.out.println("A任务执行");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("A任务执行完成");
        return "AResult";
    }
}
