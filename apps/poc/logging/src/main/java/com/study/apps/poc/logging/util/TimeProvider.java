package com.study.apps.poc.logging.util;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeProvider {

    public static Instant currentTimeUTCInstance() {
        String time = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT);
        return Instant.parse(time);
    }

    public static long currentTimeUTCMillis() {
        Instant instant = currentTimeUTCInstance();
        return instant.toEpochMilli();
    }

}
