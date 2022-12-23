package com.heytap.ad.osync.core.task;

import com.heytap.ad.osync.core.common.domain.AnyConditionResult;
import com.heytap.ad.osync.core.common.domain.TaskResult;
import com.heytap.ad.osync.core.common.enums.ResultState;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class TaskUtil {

    /**
     * Multiple dependencies boolean.
     *
     * @param upwardTasksMap the upward tasks map
     * @param subTasks       the sub tasks
     * @return the boolean
     */
    public static boolean multipleDependencies(Map<AsyncTask, List<AsyncTask>> upwardTasksMap, List<AsyncTask> subTasks) {
        for (AsyncTask subTask : subTasks) {
            if (!CollectionUtils.isEmpty(upwardTasksMap.get(subTask)) && upwardTasksMap.get(subTask).size() > 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Default any condition any condition.
     *
     * @return the any condition
     */
    public static AnyConditionResult defaultAnyCondition() {
        return new AnyConditionResult().setSuccess(false);
    }


    public static AnyConditionResult defaultAnyCondition(boolean success) {
        return new AnyConditionResult().setSuccess(success);
    }

    public static TaskResult buildFutureTaskResult(Future result) {
        return new TaskResult(result, ResultState.SUCCESS, null);
    }

}
