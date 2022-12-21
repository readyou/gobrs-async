package com.heytap.ad.osync.test.task.sence.casethree;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;

/**
 * The type Case three task e.
 *
 * @program: gobrs -async
 * @ClassName CaseOneTaskD
 * @description:
 */
@Task
public class CaseThreeTaskE extends AsyncTask {

    @Override
    public Object execute(Object o, TaskContext support) {
        System.out.println("E开始任务执行");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("E任务执行完成");
        return "EResult";
    }
}
