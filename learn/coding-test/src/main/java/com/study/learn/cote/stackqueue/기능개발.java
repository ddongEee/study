package com.study.learn.cote.stackqueue;

import java.util.*;

public class 기능개발 {
    public static void main(String[] args) {

    }

//    progresses = [1, 1, 1, 1]
//    speeds = [100, 50, 99, 100] 스피드가 1일때 완료까지 걸리는 날짜를 어떻게 계산했는지 한번 확인 바랍니다.
    public int[] solution(int[] progresses, int[] speeds) {
        int biggestDay = (int)Math.ceil((100 - progresses[0]) / (double)speeds[0]); // todo : 함정.

        int deployCount = 0;
        List<Integer> results = new ArrayList<>();
        for (int i = 0; i < progresses.length; i++) {
            int progress = progresses[i];
            int speed = speeds[i];
            int remainDays = (int)Math.ceil((100 - progress) / (double)speed);

            if (biggestDay < remainDays) {
                results.add(deployCount);
                biggestDay = remainDays;
                deployCount = 1;
            } else {
                deployCount+=1;
            }

        }

        if (deployCount != 0) {
            results.add(deployCount);
        }
        return results.stream()
                .mapToInt(Integer::intValue)
                .toArray();
    }
}
