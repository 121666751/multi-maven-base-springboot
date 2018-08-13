package com.multi.maven.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author: litao
 * @see:
 * @description:
 * @since:
 * @param:
 * @return:
 * @date Created by leole on 2018/8/3.
 */
public class LogTraceUtil {
    public LogTraceUtil() {
    }

    public static void putLogTrace(LogTraceUtil.LogTrace trace, String value) {
        MDC.put(trace.name(), value);
    }

    public static String getLogTraceAll() {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return JsonUtil.toJson(contextMap);
    }

    public static void setLogTraceAll(String traceJson) {
        if (!StringUtils.isBlank(traceJson)) {
            Map<String, Object> contextMap = JsonUtil.fromJson(traceJson);
            if (contextMap != null) {
                Set<Map.Entry<String, Object>> entrySet = contextMap.entrySet();
                Iterator var4 = entrySet.iterator();

                while (var4.hasNext()) {
                    Map.Entry<String, ?> entry = (Map.Entry) var4.next();
                    MDC.put((String) entry.getKey(), String.valueOf(entry.getValue()));
                }

            }
        }
    }

    public static void clearMDC() {
        MDC.clear();
    }

    public static String getLogTrace(LogTraceUtil.LogTrace trace) {
        return MDC.get(trace.name());
    }

    public static enum LogTrace {
        MODEL,
        SERVLETPATH,
        UUID;

        private LogTrace() {
        }
    }
}
