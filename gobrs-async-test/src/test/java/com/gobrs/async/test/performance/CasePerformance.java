package com.gobrs.async.test.performance;

import com.gobrs.async.core.GobrsAsync;
import com.gobrs.async.core.common.domain.AsyncResult;
import com.gobrs.async.test.GobrsAsyncTestApplication;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: gobrs-async
 * @ClassName CaseTimeout
 * @description:
 * @author: sizegang
 * @create: 2022-12-09
 **/
@SpringBootTest(classes = GobrsAsyncTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class CasePerformance {

    @Resource
    private GobrsAsync gobrsAsync;

    /**
     * Tcase.
     */
    @Test
    public void performanceTest() {
        long start = System.currentTimeMillis();
        gobrsAsync.go("performance", () -> "args");
        System.out.println("耗时" + (System.currentTimeMillis() - start));
    }

    @Test
    @SneakyThrows
    public void bench() {
        int n = 50000;
        benchTest(n, "performance");
        benchTest(n, "performance1");
        benchTest(n, "performance2");
        benchTest(n, "performance3");
        benchTest(n, "performance4");
    }

    @SneakyThrows
    public void benchTest(int n, String name) {
        long start = System.currentTimeMillis();
        var latch = new CountDownLatch(n);
        ExecutorService executorService = Executors.newFixedThreadPool(200);
        for (int i = 0; i < n; i++) {
            executorService.submit(() -> {
                AsyncResult result = gobrsAsync.go("performance", () -> "args");
//                log.info("result: {}", result.getResultMap());
                latch.countDown();
            });
        }
        latch.await();
        long cost = System.currentTimeMillis() - start;
        log.info("name: {}, 耗時: {} ms, qps={}", name, cost, (n * 1000) / cost);
    }
}
