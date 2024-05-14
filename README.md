Tencent CLS Logback Appender SDK
---
logback是由log4j创始人设计的又一个开源日志组件。通过使用Logback，您可以控制日志信息输送的目的地是控制台、文件、GUI 组件、甚至是套接口服务器、NT 的事件记录器、UNIX Syslog 守护进程等；您也可以控制每一条日志的输出格式；通过定义每一条日志信息的级别，您能够更加细致地控制日志的生成过程。最令人感兴趣的就是，这些可以通过一个配置文件来灵活地进行配置，而不需要修改应用的代码。


### 工程引入和配置

-  maven 工程中引入依赖

```
<dependency>
    <groupId>com.tencentcloudapi.cls</groupId>
    <artifactId>tencentcloud-cls-logback-appender</artifactId>
    <version>1.0.5</version>
</dependency>
```

- 修改logback配置文件

```
  <appender name="LoghubAppender" class="com.tencentcloudapi.cls.LoghubAppender">
        <!--必选项-->
        <endpoint>ap-guangzhou.cls.tencentcs.com</endpoint>
        <accessKeyId>${accesskey}</accessKeyId>
        <accessKeySecret>${accessKeySecret}</accessKeySecret>
        <topicId>${topicId}</topicId>

        <!-- 可选项 详见 '参数说明'-->
        <totalSizeInBytes>104857600</totalSizeInBytes>
        <maxBlockMs>0</maxBlockMs>
        <sendThreadCount>8</sendThreadCount>
        <batchSizeThresholdInBytes>524288</batchSizeThresholdInBytes>
        <batchCountThreshold>4096</batchCountThreshold>
        <lingerMs>2000</lingerMs>
        <retries>10</retries>
        <baseRetryBackoffMs>100</baseRetryBackoffMs>
        <maxRetryBackoffMs>50000</maxRetryBackoffMs>

        <!-- 可选项 设置时间格式 -->
        <timeFormat>yyyy-MM-dd'T'HH:mm:ssZ</timeFormat>
        <timeZone>Asia/Shanghai</timeZone>

        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger - %msg</pattern>
        </encoder>
        <mdcFields>THREAD_ID,MDC_KEY</mdcFields>
  </appender>
```

### Sample Code

logback 简单配置[logback-test.xml](https://github.com/TencentCloud/tencentcloud-cls-logback-appender/blob/main/src/test/resources/logback-test.xml)



### 域名地址


endpoint填写请参考[可用地域](https://cloud.tencent.com/document/product/614/18940#.E5.9F.9F.E5.90.8D)中 **API上传日志** Tab中的域名![image-20230403191435319](https://github.com/TencentCloud/tencentcloud-cls-sdk-js/blob/main/demo.png)

### 参数说明

```
#单个 producer 实例能缓存的日志大小上限，默认为 100MB。
totalSizeInBytes=104857600
#如果 producer 可用空间不足，调用者在 send 方法上的最大阻塞时间，默认为 60 秒。为了不阻塞打印日志的线程，强烈建议将该值设置成 0。
maxBlockMs=0
#执行日志发送任务的线程池大小，默认为可用处理器个数。
sendThreadCount=8
#当一个 ProducerBatch 中缓存的日志大小大于等于 batchSizeThresholdInBytes 时，该 batch 将被发送，默认为 512 KB，最大可设置成 5MB。
batchSizeThresholdInBytes=524288
#当一个 ProducerBatch 中缓存的日志条数大于等于 batchCountThreshold 时，该 batch 将被发送，默认为 4096，最大可设置成 40960。
batchCountThreshold=4096
#一个 ProducerBatch 从创建到可发送的逗留时间，默认为 2 秒，最小可设置成 100 毫秒。
lingerMs=2000
#如果某个 ProducerBatch 首次发送失败，能够对其重试的次数，默认为 10 次。
#如果 retries 小于等于 0，该 ProducerBatch 首次发送失败后将直接进入失败队列。
retries=10
#该参数越大能让您追溯更多的信息，但同时也会消耗更多的内存。
maxReservedAttempts=11
#首次重试的退避时间，默认为 100 毫秒。
#Producer 采样指数退避算法，第 N 次重试的计划等待时间为 baseRetryBackoffMs * 2^(N-1)。
baseRetryBackoffMs=100
#重试的最大退避时间，默认为 50 秒。
maxRetryBackoffMs=50000
```


### 功能优势
- 日志不落盘：产生数据通过网络发给服务端。
- 无需改造：对已使用Log4J应用，只需简单配置即可采集
- 异步非阻塞：高并发设计，后台异步发送，适合高并发写入
- 资源可控制：可以通过参数控制 producer 用于缓存待发送数据的内存大小，同时还可以配置用于执行数据发送任务的线程数量
- 自动重试： 对可重试的异常，支持配置重试次数
- 优雅关闭： 推出前会将日志全量进行发送
- 上下文还原： ""
- 感知日志上报结果："运行过程中产生的异常通过 AddError 输出出来"
