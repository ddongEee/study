package com.study.apps.poc.stock.presentation;

import com.study.apps.poc.stock.domain.page38coms.Page38ComsDirector;
import com.study.apps.poc.stock.domain.page38coms.PageSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hunt")
public class HuntingController {
    private final Page38ComsDirector page38ComsDirector;

    @GetMapping("/target")
    public List<PageSummaryDto> listTarget() throws IOException {
        return page38ComsDirector.loadTarget();
    }
}
