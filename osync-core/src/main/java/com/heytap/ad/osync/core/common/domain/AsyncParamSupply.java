package com.heytap.ad.osync.core.common.domain;

@FunctionalInterface
public interface AsyncParamSupply<T> {
    T get();
}
