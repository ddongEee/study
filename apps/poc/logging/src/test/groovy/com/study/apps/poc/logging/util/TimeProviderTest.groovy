package com.study.apps.poc.logging.util

import spock.lang.Specification

import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

class TimeProviderTest extends Specification {

    def "currentTimeUtcMillies test"() {
        expect:
        def time = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli()
        def seoulTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli()

        time == seoulTime
    }
}
