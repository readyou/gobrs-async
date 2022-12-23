package com.heytap.ad.osync.core;

import com.heytap.ad.osync.core.callback.AsyncTaskExceptionInterceptor;
import com.heytap.ad.osync.core.callback.AsyncTaskPostInterceptor;
import com.heytap.ad.osync.core.callback.AsyncTaskPreInterceptor;
import com.heytap.ad.osync.core.callback.ErrorCallback;
import com.heytap.ad.osync.core.common.def.DefaultConfig;
import com.heytap.ad.osync.core.common.domain.AsyncResult;
import com.heytap.ad.osync.core.common.enums.ExpState;
import com.heytap.ad.osync.core.common.enums.ResultState;
import com.heytap.ad.osync.core.common.exception.AsyncTaskTimeoutException;
import com.heytap.ad.osync.core.common.exception.OsyncException;
import com.heytap.ad.osync.core.config.ConfigManager;
import com.heytap.ad.osync.core.holder.BeanHolder;
import com.heytap.ad.osync.core.log.LogCreator;
import com.heytap.ad.osync.core.log.LogWrapper;
import com.heytap.ad.osync.core.task.AsyncTask;
import com.heytap.ad.osync.core.timer.OsyncTimer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.heytap.ad.osync.core.common.def.TaskStatus.TASK_INITIALIZED;
import static com.heytap.ad.osync.core.common.def.TaskStatus.TASK_TIMEOUT;
import static com.heytap.ad.osync.core.common.util.ExceptionUtil.isInterruptedException;

@Slf4j
@Data
public class TaskLoader<P, R> {
    private AtomicInteger expCode = new AtomicInteger(ExpState.DEFAULT.getCode());

    private volatile boolean running = true;

    private final ExecutorService executorService;

    private final AsyncTaskExceptionInterceptor<P> asyncExceptionInterceptor = BeanHolder.getBean(AsyncTaskExceptionInterceptor.class);

    private final AsyncTaskPreInterceptor<P> asyncTaskPreInterceptor = BeanHolder.getBean(AsyncTaskPreInterceptor.class);

    private final AsyncTaskPostInterceptor<P> asyncTaskPostInterceptor = BeanHolder.getBean(AsyncTaskPostInterceptor.class);

    private final CountDownLatch completeLatch;

    private final Map<AsyncTask, CallableTask> callableTaskMap;

    private TaskTrigger.EmptyTask emptyTask;

    private final long taskTimeoutMS;

    private volatile Throwable error;

    private final Lock lock = new ReentrantLock();

    private volatile boolean canceled = false;

    private ArrayList<Future<?>> futureLists;

    public final Map<AsyncTask<P, R>, Future<?>> futureMaps = new ConcurrentHashMap<>();

    public final Map<Class<?>, Reference<OsyncTimer.TimerListener>> timerListeners = new ConcurrentHashMap<>();

    private LogWrapper logWrapper;

    private final static ArrayList<Future<?>> EmptyFutures = new ArrayList<>(0);

    private String ruleName;

    // 记录某任务是否已开始（防止重复执行）
    public Map<CallableTask, Boolean> taskStartedMap = new ConcurrentHashMap();

    TaskLoader(String ruleName, ExecutorService executorService, Map<AsyncTask, CallableTask> callableTaskMap, long timeout) {
        this.ruleName = ruleName;
        this.executorService = executorService;
        this.callableTaskMap = callableTaskMap;
        completeLatch = new CountDownLatch(1);
        this.taskTimeoutMS = timeout;
        if (this.taskTimeoutMS > 0) {
            futureLists = new ArrayList<>(1);
        } else {
            futureLists = EmptyFutures;
        }
    }

    /**
     * 执行任务
     *
     * @return
     */
    AsyncResult load() {
        AsyncResult result = null;
        try {
            List<CallableTask> begins = getBeginTasks();
            // 并发开始执行每条任务链
            for (CallableTask task : begins) {
                // 如果只有一个任务，则直接在当前线程中执行（线程复用）
                if (begins.size() == 1) {
                    task.call();
                } else {
                    startTask(task);
                }
            }
            waitIfNecessary();
            result = buildAsyncResult(begins);
        } catch (Exception exception) {
            throw exception;
        } finally {
            return postProcess(result);
        }
    }

    /**
     * 后置处理
     * 开启日志 error 级别
     *
     * @param result
     * @return
     */
    private AsyncResult postProcess(AsyncResult result) {
        if (ConfigManager.Action.logCostTime(ruleName) && log.isErrorEnabled()) {
            String printContent = LogCreator.processLogs(logWrapper);
            log.info(printContent);
        }
        return result;
    }


    private ArrayList<CallableTask> getBeginTasks() {
        ArrayList<CallableTask> beginsWith = new ArrayList<>(1);
        for (CallableTask process : callableTaskMap.values()) {
            if (!process.hasUnsatisfiedDependcies()) {
                beginsWith.add(process);
            }
        }
        return beginsWith;
    }

    void complete() {
        completeLatch.countDown();
    }

    /**
     * Abnormal com.heytap.ad.osync.callback
     *
     * @param errorCallback Exception parameter encapsulation
     */
    void error(ErrorCallback errorCallback) {
        // 中断异常不处理
        if (isInterruptedException(errorCallback.getThrowable())) {
            return;
        }
        asyncExceptionInterceptor.exception(errorCallback);
    }

