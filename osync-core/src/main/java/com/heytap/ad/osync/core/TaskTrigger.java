package com.heytap.ad.osync.core;

import com.heytap.ad.osync.core.common.domain.AsyncParamSupply;
import com.heytap.ad.osync.core.common.util.JsonUtil;
import com.heytap.ad.osync.core.common.util.SystemClock;
import com.heytap.ad.osync.core.common.util.UuidUtil;
import com.heytap.ad.osync.core.config.ConfigManager;
import com.heytap.ad.osync.core.holder.BeanHolder;
import com.heytap.ad.osync.core.log.LogWrapper;
import com.heytap.ad.osync.core.log.TraceUtil;
import com.heytap.ad.osync.core.task.AsyncTask;
import com.heytap.ad.osync.core.threadpool.OsyncThreadPoolFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
class TaskTrigger<P, R> {
    private final TaskAdj taskAdj;

    private OsyncThreadPoolFactory threadPoolFactory = BeanHolder.getBean(OsyncThreadPoolFactory.class);

    private Map<AsyncTask, CallableTask> prepareTaskMap = Collections.synchronizedMap(new IdentityHashMap<>());

    private String ruleName;

    private EmptyTask emptyTask;

    private static Map<String, Map<AsyncTask, List<AsyncTask>>> upwardTasksMapSpace = new ConcurrentHashMap<>();

    TaskTrigger(String ruleName, TaskAdj taskAdj) {
        this.ruleName = ruleName;
        this.taskAdj = taskAdj;
        prepare();
    }

    /**
     * 预加载任务依赖关系
     * 缓存依赖任务配置、维护 任务所依赖的任务数量和任务对象
     */
    private void prepare() {
        Map<AsyncTask, List<AsyncTask>> subtasksMap = copyDependTasks(taskAdj.getDependsTasks());

        Map<AsyncTask, List<AsyncTask>> upwardTasksMap = new HashMap<>();
        for (AsyncTask task : subtasksMap.keySet()) {
            upwardTasksMap.put(task, new ArrayList<>(1));
        }

        for (AsyncTask task : subtasksMap.keySet()) {
            for (AsyncTask depended : subtasksMap.get(task)) {
                upwardTasksMap.get(depended).add(task);
            }
        }

        emptyTask = new EmptyTask();
        List<AsyncTask> noSubtasks = new ArrayList<>(1);
        for (AsyncTask task : subtasksMap.keySet()) {
            List<AsyncTask> subTasks = subtasksMap.get(task);
            if (subTasks.isEmpty()) {
                noSubtasks.add(task);
                subtasksMap.get(task).add(emptyTask);
            }
        }

        subtasksMap.put(emptyTask, new ArrayList<>(0));
        upwardTasksMap.put(emptyTask, noSubtasks);
        upwardTasksMapSpace.put(ruleName, upwardTasksMap);

        Map<AsyncTask, CallableTask> prepareTaskMapWrite = new IdentityHashMap<>();
        for (AsyncTask task : subtasksMap.keySet()) {
            CallableTask callableTask;
            if (task != emptyTask) {
                List<AsyncTask> circularDependency = upwardTasksMap.get(task).stream()
                        .filter(x -> x.getName().equals(task.getName())).collect(Collectors.toList());
                int upDepend = 0;
                if (circularDependency.size() == 0) {
                    upDepend = upwardTasksMap.get(task).size();
                }
                callableTask = new CallableTask(task, upDepend, subtasksMap.get(task), upwardTasksMap);
            } else {
                callableTask = new TerminationCallableTask(task, upwardTasksMap.get(task).size(), subtasksMap.get(task));
            }
            prepareTaskMapWrite.put(task, callableTask);
        }
        // 读写分离
        prepareTaskMap = prepareTaskMapWrite;
        if (log.isInfoEnabled()) {
            log.info("prepareTaskMap build success {}", JsonUtil.obj2String(prepareTaskMap));
        }
    }

