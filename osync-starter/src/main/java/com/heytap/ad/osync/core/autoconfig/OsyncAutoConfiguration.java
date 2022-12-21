package com.heytap.ad.osync.core.autoconfig;

import com.heytap.ad.osync.core.Osync;
import com.heytap.ad.osync.core.TaskAdj;
import com.heytap.ad.osync.core.callback.*;
import com.heytap.ad.osync.core.config.ConfigFactory;
import com.heytap.ad.osync.core.config.ConfigManager;
import com.heytap.ad.osync.core.config.OsyncConfig;
import com.heytap.ad.osync.core.engine.*;
import com.heytap.ad.osync.core.property.OsyncProperties;
import com.heytap.ad.osync.core.holder.BeanHolder;
import com.heytap.ad.osync.core.threadpool.OsyncThreadPoolFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


@Configuration
@AutoConfigureAfter({OsyncPropertyAutoConfiguration.class})
@ConditionalOnProperty(prefix = OsyncProperties.PREFIX, value = "enable", matchIfMissing = true, havingValue = "true")
@Import(BeanHolder.class)
@ComponentScan(value = {"com.heytap.ad.osync"})
public class OsyncAutoConfiguration {
    public OsyncAutoConfiguration() {
    }

    private OsyncConfig osyncConfig;


    public OsyncAutoConfiguration(OsyncConfig osyncConfig) {
        this.osyncConfig = osyncConfig;
    }

    @Bean
    public TaskAdj taskFlow() {
        return new TaskAdj();
    }

    @Bean
    public OsyncThreadPoolFactory osyncThreadPoolFactory(OsyncConfig osyncConfig) {
        return new OsyncThreadPoolFactory(osyncConfig);
    }


    @Bean
    @ConditionalOnMissingBean(value = RuleEngine.class)
    public RuleEngine ruleEngine() {
        return new RuleEngineImpl<>();
    }

    @Bean
    public ConfigFactory configFactory(OsyncConfig osyncConfig) {
        return new ConfigFactory(osyncConfig);
    }

    @ConditionalOnBean(ConfigFactory.class)
    @Bean
    public ConfigManager configManager() {
        return new ConfigManager();
    }


    @Bean
    public RulePostProcessor ruleEnginePostProcessor(ConfigManager configManager) {
        return new RulePostProcessor(configManager);
    }


    @Bean
    public Osync osync() {
        return new Osync();
    }

    @Bean
    public BeanHolder osyncSpring() {
        return new BeanHolder();
    }

    @Bean
    @ConditionalOnMissingBean(value = AsyncTaskExceptionInterceptor.class)
    public AsyncTaskExceptionInterceptor asyncExceptionInterceptor() {
        return new DefaultAsyncExceptionInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean(value = AsyncTaskPreInterceptor.class)
    public AsyncTaskPreInterceptor asyncTaskPreInterceptor() {
        return new DefaultAsyncTaskPreInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean(value = AsyncTaskPostInterceptor.class)
    public AsyncTaskPostInterceptor asyncTaskPostInterceptor() {
        return new DefaultAsyncTaskPostInterceptor();
    }

    @Bean
    public RuleUpdater ruleThermalLoading() {
        return new RuleUpdaterImpl();
    }
}