    /**
     * The process is interrupted by a task com.heytap.ad.osync.exception
     *
     * @param errorCallback the error com.heytap.ad.osync.callback
     */
    public void errorInterrupted(ErrorCallback errorCallback) {
        this.error = errorCallback.getThrowable();

        cancel();

        completeLatch.countDown();
        /**
         * manual stopAsync  com.heytap.ad.osync.exception  is null
         */
        if (errorCallback.getThrowable() != null) {
            /**
             * Global interception listening
             */
            if (isInterruptedException(errorCallback.getThrowable())) {
                return;
            }
            asyncExceptionInterceptor.exception(errorCallback);
        }
    }

    public void preInterceptor(P p, String taskName) {
        asyncTaskPreInterceptor.preProcess(p, taskName);
    }

    public void postInterceptor(P param, String taskName) {
        asyncTaskPostInterceptor.postProcess(param, taskName);
    }

    private void cancel() {
        lock.lock();
        try {
            canceled = true;
            for (Future<?> future : futureLists) {
                // Enforced interruptions
                future.cancel(true);
            }
            running = false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 主线程等待
     */
    private void waitIfNecessary() {
        try {
            if (taskTimeoutMS > 0) {
                if (!completeLatch.await(taskTimeoutMS, TimeUnit.MILLISECONDS)) {
                    cancel();
                    throw new AsyncTaskTimeoutException();
                }
            } else {
                completeLatch.await();
            }
            if (error != null) {
                throw new OsyncException(error);
            }
        } catch (InterruptedException e) {
            throw new OsyncException(e);
        }
    }


    CallableTask getCallableTask(AsyncTask asyncTask) {
        return callableTaskMap.get(asyncTask);
    }

    /**
     * 开启线程执行任务
     *
     * @param callableTask the task actuator
     */
    void startTask(CallableTask callableTask) {
        if (taskTimeoutMS > 0 || ConfigManager.getRule(ruleName).isTaskInterrupt()) {
            // If you need to interrupt then you need to save all the task threads and you need to manipulate shared variables
            try {
                lock.lock();
                if (!canceled && running) {
                    Future<?> submit = taskListenerConditional(callableTask);
                    futureLists.add(submit);
                }
            } finally {
                lock.unlock();
            }
        } else {
            // Run the command without setting the timeout period
            taskListenerConditional(callableTask);
        }
    }

    private Future<?> taskListenerConditional(CallableTask callableTask) {
        if (callableTask.task.getTimeoutInMilliseconds() > 0) {
            return timeOperator(callableTask);
        }
        return start(callableTask);
    }

    private Future<?> timeOperator(CallableTask<?> callableTask) {
        Future<?> future = executorService.submit(callableTask);
        OsyncTimer.TimerListener listener = new OsyncTimer.TimerListener() {
            @Override
            public void tick() {
                if (!future.isDone()
                        && callableTask.getTaskContext().getStatus(callableTask.getTask().getClass()).compareAndSet(TASK_INITIALIZED, TASK_TIMEOUT)
                        && future.cancel(true)) {
                    throw new AsyncTaskTimeoutException(String.format("task %s TimeoutException", callableTask.getTask().getName()));
                }
            }

            @Override
            public int getIntervalTimeInMilliseconds() {
                return callableTask.task.getTimeoutInMilliseconds();
            }
        };

        Reference<OsyncTimer.TimerListener> tl = OsyncTimer.getInstance(ConfigManager.getGlobalConfig().getTimeoutCoreSize()).addTimerListener(listener);
        timerListeners.put(callableTask.getTask().getClass(), tl);
        futureMaps.put(callableTask.task, future);
        return future;
    }

    private Future<?> start(CallableTask callableTask) {
        Future<?> future = executorService.submit(callableTask);
        futureMaps.put(callableTask.task, future);
        return future;
    }

    /**
     * 结束单条任务链
     *
     * @param subtasks the subtasks
     */
    public void stopSingleTaskLine(List<AsyncTask> subtasks) {
        CallableTask terminationTask = callableTaskMap.get(emptyTask);
        for (AsyncTask subtask : subtasks) {
            if (subtask instanceof TaskTrigger.EmptyTask) {
                terminationTask.releasingDependency();
                if (!terminationTask.hasUnsatisfiedDependcies()) {
                    terminationTask.call();
                }
                continue;
            }
            // 递归关闭子孙任务
            stopSingleTaskLine(callableTaskMap.get(subtask).subTasks);
        }
    }


    private TaskContext getTaskContext(List<CallableTask> begins) {
        return begins.get(0).getTaskContext();
    }

    private AsyncResult buildAsyncResult(List<CallableTask> begins) {
        TaskContext support = getTaskContext(begins);
        AsyncResult asyncResult = new AsyncResult();
        asyncResult.setResultMap(support.getResultMap());
        asyncResult.setCode(expCode.get());
        asyncResult.setSuccess(support.getResultMap().values().stream().allMatch(r -> r.getResultState().equals(ResultState.SUCCESS)));
        return asyncResult;
    }

}
