package com.study.apps.poc.stock.infrastructure;

import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public enum FilePath {
    ITEM_META_0001("/item_meta_0001", Extension.CSV),                                                                   // 종목코드
    DEMAND_FORECASTING_SCHEDULE_0002("/demand_forecasting_schedule_0002", Extension.CSV),                               // 수요예측일정
    DEMAND_FORECASTING_RESULT_0003("/demand_forecasting_result_0003", Extension.CSV),                                   // 수요예측결과
    PUBLIC_SUBSCRIPTION_SCHEDULE_0004("/public_subscription_schedule_0004", Extension.CSV),                             // 공모청약일정
    NEW_LISTING_0005("/new_listing_0005", Extension.CSV),                                                               // 신규상장
    SUMMARY_OF_NEW_LISTING_PUBLIC_SUBSCRIPTION_0006("/summary_of_new_listing_public_subscription_0006", Extension.CSV), // 신규상장공모요약
    _38_COMS_ITEM_META_0007("/_38_coms_item_meta_0007", Extension.CSV);                                                 // 종목코드

    public static final String ROOT_FILE_PATH = "./result";
    private final String path;
    private final Extension extension;


    FilePath(String path, Extension extension) {
        this.path = ROOT_FILE_PATH + path;
        this.extension = extension;
    }

    public static FilePath findByNameContains(final String fullPath) {
        return Arrays.stream(values())
                .filter(f -> fullPath.contains(f.name().toLowerCase()))
                .findAny().orElseThrow(() -> new RuntimeException("존재하지 않는 파일타입 : " + fullPath));
    }

    public String extension() {
        return this.extension.postfix;
    }

    public Path generatePathWithAdditional(String additional) {
        return Paths.get(generateTextPathWithAdditional(additional));
    }

    public String generateTextPathWithAdditional(final String additional) {
        if (additional == null) {
            return this.path + extension.postfix;
        }
        return this.path + "_"+ additional + extension.postfix;
    }

    @RequiredArgsConstructor
    public enum Extension {
        CSV(".csv");

        private final String postfix;
    }
}
