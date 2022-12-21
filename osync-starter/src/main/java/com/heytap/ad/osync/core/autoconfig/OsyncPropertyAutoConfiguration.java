package com.heytap.ad.osync.core.autoconfig;

import com.heytap.ad.osync.core.property.RuleConfig;
import com.heytap.ad.osync.core.config.OsyncConfig.ThreadPoolProperties;

import java.util.Objects;

import com.heytap.ad.osync.core.config.OsyncRule;
import com.heytap.ad.osync.core.property.OsyncProperties;
import com.heytap.ad.osync.core.property.LogConfig;
import com.heytap.ad.osync.core.config.OsyncConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class OsyncPropertyAutoConfiguration {


    /**
     * Gobrs config gobrs config.
     *
     * @param properties the properties
     * @return the gobrs config
     */
    @Bean
    public OsyncConfig osyncConfig(OsyncProperties properties) {

        OsyncConfig osyncConfig = new OsyncConfig();

        osyncConfig.setEnable(properties.isEnable());
        osyncConfig.setSplit(properties.getSplit());
        osyncConfig.setPoint(properties.getPoint());
        osyncConfig.setParamContext(osyncConfig.isParamContext());
        osyncConfig.setTimeout(properties.getTimeout());
        osyncConfig.setRelyDepend(properties.isRelyDepend());
        osyncConfig.setTimeoutCoreSize(properties.getTimeoutCoreSize());

        threadPool(properties, osyncConfig);

        List<RuleConfig> rules = properties.getRules();
        List<OsyncRule> rList = rules.stream().map(x -> {
            OsyncRule r = new OsyncRule();
            LogConfig logConfig = x.getLogConfig();

            if (Objects.nonNull(logConfig)) {
                r.setLogError(logConfig.getlogError());
                r.setLogCostTime(logConfig.getLogCostTime());
            }

            r.setName(x.getName());
            r.setContent(x.getContent());
            r.setTaskInterrupt(x.isTaskInterrupt());
            r.setTransaction(x.isTransaction());
            return r;
        }).collect(Collectors.toList());
        osyncConfig.setRules(rList);
        return osyncConfig;
    }

    /**
     * @param properties
     * @param osyncConfig
     */
    private void threadPool(OsyncProperties properties, OsyncConfig osyncConfig) {
        OsyncProperties.ThreadPoolProperties threadPoolProperties = properties.getThreadPoolProperties();
        if (Objects.nonNull(threadPoolProperties)) {
            ThreadPoolProperties tp = new ThreadPoolProperties();
            tp.setCorePoolSize(threadPoolProperties.getCorePoolSize());
            tp.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
            tp.setKeepAliveTime(threadPoolProperties.getKeepAliveTime());
            tp.setTimeUnit(threadPoolProperties.getTimeUnit());
            tp.setExecuteTimeOut(threadPoolProperties.getExecuteTimeOut());
            tp.setCapacity(threadPoolProperties.getCapacity());
            tp.setWorkQueue(threadPoolProperties.getWorkQueue());
            tp.setThreadNamePrefix(threadPoolProperties.getThreadNamePrefix());
            tp.setAllowCoreThreadTimeOut(threadPoolProperties.getAllowCoreThreadTimeOut());
            tp.setRejectedExecutionHandler(threadPoolProperties.getRejectedExecutionHandler());
            osyncConfig.setThreadPoolProperties(tp);
        }
    }
}
