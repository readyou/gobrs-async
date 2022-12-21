package com.heytap.ad.osync.core.engine;

import com.heytap.ad.osync.core.Osync;
import com.heytap.ad.osync.core.TaskGroup;
import com.heytap.ad.osync.core.anno.Task;
import com.heytap.ad.osync.core.common.exception.OsyncException;
import com.heytap.ad.osync.core.config.OsyncConfig;
import com.heytap.ad.osync.core.config.OsyncRule;
import com.heytap.ad.osync.core.holder.BeanHolder;
import com.heytap.ad.osync.core.task.AsyncTask;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.heytap.ad.osync.core.common.def.DefaultConfig.*;


public class RuleEngineImpl<T> extends AbstractEngine {

    /**
     * The constant sp.
     */
    public static final String sp = ",";
    /**
     * The constant tied.
     */
    public static final String tied = ":";

    @Resource
    private OsyncConfig osyncConfig;


    @Resource
    private Osync osync;

    @Override
    public void doParse(OsyncRule rule, boolean reload) {
        String[] taskFlows = rule.getContent().replaceAll("\\s+", "").split(osyncConfig.getSplit());
        List<AsyncTask> pioneer = new ArrayList<>();
        for (String taskFlow : taskFlows) {
            String[] taskArr = taskFlow.split(osyncConfig.getPoint());
            if (taskArr.length == 0) {
                throw new OsyncException("invalid task config: " + taskFlow);
            }
            String first = taskArr[0];
            String[] childFlows = first.split(sp);
            for (String task : childFlows) {
                AsyncTask asyncTask = getTaskBean(task);
                pioneer.add(asyncTask);
            }
        }
        osync.begin(rule.getName(), pioneer, reload);

        Map<String, AsyncTask> cacheTaskWrappers = new ConcurrentHashMap<>();
        for (String taskFlow : taskFlows) {
            String[] taskArr = taskFlow.split(osyncConfig.getPoint());
            List<String> arrayList = Arrays.asList(taskArr);
            String leftTaskName = arrayList.get(0);
            String[] split = leftTaskName.split(sp);
            for (String s : split) {
                TaskGroup taskGroup = osync.after(rule.getName(), getTaskBean(s));
                doChildFlow(taskGroup, cacheTaskWrappers, arrayList);
            }
        }
    }

    private void doChildFlow(TaskGroup taskGroup, Map<String, AsyncTask> cacheTaskWrappers, List<String> arrayList) {
        // 第一个/组任务已经在taskGroup中了，这里跳过
        for (int i = 1; i < arrayList.size(); i++) {
            String taskBean = arrayList.get(i);
            String[] beanArray = taskBean.split(sp);
            List<String> beanList = Arrays.asList(beanArray);
            List<AsyncTask> asyncTasks = new ArrayList<>();
            for (String beanName : beanList) {
                AsyncTask asyncTask = cacheTaskWrappers.computeIfAbsent(beanName, RuleEngineImpl::getTaskBean);
                if (asyncTask == null) {
                    throw new RuntimeException("bean not found: " + beanName);
                }
                taskGroup.then(false, asyncTask);
                asyncTasks.add(asyncTask);
            }
            taskGroup.refresh(asyncTasks);
        }
    }

    private static AsyncTask getTaskBean(String taskName) {
        String name = taskName;
        int cursor = 0;
        String[] preNamed = taskName.split(tied);
        if (taskName.contains(tied)) {
            String[] tiredNames = preNamed;
            name = tiredNames[0];
            cursor = tiredNames.length;
        }
        AsyncTask task = (AsyncTask) getBean(name);

        Task anno = task.getClass().getAnnotation(Task.class);
        if (Objects.isNull(anno)) {
            throw new OsyncException(String.format("Tasks %s are not annotated with @Task", taskName));
        }
        task.setDesc(anno.desc());
        task.setRollback(anno.rollback());
        task.setRetryCount(anno.retryCount());
        task.setContinueOnError(anno.continueOnError());
        task.setTimeoutInMilliseconds(anno.timeoutInMilliseconds());

        if (!StringUtils.isEmpty(anno.value())) {
            task.setName(anno.value());
        } else {
            task.setName(taskName);
        }

        if (taskName.contains(tied) && RULE_ANY.equals(preNamed[1])) {
            task.setAny(true);
        }

        if (taskName.contains(tied) && RULE_ANY_CONDITION.equals(preNamed[1])) {
            task.setAnyCondition(true);
        }

        if (cursor == 3 && RULE_EXCLUSIVE.equals(preNamed[2])) {
            task.setExclusive(true);
        }
        return task;
    }

    private static Object getBean(String bean) {
        return Optional.ofNullable(BeanHolder.getBean(bean))
                .orElseThrow(() -> new RuntimeException("bean not found, name is " + bean));
    }

}
