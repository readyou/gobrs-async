package com.heytap.ad.osync.core.engine;
import com.heytap.ad.osync.core.config.OsyncRule;

import java.util.List;

/**
 * Rule的动态更新器，用于配置变化时，重新加载相关配置对应的Rule
 */
public interface RuleUpdater {


    /**
     * Load.
     *
     * @param rule the com.heytap.ad.osync.rule
     */
    void load(OsyncRule rule);

    /**
     * Load.
     *
     * @param ruleList the com.heytap.ad.osync.rule list
     */
    void load(List<OsyncRule> ruleList);


}
