package com.study.apps.poc.stock;

import com.study.apps.poc.stock.application.Analyst;
import com.study.apps.poc.stock.application.Tester;
import com.study.apps.poc.stock.domain.miraeasset.MiraeAssetPageDirector;
import com.study.apps.poc.stock.domain.page38coms.Page38ComsDirector;
import com.study.apps.poc.stock.domain.page38coms.PageSummaryDto;
import com.study.apps.poc.stock.domain.samsungpop.DataLoader;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
@AllArgsConstructor
public class StockApplication {
	private final Page38ComsDirector page38ComsDirector;
	private final DataLoader dataLoader;
	private final MiraeAssetPageDirector director;
	private final Tester tester;
	private final Analyst analyst;
	public static void main(String[] args) {
		SpringApplication.run(StockApplication.class, args);
	}

//	@EventListener(ContextRefreshedEvent.class)
	public Map<String, PageSummaryDto> contextRefreshedEvent() throws IOException, InterruptedException {
//		pageDirector.writeAll(); // 모든 페이지 읽어서 file 에 write
		Map<String, PageSummaryDto> pageSummaryDtoMap = page38ComsDirector.loadPageSummaryDtoMap();
		List<String> collect = pageSummaryDtoMap.values().stream()
				.sorted(Comparator.comparing(PageSummaryDto::refinedDemandForecastingDateFrom).reversed())
				.map(PageSummaryDto::print)
				.collect(Collectors.toList());
		System.out.println(collect);
		return pageSummaryDtoMap;
	}

	@EventListener(ContextRefreshedEvent.class)
	public void test2() throws IOException, InterruptedException {
//		tester.test2();
		analyst.doSome();
	}
}
