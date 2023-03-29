package com.study.sandbox

import spock.lang.Specification

class TestSome extends Specification {
    def "something test"() {
        given:
        expect:
        1 == 1
    }
}
