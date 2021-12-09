package com.tencentcloudapi.cls;

import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import com.tencentcloudapi.cls.producer.AsyncProducerClient;
import com.tencentcloudapi.cls.producer.AsyncProducerConfig;
import com.tencentcloudapi.cls.producer.Result;
import com.tencentcloudapi.cls.producer.common.LogItem;
import com.tencentcloudapi.cls.producer.errors.ProducerException;
import com.tencentcloudapi.cls.producer.util.NetworkUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author farmerx
 * @param <E>
 */
public class LoghubAppender<E> extends UnsynchronizedAppenderBase<E> {

    private String topicId;
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String userAgent = "logback";
    private String source = "";
    private String timeFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
    private String timeZone = "UTC";
    private String totalSizeInBytes;
    private String maxBlockMs;
    private String sendThreadCount;
    private String batchSizeThresholdInBytes;
    private String batchCountThreshold;
    private String lingerMs;
    private String retries;
    private String maxReservedAttempts;
    private String baseRetryBackoffMs;
    private String maxRetryBackoffMs;

    private AsyncProducerClient producer;

    private AsyncProducerConfig producerConfig;

    private DateTimeFormatter formatter;

    protected Encoder<E> encoder;

    private String mdcFields;

    public String getEndpoint() {
        return endpoint;
    }


    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getTotalSizeInBytes() {
        return totalSizeInBytes;
    }

    public void setTotalSizeInBytes(String totalSizeInBytes) {
        this.totalSizeInBytes = totalSizeInBytes;
    }

    public String getMaxBlockMs() {
        return maxBlockMs;
    }

    public void setMaxBlockMs(String maxBlockMs) {
        this.maxBlockMs = maxBlockMs;
    }

    public String getSendThreadCount() {
        return sendThreadCount;
    }

    public void setSendThreadCount(String sendThreadCount) {
        this.sendThreadCount = sendThreadCount;
    }

    public String getBatchSizeThresholdInBytes() {
        return batchSizeThresholdInBytes;
    }

    public void setBatchSizeThresholdInBytes(String batchSizeThresholdInBytes) {
        this.batchSizeThresholdInBytes = batchSizeThresholdInBytes;
    }

    public String getBatchCountThreshold() {
        return batchCountThreshold;
    }

    public void setBatchCountThreshold(String batchCountThreshold) {
        this.batchCountThreshold=batchCountThreshold;
    }

    public String getLingerMs() {
        return lingerMs;
    }

    public void setLingerMs(String lingerMs) {
        this.lingerMs = lingerMs;
    }

    public String getRetries() {
        return retries;
    }

    public void setRetries(String retries) {
        this.retries = retries;
    }

    public String getMaxReservedAttempts() {
        return maxReservedAttempts;
    }

    public void setMaxReservedAttempts(String maxReservedAttempts) {
        this.maxReservedAttempts = maxReservedAttempts;
    }

    public String getBaseRetryBackoffMs() {
        return baseRetryBackoffMs;
    }

    public void setBaseRetryBackoffMs(String baseRetryBackoffMs) {
        this.baseRetryBackoffMs = baseRetryBackoffMs;
    }

    public String getMaxRetryBackoffMs() {
        return maxRetryBackoffMs;
    }

