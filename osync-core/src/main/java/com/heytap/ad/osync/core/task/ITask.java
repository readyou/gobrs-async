package com.heytap.ad.osync.core.task;

import com.heytap.ad.osync.core.TaskContext;

public interface ITask<Param, Result> {
    /**
     * Before the mission begins
     *
     * @param param the param
     */
    default void prepare(Param param) {
    }



    /**
     * Whether a com.heytap.ad.osync.com.heytap.ad.osync.test.task needs to be executed
     * <p>
     * The condition determines whether the com.heytap.ad.osync.com.heytap.ad.osync.test.task is executed or not.
     * This method is set as the default method because the user wants to return true by default, that is, the default selection is executed.
     *
     * @param param   the param
     * @param support the support
     * @return boolean boolean
     */
    default boolean necessary(Param param, TaskContext support) {
        return true;
    }

    /**
     * Task Executed Successfully
     *
     * @param support the support
     */
    default void onSuccess(TaskContext support) {
    }

    /**
     * Task execution failure
     *
     * @param support the support
     */
    default void onFail(TaskContext support, Exception exception) {
    }


    /**
     * rollback
     * Rewrite the method to complete the com.heytap.ad.osync.com.heytap.ad.osync.test.task com.heytap.ad.osync.com.heytap.ad.osync.test.task Equivalent to TCC's two-phase submission transaction compensation
     *
     * @param param the param
     */
    default void rollback(Param param) {

    }
}
