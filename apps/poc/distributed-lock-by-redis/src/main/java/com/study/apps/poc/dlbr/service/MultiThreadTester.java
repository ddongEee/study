package com.study.apps.poc.dlbr.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

import static com.study.apps.poc.dlbr.support.ScheduledWithLockAdvice.ScheduledWithLock;

@Slf4j
@Component
@RequiredArgsConstructor
public class MultiThreadTester {
    private final AtomicInteger executedCount = new AtomicInteger(0);
    private final CriticalRemoteDateHandler remote;
    private String runFunction = "";

//    @Scheduled(fixedDelay = 1000) public void test1()  { executedCount.incrementAndGet(); remote.plusCount(); }
//    @Scheduled(fixedDelay = 1000) public void test2()  { executedCount.incrementAndGet(); remote.plusCount(); }
//    @Scheduled(fixedDelay = 1000) public void test3()  { executedCount.incrementAndGet(); remote.plusCount(); }
//    @Scheduled(fixedDelay = 1000) public void test4()  { executedCount.incrementAndGet(); remote.plusCount(); }
//    @Scheduled(fixedDelay = 1000) public void test5()  { executedCount.incrementAndGet(); remote.plusCount(); }
//    @Scheduled(fixedDelay = 1000) public void test6()  { executedCount.incrementAndGet(); remote.plusCount(); }
//    @Scheduled(fixedDelay = 1000) public void test7()  { executedCount.incrementAndGet(); remote.plusCount(); }
//    @Scheduled(fixedDelay = 1000) public void test8()  { executedCount.incrementAndGet(); remote.plusCount(); }
//    @Scheduled(fixedDelay = 1000) public void test9()  { executedCount.incrementAndGet(); remote.plusCount(); }
//    @Scheduled(fixedDelay = 1000) public void test10() { executedCount.incrementAndGet(); remote.plusCount(); }
//    @Scheduled(fixedDelay = 1000) public void test11() { executedCount.incrementAndGet(); remote.plusCount(); }
//    @Scheduled(fixedDelay = 1000) public void test12() { executedCount.incrementAndGet(); remote.plusCount(); }
//    @Scheduled(fixedDelay = 1000) public void test13() { executedCount.incrementAndGet(); remote.plusCount(); }
//    @Scheduled(fixedDelay = 1000) public void test14() { executedCount.incrementAndGet(); remote.plusCount(); }
//    @Scheduled(fixedDelay = 1000) public void test15() { executedCount.incrementAndGet(); remote.plusCount(); }
//    @Scheduled(fixedDelay = 1000) public void test16() { executedCount.incrementAndGet(); remote.plusCount(); }
//    @Scheduled(fixedDelay = 1000) public void test17() { executedCount.incrementAndGet(); remote.plusCount(); }
//    @Scheduled(fixedDelay = 1000) public void test18() { executedCount.incrementAndGet(); remote.plusCount(); }
//    @Scheduled(fixedDelay = 1000) public void test19() { executedCount.incrementAndGet(); remote.plusCount(); }
//    @Scheduled(fixedDelay = 1000) public void test20() { executedCount.incrementAndGet(); remote.plusCount(); }

//    @Scheduled(fixedDelay = 1000) public void summary() {
//        int execCount = executedCount.get();
//        int remoteCount = remote.getCount();
//        log.info("executedCount : {},  remoteCount : {}, diff : {}", execCount, remoteCount, execCount - remoteCount);
//    }
}
