package com.heytap.ad.osync.core.log;

import com.heytap.ad.osync.core.common.util.SystemClock;

import java.util.*;

/**
 * The type Log creator.
 *
 * @program: gobrs -async
 * @ClassName LogCreator
 * @description:
 */
public class LogCreator {

    /**
     * Create com.heytap.ad.osync.com.heytap.ad.osync.test.task com.heytap.ad.osync.log string.
     *
     * @param logTracer the com.heytap.ad.osync.log tracer
     * @return the string
     */
    public String createTaskLog(LogTracer logTracer) {
        // todo 构建每个任务的日志信息
        return null;

    }

    /**
     * Process logs string
     *
     * @param logWrapper the log wrapper
     * @return the string
     */
    public static String processLogs(LogWrapper logWrapper) {
        StringBuilder printContent = new StringBuilder();
        List<LogTracer> logTracerList = new ArrayList<>();
        Long processStartTime = logWrapper.getTimeCollector().getStartTime();
        logWrapper.getTracerQueue().drainTo(logTracerList);
        printContent
                .append("【ProcessTrace】")
                .append("Total cost: ")
                .append(SystemClock.now() - processStartTime)
                .append("ms")
                .append(" | ")
                .append("traceId = ")
                .append(logWrapper.getTraceId())
                .append(" | ");
        for (LogTracer tracer : logTracerList) {
            printContent = printContent
                    .append("【task】")
                    .append(tracer.getTaskName())
                    .append(" cost ")
                    .append(":")
                    .append(tracer.getTaskCost())
                    .append("ms");

            if (!tracer.getExecuteState()) {
                printContent.append("【state】：")
                        .append("fail")
                        .append("【errMsg】: ")
                        .append(tracer.getErrorMessage());
            } else {
                printContent.append("【state】：")
                        .append("success");
            }
            printContent.append("; ->");
        }

        return printContent.substring(0, printContent.length() - 2);
    }

}
