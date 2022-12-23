package com.heytap.ad.osync.test.interrupt;

import com.heytap.ad.osync.core.Osync;
import com.heytap.ad.osync.core.common.domain.AsyncResult;
import com.heytap.ad.osync.test.OsyncTestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: gobrs-async
 * @ClassName InterruptRule
 * @description:
 **/
@SpringBootTest(classes = OsyncTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InterruptRule {
    @Autowired(required = false)
    private Osync osync;
    @Test
    public void testInterruptRule() {
        Map<Class, Object> params = new HashMap<>();

        AsyncResult result = osync.go("taskInterrupt", () -> params, 300000);

    }
}
