package com.heytap.ad.osync.core.property;

/**
 * @program: gobrs-async
 * @ClassName LogConfig
 * @description:
 **/

import com.heytap.ad.osync.core.common.def.DefaultConfig;

/**
 * The type Log config.
 */
public class LogConfig {
    /**
     * 执行异常trace log打印
     */
    private Boolean logError = DefaultConfig.LOG_ERROR;
    /**
     * 任务执行过程中耗时打印
     */
    private Boolean logCostTime = DefaultConfig.LOG_COST_TIME;

    /**
     * Gets err logabled.
     *
     * @return the err logabled
     */
    public Boolean getlogError() {
        return logError;
    }

    /**
     * Sets err logabled.
     *
     * @param logError the err logabled
     */
    public void setLogError(Boolean logError) {
        this.logError = logError;
    }

    /**
     * Gets cost logabled.
     *
     * @return the cost logabled
     */
    public Boolean getLogCostTime() {
        return logCostTime;
    }

    /**
     * Sets cost logabled.
     *
     * @param logCostTime the cost logabled
     */
    public void setLogCostTime(Boolean logCostTime) {
        this.logCostTime = logCostTime;
    }
}
