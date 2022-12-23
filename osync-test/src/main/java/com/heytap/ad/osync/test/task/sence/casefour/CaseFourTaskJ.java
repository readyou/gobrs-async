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
public class CaseFourTaskJ extends AsyncTask {

    @Override
    public Object execute(Object o, TaskContext support) {
        System.out.println("J任务执行");
        return "JResult";
    }
}