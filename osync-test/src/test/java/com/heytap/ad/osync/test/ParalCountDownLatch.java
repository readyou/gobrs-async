package com.heytap.ad.osync.test;

import com.heytap.ad.osync.core.Osync;
import com.heytap.ad.osync.core.common.domain.AsyncResult;
import com.heytap.ad.osync.core.common.domain.TaskResult;
import com.heytap.ad.osync.test.task.AService;
import com.heytap.ad.osync.test.task.CService;
import com.heytap.ad.osync.test.task.condition.CServiceCondition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @program: gobrs-async
 * @ClassName ThreadLocalTest
 * @description:
 **/
@SpringBootTest(classes = OsyncTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ParalCountDownLatch {

    @Autowired(required = false)
    private Osync osync;

    public Integer count = 1000;

    public static ExecutorService executorService = Executors.newCachedThreadPool();

    @Test
    public void threadLocalTest(){
        Set<String> cases = new HashSet<>();
        cases.add("BService");
        cases.add("GService");

        Map<Class, Object> params = new HashMap<>();
        params.put(AService.class, "1");
        params.put(CService.class, "2");

        CountDownLatch countDownLatch = new CountDownLatch(count);

        for (int i = 0; i < count; i++) {
            executorService.submit(() -> {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                AsyncResult result = osync.go("general", () -> params, 10000);
                stopWatch.stop();
                System.out.println(stopWatch.getTotalTimeMillis());
                TaskResult taskResult = result.getResultMap().get(CServiceCondition.class);
                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await(100, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("【gobrs-async】 testCondition 执行完成");

    }

}
