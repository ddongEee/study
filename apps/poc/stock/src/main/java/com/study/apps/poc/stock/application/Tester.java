package com.study.apps.poc.stock.application;

import com.study.apps.poc.stock.domain.miraeasset.MiraeAssetPageDirector;
import com.study.apps.poc.stock.domain.page38coms.Page38ComsDirector;
import com.study.apps.poc.stock.domain.page38coms.PageSummaryDto;
import com.study.apps.poc.stock.domain.page38coms.pageloader._38PageItemMetaPageManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.study.apps.poc.stock.domain.miraeasset.MiraeAssetPageDirector.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class Tester {
    private final Page38ComsDirector page38ComsDirector;
    private final MiraeAssetPageDirector miraeAssetPageDirector;
    private final _38PageItemMetaPageManager a38PageItemMetaPageManager;
    private final ItemMetaProvider itemMetaProvider;

    public void test1() throws IOException {
        List<PageSummaryDto> collects = page38ComsDirector.loadAll().stream()
                .filter(pageSummaryDto -> pageSummaryDto.getNewListingDate() != null)
                .sorted(Comparator.comparing(PageSummaryDto::getNewListingDate).reversed())
                .filter(pageSummaryDto -> pageSummaryDto.isAfterAt(LocalDate.of(2021, 1, 1)))
                .collect(Collectors.toList());

        List<String> companyNames = collects.stream()
                .map(PageSummaryDto::getCompanyName)
                .filter(s -> !s.contains("스팩"))
                .collect(Collectors.toList());

        List<ItemMeta> itemMetas = miraeAssetPageDirector.loadItemMetas();
        Map<String, ItemMeta> itemMetaMap = itemMetas.stream().collect(Collectors.toMap(ItemMeta::getKorName, Function.identity()));

        final List<String> matchedResults = new ArrayList<>();
        final List<String> notMatchedResults = new ArrayList<>();

        for (String companyName : companyNames) {
            if (itemMetaMap.containsKey(companyName)) {
                matchedResults.add(companyName);
            } else {
                notMatchedResults.add(companyName);
            }
        }
        log.info("matched : {}", matchedResults);
        log.info("not matched : {}", notMatchedResults);
        return;
    }

    public void test2() throws IOException, InterruptedException {
        itemMetaProvider.test();
    }
}
