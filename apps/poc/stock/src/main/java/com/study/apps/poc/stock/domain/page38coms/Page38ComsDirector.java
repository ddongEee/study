package com.study.apps.poc.stock.domain.page38coms;

import com.study.apps.poc.stock.domain.Initializer;
import com.study.apps.poc.stock.domain.page38coms.pageloader.DemandForecastingResultPage38ComsLoader;
import com.study.apps.poc.stock.domain.page38coms.pageloader.DemandForecastingSchedulePage38ComsLoader;
import com.study.apps.poc.stock.domain.page38coms.pageloader.NewListingItemPage38ComsLoader;
import com.study.apps.poc.stock.domain.page38coms.pageloader.PublicSubscriptionSchedulePage38ComsLoader;
import com.study.apps.poc.stock.domain.page38coms.util.StockFileManager;
import com.study.apps.poc.stock.infrastructure.FilePath;
import com.study.apps.poc.stock.infrastructure.FilePathEnvelop;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.study.apps.poc.stock.domain.page38coms.pageloader.DemandForecastingResultPage38ComsLoader.*;
import static com.study.apps.poc.stock.domain.page38coms.pageloader.DemandForecastingSchedulePage38ComsLoader.*;
import static com.study.apps.poc.stock.domain.page38coms.pageloader.NewListingItemPage38ComsLoader.*;
import static com.study.apps.poc.stock.domain.page38coms.pageloader.PublicSubscriptionSchedulePage38ComsLoader.*;

@Slf4j
@Component
@AllArgsConstructor
public class Page38ComsDirector implements Initializer {
//    https://www.samsungpop.com/mbw/search/search.do?cmd=stock_search
    private final StockFileManager stockFileManager;
    private final List<Page38ComsLoader> page38ComsLoaders;
    private final Page38ComsLoader<DemandForecastingSchedule> demandForecastingSchedulePage38ComsLoader;
    private final Page38ComsLoader<DemandForecastingResult> demandForecastingResultPage38ComsLoader;
    private final Page38ComsLoader<PublicSubscriptionSchedule> publicSubscriptionSchedulePage38ComsLoader;
    private final Page38ComsLoader<NewListingItem> newListingItemPage38ComsLoader;

    @Override
    public void preProcess() throws IOException {
        rewriteAll();
    }

    @Override
    public void afterProcess() {

    }

    public List<PageSummaryDto> loadTarget() throws IOException {
        Map<String, PageSummaryDto> pageSummaryDtoMap = loadPageSummaryDtoMap();
        return pageSummaryDtoMap.values().stream()
                .filter(PageSummaryDto::isTriableSubscription)
                .sorted(Comparator.comparing(PageSummaryDto::getPersonalContestDateTo).reversed())
                .collect(Collectors.toList());
    }

    public List<PageSummaryDto> loadAll() throws IOException {
        return stockFileManager.loadLatestLines(FilePath.SUMMARY_OF_NEW_LISTING_PUBLIC_SUBSCRIPTION_0006).stream()
                .map(PageSummaryDto::from)
                .collect(Collectors.toList());
    }

    public Map<String, PageSummaryDto> loadPageSummaryDtoMapFromOneFile() throws IOException {
        List<String> loadFromFile = stockFileManager.loadLatestLines(FilePath.SUMMARY_OF_NEW_LISTING_PUBLIC_SUBSCRIPTION_0006);
        return loadFromFile.stream()
                .map(PageSummaryDto::from)
                .collect(Collectors.toMap(PageSummaryDto::getCompanyName, Function.identity()));
    }

    public Map<String, PageSummaryDto> loadPageSummaryDtoMap() throws IOException {
        Map<String, PageSummaryDto> pageSummaryDtoMap = new HashMap<>();

        Map<String, DemandForecastingSchedule> demandForecastingScheduleMap = demandForecastingSchedulePage38ComsLoader.loadFromFile().stream()
                .collect(Collectors.toMap(DemandForecastingSchedule::getCompanyName, Function.identity()));
        demandForecastingScheduleMap.keySet().forEach(key -> {
            final DemandForecastingSchedule demandForecastingSchedule = demandForecastingScheduleMap.get(key);
            pageSummaryDtoMap.compute(key, (companyName, pageSummaryDto) -> {
                if (pageSummaryDto == null) {
                    pageSummaryDto = PageSummaryDto.creatEmpty();
                }

                return pageSummaryDto.update(demandForecastingSchedule);
            });
        });

        Map<String, DemandForecastingResult> demandForecastingResultMap = demandForecastingResultPage38ComsLoader.loadFromFile().stream()
                .collect(Collectors.toMap(DemandForecastingResult::getCompanyName, Function.identity()));
        demandForecastingResultMap.keySet().forEach(key -> {
            final DemandForecastingResult demandForecastingResult = demandForecastingResultMap.get(key);
            pageSummaryDtoMap.compute(key, (companyName, pageSummaryDto) -> {
                if (pageSummaryDto == null) {
                    pageSummaryDto = PageSummaryDto.creatEmpty();
                }

                return pageSummaryDto.update(demandForecastingResult);
            });
        });

        Map<String, PublicSubscriptionSchedule> publicSubscriptionScheduleMap = publicSubscriptionSchedulePage38ComsLoader.loadFromFile().stream()
                .collect(Collectors.toMap(PublicSubscriptionSchedule::getCompanyName, Function.identity()));
        publicSubscriptionScheduleMap.keySet().forEach(key -> {
            final PublicSubscriptionSchedule publicSubscriptionSchedule = publicSubscriptionScheduleMap.get(key);
            pageSummaryDtoMap.compute(key, (companyName, pageSummaryDto) -> {
                if (pageSummaryDto == null) {
                    pageSummaryDto = PageSummaryDto.creatEmpty();
                }

                return pageSummaryDto.update(publicSubscriptionSchedule);
            });
        });

        Map<String, NewListingItem> newListingItemMap = newListingItemPage38ComsLoader.loadFromFile().stream()
                .collect(Collectors.toMap(NewListingItem::getCompanyName, Function.identity()));
        newListingItemMap.keySet().forEach(key -> {
            final NewListingItem newListingItem = newListingItemMap.get(key);
            pageSummaryDtoMap.compute(key, (companyName, pageSummaryDto) -> {
                if (pageSummaryDto == null) {
                    pageSummaryDto = PageSummaryDto.creatEmpty();
                }

                return pageSummaryDto.update(newListingItem);
            });
        });
        return pageSummaryDtoMap;
    }

