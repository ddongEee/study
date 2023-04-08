package com.study.apps.poc.stock.domain.page38coms.pageloader;

import com.study.apps.poc.stock.domain.page38coms.Page38ComsDirector;
import com.study.apps.poc.stock.domain.page38coms.util.ElementTextExtractor;
import com.study.apps.poc.stock.domain.page38coms.util.StockFileManager;
import lombok.*;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

//수요예측결과
@Component
@RequiredArgsConstructor
public class DemandForecastingResultPage38ComsLoader implements Page38ComsDirector.Page38ComsLoader<DemandForecastingResultPage38ComsLoader.DemandForecastingResult> {
    private final StockFileManager stockFileManager;

    @Override
    public Page38ComsDirector.PageMeta pageMeta() {
        return Page38ComsDirector.PageMeta.DEMAND_FORECASTING_RESULT;
    }

    @Override
    public DemandForecastingResult parse(Element e) {
        ElementTextExtractor extractor = ElementTextExtractor.create(e);
        String orgCompetitionRate;
        try {
            orgCompetitionRate = extractor.extractClearly(11,0);
        } catch (IndexOutOfBoundsException exception) {
            // 값이 없을경우
            orgCompetitionRate = "";
        }

        String chargedOrganization;
        try {
            chargedOrganization = extractor.extractClearlyWithoutComma(15,0);
        } catch (IndexOutOfBoundsException exception) {
            // 값이 없을경우 있음..
            chargedOrganization = "";
        }

        return DemandForecastingResult.builder()
//                .companyName(e.childNode(1).childNode(0).childNode(0).outerHtml().replace("(유가)", ""))
                .companyName(extractor.extractClearly(1,0,0))
                .forecastingDate(extractor.extractClearly(3,0))
                .hopeContestPrice(extractor.extractClearly(5,0))
                .fixContestPrice(extractor.extractClearly(7,0))
                .totalContestPrice(extractor.extractClearly(9,0))
                .orgCompetitionRate(orgCompetitionRate) // try catch 감싸기..
                .obligationToHoldIt(extractor.extractClearly(13,0))
                .chargedOrganization(chargedOrganization)
                .build();
    }

    @Override
    public List<DemandForecastingResult> loadFromFile() throws IOException {
        List<String> rows = this.stockFileManager.loadLatestLines(pageMeta().savingFilePath());
        return rows.stream()
                .map(DemandForecastingResult::from)
                .collect(Collectors.toList());
    }

    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class DemandForecastingResult implements Page38ComsDirector.Page38ComsLoader.ParsedResult {
        @Getter private final String companyName;           // 기업명              : LG에너지솔루션(유가)
        @Getter private final String forecastingDate;       // 예측일              : 2022.01.11
        @Getter private final String hopeContestPrice;      // 공모희망가(원)        : 257,000~300,000
        @Getter private final String fixContestPrice;       // 공모가(원)           : 300,000
        @Getter private final String totalContestPrice;     // 공모금액(백만원)       : 10,922,500
        @Getter private final String orgCompetitionRate;    // 기관경쟁률 : 1:100    : 2023.37:1
        @Getter private final String obligationToHoldIt;    // 의무보유확약 : 1.12 %  : 77.38%
        @Getter private final String chargedOrganization;   // 주간사               : KB증권,대신증권,신한금융,미래에셋증권,신영증권,하나금융,하이투자

        public static DemandForecastingResult from(String row) {
            String[] split = row.split("\\|", 8);
            return builder()
                    .companyName(split[0])
                    .forecastingDate(split[1])
                    .hopeContestPrice(split[2])
                    .fixContestPrice(split[3])
                    .totalContestPrice(split[4])
                    .orgCompetitionRate(split[5])
                    .obligationToHoldIt(split[6])
//                    .chargedOrganization(split.length == 8 ? split[7] : "")
                    .chargedOrganization(split[7])
                    .build();
        }

        @Override
        public String writeAsPipelineFormat() {
            return companyName
            + "|" + forecastingDate
            + "|" + hopeContestPrice
            + "|" + fixContestPrice
            + "|" + totalContestPrice
            + "|" + orgCompetitionRate
            + "|" + obligationToHoldIt
            + "|" + chargedOrganization;
        }

        @Override
        public boolean isNotFilter() {
            return true;
        }
    }
}
