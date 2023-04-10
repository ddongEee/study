package com.study.apps.poc.stock.presentation;

import com.study.apps.poc.stock.domain.page38coms.Page38ComsDirector;
import com.study.apps.poc.stock.domain.page38coms.PageSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class AnalysisController {
    private final Page38ComsDirector page38ComsDirector;

    @GetMapping("/case1")
    public List<PageSummaryDto> case1() throws IOException {
        Map<String, PageSummaryDto> pageSummaryDtoMap = page38ComsDirector.loadPageSummaryDtoMap();
        return pageSummaryDtoMap.values().stream()
                .filter(PageSummaryDto::allDoneProcessed)
                .sorted(Comparator.comparing(PageSummaryDto::refinedDemandForecastingDateFrom).reversed())
                .collect(Collectors.toList());
    }

}
