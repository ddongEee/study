package com.study.apps.poc.dlbr;

import com.study.apps.poc.dlbr.service.DnsDiscoveryHelper;
import com.study.apps.poc.dlbr.service.RedisPropertyHolder;
import com.study.apps.poc.dlbr.service.RedisTester;
import com.study.apps.poc.dlbr.service.SpringPropertyHolder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class Worker {
    private final RedisTester redisTester;
    private final DnsDiscoveryHelper dnsDiscoveryHelper;
    private final RedisPropertyHolder redisPropertyHolder;
    private final SpringPropertyHolder springPropertyHolder;

    private static final String KEY = "endpointCNAME";

    @Scheduled(fixedDelay = 1000)
    public void testSchedule() {
        if (springPropertyHolder.isLocal()) {
//            dnsDiscoveryHelper.nsLookupCNAMEAll("database-1-instance-1-ap-northeast-2c.xxx.ap-northeast-2.rds.amazonaws.com.");
//            redisTester.testSet(KEY, KEY);
        } else {
            String host = redisPropertyHolder.getHost();
            final String cname = dnsDiscoveryHelper.nsLookupCNAME(host);
            String wroteValue = redisTester.setAndGet(KEY, cname);
            log.info("wrote! : " + wroteValue);
        }
    }
}
