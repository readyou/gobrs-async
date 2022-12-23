package com.heytap.ad.osync.core.engine;

import com.heytap.ad.osync.core.Osync;
import com.heytap.ad.osync.core.config.OsyncRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

public class RuleUpdaterImpl implements RuleUpdater {

    Logger logger = LoggerFactory.getLogger(RuleUpdaterImpl.class);
    @Resource
    private RuleEngine ruleEngine;

    @Resource
    private Osync osync;

    @Override
    public void load(OsyncRule rule) {
        try {
            ruleEngine.doParse(rule, true);
            osync.initTrigger(rule.getName(), true);
            logger.info("osync.rule {} update success", rule.getName());
        } catch (Exception ex) {
            logger.error("osync.rule {} update fail {}", rule.getName(), ex);
        }
    }


    @Override
    public void load(List<OsyncRule> ruleList) {
        ruleList.stream().parallel().forEach(x -> {
            try {
                ruleEngine.doParse(x, true);
                osync.initTrigger(x.getName(), true);
                logger.info("osync.rule {} update success !!!", x.getName());
            } catch (Exception ex) {
                logger.error("osync.rule {} update fail", x.getName(), ex);
            }
        });
    }
}
