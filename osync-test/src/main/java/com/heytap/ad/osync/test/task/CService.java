package com.heytap.ad.osync.test.task;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.task.AsyncTask;
import com.heytap.ad.osync.core.common.domain.TaskResult;
import lombok.extern.slf4j.Slf4j;


/**
 * The type C service.
 *
 * @program: gobrs -async-starter
 * @ClassName CService
 * @description:
 */
@Slf4j
@Task
public class CService extends AsyncTask<String, Integer> {

    /**
     * The .
     */
    int i = 10000;

    @Override
    public void prepare(String o) {
        log.info(this.getName() + " 使用线程---" + Thread.currentThread().getName());
    }

    @Override
    public Integer execute(String o, TaskContext support) {
        try {
            System.out.println("CService Begin");
            //获取 所依赖的父任务的结果
            Integer rt = getResult(support);
            String result = getResult(support, AService.class, String.class);
            TaskResult<Integer> tk = getTaskResult(support);
            TaskResult<String> taskResult = getTaskResult(support, AService.class, String.class);
            Thread.sleep(300);
            for (int i1 = 0; i1 < i; i1++) {
                i1 += i1;
            }

            System.out.println("CService Finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean necessary(String o, TaskContext support) {
        return true;
    }


    @Override
    public void onSuccess(TaskContext support) {
        // 获取自身task 执行完成之后的结果
        Integer result = getResult(support);

        //获取 任务结果封装 包含执行状态
        TaskResult<Integer> taskResult = getTaskResult(support);
    }

}
