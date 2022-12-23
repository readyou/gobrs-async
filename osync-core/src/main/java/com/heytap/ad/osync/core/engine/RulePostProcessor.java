package com.heytap.ad.osync.core.engine;

import com.heytap.ad.osync.core.Osync;
import com.heytap.ad.osync.core.common.exception.NotFoundRuleException;
import com.heytap.ad.osync.core.config.ConfigManager;
import com.heytap.ad.osync.core.config.OsyncConfig;
import com.heytap.ad.osync.core.config.OsyncRule;
import com.heytap.ad.osync.core.holder.BeanHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class RulePostProcessor implements ApplicationListener<ApplicationReadyEvent> {
    private final Logger logger = LoggerFactory.getLogger(RulePostProcessor.class);

    private ConfigManager configManager;

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public RulePostProcessor(ConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (initialized.compareAndSet(false, true)) {
            init();
        }
    }

    private void init() {
        OsyncConfig properties = BeanHolder.getBean(OsyncConfig.class);
        Osync osync = BeanHolder.getBean(Osync.class);
        List<OsyncRule> rules = properties.getRules();
        RuleEngine engine = BeanHolder.getBean(RuleEngine.class);
        Optional.ofNullable(rules).map((data) -> {
            /**
             * The primary purpose of resolving a com.heytap.ad.osync.rule is to check that the com.heytap.ad.osync.rule is correct
             * Extensible com.heytap.ad.osync.com.heytap.ad.osync.test.task flow resolution up
             *
             *  recommend : Custom rules com.heytap.ad.osync.engine can be extended using SPI
             */
            try {
                for (OsyncRule rule : data) {
                    configManager.addRule(rule.getName(), rule);
                    engine.doParse(rule, false);
                    osync.initTrigger(rule.getName());
                }
            } catch (Exception exception) {
                logger.error("RulePostProcessor parse error{}", exception);
                throw exception;
            }
            logger.info("Osync Load Successful");
            return 1;
        }).orElseThrow(() -> new NotFoundRuleException("com.heytap.ad.osync.rule parse error"));
    }
}
