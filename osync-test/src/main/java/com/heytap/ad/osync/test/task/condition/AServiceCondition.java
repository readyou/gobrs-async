package com.heytap.ad.osync.test.task.condition;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.common.domain.AnyConditionResult;
import com.heytap.ad.osync.core.task.AsyncTask;

/**
 * The type A service.
 *
 * @program: gobrs -async-starter
 * @ClassName AService
 * @description: 任务依赖类型
 * AServiceCondition,BServiceCondition,CServiceCondition->DServiceCondition:anyCondition
 * <p>
 * 简化配置
 * <p>
 * A,B,C->D:anyCondition
 * <p>
 * D根据 A,B,C 返回的任务结果中的 AnyCondition 的state状态 进行判断是否继续执行 子任务
 */
@Task(continueOnError = true)
public class AServiceCondition extends AsyncTask {

    /**
     * The .
     */
    int sums = 10000;

    @Override
    public AnyConditionResult<String> execute(Object o, TaskContext support) {
        try {
            System.out.println("AServiceCondition Begin");
            Thread.sleep(300);
            for (int i1 = 0; i1 < sums; i1++) {
                i1 += i1;
            }
            System.out.println("AServiceCondition Finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new AnyConditionResult<String>().setSuccess(false);
        }
        return new AnyConditionResult<String>().setSuccess(true);
    }

}
