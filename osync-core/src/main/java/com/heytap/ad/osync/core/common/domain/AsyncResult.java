package com.heytap.ad.osync.core.common.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class AsyncResult {

    /**
     * 整流程 执行结果code
     */
    private Integer code;

    /**
     * 整流程 执行是否成功
     */
    private boolean success;

    /**
     * 执行结果封装
     * key com.heytap.ad.osync.test.task 类
     * value 执行结果 （单任务）
     */
    private Map<Class, TaskResult> resultMap = new ConcurrentHashMap<>();

}
