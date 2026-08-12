package com.skincare.plan.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlanFinishRequestDto {

    // 앵커링 슬라이더로 입력받은 after_scores
    // trouble_scores의 Max값 항목 1개
    private String afterScoreKey;   // 예: "피지량", "댕김", "여드름", "붉은기"
    private Double afterScoreValue; // 1~10점
}