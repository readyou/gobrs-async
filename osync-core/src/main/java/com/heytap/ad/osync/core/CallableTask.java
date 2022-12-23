package com.heytap.ad.osync.core;

import com.heytap.ad.osync.core.callback.ErrorCallback;
import com.heytap.ad.osync.core.common.domain.AnyConditionResult;
import com.heytap.ad.osync.core.common.domain.AsyncParamSupply;
import com.heytap.ad.osync.core.common.domain.TaskResult;
import com.heytap.ad.osync.core.common.domain.TaskStatus;
import com.heytap.ad.osync.core.common.enums.ResultState;
import com.heytap.ad.osync.core.config.ConfigManager;
import com.heytap.ad.osync.core.log.TraceUtil;
import com.heytap.ad.osync.core.task.AsyncTask;
import com.heytap.ad.osync.core.task.TaskUtil;
import com.heytap.ad.osync.core.timer.OsyncTimer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.lang.ref.Reference;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.heytap.ad.osync.core.common.def.TaskStatus.TASK_FINISHED;
import static com.heytap.ad.osync.core.common.def.TaskStatus.TASK_INITIALIZED;

@Slf4j
public class CallableTask<Result> implements Callable<Result>, Cloneable {
    protected TaskContext taskContext;
    final AsyncTask task;
    // 上游任务最少完成数量
    private volatile int dependTaskQuantity;
    final List<AsyncTask> subTasks;
    private AsyncParamSupply param;
    private Lock lock;
    private Map<AsyncTask, List<AsyncTask>> upwardTasksMap = new ConcurrentHashMap<>();

    CallableTask(AsyncTask asyncTask, int depends, List<AsyncTask> subTasks) {
        this.task = asyncTask;
        this.dependTaskQuantity = depends > 1 & task.isAny() ? 1 : depends;
        this.subTasks = subTasks;
    }

    CallableTask(AsyncTask asyncTask, int depends, List<AsyncTask> subTasks, Map<AsyncTask, List<AsyncTask>> upwardTasksMap) {
        this.task = asyncTask;
        this.dependTaskQuantity = depends > 1 & task.isAny() ? 1 : depends;
        this.subTasks = subTasks;
        this.upwardTasksMap = upwardTasksMap;
    }


    /**
     * Initialize the object cloned from prototype.
     *
     * @param support the support
     * @param param   the param
     */
    void init(TaskContext support, AsyncParamSupply param) {
        this.taskContext = support;
        this.param = param;
    }

    @Override
    public Result call() {
        Object parameter = getParameter(task);

        preparation();

        TaskLoader taskLoader = taskContext.getTaskLoader();
        Object result = null;
        try {
            if (needToExecute(parameter, task)) {
                task.prepare(parameter);

                taskLoader.preInterceptor(parameter, task.getName());

                result = task.execute0(parameter, taskContext);

                setStatusFinished();

                taskLoader.postInterceptor(result, task.getName());

                if (ConfigManager.getGlobalConfig().isParamContext()) {
                    taskContext.getResultMap().put(task.getClass(), buildSuccessResult(result));
                }

                task.onSuccess(taskContext);
            }

            if (taskLoader.isRunning()) {
                nextTaskByCase(taskLoader, result);
            }
        } catch (Exception e) {
            try {
                exceptionProcess(parameter, taskLoader, e);
            } catch (Exception exception) {
                log.error("<{}> [{}] exceptionProcess error {} ", TraceUtil.get(), task.getName(), e);
                taskLoader.stopSingleTaskLine(subTasks);
            }
        } finally {
            clearTimerListener();
        }
        return (Result) result;
    }


    /**
     * 执行任务准备阶段：
     * * 取消没必要继续执行的任务
     */
    private void preparation() {
        if (task.isExclusive()) {
            List<AsyncTask> asyncTaskList = upwardTasksMap.get(task);

            Map<AsyncTask<?, Result>, Future<?>> futureMaps = taskContext.getTaskLoader().futureMaps;
            futureMaps.forEach((x, y) -> {
                if (asyncTaskList.contains(x)) {
                    y.cancel(true);
                }
            });
        }
    }

    private void setStatusFinished() {
        taskContext.getStatus(task.getClass()).compareAndSet(TASK_INITIALIZED, TASK_FINISHED);
    }


