package com.heytap.ad.osync.core.config;

import com.heytap.ad.osync.core.common.def.DefaultConfig;
import com.heytap.ad.osync.core.common.exception.OsyncException;
import com.heytap.ad.osync.core.holder.BeanHolder;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * The type Config manager.
 *
 * @program: gobrs -async
 * @ClassName ConfigManager
 * @description:
 */
public class ConfigManager {


    /**
     * The constant configFactory.
     */
    public static ConfigFactory configFactory = BeanHolder.getBean(ConfigFactory.class);

    /**
     * Config instance config factory.
     *
     * @return the config factory
     */
    public static ConfigFactory configInstance() {
        return checkAndGet();
    }

    /**
     * Gets global config.
     *
     * @return the global config
     */
    public static OsyncConfig getGlobalConfig() {
        return checkAndGet().getGobrsConfig();
    }

    /**
     * Gets rule.
     *
     * @param ruleName the rule name
     * @return the rule
     */
    public static OsyncRule getRule(String ruleName) {
        return checkAndGet().getProcessRules().get(ruleName);
    }


    /**
     * @return
     */
    private static ConfigFactory checkAndGet() {
        if (Objects.isNull(configFactory)) {
            configFactory = BeanHolder.getBean(ConfigFactory.class);
            if (Objects.isNull(configFactory)) {
                throw new OsyncException("ConfigFactory is null");
            }
        }
        return configFactory;
    }

    /**
     * Gets config.
     *
     * @return the config
     */
    public OsyncConfig getConfig() {
        return checkAndGet().getGobrsConfig();
    }

    /**
     * Add rule.
     *
     * @param ruleName the rule name
     * @param rule     the rule
     */
    public void addRule(String ruleName, OsyncRule rule) {
        configFactory.addRule(ruleName, rule);
    }


    /**
     * The type Action.
     */
    public static class Action {
        /**
         * Logable boolean.
         * 执行异常 打印
         * 包含traceId
         *
         * @param ruleName the rule name
         * @return the boolean
         */
        public static boolean logError(String ruleName) {
            return defaultRule(() -> getRule(ruleName).getLogError(), DefaultConfig.LOG_ERROR);
        }

        /**
         * Cost boolean.
         * 执行链路打印
         * 执行时长
         *
         * @param ruleName the rule name
         * @return the boolean
         */
        public static boolean logCostTime(String ruleName) {
            return defaultRule(() -> getRule(ruleName).getLogCostTime(), DefaultConfig.LOG_COST_TIME);
        }

        /**
         * Default rule boolean.
         *
         * @param action       the action
         * @param defaultValue the default value
         * @return the boolean
         */
        public static Boolean defaultRule(Supplier<Boolean> action, Boolean defaultValue) {
            try {
                return action.get();
            } catch (Exception ex) {
                return defaultValue;
            }
        }

    }

}
