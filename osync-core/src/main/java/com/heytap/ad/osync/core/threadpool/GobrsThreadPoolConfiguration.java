package com.heytap.ad.osync.core.threadpool;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;


public abstract class GobrsThreadPoolConfiguration implements InitializingBean {

    @Autowired(required = false)
    private OsyncThreadPoolFactory osyncThreadPoolFactory;

    public void initialize(OsyncThreadPoolFactory factory) {
        doInitialize(factory);
    }

    protected abstract void doInitialize(OsyncThreadPoolFactory factory);

    @Override
    public void afterPropertiesSet() throws Exception {
        if (Objects.nonNull(osyncThreadPoolFactory)) {
            initialize(osyncThreadPoolFactory);
        }
    }
}
