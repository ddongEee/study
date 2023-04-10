package com.study.learn.cote;

//https://school.programmers.co.kr/learn/courses/30/lessons/147355
public class 크기가_작은_부분문자열 {
    // valueof
    public static void main(String[] args) {
//        solutionV2("3141592" 	,"271");
//        solutionV2("500220839878" 	,"7");
        solutionV2("10203" 	,"15");
    }

    public static int solution(String t, String p) {
        int pLength = p.split("").length;
        String[] splitT = t.split("");
        int tLength = splitT.length;
        int answer = 0;
        int to = tLength - pLength;
        for (int i = 0; i <= to; i++) { // todo 체크.. 어떻게 생각했나. 어떤식으로 생각해야 하나?
            String substring = t.substring(i, i + pLength);
            System.out.println(substring);
            StringBuilder compTvalue = new StringBuilder();
            for (int j = 0; j < pLength ; j++) {
                compTvalue.append(splitT[i + j]);
            }
            if (Integer.parseInt(compTvalue.toString()) <= Integer.parseInt(p)) {
                answer+=1;
            }
        }
        return answer;
    }

    public static int solutionV2(String t, String p) {
        int pLength = p.length();
        int answer = 0;
        int to = t.length() - p.length();
        for (int i = 0; i <= to; i++) { // todo 체크.. 어떻게 생각했나. 어떤식으로 생각해야 하나?
            String compTvalue = t.substring(i, i + pLength);
            System.out.println(compTvalue);
            if (Integer.parseInt(compTvalue) <= Integer.parseInt(p)) {
                answer+=1;
            }
        }
        return answer;
    }
}
