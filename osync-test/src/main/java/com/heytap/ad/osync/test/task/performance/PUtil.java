package com.heytap.ad.osync.test.task.performance;

import java.util.Random;

public class PUtil {
    private static Random random = new Random();

    private static int ms = 10;
    public static void setMs(int m) {
        ms = m;
    }

    public static void sleep() {
        try {
            Thread.sleep((int) ms);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