    private void clearTimerListener() {
        Reference<OsyncTimer.TimerListener> listenerReference = getListenerReference();
        if (Objects.nonNull(listenerReference)) {
            listenerReference.clear();
        }
    }

    private Reference<OsyncTimer.TimerListener> getListenerReference() {
        Map<Class<?>, Reference<OsyncTimer.TimerListener>> timerListeners = taskContext.getTaskLoader().getTimerListeners();
        return timerListeners.get(task.getClass());
    }

    /**
     * 判断任务是否有必要执行
     * 1、necessary 返回true
     * 2、如果具备执行结果 则无需执行
     */
    private boolean needToExecute(Object parameter, AsyncTask task) {
        return task.necessary(parameter, taskContext) && (Objects.isNull(taskContext.getResultMap().get(task.getClass())));
    }

    private void exceptionProcess(Object parameter, TaskLoader taskLoader, Exception e) {
        if (!retryTask(parameter, taskLoader)) {
            taskContext.getResultMap().put(task.getClass(), buildErrorResult(null, e));

            task.onFailureTrace(taskContext, e);

            // 事物任务回滚
            rollback(taskLoader);

            // 配置 taskInterrupt = true 则某一任务异常后结束整个任务流程 默认 false
            if (ConfigManager.getRule(taskLoader.getRuleName()).isTaskInterrupt()) {
                taskLoader.errorInterrupted(errorCallback(parameter, e, taskContext, task));
            } else {
                taskLoader.error(errorCallback(parameter, e, taskContext, task));
                // 当然任务失败 是否继续执行子任务
                if (task.isContinueOnError()) {
                    nextTask(taskLoader, TaskUtil.defaultAnyCondition(false));
                } else {
                    if (TaskUtil.multipleDependencies(upwardTasksMap, subTasks)) {
                        nextTask(taskLoader, TaskUtil.defaultAnyCondition(false));
                    } else {
                        taskLoader.stopSingleTaskLine(subTasks);
                    }
                }
            }
        }
    }

    /**
     * 根据条件执行任务
     *
     * @param taskLoader
     * @param result
     */
    private void nextTaskByCase(TaskLoader taskLoader, Object result) {
        if (result instanceof AnyConditionResult) {
            nextTask(taskLoader, (AnyConditionResult) result);
            return;
        }
        nextTask(taskLoader);
    }


    /**
     * 获取任务参数
     *
     * @return
     */
    private Object getParameter(AsyncTask task) {
        Object parameter = param.get();
        if (parameter instanceof Map) {
            Object param = ((Map<?, ?>) parameter).get(task.getClass());
            return param == null ? ((Map<?, ?>) parameter).get(task.getClass().getName()) : param;
        }
        return parameter;
    }


    /**
     * 任务重试 必须注解开启
     *
     * @param parameter
     * @param taskLoader
     * @return
     */
    private boolean retryTask(Object parameter, TaskLoader taskLoader) {
        try {
            AtomicInteger retriedCounts = taskContext.getStatus(task.getClass()).getRetriedCounts();
            TaskStatus status = getTaskContext().getStatus(task.getClass());
            if ((status.getStatus().get() == TASK_INITIALIZED) && task.getRetryCount() > 1 && task.getRetryCount() > retriedCounts.get()) {
                retriedCounts.incrementAndGet();
                doTaskWithRetryConditional(parameter, taskLoader);
                if (task.isContinueOnError()) {
                    nextTask(taskLoader);
                }
                return true;
            }
            return false;
        } catch (Exception exception) {
            return retryTask(parameter, taskLoader);
        }
    }

    /**
     * 根据条件 选择性任务重试
     *
     * @param parameter
     * @param taskLoader
     */
    private void doTaskWithRetryConditional(Object parameter, TaskLoader taskLoader) {
        Object result = task.execute0(parameter, taskContext);
        setStatusFinished();
        try {
            taskLoader.postInterceptor(result, task.getName());
            if (ConfigManager.getGlobalConfig().isParamContext()) {
                taskContext.getResultMap().put(task.getClass(), buildSuccessResult(result));
            }
            task.onSuccess(taskContext);
        } catch (Exception ex) {
            log.error("", ex);
        }
    }

    public void nextTask(TaskLoader taskLoader) {
        nextTask(taskLoader, TaskUtil.defaultAnyCondition());
    }

