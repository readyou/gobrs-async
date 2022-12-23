package com.heytap.ad.osync.core.callback;

import com.heytap.ad.osync.core.TaskContext;
import com.heytap.ad.osync.core.task.AsyncTask;
import com.heytap.ad.osync.core.common.domain.AsyncParamSupply;

public class ErrorCallback<Param> {

    /**
     * The Param.
     */
    AsyncParamSupply<Param> param;
    /**
     * The Exception.
     */
    Exception exception;
    /**
     * The Support.
     */
    TaskContext support;
    /**
     * The Task.
     */
    AsyncTask task;


    /**
     * Instantiates a new Error com.heytap.ad.osync.callback.
     *
     * @param param     the param
     * @param exception the com.heytap.ad.osync.exception
     * @param support   the support
     * @param task      the com.heytap.ad.osync.com.heytap.ad.osync.test.task
     */
    public ErrorCallback(AsyncParamSupply param, Exception exception, TaskContext support, AsyncTask task) {
        this.param = param;
        this.exception = exception;
        this.support = support;
        this.task = task;
    }

    /**
     * Gets param.
     *
     * @return the param
     */
    public AsyncParamSupply<Param> getParam() {
        return param;
    }

    /**
     * Sets param.
     *
     * @param param the param
     */
    public void setParam(AsyncParamSupply param) {
        this.param = param;
    }

    /**
     * Gets throwable.
     *
     * @return the throwable
     */
    public Exception getThrowable() {
        return exception;
    }

    /**
     * Sets throwable.
     *
     * @param throwable the throwable
     */
    public void setThrowable(Exception throwable) {
        this.exception = throwable;
    }

    /**
     * Gets support.
     *
     * @return the support
     */
    public TaskContext getSupport() {
        return support;
    }

    /**
     * Sets support.
     *
     * @param support the support
     */
    public void setSupport(TaskContext support) {
        this.support = support;
    }

    /**
     * Gets com.heytap.ad.osync.com.heytap.ad.osync.test.task.
     *
     * @return the com.heytap.ad.osync.com.heytap.ad.osync.test.task
     */
    public AsyncTask getTask() {
        return task;
    }

    /**
     * Sets com.heytap.ad.osync.com.heytap.ad.osync.test.task.
     *
     * @param task the com.heytap.ad.osync.com.heytap.ad.osync.test.task
     */
    public void setTask(AsyncTask task) {
        this.task = task;
    }




}
