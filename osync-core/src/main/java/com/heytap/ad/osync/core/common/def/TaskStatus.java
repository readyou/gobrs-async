package com.heytap.ad.osync.core.common.def;

public interface TaskStatus {
    int TASK_INITIALIZED = 0;
    int TASK_FINISHED = Integer.MAX_VALUE;
    int TASK_TIMEOUT = -1;
}
