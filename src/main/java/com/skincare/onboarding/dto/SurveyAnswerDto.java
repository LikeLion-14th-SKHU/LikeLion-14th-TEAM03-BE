package com.skincare.onboarding.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SurveyAnswerDto {

    // A 섹션 - 피지/모공
    private int a1, a2, a3, a4;

    // B 섹션 - 보습
    private double b1, b2, b3;

    // C 섹션 - 복합성 최우선
    private String c1; // "zone_diff_true" or "zone_diff_false"

    // D 섹션 - 민감성
    private double d1, d2;

    // E 섹션 - 붉은기
    private double e1, e2;

    // F 섹션 - 여드름
    private int f1;
    private Integer f2; // f1=1이면 null

    // G 섹션 - 흔적
    private int g1;

    // H 섹션 - 안전/이력
    private List<String> h1; // 복수 선택
    private String h2; // "none" or "바르는약" or "먹는약" or "둘다"
    private String h3; // "current" or "past" or "irritated" or "never"
}