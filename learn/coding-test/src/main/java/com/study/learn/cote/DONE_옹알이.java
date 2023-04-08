package com.study.learn.cote;

import java.util.ArrayList;
import java.util.List;

public class DONE_옹알이 {

    class Solution {
        private final List<String> cases;
        public Solution() {
            this.cases = makeAllCase();
        }

        private List<String> makeAllCase() {
            List<String> words = new ArrayList();
            words.add("aya");
            words.add("ye");
            words.add("woo");
            words.add("ma");
            words.add("");

            List<String> cases = new ArrayList();
            for (String word1 : words) {
                for (String word2 : words) {
                    for (String word3 : words) {
                        for (String word4 : words) {
                            for (String word5 : words) {
                                String result = word1 + word2 + word3 + word4 + word5;
                                if (!cases.contains(result)) {
                                    cases.add(result);
                                }
                            }
                        }
                    }
                }
            }
            System.out.println(cases.toString());
            return cases;
        }

        public int solution(String[] babbling) {
            int answer = 0;
            for (String bab : babbling) {
                if (canPronaunce(bab)) {
                    answer+=1;
                }
            }
            return answer;
        }

        // return 되는 값은 1~100 이 될수있음!
        // 1. "aya", "ye", "woo", "ma" 의 모든 조합을 만들어 contains 만 확인 할수 있지만, 동일 단어 반복 케이스 있음.
        // - 하나의 단어 길이가 15개라서, 조합이 생각보다 많아 질수있음 << 한번씩만 들어온다는 제약사항 있음.
        // - 위 제약사항 기반으로  11개 부터는 발음할수 없음, 1번씩만나오기 때문에
        // 2. ??? 다른방법 생각해보기
        //  - stack?
        public boolean canPronaunce(String word) {
            if(word.length() > 11) {
                return false;
            }
            return this.cases.contains(word);
        }
    }
}
