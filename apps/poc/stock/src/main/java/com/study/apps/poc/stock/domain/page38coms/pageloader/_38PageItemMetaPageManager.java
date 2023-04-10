package com.study.apps.poc.stock.domain.page38coms.pageloader;

import com.study.apps.poc.stock.domain.Initializer;
import com.study.apps.poc.stock.domain.Market;
import com.study.apps.poc.stock.domain.page38coms.util.ElementTextExtractor;
import com.study.apps.poc.stock.domain.page38coms.util.StockFileManager;
import com.study.apps.poc.stock.infrastructure.FilePath;
import com.study.apps.poc.stock.infrastructure.FilePathEnvelop;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class _38PageItemMetaPageManager implements Initializer {
    private static final int MAX_PAGE_ROW_20 = 20;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private final StockFileManager stockFileManager;

//    public static final List<String> dupItemNames = new ArrayList<>();

    private List<String> getDupItemNames() {
        final List<String> dupItemNames = new ArrayList<>();
        final Map<String, String> logic = new HashMap<>();
        List<_38ComsItemMeta> comsItemMetas = null;
        try {
            comsItemMetas = loadAllFromFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        comsItemMetas.forEach(comsItemMeta -> {
            logic.compute(comsItemMeta.getItemName(), (s, s2) -> {
                if (s2 != null) {
                    dupItemNames.add(s);
                }
                return s;
            });
        });
        return dupItemNames;
    }

    @Override
    public void preProcess() throws IOException, InterruptedException {
        loadAllFromWeb();
    }

    @Override
    public void afterProcess() {

    }

    public List<_38ComsItemMeta> loadAllFromFile() throws IOException {
        return stockFileManager.loadLatestLines(FilePath._38_COMS_ITEM_META_0007)
                               .stream()
                               .map(_38ComsItemMeta::from)
                               .collect(Collectors.toList());
    }

    private List<_38ComsItemMeta> loadAllFromWeb() throws IOException, InterruptedException {
        List<_38ComsItemMeta> _38ComsItemMetas = new ArrayList<>();

        final int lastPageNumber = 299;
        for (int pageNumber = 1; pageNumber <= lastPageNumber; pageNumber++) {
            _38ComsItemMetas.addAll(loadFromWebByPageNumber(pageNumber));
            log.info("[기업정보조회] Done to load : {}-{}", lastPageNumber, pageNumber);
            Thread.sleep(1000);
        }

        return _38ComsItemMetas;
    }

    public List<_38ComsItemMeta> loadFromWebByPageNumber(final int pageNumber) throws IOException {
        final List<_38ComsItemMeta> results = new ArrayList<>();
        Document doc = Jsoup.connect("https://www.38.co.kr/html/forum/com_list/?&page="+pageNumber).get();
        Elements trContents = doc.getElementsByAttributeValue("summary", "기업정보 수정현황");
        Elements trs = trContents.select("tbody tr");
        for (Element element : trs) {
            String bgcolor = element.attr("bgcolor");
            if (bgcolor.equals("")) {
                continue;
            }

            ElementTextExtractor extractor = ElementTextExtractor.create(element);
            String businessType = "";
            try {
                businessType = extractor.extractClearly(7, 0); // 없는 경우도 있음
            } catch (IndexOutOfBoundsException e) {

            }

            String itemCode = extractor.extractClearly(3, 0);
//            if (itemCode.equals("094710")) {
//                UNLISTED|094710|세원|방송수신기및기타영상음향기기제조업|0억원|2007.11.15 무시..
//                continue;
//            }

            results.add(_38ComsItemMeta.builder()
                    .market(Market.findByKorName(extractor.extractClearly(1,0,0)))  // 시장구분
                    .itemCode(itemCode)    // 종목코드
                    .itemName(extractor.extractClearly(5,0,0))  // 종목명
                    .businessType(businessType)    // 업종
                    .capital(extractor.extractClearly(9,0))    // 자본금
                    .modifiedAt(LocalDate.parse(extractor.extractClearly(11,0), DATE_FORMAT))   // 수정(등록)일
                    .build());
        }
        return results;
    }

    public void rewriteAll() throws IOException, InterruptedException {
        List<_38ComsItemMeta> comsItemMetas = loadAllFromWeb();
        List<String> csvFormattedLines = comsItemMetas.stream()
                .map(_38ComsItemMeta::toCsvFormatted)
                .collect(Collectors.toList());
        stockFileManager.write(FilePathEnvelop.create(FilePath._38_COMS_ITEM_META_0007), csvFormattedLines);
    }

    public Map<String, _38ComsItemMeta> loadItemCodeMap() throws IOException {
        return loadAllFromFile().stream()
                .collect(Collectors.toMap(_38ComsItemMeta::getItemCode, Function.identity()));
    }

    public Map<String, _38ComsItemMeta> loadItemNameMapWithoutDup() throws IOException {
        List<String> dupItemNames = getDupItemNames();
        return loadAllFromFile().stream()
                .filter(m -> !dupItemNames.contains(m.getItemName()))
                .collect(Collectors.toMap(_38ComsItemMeta::getItemName, Function.identity()));
    }


    public void updateFile() throws IOException, InterruptedException {
        Map<String, _38ComsItemMeta> savedItemMetaMap = loadItemCodeMap();
        int totalChangedCount = 0;
        int changedCount;
        for (int pageNumber = 1; pageNumber < 299 ; pageNumber++) {
            log.info("[기업정보조회] read {}", pageNumber);
            changedCount = 0;
            List<_38ComsItemMeta> collect = loadFromWebByPageNumber(pageNumber).stream()
                    .sorted(Comparator.comparing(_38ComsItemMeta::getModifiedAt))
                    .collect(Collectors.toList());
            for (_38ComsItemMeta meta : collect) {
                _38ComsItemMeta savedItemMeta = savedItemMetaMap.get(meta.getItemCode());
                if (savedItemMeta != null && savedItemMeta.equals(meta)) { // 이미 존재
                    continue;
                }
                changedCount++;
                totalChangedCount++;
                savedItemMetaMap.put(meta.getItemCode(), meta);
            }

            if (changedCount == 0) { // 모든 아이템이 변경 되지 않았으면 종료
                break;
            }
            Thread.sleep(1000);
        }

        if (totalChangedCount == 0) { // 변경없다면 아무 작업 안함.
            return;
        }

        List<String> finalResults = savedItemMetaMap.values().stream()
                .map(_38ComsItemMeta::toCsvFormatted)
                .collect(Collectors.toList());

        stockFileManager.write(FilePathEnvelop.create(FilePath._38_COMS_ITEM_META_0007), finalResults);
    }

    @Getter
    @Builder
    @EqualsAndHashCode
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class _38ComsItemMeta {
        private final Market market;
        private final String itemCode;
        private final String itemName;
        private final String businessType;
        private final String capital;
        private final LocalDate modifiedAt;

        private static _38ComsItemMeta from(final String row) {
            String[] split = row.split("\\|", 6);
            return builder()
                    .market(Market.valueOf(split[0]))
                    .itemCode(split[1])
                    .itemName(split[2])
                    .businessType(split[3])
                    .capital(split[4])
                    .modifiedAt(LocalDate.parse(split[5], DATE_FORMAT))
                    .build();
        }

        private String toCsvFormatted() {
            return this.market.name()
            + "|" + this.itemCode
            + "|" + this.itemName
            + "|" + this.businessType
            + "|" + this.capital
            + "|" + this.modifiedAt.format(DATE_FORMAT);
        }

        public String getModifiedYearAndMonthDay() {
            return modifiedAt.format(DATE_FORMAT);
        }
    }
}
