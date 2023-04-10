package com.study.learn.cote.stackqueue;
import java.util.*;

//https://school.programmers.co.kr/learn/courses/30/lessons/12906
public class 같은_숫자는_싫어 {

    public static void main(String[] args) {

    }

    public int[] solution(int []arr) {
        Queue<Integer> dupRemovedQueue = new LinkedList<>();
        int wrote = -1;
        for (int i : arr) {
            if (wrote!= i) {
                wrote = i;
                dupRemovedQueue.add(i);
            }
        }
        return dupRemovedQueue.stream()
                .mapToInt(Integer::intValue)
                .toArray();
    }
}
