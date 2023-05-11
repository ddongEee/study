package com.study.apps.poc.logging;

import com.study.apps.poc.logging.service.CloudWatchDirector;
import com.study.apps.poc.logging.service.StsDirector;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static com.study.apps.poc.logging.service.CloudWatchDirector.*;
import static com.study.apps.poc.logging.service.StsDirector.Identity;

@RestController
@RequiredArgsConstructor
public class AwsSdkController {
    private final StsDirector stsDirector;
    private final CloudWatchDirector cloudWatchDirector;

    @GetMapping("/identity")
    public Identity printCurrentProfileIdentity() {
        return stsDirector.printGetCallerIdentity();
    }

    @GetMapping("/metric/list")
    public void printMetrics() {
        cloudWatchDirector.listMetrics("SITE/TRAFFIC");
    }

    @GetMapping("/metric/pub/{dataPoint}")
    public void publishMetric(@PathVariable Double dataPoint) {
        cloudWatchDirector.publishMetric(dataPoint);
        cloudWatchDirector.listMetrics("SITE/TRAFFIC");
    }

    @GetMapping("/metric/{metricName}")
    public void metric(@PathVariable String metricName) {
        cloudWatchDirector.get(metricName);
    }

    @GetMapping("/logs/create-log-group-and-stream")
    public void createLogsStuff() {
        cloudWatchDirector.createLogGroupAndStream(TEST_LOG_GROUP_NAME, TEST_LOG_STREAM_NAME);
    }

    @GetMapping("/logs/test")
    public void testLogs() {
        cloudWatchDirector.putLogEvents(TEST_LOG_GROUP_NAME, TEST_LOG_STREAM_NAME);
        cloudWatchDirector.getLogEvents(TEST_LOG_GROUP_NAME, TEST_LOG_STREAM_NAME);
    }
}
