package com.study.apps.poc.stock.domain.samsungpop;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.study.apps.poc.stock.domain.samsungpop.Constant.*;

//https://koscom.gitbook.io/open-api/onlineqna
@Slf4j
@Component
public class DataLoader {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class QueryCondition {
        private final String stockCode;
    }
    public StockPricePackage dosome(final QueryCondition condition) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        final String idx = "fid1004";
        final String gid = "1004";
        String body =
        "[" +
                "{" +
                    "\"idx\":\""+idx+"\"," +
                    "\"gid\":\""+gid+"\"," +
                    "\"fidCodeBean\":{" +
                        "\""+_3_종목코드+"\":\""+condition.getStockCode()+"\"" +
                        ",\"2158\":\"1000000\"" +
                        ",\"9101\":\"6\"" +
                        ",\"9104\":\"Q\"" +
                        ",\"9210\":\"0503\"" +
                    "}," +
                    "\"outFid\":\"4,5,6,7,8,22,23,24,9,500\"," +
                    "\"isList\":\"1\"," +
                    "\"order\":\"ASC\"," +
                    "\"reqCnt\":20," +
                    "\"actionKey\":\"0\"," +
                    "\"saveBufLen\":\"1\"," +
                    "\"saveBuf\":\"1\"" +
                "}" +
        "]";
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        final String url = "https://www.samsungpop.com/wts/fidBuilder.do";
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        ObjectMapper mapper = new ObjectMapper();

        Response response = mapper.readValue(exchange.getBody(), Response.class);
        if (!exchange.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("주식 호출 실패");
        }
        log.info("response : {}", response);
        StockPricePackage stockPricePackage = response.generateStockPricePackage(idx);
        return stockPricePackage;
    }

    @SuppressWarnings("unchecked")
    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Response {
        private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
        @JsonAnySetter
        Map<String, Object> resultMap = new LinkedHashMap<>();

        public String getErrorMessage() {
            return (String) this.resultMap.get("errorMsg");
        }

        private StockPricePackage generateStockPricePackage(final String idx) {
            final List<StockPrice> stockPrices = new ArrayList<>();
            final List<Object> objects = ((Map<String, List<Object>>) this.resultMap.get(idx)).get("data");
            for (Object object : objects) {
                final Map<String, String> convertedMap = (Map<String, String>) object;
                stockPrices.add(
                        StockPrice.builder()
                        .date(LocalDate.parse(convertedMap.get(_500_일자), DATE_FORMAT))   // 일자  : "20220128"
                        .fluctuationRange(Long.parseLong(convertedMap.get(_6_등락폭)))     // 등락폭 : "250"
                        .fluctuationRate(Double.parseDouble(convertedMap.get(_7_등락률)))  // 등락률 : "1.03"
                        .transactionVolumn(Long.parseLong(convertedMap.get(_8_거래량)))    // 거래량 : "116827"
                        .staringPrice(Long.parseLong(convertedMap.get(_22_시가)))          // 시가  : "24200"
                        .endingPrice(Long.parseLong(convertedMap.get(_4_종가)))            // 종가  : "24550"
                        .highestPrice(Long.parseLong(convertedMap.get(_23_고가)))          // 고가  : "24800"
                        .lowestPrice(Long.parseLong(convertedMap.get(_24_저가)))           // 저가  : "22700"
//                                                   convertedMap.get("5");          // ???  : "6"
//                                                   convertedMap.get("9");          // ???  : "2797"
                        .build()
                );
            }

            return StockPricePackage.builder()
                    .name("")
                    .code("")
                    .stockPrices(stockPrices)
                    .build();
        }
    }

    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class StockPricePackage {
        private final String name;
        private final String code;
        private final List<StockPrice> stockPrices;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class StockPrice {
        private final LocalDate date;
        private final Long fluctuationRange;
        private final Double fluctuationRate;
        private final Long transactionVolumn;
        private final Long staringPrice;
        private final Long endingPrice;
        private final Long highestPrice;
        private final Long lowestPrice;
    }
}
