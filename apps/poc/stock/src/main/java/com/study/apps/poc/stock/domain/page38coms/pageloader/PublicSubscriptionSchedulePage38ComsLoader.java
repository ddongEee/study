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

import static com.study.apps.poc.stock.domain.page38coms.Page38ComsDirector.*;
import static com.study.apps.poc.stock.domain.page38coms.pageloader.PublicSubscriptionSchedulePage38ComsLoader.*;

//공모청약일정
@Component
@RequiredArgsConstructor
public class PublicSubscriptionSchedulePage38ComsLoader implements Page38ComsLoader<PublicSubscriptionSchedulePage38ComsLoader.PublicSubscriptionSchedule> {
    private final StockFileManager stockFileManager;

    @Override
    public PageMeta pageMeta() {
        return PageMeta.PUBLIC_SUBSCRIPTION_SCHEDULE;
    }

    @Override
    public PublicSubscriptionSchedule parse(Element e) {
        ElementTextExtractor extractor = ElementTextExtractor.create(e);
        String chargedOrganization;
        try {
//            chargedOrganization = e.childNode(11).childNode(0).outerHtml();
            chargedOrganization = extractor.extractClearlyWithoutComma(11,0);
        } catch (IndexOutOfBoundsException exception) {
            // 값이 없을경우 있음..
            chargedOrganization = "";
        }

        return PublicSubscriptionSchedule.builder()
//                .companyName(e.childNode(1).childNode(1).childNode(0).childNode(0).outerHtml().replace("(유가)", ""))
                .companyName(extractor.extractClearly(1,1,0,0))
                .contestDate(extractor.extractClearly(3,0))
                .fixedContestPrice(extractor.extractClearly(5,0))
                .hopedContestPrice(extractor.extractClearly(7,0))
                .subscriptionCompetitionRate(extractor.extractClearly(9,0))
                .chargedOrganization(chargedOrganization)
                .build();
    }

    @Override
    public List<PublicSubscriptionSchedule> loadFromFile() throws IOException {
        List<String> rows = this.stockFileManager.loadLatestLines(pageMeta().savingFilePath());
        return rows.stream()
                .map(PublicSubscriptionSchedule::from)
                .collect(Collectors.toList());
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class PublicSubscriptionSchedule implements ParsedResult {
        @Getter private final String companyName;                 // 종목명    :  LG에너지솔루션(유가)
        @Getter private final String contestDate;                 // 공모주일정 : 2022.01.18~01.19
        @Getter private final String fixedContestPrice;           // 확정공모가 : 300,000	or (-)
        @Getter private final String hopedContestPrice;           // 희망공모가 : 257,000~300,000
        @Getter private final String subscriptionCompetitionRate; // 청약경쟁률 : 69.34:1	or ()
        @Getter private final String chargedOrganization;         // 주관사    : KB증권,대신증권,신한금융,미래에셋증권,신영증권,하나금융,하이투자

        public static PublicSubscriptionSchedule from(final String row) {
            String[] split = row.split("\\|", 6);
            return builder()
                    .companyName(split[0])
                    .contestDate(split[1])
                    .fixedContestPrice(split[2])
                    .hopedContestPrice(split[3])
                    .subscriptionCompetitionRate(split[4])
                    .chargedOrganization(split[5])
                    .build();
        }

        @Override
        public String writeAsPipelineFormat() {
            return companyName
            + "|" + contestDate
            + "|" + fixedContestPrice
            + "|" + hopedContestPrice
            + "|" + subscriptionCompetitionRate
            + "|" + chargedOrganization;
        }

        @Override
        public boolean isNotFilter() {
            return true;
        }
    }
}