    /**
     * 执行下一任务 （子任务）
     *
     * @param taskLoader      the com.heytap.ad.osync.com.heytap.ad.osync.test.task loader
     * @param conditionResult the com.heytap.ad.osync.com.heytap.ad.osync.test.task conditionResult
     */
    public void nextTask(TaskLoader taskLoader, AnyConditionResult conditionResult) {
        if (CollectionUtils.isEmpty(subTasks)) {
            return;
        }
        for (int i = 0; i < subTasks.size(); i++) {
            CallableTask process = taskLoader.getCallableTask(subTasks.get(i));
            if (process.task.isAnyCondition()) {
                // 两种情况下继续往下执行，
                // 1. 所有依赖任务都执行完了
                // 2. conditionResult的执行结果成功
                if (process.releasingDependency() == 0 || conditionResult.getSuccess()) {
                    synchronized (process.task) {
                        Boolean aBoolean = (Boolean) taskLoader.taskStartedMap.get(process);
                        if (Objects.isNull(aBoolean)) {
                            taskLoader.taskStartedMap.put(process, true);
                            doTask(taskLoader, process, isCycleThread(i));
                        }
                    }
                }
            } else {
                if (process.releasingDependency() == 0) {
                    doTask(taskLoader, process, isCycleThread(i));
                }
            }
        }
    }

    /**
     * 线程复用
     * 1、最后一个子任务使用父任务的线程
     * 2、父任务未设置超时时间的任务 具备线程复用的能力
     *
     * @param i
     * @return
     */
    private boolean isCycleThread(int i) {
        return i == subTasks.size() - 1 && Objects.isNull(getListenerReference());
    }

    /**
     * cycleThread 线程复用
     * A->C,D
     * 此时 D会使用A的线程继续执行任务 而不会再开启线程 节省了线程开销和线程上下文切换
     *
     * @param taskLoader
     * @param callableTask
     */
    private void doTask(TaskLoader taskLoader, CallableTask callableTask, boolean cycleThread) {
        if (!cycleThread) {
            taskLoader.startTask(callableTask);
        } else {
            // 在当前线程中执行
            callableTask.call();
        }
    }

    /**
     * 是否还有自身所依赖的任务
     *
     * @return boolean boolean
     */
    boolean hasUnsatisfiedDependcies() {
        lock.lock();
        try {
            return dependTaskQuantity != 0;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 完成/减少一个依赖任务
     *
     * @return int int
     */
    public int releasingDependency() {
        lock.lock();
        try {
            return --dependTaskQuantity;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object clone() {
        try {
            CallableTask cloned = (CallableTask) super.clone();
            cloned.lock = new ReentrantLock();
            return cloned;
        } catch (Exception e) {
            throw new InternalError();
        }
    }

    private void rollback(TaskLoader taskLoader) {
        if (ConfigManager.getRule(taskLoader.getRuleName()).isTransaction()) {
            if (!this.task.isRollback()) {
                return;
            }
            List<AsyncTask> asyncTaskList = upwardTasksMap.get(this.task);
            if (asyncTaskList == null || asyncTaskList.isEmpty()) {
                return;
            }

            taskContext.getExecutorService().execute(() -> rollback(asyncTaskList, taskContext));
        }
    }

    private void rollback(List<AsyncTask> asyncTasks, TaskContext support) {
        for (AsyncTask asyncTask : asyncTasks) {
            try {
                if (support.getParam() instanceof Map) {
                    asyncTask.rollback(((Map<?, ?>) support.getParam()).get(this.getClass()));
                } else {
                    asyncTask.rollback(support.getParam());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            // 递归回滚
            List<AsyncTask> asyncTaskList = upwardTasksMap.get(asyncTask);
            rollback(asyncTaskList, support);
        }
    }


    public AsyncTask getTask() {
        return task;
    }

    public TaskContext getTaskContext() {
        return taskContext;
    }

    private TaskResult buildSuccessResult(Object result) {
        return new TaskResult(result, ResultState.SUCCESS, null);
    }


    private TaskResult buildErrorResult(Object result, Exception ex) {
        return new TaskResult(result, ResultState.SUCCESS, ex);
    }

    private ErrorCallback errorCallback(Object result, Exception e, TaskContext support, AsyncTask asyncTask) {
        return new ErrorCallback(param, e, support, asyncTask);
    }

}
