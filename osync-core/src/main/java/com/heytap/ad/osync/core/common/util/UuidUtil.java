package com.heytap.ad.osync.core.common.util;

import java.security.InvalidParameterException;
import java.util.UUID;

public class UuidUtil {
    public static String uuid(int len) {
        String id = uuid();
        if (len <= 0 || len > id.length()) {
            throw new InvalidParameterException("len should between (0, 32]");
        }
        return id.substring(32 - len);
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static void main(String[] args) {
        System.out.println(uuid());
        System.out.println(uuid(3));
        System.out.println(uuid(32));
        System.out.println(uuid(0));
    }
}
