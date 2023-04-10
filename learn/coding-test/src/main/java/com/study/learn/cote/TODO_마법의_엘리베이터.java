package com.study.learn.cote;

//https://school.programmers.co.kr/learn/courses/30/lessons/148653
public class TODO_마법의_엘리베이터 {
    public static void main(String[] args) {
        System.out.println(solution(16));
        System.out.println(solution(2554));
        System.out.println(solution(921));
    }
    public static int solution(int storey) {
        String currentFloor = String.valueOf(storey);
        String[] splitCurrentFloor = currentFloor.split("");
        int answer = 0;
        for (String a : splitCurrentFloor) {
            Integer value = Integer.valueOf(a);
            if ( value > 5) {
                answer += 10 - value + 1;
            } else {
                answer += value;
            }
        }
        return answer;
    }
}
