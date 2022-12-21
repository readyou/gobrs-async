package com.heytap.ad.osync.test;

import com.heytap.ad.osync.core.config.OsyncRule;
import com.heytap.ad.osync.core.engine.RuleUpdater;
import com.heytap.ad.osync.core.engine.RuleUpdaterImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * The type Case thread pool com.heytap.ad.osync.config.
 *
 * @program: gobrs -async
 * @ClassName CaseThreadPoolConfig
 * @description:
 */
@SpringBootTest
public class RuleConfigUpdate {

    @Autowired(required = false)
    private RuleUpdater ruleUpdater;


    @Test
    public void updateRule() {
        OsyncRule r = new OsyncRule();
        r.setName("anyConditionGeneral");
        r.setContent("AService->CService->EService->GService; BService->DService->FService->HService;");
        ruleUpdater.load(r);
    }
}
