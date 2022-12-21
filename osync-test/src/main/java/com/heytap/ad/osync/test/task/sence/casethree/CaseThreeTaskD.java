package com.heytap.ad.osync.test.task.sence.casethree;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;

/**
 * @program: gobrs-async
 * @ClassName CaseOneTaskD
 * @description:
 **/
@Task
public class CaseThreeTaskD extends AsyncTask {

    @Override
    public Object execute(Object o, TaskContext support) {
        System.out.println("D任务执行");
        return "DResult";
    }
}
