package com.study.apps.poc.logging.service;

import com.study.apps.poc.logging.SdkClientSupporter;
import com.study.apps.poc.logging.util.TimeProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class CloudWatchDirector {
    public static final String TEST_NAMESPACE = "SITE/TRAFFIC";
    public static final String TEST_LOG_GROUP_NAME = "/test/test-log-group";
    public static final String TEST_LOG_STREAM_NAME = "testStream";
    private final CloudWatchClient client;
    private final CloudWatchLogsClient logsClient;

    public CloudWatchDirector(final SdkClientSupporter supporter) {
        this.client = CloudWatchClient.builder()
                .region(supporter.targetRegion())
                .credentialsProvider(supporter.credentialsProvider())
                .build();

        this.logsClient = CloudWatchLogsClient.builder()
                .region(supporter.targetRegion())
                .credentialsProvider(supporter.credentialsProvider())
                .build();
    }

    public void listMetrics(final String namespace) {
        boolean done = false;
        String nextToken = null;
        while (!done) {
            ListMetricsRequest request = ListMetricsRequest.builder()
                    .namespace(namespace)
                    .nextToken(nextToken)
                    .build();
            ListMetricsResponse response = client.listMetrics(request);

            for (Metric metric : response.metrics()) {
                log.info("metricName : {} ,namespace : {} ,dimensions : {}", metric.metricName(), metric.namespace(), metric.dimensions());
            }

            done = response.nextToken() == null;
            nextToken = response.nextToken();
        }
    }

    public void putMetricData(Double dataPoint) {
        Dimension dimension = Dimension.builder()
                .name("UNIQUE_PAGES")
                .value("URLS")
                .build();
        Dimension dimension2 = Dimension.builder()
                .name("InstanceId")
                .value("777")
                .build();

        MetricDatum datum = MetricDatum.builder()
                .metricName("PAGES_VISITED")
                .unit(StandardUnit.NONE)
                .value(dataPoint)
                .timestamp(TimeProvider.currentTimeUTCInstance())
                .dimensions(dimension, dimension2)
                .storageResolution(1) // (setStorageResolution) :: https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/cloudwatch/model/MetricDatum.html
                .build();

        PutMetricDataRequest request = PutMetricDataRequest.builder()
                .namespace(TEST_NAMESPACE)
                .metricData(datum)
                .build();

        PutMetricDataResponse putMetricDataResponse = client.putMetricData(request);
        log.info("status: {}", putMetricDataResponse.sdkHttpResponse().statusCode());
    }

    public void publishMetric(Double dataPoint) {
        MetricDatum datum = MetricDatum.builder()
                .metricName("PAGES_VISITED")
                .unit(StandardUnit.NONE)
                .value(dataPoint)
                .timestamp(TimeProvider.currentTimeUTCInstance())
//                .dimensions(dimension, dimension2)
                .storageResolution(60) // (setStorageResolution) :: https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/cloudwatch/model/MetricDatum.html
                .build();

        PutMetricDataRequest request = PutMetricDataRequest.builder()
                .namespace(TEST_NAMESPACE)
                .metricData(datum)
                .build();

        PutMetricDataResponse putMetricDataResponse = client.putMetricData(request);
        log.info("datapoint : {} - status: {}", dataPoint, putMetricDataResponse.sdkHttpResponse().statusCode());
    }

    public void get(final String metricName) {
        Metric met = Metric.builder()
                .metricName(metricName)
                .namespace(TEST_NAMESPACE)
                .build();

        MetricStat metStat = MetricStat.builder()
                .stat("Minimum")
                .period(60)
                .metric(met)
                .build();

        MetricDataQuery dataQuery = MetricDataQuery.builder()
                .metricStat(metStat)
                .id("m4")
                .returnData(true)
                .build();

        List<MetricDataQuery> dq = new ArrayList<>();
        dq.add(dataQuery);

        Instant from = Instant.now().minusSeconds(600);
        Instant to = Instant.now();

        GetMetricDataRequest getMetricDataRequest = GetMetricDataRequest.builder()
                .maxDatapoints(100)
                .startTime(from)
                .endTime(to)
                .metricDataQueries(dq)
                .build();

        GetMetricDataResponse response = client.getMetricData(getMetricDataRequest);
        List<MetricDataResult> data = response.metricDataResults();

        for (MetricDataResult result : data) {
            log.info("[label:status]({}:{})", result.label(), result.statusCode().toString());
        }
    }

    public void createLogGroupAndStream(String logGroupName, String streamName) {
        CreateLogGroupRequest request = CreateLogGroupRequest.builder()
                .logGroupName(logGroupName)
                .build();
        CreateLogGroupResponse response = logsClient.createLogGroup(request);
        log.info("createLogGroup[{}] is {}", logGroupName, response.sdkHttpResponse().isSuccessful() ? "DONE" : "FAILED");

        CreateLogStreamRequest streamRequest = CreateLogStreamRequest.builder()
                .logGroupName(logGroupName)
                .logStreamName(streamName)
                .build();
        CreateLogStreamResponse streamResponse = logsClient.createLogStream(streamRequest);
        log.info("createLogStream[{}] is {}", streamName, streamResponse.sdkHttpResponse().isSuccessful() ? "DONE" : "FAILED");
    }

    public void putLogEvents(String logGroupName, String streamName) {
        InputLogEvent inputLogEvent = InputLogEvent.builder()
                .message("{ \"key1\": \"value2\", \"key2\": \"value3\" }")
                .timestamp(TimeProvider.currentTimeUTCMillis()) // todo : epoch time 맞는지 체크
                .build();

        PutLogEventsRequest putLogEventsRequest = PutLogEventsRequest.builder()
                .logEvents(Arrays.asList(inputLogEvent))
                .logGroupName(logGroupName)
                .logStreamName(streamName)
                .build();

        logsClient.putLogEvents(putLogEventsRequest);
        log.info("Successfully put CloudWatch log event : {}", TimeProvider.currentTimeUTCMillis());
    }

    public void getLogEvents(String logGroupName, String logStreamName) {
        GetLogEventsRequest getLogEventsRequest = GetLogEventsRequest.builder()
                .logGroupName(logGroupName)
                .logStreamName(logStreamName)
                .startFromHead(true)
                .build();

        GetLogEventsResponse logEvents = logsClient.getLogEvents(getLogEventsRequest);
        List<OutputLogEvent> events = logEvents.events();
        for (OutputLogEvent event : events) {
            log.info(event.message());
        }

        log.info("Successfully got CloudWatch log events!");
    }
}
