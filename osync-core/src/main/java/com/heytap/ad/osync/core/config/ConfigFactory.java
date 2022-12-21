package com.heytap.ad.osync.core.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigFactory {

    /**
     * 任务流程独特配置
     */
    private Map<String, OsyncRule> processRules = new ConcurrentHashMap();

    /**
     * 全局配置
     */
    private OsyncConfig osyncConfig;

    /**
     * Gets process rules.
     *
     * @return the process rules
     */
    public Map<String, OsyncRule> getProcessRules() {
        return processRules;
    }

    /**
     * Sets process rules.
     *
     * @param processRules the process rules
     */
    public void setProcessRules(Map<String, OsyncRule> processRules) {
        this.processRules = processRules;
    }

    /**
     * Gets gobrs async properties.
     *
     * @return the gobrs async properties
     */
    public OsyncConfig getGobrsConfig() {
        return osyncConfig;
    }

    /**
     * Sets gobrs async properties.
     */
    public void setOsyncProperties(OsyncConfig osyncConfig) {
        this.osyncConfig = osyncConfig;
    }

    /**
     * Instantiates a new Config cache manager.
     *
     * @param osyncConfig the gobrs async properties
     */
    public ConfigFactory(OsyncConfig osyncConfig) {
        this.osyncConfig = osyncConfig;
    }

    /**
     * Add rule rule.
     *
     * @param ruleName the rule name
     * @param rule     the rule
     * @return the rule
     */
    public OsyncRule addRule(String ruleName, OsyncRule rule) {
        return this.processRules.put(ruleName, rule);
    }
}
