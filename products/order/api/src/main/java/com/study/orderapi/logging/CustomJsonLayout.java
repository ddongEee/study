package com.study.orderapi.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.contrib.json.classic.JsonLayout;
import org.slf4j.MDC;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * 요구사항 정의:
 * 1. json log format 은 어떻게?
 *  - mdc field 가 아닌 custom group filed Name ? ex) userInfos : { ... }
 *  - 아니면 flat 하게? userId, transactionId..
 * 2. 확인할수 없는 context 정보.
 *  - case1. service up 혹은 background 에서 실행될시 찍히는 log(transactionId, userContext 없음)
 *      - empty 처리 혹은 임의값. ex) userId = SYSTEM..
 *  - case2. public page 혹은 login page?
 */
public class CustomJsonLayout extends JsonLayout {
    @Override
    protected void addCustomDataToJsonMap(Map<String, Object> map, ILoggingEvent event) {
        super.addCustomDataToJsonMap(map, event);
        final String eventName = Objects.toString(MDC.get("event"), "");
        final String userId = Objects.toString(MDC.get("userId"), "");
        final String transactionId = Objects.toString(MDC.get("transactionId"), "");

        // customOption1. grouping 형태
        Properties properties = new Properties();
        properties.put("event", eventName);
        properties.put("userId", userId);
        properties.put("transactionId", transactionId);
        map.put("helloGroup", properties);

        // customOption2. flat 형태
        map.put("event", eventName);
        map.put("userId", userId);
        map.put("transactionId", transactionId);

        // custom field 에서 사용된, mdc field 는 제거한다.
        // MDC.clear()는 나중에 호출해줄것!
        map.remove("mdc");
    }
}
