package com.study.apps.poc.stock.domain.page38coms.pageloader;

import com.study.apps.poc.stock.domain.page38coms.Page38ComsDirector.Page38ComsLoader;
import com.study.apps.poc.stock.domain.page38coms.util.ElementTextExtractor;
import com.study.apps.poc.stock.domain.page38coms.util.StockFileManager;
import lombok.*;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.study.apps.poc.stock.domain.page38coms.Page38ComsDirector.PageMeta;
//수요예측일정
@Component
@RequiredArgsConstructor
public class DemandForecastingSchedulePage38ComsLoader implements Page38ComsLoader<DemandForecastingSchedulePage38ComsLoader.DemandForecastingSchedule> {
    private final StockFileManager stockFileManager;

    @Override
    public PageMeta pageMeta() {
        return PageMeta.DEMAND_FORECASTING_SCHEDULE;
    }

    @Override
    public DemandForecastingSchedule parse(Element e) {
        ElementTextExtractor extractor = ElementTextExtractor.create(e);
        if (e.childNode(1).attr("bgcolor").equals("#DADADA")) {
            return DemandForecastingSchedule.builder()
                    .companyName("")
                    .build();
        }

        final String companyName = extractor.extractClearly(1,1,0);

        return DemandForecastingSchedule.builder()
                .companyName(companyName)
                .demandForecastingDate(companyName.equals("코디엠") ? "2015.12.10~12.11" : extractor.extractClearly(3,0)) // data 이슈로.. 임시..
                .hopePrice(extractor.extractClearly(5,0))
                .definedPrice(extractor.extractClearly(7,0))
                .demandTotalPrice(extractor.extractClearly(9,0))
                .chargedOrganization(extractor.extractClearlyWithoutComma(11,0))
                .build();
    }

    @Override
    public List<DemandForecastingSchedule> loadFromFile() throws IOException {
        List<String> rows = this.stockFileManager.loadLatestLines(pageMeta().savingFilePath());
        return rows.stream()
                .map(DemandForecastingSchedule::from)
                .collect(Collectors.toList());
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class DemandForecastingSchedule implements ParsedResult {
        @Getter private final String companyName;           // 종목명        : LG에너지솔루션(유가)
        @Getter private final String demandForecastingDate; // 수요예측일     : 2022.01.11~01.12
        @Getter private final String hopePrice;             // 희망공모가     : 257,000~300,000
        @Getter private final String definedPrice;          // 확정공모가     : 300,000
        @Getter private final String demandTotalPrice;      // 공모금액(백만)  : 10,922,500
        @Getter private final String chargedOrganization;   // 주관사        : KB증권,대신증권,신한금융,미래에셋증권,신영증권,하나금융,하이투자

        public static DemandForecastingSchedule from(String row) {
            String[] split = row.split("\\|",6);
            return builder()
                    .companyName(split[0])
                    .demandForecastingDate(split[1])
                    .hopePrice(split[2])
                    .definedPrice(split[3])
                    .demandTotalPrice(split[4])
                    .chargedOrganization(split[5])
                    .build();
        }

        @Override
        public String writeAsPipelineFormat() {
            return companyName
            + "|" + demandForecastingDate
            + "|" + hopePrice
            + "|" + definedPrice
            + "|" + demandTotalPrice
            + "|" + chargedOrganization;
        }

        private boolean companyNameIsEmpty() {
            return "".equals(this.companyName);
        }

        private boolean companyNameIsNotEmpty() {
            return !this.companyNameIsEmpty();
        }

        @Override
        public boolean isNotFilter() {
            // 종목명이 잘못된경우
            return this.companyNameIsNotEmpty();
        }
    }
}