    public void rewriteAll() throws IOException {

        page38ComsLoaders.forEach(pageLoader -> {
            final List<String> results = new ArrayList<>();
            final PageMeta pageMeta = pageLoader.pageMeta();
            final int lastPageNumber = findLastPageNumber(pageMeta);

            for (int pageNumber = 1; pageNumber <= lastPageNumber; pageNumber ++) {
                try {
                    Document doc = Jsoup.connect(pageMeta.targetPageUrl(pageNumber)).get();
                    Elements trContents = doc.getElementsByAttributeValue("summary", pageMeta.rootElementSummary());
                    Elements trs = trContents.select("tbody tr");
                    List<String> loadResult = trs.stream()
                            .map((Function<Element, Page38ComsLoader.ParsedResult>) pageLoader::parse)
                            .filter(Page38ComsLoader.ParsedResult::isNotFilter)
                            .map(Page38ComsLoader.ParsedResult::writeAsPipelineFormat)
                            .collect(Collectors.toList());
                    results.addAll(loadResult);
                    Thread.sleep(200);
                    log.info("{} : {} - {} done !", pageMeta.rootElementSummary(), lastPageNumber, pageNumber);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // write
            try {
                stockFileManager.write(FilePathEnvelop.create(pageMeta.savingFilePath()), results);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Map<String, PageSummaryDto> pageSummaryDtoMap = loadPageSummaryDtoMap();
        List<String> csvPageSummaries = pageSummaryDtoMap.values().stream()
                .map(PageSummaryDto::toCsvFormatted)
                .collect(Collectors.toList());
        stockFileManager.write(FilePathEnvelop.create(FilePath.SUMMARY_OF_NEW_LISTING_PUBLIC_SUBSCRIPTION_0006), csvPageSummaries);
    }

    private int findLastPageNumber(final PageMeta pageMeta) {
        try {
            Document doc = Jsoup.connect(pageMeta.indexPageUrl()).get();
            String fakeLastPageNumber = doc.getElementsMatchingOwnText("마지막").attr("href").split("page=")[1];
            Document doc2 = Jsoup.connect(pageMeta.targetPageUrl(Integer.parseInt(fakeLastPageNumber))).get();
            String lastPageNumber = doc2.getElementsMatchingOwnText("마지막").attr("href").split("page=")[1];
            return Integer.parseInt(lastPageNumber);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum PageMeta {
        DEMAND_FORECASTING_SCHEDULE("수요예측일정","r", "수요예측일정", FilePath.DEMAND_FORECASTING_SCHEDULE_0002),
        DEMAND_FORECASTING_RESULT("수요예측결과", "r1", "수요예측결과", FilePath.DEMAND_FORECASTING_RESULT_0003),
        PUBLIC_SUBSCRIPTION_SCHEDULE("공모청약일정", "k", "공모주 청약일정", FilePath.PUBLIC_SUBSCRIPTION_SCHEDULE_0004),
        NEW_LISTING("신규상장", "nw", "신규상장종목", FilePath.NEW_LISTING_0005);

        private static final String BASE_URL = "http://www.38.co.kr/html/fund/index.htm";

        private final String name;
        private final String urlKey;
        private final String rootElementSummary;
        private final FilePath savingFilePath;

        public String indexPageUrl() {
            return BASE_URL + "?o=" + urlKey;
        }

        public String targetPageUrl(final int pageNumber) {
            return indexPageUrl() + "&page=" + pageNumber;
        }

        public String rootElementSummary() {
            return this.rootElementSummary;
        }

        public FilePath savingFilePath() {
            return this.savingFilePath;
        }
    }

    public interface Page38ComsLoader<T extends Page38ComsLoader.ParsedResult> {
        PageMeta pageMeta();
        T parse(final Element element);
        List<T> loadFromFile() throws IOException;

        interface ParsedResult {
            String writeAsPipelineFormat();
            boolean isNotFilter();
        }
    }
}
