package com.heytap.ad.osync.core.common.domain;

import com.heytap.ad.osync.core.common.enums.ResultState;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Single com.heytap.ad.osync.com.heytap.ad.osync.test.task execution result encapsulation
 * 任务结果封装
 *
 * @param <V> the type parameter
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class TaskResult<V> {
    private V result;

    private ResultState resultState;

    private Exception ex;

    public TaskResult(V result, ResultState resultState) {
        this(result, resultState, null);
    }

    public TaskResult(V result, ResultState resultState, Exception ex) {
        if (result instanceof AnyConditionResult) {
            this.result = (V) ((AnyConditionResult<?>) result).getResult();
        } else {
            this.result = result;
        }
        this.resultState = resultState;
        this.ex = ex;
    }

}
