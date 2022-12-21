package com.heytap.ad.osync.test;

import com.heytap.ad.osync.core.Osync;
import com.heytap.ad.osync.core.common.domain.AsyncResult;
import com.heytap.ad.osync.test.task.AService;
import com.heytap.ad.osync.test.task.CService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

/**
 * The type Case general.
 *
 * @program: gobrs -async
 * @ClassName CaseOne
 * @description:
 */
@SpringBootTest(classes = OsyncTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CaseGeneral {


    @Autowired(required = false)
    private Osync osync;

    /**
     * Tcase.
     */
    @Test
    public void tcase() {
        Set<String> cases = new HashSet<>();
        cases.add("BService");
        cases.add("GService");

        Map<Class, Object> params = new HashMap<>();
        params.put(AService.class, "1");
        params.put(CService.class, "2");

        AsyncResult asyncResult = osync.go("general", () -> params, 300000);
    }

}
