package com.study.apps.poc.stock.domain.miraeasset;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.study.apps.poc.stock.domain.DynamicResponse;
import com.study.apps.poc.stock.domain.Initializer;
import com.study.apps.poc.stock.domain.ItemCategory;
import com.study.apps.poc.stock.domain.page38coms.util.StockFileManager;
import com.study.apps.poc.stock.infrastructure.FilePath;
import com.study.apps.poc.stock.infrastructure.FilePathEnvelop;
import lombok.*;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@SuppressWarnings("ALL")
@RequiredArgsConstructor
public class MiraeAssetPageDirector implements Initializer {
    private final StockFileManager stockFileManager;

    @Override
    public void preProcess() throws IOException {
        rewriteAllLoadedItemMetaPackage();
    }

    @Override
    public void afterProcess() {

    }

    public List<ItemMeta> loadItemMetas() throws IOException {
        return stockFileManager.loadLatestLines(FilePath.ITEM_META_0001).stream()
                .map(row -> ItemMeta.fromCsvFormat(row))
                .collect(Collectors.toList());
    }

    public ItemMetaPackage loadItemMetaPackageFromFile() throws IOException {
        List<ItemMeta> itemMetas = loadItemMetas();
        Map<ItemCategory, ItemMeta> itemMetaMap = itemMetas.stream()
                .collect(Collectors.toMap(ItemMeta::getItemCategory, Function.identity()));

        return ItemMetaPackage.builder()
                .itemMetas(itemMetas)
                .itemMetaMap(itemMetaMap)
                .build();
    }

    private ItemMetaPackage loadItemMetaPackage() throws JsonProcessingException {
        String cookies = generateCookies();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Cookie", cookies);

        HttpEntity<String> entity = new HttpEntity<>("", headers);
        final String url = "https://securities.miraeasset.com/code/wtscode.wjson";
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        if (!exchange.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("주식 호출 실패");
        }
        DynamicResponse dynamicResponse = DynamicResponse.of(exchange.getBody());
        return ItemMetaPackage.create(dynamicResponse);
    }

    public void rewriteAllLoadedItemMetaPackage() throws IOException {
        ItemMetaPackage itemMetaPackage = loadItemMetaPackage();
        stockFileManager.write(FilePathEnvelop.create(FilePath.ITEM_META_0001), itemMetaPackage.toFormattedResults());
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ItemMetaPackage {
        private final List<ItemMeta> itemMetas;
        private final Map<ItemCategory, ItemMeta> itemMetaMap;

        public static ItemMetaPackage create(final DynamicResponse dynamicResponse) {
            final List<ItemMeta> itemMetas = new ArrayList<>();
            final Map<ItemCategory, ItemMeta> itemMetaMap = new HashMap<>();
            Map<String, Object> resultMap = dynamicResponse.getResultMap();
            List<Object> stocks = (List<Object>) resultMap.get(ItemCategory.STOCK.name());
            for (Object stock : stocks) {
                Map<String, String> metaMap = (Map<String, String>) stock;
                ItemMeta meta = ItemMeta.builder()
                        .itemCategory(ItemCategory.STOCK)
                        .korName(metaMap.get("KOR_ITMN").replaceAll("(/|%)", ""))
                        .engName(metaMap.get("ENG_ITMN"))
                        .itemCode(metaMap.get("ITM_CD"))
                        .stnCode(metaMap.get("STNCODE"))
                        .build();
                if ("043500".equals(meta.getItemCode())) { // 드림텍 중복으로 예전꺼 삭제
                    continue;
                }
                itemMetas.add(meta);
                itemMetaMap.put(meta.getItemCategory(), meta);
            }
            return builder()
                    .itemMetas(itemMetas)
                    .itemMetaMap(itemMetaMap)
                    .build();
        }



        public List<String> toFormattedResults() {
            return this.itemMetas.stream()
                    .map(ItemMeta::toFormatedString)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ItemMeta {
        private final ItemCategory itemCategory;
        private final String korName;
        private final String engName;
        private final String itemCode;
        private final String stnCode;

        public String toFormatedString() {
            return itemCategory
            + "|" + korName
            + "|" + engName
            + "|" + itemCode
            + "|" + stnCode;
        }

        public static ItemMeta fromCsvFormat(final String row) {
            String[] split = row.split("\\|", 5);
            return builder()
                    .itemCategory(ItemCategory.valueOf(split[0]))
                    .korName(split[1])
                    .engName(split[2])
                    .itemCode(split[3])
                    .stnCode(split[4])
                    .build();
        }
    }

    private String generateCookies() {
        RestTemplate template = new RestTemplate();
        HttpEntity<Object> request = new HttpEntity<>("");

        final String url = "https://securities.miraeasset.com/hkt/hkt2101/p02.do";
        HttpEntity<String> response = template.exchange(url, HttpMethod.GET, request, String.class);
        HttpHeaders headers = response.getHeaders();
        RequiredCookies cookies = new RequiredCookies(headers.get(HttpHeaders.SET_COOKIE));
        return cookies.generateCookies();
    }

    private final static class RequiredCookies {
        private String ccsession;
        private String ccguid;
        private String wmonid;
        private String mireadw_d;

        public RequiredCookies(List<String> cookies) {
            for (String cookie : cookies) {
                if (cookie.contains("ccsession")) {
                    this.ccsession = cookie.split(";")[0];
                  continue;
                }
                if (cookie.contains("ccguid")) {
                    this.ccguid = cookie.split(";")[0];
                    continue;
                }
                if (cookie.contains("WMONID")) {
                    this.wmonid = cookie.split(";")[0];
                    continue;
                }
                if (cookie.contains("MIREADW_D")) {
                    this.mireadw_d = cookie.split(";")[0];
                    continue;
                }
            }
        }

        public String generateCookies() {
            return String.format("%s; %s %s; %s;", this.ccsession, this.ccguid, this.wmonid, this.mireadw_d);
        }
    }
}
