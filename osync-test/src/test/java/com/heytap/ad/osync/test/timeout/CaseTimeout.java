package com.heytap.ad.osync.test.timeout;

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
 * @ClassName CaseTimeout
 * @description:
 **/
@SpringBootTest(classes = OsyncTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CaseTimeout {


    @Autowired(required = false)
    private Osync osync;

    /**
     * Tcase.
     */
    @Test
    public void timeoutTest() {
        Map<Class, Object> params = new HashMap<>();
        AsyncResult asyncResult = osync.go("timeoutRule", () -> params, 300000);
    }

}
