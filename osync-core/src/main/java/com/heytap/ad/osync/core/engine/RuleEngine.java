package com.heytap.ad.osync.core.engine;


import com.heytap.ad.osync.core.config.OsyncRule;

public interface RuleEngine {
    /**
     * Parse.
     *
     * @param ruleJsonStr the ruleJsonStr
     * @return
     */
    void parse(String ruleJsonStr);

    /**
     * Do parse.
     *
     * @param r      the r
     * @param reload the reload
     */
    void doParse(OsyncRule r, boolean reload);

}
