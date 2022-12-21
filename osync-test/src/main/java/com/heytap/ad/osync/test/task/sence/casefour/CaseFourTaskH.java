package com.heytap.ad.osync.test.task.sence.casefour;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;

/**
 * @program: gobrs-async
 * @ClassName GobrsTaskB
 * @description:
 **/
@Task
public class CaseFourTaskH extends AsyncTask {

    @Override
    public Object execute(Object o, TaskContext support) {
        System.out.println("H开始任务执行");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("H任务执行结束");
        return "HResult";
    }
}
