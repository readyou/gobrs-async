package com.heytap.ad.osync.test.task.condition;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;

/**
 * The type D service.
 *
 * @program: gobrs -async-starter
 * @ClassName DService
 * @description: 任务依赖类型
 * AServiceCondition,BServiceCondition,CServiceCondition->DServiceCondition:anyCondition
 * <p>
 * 简化配置
 * <p>
 * A,B,C->D:anyCondition
 * <p>
 * D根据 A,B,C 返回的任务结果中的 AnyCondition 的state状态 进行判断是否继续执行 子任务
 */
@Task
public class DServiceCondition extends AsyncTask<Object, Boolean> {

    /**
     * The .
     */
    int sums = 1;

    @Override
    public Boolean execute(Object o, TaskContext support) {
        System.out.println("DServiceCondition Begin");
        for (int i1 = 0; i1 < sums; i1++) {
            i1 += i1;
        }
        System.out.println("DServiceCondition Finish");
        return true;
    }


}