    public void setMaxRetryBackoffMs(String maxRetryBackoffMs) {
        this.maxRetryBackoffMs = maxRetryBackoffMs;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Encoder<E> getEncoder() {
        return encoder;
    }

    public void setEncoder(Encoder<E> encoder) {
        this.encoder = encoder;
    }

    public void setMdcFields(String mdcFields) {
        this.mdcFields = mdcFields;
    }

    private final LoghubAppenderCallback<E> loghubAppenderCallback = new LoghubAppenderCallback<E>() {
        @Override
        public void onCompletion(Result result) {
            if (!result.isSuccessful()) {
                addError( "Failed to send log, topic="  + topicId
                        + ", source=" + source
                        + ", errorCode=" + result.getErrorCode()
                        + ", errorMessage=" + result.getErrorMessage());
            }
        }
    };

    @Override
    public void start() {
        try {
            formatter = DateTimeFormat.forPattern(timeFormat).withZone(DateTimeZone.forID(timeZone));
            if (source==null || source.isEmpty()) {
                source = NetworkUtils.getLocalMachineIP();
            }
            producerConfig = new AsyncProducerConfig(endpoint, accessKeyId, accessKeySecret, source);
            this.setProduceConfig();
            producer = new AsyncProducerClient(producerConfig);
            super.start();
        } catch (Exception e) {
            addError("Failed to start LoghubAppender.", e);
        }
    }

    @Override
    public void stop() {
        try {
            doStop();
        } catch (Exception e) {
            addError("Failed to stop LoghubAppender.", e);
        }
    }

    private void doStop() throws InterruptedException, ProducerException {
        if (!isStarted()) {
            return;
        }
        super.stop();
        producer.close();
    }


    @Override
    public void append(E eventObject) {
        try {
            appendEvent(eventObject);
        } catch (Exception e) {
            addError("Failed to append event.", e);
        }
    }

    private void appendEvent(E eventObject) {
        if (!(eventObject instanceof LoggingEvent)) {
            return;
        }
        LoggingEvent event = (LoggingEvent) eventObject;

        LogItem item = new LogItem();
        item.SetTime((int) (event.getTimeStamp() / 1000));

        if(formatter!=null){
            DateTime dateTime = new DateTime(event.getTimeStamp());
            item.PushBack("time", dateTime.toString(formatter));
        }

        item.PushBack("level", event.getLevel().toString());
        item.PushBack("thread", event.getThreadName());

        StackTraceElement[] caller = event.getCallerData();
        if (caller != null && caller.length > 0) {
            item.PushBack("location", caller[0].toString());
        }

        String message = event.getFormattedMessage();
        item.PushBack("message", message);

        IThrowableProxy iThrowableProxy = event.getThrowableProxy();
        if (iThrowableProxy != null) {
            String throwable = getExceptionInfo(iThrowableProxy);
            throwable += fullDump(event.getThrowableProxy().getStackTraceElementProxyArray());
            item.PushBack("throwable", throwable);
        }

        if (this.encoder != null) {
            item.PushBack("log", new String(this.encoder.encode(eventObject)));
        }

        Optional.ofNullable(mdcFields).ifPresent(
                f->event.getMDCPropertyMap().entrySet().stream()
                        .filter(v-> Arrays.stream(f.split(",")).anyMatch(i->i.equals(v.getKey())))
                        .forEach(map-> item.PushBack(map.getKey(),map.getValue()))
        );
        try {
            List<LogItem> logItems = new ArrayList<>();
            logItems.add(item);
            producer.putLogs(topicId, logItems, loghubAppenderCallback);
        } catch (Exception e) {
            addError("Failed to send log, topicId=" + topicId
                    + ", source=" + source
                    + ", logItem=" + item
                    + " err message "+ e.getMessage());
        }
    }

    private String getExceptionInfo(IThrowableProxy iThrowableProxy) {
        String s = iThrowableProxy.getClassName();
        String message = iThrowableProxy.getMessage();
        return (message != null) ? (s + ": " + message) : s;
    }

    private String fullDump(StackTraceElementProxy[] stackTraceElementProxyArray) {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElementProxy step : stackTraceElementProxyArray) {
            builder.append(CoreConstants.LINE_SEPARATOR);
            String string = step.toString();
            builder.append(CoreConstants.TAB).append(string);
            ThrowableProxyUtil.subjoinPackagingData(builder, step);
        }
        return builder.toString();
    }


    private void setProduceConfig() {
        if (totalSizeInBytes != null && !totalSizeInBytes.isEmpty()) {
            producerConfig.setTotalSizeInBytes(Integer.parseInt(totalSizeInBytes));
        }
        if (maxBlockMs != null && !maxBlockMs.isEmpty()) {
            producerConfig.setMaxBlockMs(Long.parseLong(maxBlockMs));
        }
        if (sendThreadCount != null && !sendThreadCount.isEmpty()) {
            producerConfig.setSendThreadCount(Integer.parseInt(sendThreadCount));
        }
        if (batchSizeThresholdInBytes != null && !batchSizeThresholdInBytes.isEmpty()) {
            producerConfig.setBatchSizeThresholdInBytes(Integer.parseInt(batchSizeThresholdInBytes));
        }
        if (batchCountThreshold != null && !batchCountThreshold.isEmpty()) {
            producerConfig.setBatchCountThreshold(Integer.parseInt(batchCountThreshold));
        }
        if (lingerMs != null && !lingerMs.isEmpty()) {
            producerConfig.setLingerMs(Integer.parseInt(lingerMs));
        }
        if (retries != null && !retries.isEmpty()) {
            producerConfig.setRetries(Integer.parseInt(retries));
        }
        if (maxReservedAttempts != null && !maxReservedAttempts.isEmpty()) {
            producerConfig.setMaxReservedAttempts(Integer.parseInt(maxReservedAttempts));
        }
        if (baseRetryBackoffMs !=null && !baseRetryBackoffMs.isEmpty()) {
            producerConfig.setBaseRetryBackoffMs(Long.parseLong(baseRetryBackoffMs));
        }
        if (maxRetryBackoffMs !=null && !maxRetryBackoffMs.isEmpty()) {
            producerConfig.setMaxRetryBackoffMs(Long.parseLong(maxRetryBackoffMs));
        }
    }
}
