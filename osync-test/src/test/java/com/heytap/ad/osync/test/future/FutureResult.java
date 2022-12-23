package com.heytap.ad.osync.test.future;

import com.heytap.ad.osync.core.Osync;
import com.heytap.ad.osync.test.OsyncTestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Future result.
 *
 * @program: gobrs -async
 * @ClassName FutureResult
 * @description:
 */
@SpringBootTest(classes = OsyncTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FutureResult {

    @Autowired
    private Osync osync;

    /**
     * 获取非强依赖任务的返回结果  如下： C任务获取A任务的返回结果， 从任务配置上看 C并不需要A 执行完成后再执行C 所有 通过Future方式 C有能力获取到A的返回结果
     * Future task rule.
     * - name: "futureTaskRule"
     *   content: "futureTaskA->futureTaskB;futureTaskC->futureTaskD"
     *   task-interrupt: true # 局部异常是否打断主流程 默认 false
     */
    @Test
    public void futureTaskRule() {
        Map<String, Object> params = new HashMap<>();
        osync.go("futureTaskRule", () -> params);
    }

}
