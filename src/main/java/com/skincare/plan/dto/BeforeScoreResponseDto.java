package com.skincare.plan.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BeforeScoreResponseDto {
    private final String afterScoreKey;   // MAX값 항목명
    private final Double beforeScoreValue; // 온보딩 시 해당 항목 점수
}