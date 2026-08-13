package com.skincare.onboarding.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SurveyAnswerDto {

    // A 섹션 - 피지/모공
    @Min(value = 1, message = "a1은 1 이상이어야 합니다")
    @Max(value = 4, message = "a1은 4 이하여야 합니다")
    private int a1;

    @Min(value = 1, message = "a2는 1 이상이어야 합니다")
    @Max(value = 4, message = "a2는 4 이하여야 합니다")
    private int a2;

    @Min(value = 1, message = "a3는 1 이상이어야 합니다")
    @Max(value = 4, message = "a3는 4 이하여야 합니다")
    private int a3;

    @Min(value = 1, message = "a4는 1 이상이어야 합니다")
    @Max(value = 4, message = "a4는 4 이하여야 합니다")
    private int a4;

    // B 섹션 - 보습
    @Min(value = 1, message = "b1은 1 이상이어야 합니다")
    @Max(value = 4, message = "b1은 4 이하여야 합니다")
    private double b1;

    @Min(value = 1, message = "b2는 1 이상이어야 합니다")
    @Max(value = 4, message = "b2는 4 이하여야 합니다")
    private double b2;

    @Min(value = 1, message = "b3는 1 이상이어야 합니다")
    @Max(value = 4, message = "b3는 4 이하여야 합니다")
    private double b3;

    // C 섹션 - 복합성 최우선
    @Pattern(regexp = "^(zone_diff_true|zone_diff_false)$",
            message = "c1은 zone_diff_true 또는 zone_diff_false여야 합니다")
    private String c1;

    // D 섹션 - 민감성
    @Min(value = 1, message = "d1은 1 이상이어야 합니다")
    @Max(value = 4, message = "d1은 4 이하여야 합니다")
    private double d1;

    @Min(value = 1, message = "d2는 1 이상이어야 합니다")
    @Max(value = 4, message = "d2는 4 이하여야 합니다")
    private double d2;

    // E 섹션 - 붉은기
    @Min(value = 1, message = "e1은 1 이상이어야 합니다")
    @Max(value = 4, message = "e1은 4 이하여야 합니다")
    private double e1;

    @Min(value = 1, message = "e2는 1 이상이어야 합니다")
    @Max(value = 4, message = "e2는 4 이하여야 합니다")
    private double e2;

    // F 섹션 - 여드름
    @Min(value = 1, message = "f1은 1 이상이어야 합니다")
    @Max(value = 4, message = "f1은 4 이하여야 합니다")
    private int f1;

    private Integer f2; // f1=1이면 null

    // G 섹션 - 흔적
    @Min(value = 1, message = "g1은 1 이상이어야 합니다")
    @Max(value = 4, message = "g1은 4 이하여야 합니다")
    private int g1;

    // H 섹션 - 안전/이력
    private List<String> h1;

    private String h2;

    @Pattern(regexp = "^(current|past|irritated|never)$",
            message = "h3은 current, past, irritated, never 중 하나여야 합니다")
    private String h3;
}