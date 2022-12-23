package com.heytap.ad.osync.core.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class RuleConfig {

    private String name;

    private String content;

    private LogConfig logConfig;

    /**
     * 任务流程 某任务中断是否终止整个任务流程
     */
    private boolean taskInterrupt = false;

    /**
     * 流程事务
     */
    boolean transaction = false;

}
