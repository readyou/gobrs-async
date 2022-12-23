package com.heytap.ad.osync.test.task.sence.caseone;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;

/**
 * @program: gobrs-async
 * @ClassName CaseOneTaskC
 * @description:
 **/
@Task
public class CaseOneTaskC extends AsyncTask {

    @Override
    public Object execute(Object o, TaskContext support) {
        System.out.println("C任务执行");
        return "CResult";
    }
}