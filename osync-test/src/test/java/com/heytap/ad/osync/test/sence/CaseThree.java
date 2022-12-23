package com.heytap.ad.osync.test.sence;

import com.heytap.ad.osync.core.Osync;
import com.heytap.ad.osync.test.OsyncTestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: gobrs-async
 * @ClassName CaseOne
 * @description:
 **/
@SpringBootTest(classes = OsyncTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CaseThree {

    /**
     * A -> B,C,D
     */

    @Autowired
    private Osync osync;


    @Test
    public void caseThree() {
        Map<String, Object> params = new HashMap<>();
        osync.go("caseThree", () -> params);

    }

}
