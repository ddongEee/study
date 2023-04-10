package com.study.learn.cote;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// https://school.programmers.co.kr/learn/courses/30/lessons/150370
public class CHECK_개인정보_수집_유효기간 {
    public static void main(String[] args) {
        String[] terms = new String[]{"A 6", "B 12", "C 3"};
        String[] privacies = new String[]{"2021.05.02 A", "2021.07.01 B", "2022.02.19 C", "2022.02.20 C"};
        int[] solution = Solution.solution("2022.05.19", terms, privacies);
        System.out.println(solution);
    }
    // YYYY 는 최소 2000 이므로 . 2000.01.01 을 기준으로 계산하기 << 필요없을듯
// 1차원적으로 길이 비교.
    static class Solution {
        // 1차원으로 변경하여 비교를 쉽게
        public static int[] solution(String today, String[] terms, String[] privacies) {
            // term 의 값을 일자수로 변환
            Map<String, Integer> termMap = new HashMap();
            for (String term : terms) {
                String[] termAndExpMonth = term.split(" ");
                Integer toDay = Integer.valueOf(termAndExpMonth[1])*28;
                termMap.put(termAndExpMonth[0], toDay);
            }
            // 프라이버시마다 index. 숫자로 변환 . list <integer> , index 는 result .
            List<Integer> expDateByPrivacy = new ArrayList();
            for (String privacy : privacies) {
                String[] dateAndTerm = privacy.split(" ");
                String[] dates = dateAndTerm[0].split("\\.");
                Integer bar = Integer.valueOf(dates[2]) + Integer.valueOf(dates[1])*28 +Integer.valueOf(dates[0])*28*12;
                Integer plusDueDay = termMap.get(dateAndTerm[1]);
                expDateByPrivacy.add(bar + plusDueDay);
            }

            // today 숫자로 변환
            String[] todayDates = today.split("\\.");
            Integer todayBar = Integer.valueOf(todayDates[2]) + Integer.valueOf(todayDates[1])*28 +Integer.valueOf(todayDates[0])*28*12;


            List<Integer> results = new ArrayList<>();
            for (int i = 0; i < expDateByPrivacy.size(); i++) {
                if (expDateByPrivacy.get(i) <= todayBar) { // todo : 요기가 의심스러움
                    results.add(i+1);
                }
            }

            // 프라이버시를 돌면서 today 보다 큰것들을 result list 넣어주기
            int[] answer = new int[results.size()];
            for (int i = 0; i < results.size(); i++) {
                answer[i] = results.get(i);
            }

            return answer;
        }
    }
}
