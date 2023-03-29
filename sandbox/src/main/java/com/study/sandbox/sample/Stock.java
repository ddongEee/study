package com.study.sandbox.sample;

//import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.libs.opencsv.SecretLoader;
import java.util.List;

// ref : https://www.data.go.kr/iim/api/selectAPIAcountView.do
public class Stock {
    private static final String FULL_URL = "https://apis.data.go.kr/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo?serviceKey=%s&numOfRows=2&resultType=json";
    private static final String RESULT_TYPE_JSON = "json";
    private static final int NUM_OF_ROWS = 10;

    public static void main(String[] args) throws Exception {
        String serviceKey = SecretLoader.findByKey(SecretLoader.SourceKey.DATA_GO_KR_API_KEY);
//        String response = HttpClient.get(String.format(FULL_URL, serviceKey));
        String response = "{\"response\":{\"header\":{\"resultCode\":\"00\",\"resultMsg\":\"NORMAL SERVICE.\"},\"body\":{\"numOfRows\":2,\"pageNo\":1,\"totalCount\":2059588,\"items\":{\"item\":[{\"basDt\":\"20230327\",\"srtnCd\":\"900110\",\"isinCd\":\"HK0000057197\",\"itmsNm\":\"이스트아시아홀딩스\",\"mrktCtg\":\"KOSDAQ\",\"clpr\":\"156\",\"vs\":\"0\",\"fltRt\":\"0\",\"mkp\":\"156\",\"hipr\":\"157\",\"lopr\":\"151\",\"trqu\":\"1661993\",\"trPrc\":\"255521206\",\"lstgStCnt\":\"291932050\",\"mrktTotAmt\":\"45541399800\"},{\"basDt\":\"20230327\",\"srtnCd\":\"900270\",\"isinCd\":\"HK0000214814\",\"itmsNm\":\"헝셩그룹\",\"mrktCtg\":\"KOSDAQ\",\"clpr\":\"327\",\"vs\":\"8\",\"fltRt\":\"2.51\",\"mkp\":\"320\",\"hipr\":\"332\",\"lopr\":\"309\",\"trqu\":\"550464\",\"trPrc\":\"175054223\",\"lstgStCnt\":\"82824858\",\"mrktTotAmt\":\"27083728566\"}]}}}}";
    }

    public static class StockPriceInfo {
        public Response response;

        public static final class Response {
            public Header header;
            public Body body;

            public static final class Header {
                public String resultCode; // "00"
                public String resultMsg;  // "NORMAL SERVICE."
            }

            public static final class Body {
                public long numOfRows;   // 2
                public long pageNo;      // 1
                public long totalCount;  // 2059588
                public Items items;

                public static final class Items {
                    public List<Item> item;
                    public static final class Item {
                        public String basDt;      // 기준일자    | "20230327"
                        public String srtnCd;     // 단축코드    | "900110"
                        public String isinCd;     // ISIN코드  | "HK0000057197"
                        public String itmsNm;     // 종목명     | "이스트아시아홀딩스"
                        public String mrktCtg;    // 시장구분    | "KOSDAQ"
                        public String clpr;       // 종가      | "156"
                        public String vs;         // 대비      | "0"
                        public String fltRt;      // 등락률     | "0"
                        public String mkp;        // 시가      | "156"
                        public String hipr;       // 고가      | "157"
                        public String lopr;       // 저가      | "151"
                        public String trqu;       // 거래량     | "1661993"
                        public String trPrc;      // 거래대금    | "255521206"
                        public String lstgStCnt;  // 상장주식수   | "291932050"
                        public String mrktTotAmt; // 시가총액    | "45541399800"
                    }
                }
            }
        }
    }
}
