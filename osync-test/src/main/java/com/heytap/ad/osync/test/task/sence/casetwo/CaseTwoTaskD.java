package com.heytap.ad.osync.test.task.sence.casetwo;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;

/**
 * @program: gobrs-async
 * @ClassName CaseOneTaskD
 * @description:
 **/
@Task
public class CaseTwoTaskD extends AsyncTask {

    @Override
    public Object execute(Object o, TaskContext support) {
        System.out.println("D任务执行");
        return "DResult";
    }
}
