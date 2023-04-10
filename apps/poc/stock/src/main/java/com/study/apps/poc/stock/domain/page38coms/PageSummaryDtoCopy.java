package com.study.apps.poc.stock.domain.page38coms;//package com.study.apps.poc.stock.domain.page38coms;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
//import java.util.Objects;
//
//import static com.study.apps.poc.stock.domain.page38coms.pageloader.DemandForecastingResultPageLoader.DemandForecastingResult;
//import static com.study.apps.poc.stock.domain.page38coms.pageloader.DemandForecastingSchedulePageLoader.DemandForecastingSchedule;
//import static com.study.apps.poc.stock.domain.page38coms.pageloader.NewListingItemPageLoader.NewListingItem;
//import static com.study.apps.poc.stock.domain.page38coms.pageloader.PublicSubscriptionSchedulePageLoader.PublicSubscriptionSchedule;
//
//@Getter
//@NoArgsConstructor(access = AccessLevel.PRIVATE)
//public class PageSummaryDtoCopy {
//    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
//
//    private String companyName;                       // 종목명        : LG에너지솔루션(유가)
//    private String finalOfferingPrice;                // 공모가(원)     : 300,000
//    private String chargedOrganization;               // 주관사        : KB증권,대신증권,신한금융,미래에셋증권,신영증권,하나금융,하이투자
//
//    private String demandForecastingDate;             // 수요예측일     : 2022.01.11~01.12
//    private LocalDate demandForecastingDateFrom;
//    private LocalDate demandForecastingDateTo;
//    private String forecastingDate;                   // 예측일        : 2022.01.11
//
//    private String hopePrice;                         // 희망공모가     : 257,000~300,000
//    private String hopeContestPrice;                  // 공모희망가(원)  : 257,000~300,000
//    private String hopedContestPrice;                 // 희망공모가     : 257,000~300,000
//    private String demandTotalPrice;                  // 공모금액(백만)  : 10,922,500
//    private String totalContestPrice;                 // 공모금액(백만원) : 10,922,500
//
//    // 기관 공모관련
//    private String orgCompetitionRate;                // 기관경쟁률      :  2023.37:1
//    private String obligationToHoldIt;                // 의무보유확약     : 77.38%
//
//    // 개인 청약관련
//    private String personalContestDate;               // 공모주일정 : 2022.01.18~01.19
//    private LocalDate personalContestDateFrom;        // 공모주일정 : 2022.01.18~01.19
//    private LocalDate personalContestDateTo;          // 공모주일정 : 2022.01.18~01.19
//    private String subscriptionCompetitionRate;       // 청약경쟁률 : 69.34:1	or ()
//
//    // 수요예측일정
//    private boolean demandForecastingScheduleUpdated = false;
////    private String demandForecastingDate;           // 수요예측일     : 2022.01.11~01.12
////    private String hopePrice;                       // 희망공모가     : 257,000~300,000
////    private String demandTotalPrice;                // 공모금액(백만)  : 10,922,500
//
//    // 수요예측결과
//    private boolean demandForecastingResultUpdated = false;
////    private String forecastingDate;                 // 예측일              : 2022.01.11
////    private String hopeContestPrice;                // 공모희망가(원)        : 257,000~300,000
////    private String totalContestPrice;               // 공모금액(백만원)       : 10,922,500
////    private String orgCompetitionRate;              // 기관경쟁률 : 1:100    : 2023.37:1
////    private String obligationToHoldIt;              // 의무보유확약 : 1.12 %  : 77.38%
//
//    // 공모청약일정
//    private boolean publicSubscriptionScheduleUpdated = false;
////    private String contestDate;                     // 공모주일정 : 2022.01.18~01.19
////    private String hopedContestPrice;               // 희망공모가 : 257,000~300,000
////    private String subscriptionCompetitionRate;     // 청약경쟁률 : 69.34:1	or ()
//
//    // 신규상장
//    private boolean newListingItemUpdated = false;
//    private String newListingDate;                    // 신규 상장일           : 2022/01/27
//    private String currentPrice;                      // 현재가(원)           : 450,000
//    private String comparedToPreviousDayPercent;      // 전일비(%)            : -10.89%
//    private String comparedOfferingPricePercent;      // 공모가대비 등락률(%)   : 50%
//    private String firstStartingPrice;                // 시초가(원)           : 597,000
//    private String FirstStaringPricePerOfferingPrice; // 시초/공모(%)         : 99%
//    private String firstEndingPrice;                  // 첫날종가(원)          : 505,000 or (예정)
//
//    @JsonIgnore
//    public boolean isTriableSubscription() {
//        LocalDate now = LocalDate.now();
//        return now.isEqual(this.personalContestDateTo) || now.isBefore(this.personalContestDateTo);
//    }
//
//    /**
//     * 데이터 분석용
//     */
//    @JsonIgnore
//    public boolean allDoneProcessed() {
//        if(!(demandForecastingScheduleUpdated && demandForecastingResultUpdated && publicSubscriptionScheduleUpdated && newListingItemUpdated)) {
//            return false; // 하나라도 데이터 업데이트 안되있으면.. 진행중
//        }
//
//        if(firstEndingPrice.equals("예정")) {
//            return false; // 첫날종가 아직 나오지 않았다면.. 진행중..
//        }
//
//        if(subscriptionCompetitionRate.equals("")) {
//            return false; // 개인 청약 경쟁률 안떳으면.. 진행중..
//        }
//
//        if(orgCompetitionRate.equals("")) {
//            return false; // 기관 경쟁률 안떳으면.. 진행중..
//        }
//
//        return true;
//    }
//
////    public boolean notEqualsAll() {
////        return !equalsAll();
////    }
////
////    private String removeDirty(final String text) {
////        return text == null ? null : text.replaceAll("\\(확정\\)", "");
////    }
////
////    public boolean equalsAll() {
////        return removeDirty(definedPrice).equals(removeDirty(fixContestPrice))
////                && removeDirty(definedPrice).equals(removeDirty(fixedContestPrice))
////                && removeDirty(definedPrice).equals(removeDirty(offeringPrice));
////    }
////
////    public String printComp() {
////        return companyName
////                + ":" + removeDirty(definedPrice)
////        + ":" + removeDirty(fixContestPrice)
////        + ":" + removeDirty(fixedContestPrice)
////        + ":" + removeDirty(offeringPrice) + "\n";
////    }
//
//    public boolean notScheduledDemandForecastingDate() {
//        return !scheduledDemandForecastingDate();
//    }
//
//    public boolean scheduledDemandForecastingDate() {
//        String[] splitDate = this.demandForecastingDate.split("~", 2);
//        String firstDate = splitDate[0];
//        if (firstDate.equals("-")) {
//            return false;
//        }
//
//        try {
//
//            LocalDate parse = LocalDate.parse(firstDate, DATE_FORMAT);
//            return true;
//        } catch (DateTimeParseException exception) {
//            return false;
//        }
//    }
//
//    public String print() {
//        return this.companyName + ":" + this.refinedDemandForecastingDateFrom() + "\n";
//    }
//
//
//
//    // 아래는 생성 및 업데이트 관련
//    public static PageSummaryDtoCopy creatEmpty() {
//        return new PageSummaryDtoCopy();
//    }
//
//    public PageSummaryDtoCopy update(DemandForecastingSchedule input) {
//        this.demandForecastingScheduleUpdated = true;
//        this.companyName = input.getCompanyName();
//        updateDemandForecastingDate(input.getDemandForecastingDate());
//        this.hopePrice = input.getHopePrice();
//        updateOfferingPrice(input.getDefinedPrice());
//        this.demandTotalPrice = input.getDemandTotalPrice();
//        updateChargedOrganization(input.getChargedOrganization());
//        return this;
//    }
//
//    public PageSummaryDtoCopy update(DemandForecastingResult input) {
//        this.demandForecastingResultUpdated = true;
//        this.companyName = input.getCompanyName();
//        this.forecastingDate = input.getForecastingDate();
//        this.hopeContestPrice = input.getHopeContestPrice();
//        updateOfferingPrice(input.getFixContestPrice());
//        this.totalContestPrice = input.getTotalContestPrice();
//        this.orgCompetitionRate = input.getOrgCompetitionRate();
//        this.obligationToHoldIt = input.getObligationToHoldIt();
//        updateChargedOrganization(input.getChargedOrganization());
//        return this;
//    }
//
//    public PageSummaryDtoCopy update(PublicSubscriptionSchedule input) {
//        this.publicSubscriptionScheduleUpdated = true;
//        this.companyName = input.getCompanyName();
//        this.personalContestDate = input.getContestDate();
//        updateContestDate(input.getContestDate());
//        updateOfferingPrice(input.getFixedContestPrice());
//        this.hopedContestPrice = input.getHopedContestPrice();
//        this.subscriptionCompetitionRate = input.getSubscriptionCompetitionRate();
//        updateChargedOrganization(input.getChargedOrganization());
//        return this;
//    }
//
//    public PageSummaryDtoCopy update(NewListingItem input) {
//        this.newListingItemUpdated = true;
//        this.companyName = input.getCompanyName();
//        this.newListingDate = input.getNewListingDate();
//        this.currentPrice = input.getCurrentPrice();
//        this.comparedToPreviousDayPercent = input.getComparedToPreviousDayPercent();
//        updateOfferingPrice(input.getOfferingPrice());
//        this.comparedOfferingPricePercent = input.getComparedOfferingPricePercent();
//        this.firstStartingPrice = input.getFirstStartingPrice();
//        this.FirstStaringPricePerOfferingPrice = input.getFirstStaringPricePerOfferingPrice();
//        this.firstEndingPrice = input.getFirstEndingPrice();
//        return this;
//    }
//
//    // support methods
//    private void updateChargedOrganization(final String chargedOrganization) {
//        if (chargedOrganization == null) {
//            return;
//        }
//        this.chargedOrganization = chargedOrganization;
//    }
//
//    private void updateOfferingPrice(final String offeringPrice) {
//        if (offeringPrice == null || offeringPrice.equals("-")) {
//            return;
//        }
//
//        String refinedOfferingPrice = offeringPrice.replaceAll("\\(예정\\)", "")
//                                                   .replaceAll("\\(확정\\)", "");
//        if (this.finalOfferingPrice == null) {
//            this.finalOfferingPrice = refinedOfferingPrice;
//            return;
//        }
//
//        int finalOfferingPriceInteger = Integer.parseInt(this.finalOfferingPrice);
//        int refinedOfferingPriceInteger = Integer.parseInt(refinedOfferingPrice);
//        this.finalOfferingPrice = finalOfferingPriceInteger > refinedOfferingPriceInteger ? this.finalOfferingPrice : refinedOfferingPrice;
//    }
//
//    private void updateDemandForecastingDate(final String demandForecastingDate) {
//        this.demandForecastingDate = demandForecastingDate;
//        String[] splitDate = demandForecastingDate.split("~", 2);
//
//        String fromDate = splitDate[0];
//        String toDate = splitDate[1];
//        if (fromDate != null && !fromDate.equals("-")) {
//            this.demandForecastingDateFrom = LocalDate.parse(fromDate, DATE_FORMAT);
//        }
//
//        if (toDate != null && !toDate.equals("-")) {
//            String yyyy = fromDate.split("\\.", 3)[0];
//            this.demandForecastingDateTo = LocalDate.parse(yyyy+"."+toDate, DATE_FORMAT);
//        }
//    }
//
//    private void updateContestDate(final String contestDate) {
//        this.personalContestDate = contestDate;
//        String[] splitDate = contestDate.split("~", 2);
//        String fromDate = splitDate[0];
//        String toDate = splitDate[1];
//
//        if (fromDate != null && !fromDate.equals("-")) {
//            this.personalContestDateFrom = LocalDate.parse(fromDate, DATE_FORMAT);
//        }
//
//        if (toDate != null && !toDate.equals("-")) {
//            String yyyy = fromDate.split("\\.", 3)[0];
//            this.personalContestDateTo = LocalDate.parse(yyyy+"."+toDate, DATE_FORMAT);
//        }
//    }
//
//    public LocalDate refinedDemandForecastingDateFrom() {
//        return Objects.requireNonNullElseGet(this.demandForecastingDateFrom, () -> LocalDate.of(2004, 9, 5));
//    }
//
//    @JsonIgnore
//    public LocalDate getPersonalContestDateTo() {
//        return this.personalContestDateTo;
//    }
//}
