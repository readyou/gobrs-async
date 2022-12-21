package com.heytap.ad.osync.core.log;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * The type Log wrapper.
 *
 * @program: gobrs -async
 * @ClassName LogWrapper
 * @description:
 */
@Accessors(chain = true)
@Data
public class LogWrapper {

    private String traceId;

    private final LinkedBlockingQueue<LogTracer> tracerQueue = new LinkedBlockingQueue<>();

    private TimeCollector timeCollector;

    private Long processCost;


    /**
     * Add trace.
     *
     * @param logTracer the log tracer
     */
    public void addTrace(LogTracer logTracer) {
        tracerQueue.add(logTracer);
    }

    /**
     * The type Time collector.
     */
    @Builder
    @Data
    public static class TimeCollector {

        private Long startTime;

        private Long endTime;
    }

    @Override
    public String toString() {
        return "LogWrapper{" +
                "traceId=" + traceId +
                ", tracerQueue=" + tracerQueue +
                ", timeCollector=" + timeCollector +
                ", processCost=" + processCost +
                '}';
    }
}
