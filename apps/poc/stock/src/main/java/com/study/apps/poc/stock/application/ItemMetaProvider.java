package com.study.apps.poc.stock.application;

import com.study.apps.poc.stock.domain.Market;
import com.study.apps.poc.stock.domain.page38coms.pageloader._38PageItemMetaPageManager;
import com.study.apps.poc.stock.domain.page38coms.util.StockFileManager;
import com.study.apps.poc.stock.infrastructure.FilePath;
import com.study.apps.poc.stock.infrastructure.FilePathEnvelop;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.study.apps.poc.stock.domain.page38coms.pageloader._38PageItemMetaPageManager._38ComsItemMeta;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemMetaProvider {
    private final _38PageItemMetaPageManager a38PageItemMetaPageManager;
    private final StockFileManager stockFileManager;

    public void test() throws IOException, InterruptedException {

    }

    // 마켓 그룹당 개수
    public Map<Market, ArrayList<_38ComsItemMeta>> loadMarketGroupedMap() throws IOException {
        List<_38ComsItemMeta> itemMetas = a38PageItemMetaPageManager.loadAllFromFile();
        return itemMetas.stream().collect(
                Collectors.groupingBy(_38ComsItemMeta::getMarket, HashMap::new, Collectors.toCollection(ArrayList::new))
        );
    }

    // 일별로 row 업데이트 되는 개수. update 할때 여러 페이지 보도록 설계 필요할듯
    public void printUpdateCountByDay() throws IOException {
        HashMap<String, ArrayList<_38ComsItemMeta>> resultMap = a38PageItemMetaPageManager.loadAllFromFile()
                .stream()
                .collect(
                        Collectors.groupingBy(_38ComsItemMeta::getModifiedYearAndMonthDay, HashMap::new, Collectors.toCollection(ArrayList::new))
                );
        StringBuilder builder = new StringBuilder();
        for (String yyyymmdd : resultMap.keySet().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
            builder.append(yyyymmdd).append(":").append(resultMap.get(yyyymmdd).size()).append("\n");
        }

        log.info(builder.toString());
    }

    // 2개의 파일 비교
    public void compareTwoFile() throws IOException {
        FilePathEnvelop main = FilePathEnvelop.create(FilePath._38_COMS_ITEM_META_0007, "202202031356");
        FilePathEnvelop comp = FilePathEnvelop.create(FilePath._38_COMS_ITEM_META_0007, "202202031358");
        stockFileManager.printCompareTwoFile(main, comp);
    }
}
