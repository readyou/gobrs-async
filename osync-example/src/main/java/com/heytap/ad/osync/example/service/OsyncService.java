package com.heytap.ad.osync.example.service;

import com.heytap.ad.osync.core.Osync;
import com.heytap.ad.osync.core.common.domain.AsyncResult;
import com.heytap.ad.osync.core.config.OsyncRule;
import com.heytap.ad.osync.core.engine.RuleUpdaterImpl;
import com.heytap.ad.osync.test.task.condition.AServiceCondition;
import com.heytap.ad.osync.test.task.condition.CServiceCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The type Gobrs service.
 *
 * @program: gobrs -async-core
 * @ClassName GobrsService
 * @description:
 */
@Service
public class OsyncService {


    @Autowired(required = false)
    private Osync osync;

    @Autowired(required = false)
    private RuleUpdaterImpl ruleThermalLoad;


    /**
     * Performance test.
     */
    public void performanceTest() {
        osync.go("performance", () -> "");
    }


    /**
     * Gobrs async.
     */
    public void osync() {
        osync.go("test", () -> new Object());
    }

    /**
     * Gobrs test async result.
     *
     * @return the async result
     */
    public AsyncResult osyncTest() {
        Map<Class, Object> params = new HashMap<>();
        params.put(AServiceCondition.class, "1");
        params.put(CServiceCondition.class, "2");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        AsyncResult resp = osync.go("general", () -> params);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
        return resp;
    }

    /**
     * gobrsTest 可以使用 jmeter 一直访问着
     * 然后在浏览器调用 http://localhost:9999/gobrs/updateRule  看规则变更效果
     */
    public void updateRule() {
        OsyncRule r = new OsyncRule();
        r.setName("anyConditionGeneral");
        r.setContent("AService->CService->EService->GService; BService->DService->FService->HService;");
        ruleThermalLoad.load(r);
    }

}
