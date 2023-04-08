package com.study.apps.poc.stock.domain.page38coms.pageloader;

import com.study.apps.poc.stock.domain.page38coms.Page38ComsDirector.Page38ComsLoader;
import com.study.apps.poc.stock.domain.page38coms.Page38ComsDirector.PageMeta;
import com.study.apps.poc.stock.domain.page38coms.util.ElementTextExtractor;
import com.study.apps.poc.stock.domain.page38coms.util.StockFileManager;
import lombok.*;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.study.apps.poc.stock.domain.page38coms.pageloader.NewListingItemPage38ComsLoader.*;

// 신규상장 페이지
@Component
@RequiredArgsConstructor
public class NewListingItemPage38ComsLoader implements Page38ComsLoader<NewListingItemPage38ComsLoader.NewListingItem> {
    private final StockFileManager stockFileManager;

    @Override
    public PageMeta pageMeta() {
        return PageMeta.NEW_LISTING;
    }

    @Override
    public NewListingItem parse(Element e) {
        ElementTextExtractor extractor = ElementTextExtractor.create(e);
        return NewListingItem.builder()
                .companyName(extractor.extractClearly(1,0,0,0))
                .newListingDate(extractor.extractClearly(3,0))
                .currentPrice(extractor.extractClearly(5,0))
                .comparedToPreviousDayPercent(extractor.extractClearly(7,0,0))
                .offeringPrice(extractor.extractClearly(9,0))
                .comparedOfferingPricePercent(extractor.extractClearly(11,0,0))
                .firstStartingPrice(extractor.extractClearly(13,0))
                .firstStaringPricePerOfferingPrice(extractor.extractClearly(15,0,0))
                .firstEndingPrice(extractor.extractClearly(17,0))
                .build();
    }

    @Override
    public List<NewListingItem> loadFromFile() throws IOException {
        List<String> rows = this.stockFileManager.loadLatestLines(pageMeta().savingFilePath());
        return rows.stream()
                .map(NewListingItem::from)
                .collect(Collectors.toList());
    }

    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class NewListingItem implements ParsedResult {
        @Getter private final String companyName;                       // 기업명              : LG에너지솔루션(유가)
        @Getter private final String newListingDate;                    // 신규 상장일           : 2022/01/27
        @Getter private final String currentPrice;                      // 현재가(원)           : 450,000
        @Getter private final String comparedToPreviousDayPercent;      // 전일비(%)            : -10.89%
        @Getter private final String offeringPrice;                     // 공모가(원)           : 300,000
        @Getter private final String comparedOfferingPricePercent;      // 공모가대비 등락률(%)   : 50%
        @Getter private final String firstStartingPrice;                // 시초가(원)           : 597,000
        @Getter private final String firstStaringPricePerOfferingPrice; // 시초/공모(%)         : 99%
        @Getter private final String firstEndingPrice;                  // 첫날종가(원)          : 505,000 or (예정)

        public static NewListingItem from(final String row) {
            String[] split = row.split("\\|", 9);
            return builder()
                    .companyName(split[0])
                    .newListingDate(split[1])
                    .currentPrice(split[2])
                    .comparedToPreviousDayPercent(split[3])
                    .offeringPrice(split[4])
                    .comparedOfferingPricePercent(split[5])
                    .firstStartingPrice(split[6])
                    .firstStaringPricePerOfferingPrice(split[7])
                    .firstEndingPrice(split[8])
                    .build();
        }

        private boolean isNotScheduling() {
            return !isScheduling();
        }

        private boolean isScheduling() {
            return "예정".equals(this.firstEndingPrice);
        }

        @Override
        public String writeAsPipelineFormat() {
            return companyName
                    + "|" + newListingDate
                    + "|" + currentPrice
                    + "|" + comparedToPreviousDayPercent
                    + "|" + offeringPrice
                    + "|" + comparedOfferingPricePercent
                    + "|" + firstStartingPrice
                    + "|" + firstStaringPricePerOfferingPrice
                    + "|" + firstEndingPrice;
        }

        @Override
        public boolean isNotFilter() {
            return true; // 일단 모두 저장!
        }
    }
}