    private Map<AsyncTask, List<AsyncTask>> copyDependTasks(Map<AsyncTask, List<AsyncTask>> handlerMap) {
        IdentityHashMap<AsyncTask, List<AsyncTask>> rt = new IdentityHashMap<>();
        for (AsyncTask asyncTask : handlerMap.keySet()) {
            rt.put(asyncTask, new ArrayList<>(handlerMap.get(asyncTask)));
        }
        return rt;
    }


    /**
     * 触发任务加载 环境准备
     * 链路日志
     * 线程池配置
     * 从缓存好的预加载配置中 clone副本 浅clone 只备份基本类型
     *
     * @param param   the param
     * @param timeout the timeout
     * @return the com.heytap.ad.osync.com.heytap.ad.osync.test.task loader
     */
    TaskLoader trigger(AsyncParamSupply<P> param, long timeout) {

        IdentityHashMap<AsyncTask, CallableTask> newProcessMap = new IdentityHashMap<>(prepareTaskMap.size());
        TaskLoader<P, R> loader = new TaskLoader<>(ruleName, threadPoolFactory.getThreadPoolExecutor(), newProcessMap, timeout);

        TaskContext taskContext = related(param, loader);
        for (AsyncTask task : prepareTaskMap.keySet()) {
            /**
             * clone Process for Thread isolation
             */
            CallableTask processor = (CallableTask<?>) prepareTaskMap.get(task).clone();

            processor.init(taskContext, param);

            newProcessMap.put(task, processor);
        }

        return loader;
    }

    /**
     * 设置 任务总线和任务加载器关联关系
     * 配置设置 环境加载
     *
     * @param param
     * @param loader
     * @return
     */
    private TaskContext related(AsyncParamSupply<P> param, TaskLoader<P, R> loader) {

        TaskContext taskContext = buildTaskContext(param);

        taskContext.setTaskLoader(loader);

        logAdvance(taskContext);

        loader.setEmptyTask(emptyTask);

        /**
         * The thread pool is obtained from the factory, and the thread pool parameters can be dynamically adjusted
         */
        taskContext.setExecutorService(threadPoolFactory.getThreadPoolExecutor());
        return taskContext;
    }


    /**
     * 终止任务 在整个任务流程结束后 会调用该任务类执行 completed()
     */
    private class TerminationCallableTask<P, R> extends CallableTask<Object> {
        TerminationCallableTask(AsyncTask<P, R> handler, int depdending, List<AsyncTask> dependedTasks) {
            super(handler, depdending, dependedTasks);
        }

        @Override
        public Object call() {
            taskContext.taskLoader.complete();
            return null;
        }
    }

    /**
     * 类似于 nil 的哨兵对象，用来简化系统处理
     *
     * @param <P>
     * @param <R>
     */
    public class EmptyTask<P, R> extends AsyncTask<P, R> {
        @Override
        public R execute(P p, TaskContext taskContext) {
            return null;
        }
    }

    /**
     * 获取任务流程 总线
     * taskContext 封装流程中重要的 配置信息和任务执行过程中的流转信息
     *
     * @param supply
     * @return
     */
    private TaskContext buildTaskContext(AsyncParamSupply supply) {
        return new TaskContext().setParam(supply.get()).setRuleName(ruleName);
    }


    /**
     * 1、traceId
     * 2、日志
     *
     * @param taskContext
     */
    private void logAdvance(TaskContext taskContext) {
        String traceId = UuidUtil.uuid(10);
        TraceUtil.set(traceId);
        boolean logCostTime = ConfigManager.Action.logCostTime(ruleName);
        if (logCostTime) {
            LogWrapper.TimeCollector timeCollector =
                    LogWrapper.TimeCollector.builder()
                            .startTime(SystemClock.now())
                            .build();
            LogWrapper logWrapper = new LogWrapper()
                    .setTraceId(traceId)
                    .setTimeCollector(timeCollector);
            taskContext.setLogWrapper(logWrapper);
            taskContext.getTaskLoader().setLogWrapper(logWrapper);
        }

    }

}
