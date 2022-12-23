package com.heytap.ad.osync.core.common.def;

public interface DefaultConfig {

    String TASK_NAME = "asyncTaskName";

    Integer THREAD_POOL_QUEUE_SIZE = 10000;

    Long KEEP_ALIVE_TIME_MS = 30000L;

    Long EXECUTE_TIMEOUT_MS = 10000L;

    String RULE_ANY = "any";

    String RULE_ANY_CONDITION = "anyCondition";

    String RULE_EXCLUSIVE = "exclusive";

    int RETRY_COUNT = 0;

    boolean CONTINUE_ON_ERROR = false;

    boolean TRANSACTION = false;

    /**
     */
    boolean ANY_CONDITION_STATE = true;

    Integer CORE_POOL_SIZE = 100;

    Integer MAX_POOL_SIZE = 200;

    String REJECT_POLICY = "AbortPolicy";

    int TASK_TIME_OUT = 0;

    boolean LOG_ERROR = true;

    boolean LOG_COST_TIME = true;

}
