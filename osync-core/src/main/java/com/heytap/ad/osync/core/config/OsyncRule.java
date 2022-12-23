package com.heytap.ad.osync.core.config;

import com.heytap.ad.osync.core.common.def.DefaultConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class OsyncRule {

    private String name;

    private String content;


    /**
     * 执行异常trace log打印
     */
    private Boolean logError = DefaultConfig.LOG_ERROR;

    /**
     * 任务执行过程中耗时打印
     */
    private Boolean logCostTime = DefaultConfig.LOG_COST_TIME;

    /**
     * Whether the execution com.heytap.ad.osync.exception interrupts the workflow
     * 任务流程 某任务中断是否终止整个任务流程
     */
    private boolean taskInterrupt = false;

    /**
     * 流程事务
     */
    boolean transaction = false;


}
