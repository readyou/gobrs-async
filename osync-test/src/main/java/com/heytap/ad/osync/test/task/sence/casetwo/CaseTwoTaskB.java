package com.heytap.ad.osync.test.task.sence.casetwo;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;

/**
 * @program: gobrs-async
 * @ClassName GobrsTaskB
 * @description:
 **/
@Task
public class CaseTwoTaskB extends AsyncTask {

    @Override
    public Object execute(Object o, TaskContext support) {
        System.out.println("B开始任务执行");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("B任务执行");
        return "BResult";
    }
}
