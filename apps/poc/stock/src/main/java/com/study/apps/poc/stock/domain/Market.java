package com.study.apps.poc.stock.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public enum Market {
    KOSPI("거래소"),
    KOSDAQ("코스닥"),
    UNLISTED("비상장"),
    K_OTC("K-OTC"),
    KONEX("코넥스");

    @Getter private final String korName;

    private static final Map<String, Market> korNameMap = Stream.of(values())
            .collect(Collectors.toMap(Market::getKorName, Function.identity()));

    public static Market findByKorName(final String korName) {
        return korNameMap.get(korName);
    }
}
