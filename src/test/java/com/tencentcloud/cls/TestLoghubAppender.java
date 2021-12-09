package com.tencentcloud.cls;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import com.tencentcloudapi.cls.producer.AsyncProducerConfig;
import org.junit.AfterClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import static org.junit.Assert.assertNotEquals;
import org.slf4j.MDC;

public class TestLoghubAppender {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestLoghubAppender.class);

//    @AfterClass
//    public static void checkStatusList() {
//        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
//        StatusManager statusManager = lc.getStatusManager();
//        List<Status> statusList = statusManager.getCopyOfStatusList();
//        for (Status status : statusList) {
//            int level = status.getLevel();
//            assertNotEquals(status.getMessage(), Status.ERROR, level);
//            assertNotEquals(status.getMessage(), Status.WARN, level);
//        }
//    }
//
//    @Test
//    public void tesWarnLogMessage() {
//        LOGGER.warn("This is a test warn message logged by logback.");
//    }
//
//    @Test
//    public void testLogThrowable() {
//        MDC.put("MDC_KEY","MDC_VALUE");
//        MDC.put("THREAD_ID", String.valueOf(Thread.currentThread().getId()));
//        LOGGER.error("This is a test error message logged by logback.",
//                new UnsupportedOperationException("Logback UnsupportedOperationException"));
//    }
}
