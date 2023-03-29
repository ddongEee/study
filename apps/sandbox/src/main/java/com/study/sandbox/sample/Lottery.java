package com.study.sandbox.sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.study.libs.opencsv.CsvHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Lottery {
    public static final String TEMP_FILE_DIR = System.getProperty("user.home") + "/Projects/personal/study/apps/sandbox/temp-files";
    private static final int START_SEARCHING_LOTTO_ROUND_NUMBER = 1061;
    private static final int CURRENT_LOTTO_ROUND_NUMBER = 1060; // todo : 변경필요
    private static final long CALL_SLEEP_MILLIS = 200;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
//        searchAndMakeCSV();
        Path filePath = Paths.get(TEMP_FILE_DIR + "lotto.csv");
        List<LottoCSV> lottos = CsvHandler.readFrom(filePath, LottoCSV.class);
        System.out.println(lottos);
    }

    private static void searchAndMakeCSV() throws Exception {
        List<LottoResponse> lottoResponses = requestLottoData();
        Collections.sort(lottoResponses);
        Path filePath = Paths.get(TEMP_FILE_DIR + "lotto.csv");
        CsvHandler.initFile(filePath);
        List<LottoCSV> lottoCSVList = parseLottoResponseToCsv(lottoResponses);
        CsvHandler.writeTo(filePath, lottoCSVList);
    }

    private static List<LottoResponse> requestLottoData() throws JsonProcessingException, InterruptedException {
        List<LottoResponse> lottoResponses = new ArrayList<>();
        for (int no = START_SEARCHING_LOTTO_ROUND_NUMBER; no <= CURRENT_LOTTO_ROUND_NUMBER; no++) {
            String response = HttpClient.get("https://www.dhlottery.co.kr/common.do?method=getLottoNumber&drwNo="+no);
            LottoResponse parsedResponse = mapper.readerFor(LottoResponse.class)
                    .readValue(response);
            if (parsedResponse.isFailed()) {
                throw new RuntimeException("복권 로드 실패!!! 회차 : " + no);
            }
            lottoResponses.add(parsedResponse);
            Thread.sleep(CALL_SLEEP_MILLIS);
            System.out.printf("[Until:%s] CALLED NUMBER : %s%n", CURRENT_LOTTO_ROUND_NUMBER, no);
        }
        return lottoResponses;
    }

    private static List<LottoCSV> parseLottoResponseToCsv(List<LottoResponse> lottoResponses) {
        List<LottoCSV> results = new ArrayList<>();
        for (LottoResponse response : lottoResponses) {
            LottoCSV lottoCSV = new LottoCSV();
            lottoCSV.drewRoundNumber = response.drwNo;
            lottoCSV.drewAt = LocalDate.parse(response.drwNoDate);
            lottoCSV.totalSellAmount = response.totSellamnt;
            lottoCSV.firstWinnerAmount = response.firstWinamnt;
            lottoCSV.firstPrizeWinnerNumber = response.firstPrzwnerCo;
            lottoCSV.drewNumber1 = response.drwtNo1;
            lottoCSV.drewNumber2 = response.drwtNo2;
            lottoCSV.drewNumber3 = response.drwtNo3;
            lottoCSV.drewNumber4 = response.drwtNo4;
            lottoCSV.drewNumber5 = response.drwtNo5;
            lottoCSV.drewNumber6 = response.drwtNo6;
            lottoCSV.drewBonusNumber = response.bnusNo;
            results.add(lottoCSV);
        }
        return results;
    }

    public static final class LottoResponse implements Comparable<LottoResponse>{
        private static final String SUCCESS_CODE = "success";
        public String returnValue;  // 요청결과           -  "success",
        public long drwNo;          // 로또회차            -  861,
        public String drwNoDate;    // 날짜              -  "2019-06-01",
        public long totSellamnt;    // 총상금액           -  81032551000,
        public long firstWinamnt;   // 1등 상금액         -  4872108844,
        public long firstPrzwnerCo; // 1등 당첨인원        -  4,
        public long drwtNo1;        // 로또번호 1         -  11
        public long drwtNo2;        // 로또번호 2         -  17,
        public long drwtNo3;        // 로또번호 3         -  19,
        public long drwtNo4;        // 로또번호 4         -  21,
        public long drwtNo5;        // 로또번호 5         -  22,
        public long drwtNo6;        // 로또번호 6         -  25,
        public long bnusNo;         // 로또 보너스 번호     -  24,
        public long firstAccumamnt; // ???              -  19488435376,

        public boolean isFailed() {
            return !returnValue.equals(SUCCESS_CODE);
        }

        @Override
        public int compareTo(LottoResponse o) {
            return (int) o.drwNo;
        }
    }

    public static final class LottoCSV {
        @CsvBindByName(column ="drewRoundNumber")
        public long drewRoundNumber;        // 로또회차           -  861
        @CsvDate("yyyy-MM-dd")
        @CsvBindByName(column ="drewAt")
        public LocalDate drewAt;            // 날짜              -  "2019-06-01"
        @CsvBindByName(column ="totalSellAmount")
        public long totalSellAmount;        // 총상금액           -  81032551000
        @CsvBindByName(column ="firstWinnerAmount")
        public long firstWinnerAmount;      // 1등 상금액         -  4872108844
        @CsvBindByName(column ="firstPrizeWinnerNumber")
        public long firstPrizeWinnerNumber; // 1등 당첨인원        -  4
        @CsvBindByName(column ="drewNumber1")
        public long drewNumber1;            // 로또번호 1         -  1
        @CsvBindByName(column ="drewNumber2")
        public long drewNumber2;            // 로또번호 2         -  17
        @CsvBindByName(column ="drewNumber3")
        public long drewNumber3;            // 로또번호 3         -  19
        @CsvBindByName(column ="drewNumber4")
        public long drewNumber4;            // 로또번호 4         -  21
        @CsvBindByName(column ="drewNumber5")
        public long drewNumber5;            // 로또번호 5         -  22
        @CsvBindByName(column ="drewNumber6")
        public long drewNumber6;            // 로또번호 6         -  25
        @CsvBindByName(column ="drewBonusNumber")
        public long drewBonusNumber;        // 로또 보너스 번호     -  24

        public LottoCSV() {}
    }

    public static final class Lotto {
        public long drewRoundNumber;        // 로또회차           -  861
        public LocalDate drewAt;            // 날짜              -  "2019-06-01"
        public long totalSellAmount;        // 총상금액           -  81032551000
        public long firstWinnerAmount;      // 1등 상금액         -  4872108844
        public long firstPrizeWinnerNumber; // 1등 당첨인원        -  4
        public long drewNumber1;            // 로또번호 1         -  1
        public long drewNumber2;            // 로또번호 2         -  17
        public long drewNumber3;            // 로또번호 3         -  19
        public long drewNumber4;            // 로또번호 4         -  21
        public long drewNumber5;            // 로또번호 5         -  22
        public long drewNumber6;            // 로또번호 6         -  25
        public long drewBonusNumber;        // 로또 보너스 번호     -  24
    }
}
