package com.heytap.ad.osync.test.performance;

import com.heytap.ad.osync.core.Osync;
import com.heytap.ad.osync.core.common.domain.AsyncResult;
import com.heytap.ad.osync.test.OsyncTestApplication;
import com.heytap.ad.osync.test.task.performance.PUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(classes = OsyncTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class CasePerformance {

    @Resource
    private Osync osync;

    /**
     * Tcase.
     */
    @Test
    public void performanceTest() {
        long start = System.currentTimeMillis();
        osync.go("performance", () -> "args");
        System.out.println("耗时" + (System.currentTimeMillis() - start));
    }

    @Test
    @SneakyThrows
    public void bench() {
        int n = 50000;
        PUtil.setMs(5);
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
                AsyncResult result = null;
                try {
                    result = osync.go(name, () -> "args");
//                    log.info("result: {}", result.getResultMap());
                } catch (Exception e) {
                    log.error("", e);
                }
                latch.countDown();
            });
        }
        latch.await();
        long cost = System.currentTimeMillis() - start;
        log.info("name: {}, 耗時: {} ms, qps={}", name, cost, (n * 1000) / cost);
    }
}
