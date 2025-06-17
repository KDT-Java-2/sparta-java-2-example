package com.sparta.bootcamp.java_2_example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JavaProgramming {
    public static void main(String[] args) {
        List<String> languages = Arrays.asList("Java", "Python", "Go", "JavaScript");

        List<String> result = languages.stream()
                .filter(lang -> lang.length() > 3) // 1. 길이가 3보다 큰 언어 필터링
                .peek(lang -> System.out.println("필터링 후: " + lang)) // 2. 필터링된 결과 엿보기
                .map(String::toUpperCase) // 3. 대문자로 변환
                .peek(lang -> System.out.println("대문자 변환 후: " + lang)) // 4. 변환된 결과 엿보기
                .collect(Collectors.toList()); // 5. 최종 연산 (이 코드가 없으면 peek은 동작 안 함)

        System.out.println("\n최종 결과 리스트: " + result);
    }
}