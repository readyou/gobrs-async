package com.heytap.ad.osync.test.task.condition;

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
public class EServiceCondition extends AsyncTask<Object, Boolean> {

    /**
     * The .
     */
    int i = 10000;


    @Override
    public Boolean execute(Object o, TaskContext support) {
//        System.out.println("EServiceCondition Begin");
        for (int i1 = 0; i1 < i; i1++) {
            i1 += i1;
        }
//        System.out.println("EServiceCondition Finish");
        return true;
    }
}
