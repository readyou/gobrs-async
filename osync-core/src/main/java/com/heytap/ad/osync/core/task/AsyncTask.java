package com.heytap.ad.osync.core.task;


import com.heytap.ad.osync.core.log.TraceUtil;
import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.callback.ErrorCallback;
import com.heytap.ad.osync.core.common.enums.ExpState;
import com.heytap.ad.osync.core.common.util.SystemClock;
import com.heytap.ad.osync.core.config.ConfigManager;
import com.heytap.ad.osync.core.log.LogTracer;
import com.heytap.ad.osync.core.log.LogWrapper;
import com.heytap.ad.osync.core.common.def.DefaultConfig;
import com.heytap.ad.osync.core.common.domain.AnyConditionResult;
import com.heytap.ad.osync.core.common.domain.TaskResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.heytap.ad.osync.core.common.util.ExceptionUtil.isInterruptedException;

/**
 * 每一个任务需要继承此 抽象类
 *
 * @param <Param>  the type parameter
 * @param <Result> the type parameter
 * @program: gobrs -async-starter
 * @ClassName AsyncTask
 * @description:
 */
@Slf4j
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public abstract class AsyncTask<Param, Result> implements ITask<Param, Result> {
    private String name;

    private String desc;

    /**
     * Transaction com.heytap.ad.osync.com.heytap.ad.osync.test.task
     */
    private boolean rollback = DefaultConfig.TRANSACTION;

    private int retryCount = DefaultConfig.RETRY_COUNT;

    /**
     * Whether to execute a subtask if it fails
     */
    private boolean continueOnError = DefaultConfig.CONTINUE_ON_ERROR;

    private int timeoutInMilliseconds = DefaultConfig.TASK_TIME_OUT;

    /**
     * if true => execute when any of parentTasks finished
     */

    private boolean any = false;

    private boolean anyCondition = false;

    /**
     * exclusive的任务一旦开始执行，就取消其余上游任务（当前task所有未完成的upstream task）
     */
    private boolean exclusive = false;

    /**
     * get result of current com.heytap.ad.osync.com.heytap.ad.osync.test.task
     *
     * @param support the support
     * @return result result
     */
    public Result getResult(TaskContext support) {
        return getResult(support, false);
    }

    /**
     * Gets result.
     *
     * @param support      the support
     * @param anyCondition the any condition
     * @return the result
     */
    public Result getResult(TaskContext support, boolean anyCondition) {
        TaskResult<Result> taskResult = getTaskResult(support);
        if (anyCondition == true && taskResult != null) {
            return (Result) new AnyConditionResult().setResult(taskResult.getResult());
        }
        if (taskResult != null) {
            return taskResult.getResult();
        }
        return null;
    }

    public Result execute0(Param param, TaskContext support) {
        Long startTime = SystemClock.now();
        Result task;
        Exception exeError = null;
        try {
            task = execute(param, support);
        } catch (Exception exception) {
            exeError = exception;
            throw exception;
        } finally {
            boolean logCostTime = ConfigManager.Action.logCostTime(support.getRuleName());
            if (logCostTime &&
                    Objects.nonNull(support.getLogWrapper())) {
                long costTime = SystemClock.now() - startTime;
                LogTracer logTracer = LogTracer.builder()
                        .taskName(this.getName())
                        .taskCost(costTime)
                        .executeState(exeError == null ? true : false)
                        .errorMessage(exeError == null ? "" : exeError.getMessage())
                        .build();
                LogWrapper logWrapper = support.getLogWrapper();
                logWrapper.addTrace(logTracer);
                logWrapper.setProcessCost(costTime);
                log.info("<{}> [{}] execution", logWrapper.getTraceId(), this.getName());

            }
        }
        return task;
    }


    /**
     * Tasks to be performed
     *
     * @param param   the param
     * @param support the support
     * @return result result
     */
    public abstract Result execute(Param param, TaskContext support);

    /**
     * On failure trace.
     * 执行失败 回调
     *
     * @param support   the support
     * @param exception the com.heytap.ad.osync.exception
     */
    public void onFailureTrace(TaskContext support, Exception exception) {
        if (isInterruptedException(exception)) {
            return;
        }
        boolean logable = ConfigManager.Action.logError(support.getRuleName());
        if (logable) {
            log.error("<{}> {} error", TraceUtil.get(), this.getName(), exception);
        }
        onFail(support, exception);
    }


    /**
     * get result of depend on class
     *
     * @param <Result> the type parameter
     * @param support  the support
     * @param clazz    depend on class
     * @param type     the type
     * @return result result
     */
    public <Result> Result getResult(TaskContext support, Class<? extends ITask> clazz, Class<Result> type) {
        TaskResult<Result> taskResult = getTaskResult(support, clazz, type);
        if (taskResult != null) {
            return taskResult.getResult();
        }
        return null;
    }


    /**
     * get taskResult of current com.heytap.ad.osync.com.heytap.ad.osync.test.task
     *
     * @param support the support
     * @return com.heytap.ad.osync.com.heytap.ad.osync.test.task result
     */
    public TaskResult<Result> getTaskResult(TaskContext support) {
        Map<Class, TaskResult> resultMap = support.getResultMap();
        return resultMap.get(this.getClass()) != null ? resultMap.get(this.getClass()) : resultMap.get(depKey(this.getClass()));
    }

    /**
     * get taskResult of depend on class
     *
     * @param <Result> TaskResult<R>
     * @param support  the support
     * @param clazz    depend on class
     * @param type     the type
     * @return com.heytap.ad.osync.com.heytap.ad.osync.test.task result
     */
    public <Result> TaskResult<Result> getTaskResult(TaskContext support, Class<? extends ITask> clazz, Class<Result> type) {
        Map<Class, TaskResult> resultMap = support.getResultMap();
        return resultMap.get(clazz) != null ? resultMap.get(clazz) : resultMap.get(depKey(clazz));
    }

    /**
     * get com.heytap.ad.osync.com.heytap.ad.osync.test.task param
     *
     * @param support the support
     * @return param param
     */
    public Param getParam(TaskContext support) {
        Object param = support.getParam();
        if (Objects.nonNull(param)) {
            return (Param) param;
        }
        return null;
    }


    /**
     * Gets task future.
     *
     * @param <Result> the type parameter
     * @param support  the support
     * @param clazz    the clazz
     * @param type     the type
     * @return the task future
     */
    public <Result> Future<Result> getTaskFuture(TaskContext support, Class<? extends ITask> clazz, Class<Result> type) {
        Object o = support.getTaskLoader().futureMaps.get(clazz);
        if (Objects.nonNull(o)) {
            return ((Future<Result>) o);
        }
        return null;
    }


    /**
     * Gets task future result.
     *
     * @param <Result> the type parameter
     * @param support  the support
     * @param clazz    the clazz
     * @param type     the type
     * @param timeout  the timeout
     * @param unit     the unit
     * @return the task future result
     */
    public <Result> Object getTaskFutureResult(TaskContext support, Class<? extends ITask> clazz, Class<Result> type, long timeout, TimeUnit unit) {
        Object o = support.getTaskLoader().futureMaps.get(clazz);
        if (Objects.nonNull(o)) {
            try {
                return ((Future<Result>) o).get(timeout, unit);
            } catch (Exception e) {
                log.error("task {} getTaskFuture error {}", this.getName(), e);
            }
        }
        return null;
    }


    /**
     * Dep key string.
     *
     * @param clazz the clazz
     * @return the string
     */
    String depKey(Class clazz) {
        char[] cs = clazz.getSimpleName().toCharArray();
        cs[0] += 32;
        return String.valueOf(cs);
    }

    /**
     * Stop async boolean.
     * 主动中断任务流程 API调用
     *
     * @param taskContext the taskContext
     * @return the boolean
     */
    public boolean stopAsync(TaskContext taskContext) {
        try {
            ErrorCallback<Param> errorCallback = new ErrorCallback<Param>(() -> taskContext.getParam(), null, taskContext, this);
            taskContext.taskLoader.setExpCode(new AtomicInteger(ExpState.DEFAULT.getCode()));
            taskContext.taskLoader.errorInterrupted(errorCallback);
        } catch (Exception ex) {
            log.error("stopAsync error {}", ex);
            return false;
        }
        return true;
    }

    /**
     * Stop async boolean.
     *
     * @param support the support
     * @param expCode the exp code
     * @return the boolean
     */
    public boolean stopAsync(TaskContext support, Integer expCode) {
        try {
            support.taskLoader.setRunning(false);
            support.taskLoader.setExpCode(new AtomicInteger(expCode));

            ErrorCallback<Param> errorCallback = new ErrorCallback<Param>(() -> support.getParam(), null, support, this);
            support.taskLoader.errorInterrupted(errorCallback);

        } catch (Exception ex) {
            log.error("stopAsync error {} ", ex);
            return false;
        }
        return true;
    }


    /**
     * Gets process trace id.
     *
     * @return the trace id
     */
    public String getProcessTraceId() {
        Object tr = TraceUtil.get();
        return tr != null ? tr.toString() : null;
    }

    /**
     * Gets formatted trace id.
     *
     * @return the formatted trace id
     */
    public String getFormattedTraceId() {
        String processTraceId = getProcessTraceId();
        return String.format("<%s> [%s]", processTraceId, this.getName());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

}
