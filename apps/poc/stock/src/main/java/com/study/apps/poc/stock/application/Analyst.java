package com.study.apps.poc.stock.application;

import com.study.apps.poc.stock.domain.page38coms.Page38ComsDirector;
import com.study.apps.poc.stock.domain.page38coms.PageSummaryDto;
import com.study.apps.poc.stock.domain.page38coms.pageloader._38PageItemMetaPageManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.study.apps.poc.stock.domain.page38coms.pageloader._38PageItemMetaPageManager.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class Analyst {
    private final Page38ComsDirector page38ComsDirector;
    private final _38PageItemMetaPageManager itemMetaPageManager;


    public void doSome() throws IOException {
        List<PageSummaryDto> pageSummaries = page38ComsDirector.loadAll();
        Map<String, _38ComsItemMeta> itemNameMap = itemMetaPageManager.loadItemNameMapWithoutDup();

        List<String> contains = new ArrayList<>();
        List<String> notContains = new ArrayList<>();
        for (PageSummaryDto summary : pageSummaries) {
            String companyName = summary.getCompanyName();
            if (itemNameMap.containsKey(companyName)) {
                contains.add(companyName);
            } else {
                notContains.add(companyName);
            }
        }
        System.out.println("done");
    }
}
