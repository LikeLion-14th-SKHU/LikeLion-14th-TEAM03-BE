package com.skincare.plan.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlanFinishRequestDto {

    // 앵커링 슬라이더로 입력받은 after_scores
    // trouble_scores의 Max값 항목 1개
    @Pattern(regexp = "^(피지량|댕김|여드름|붉은기)$",
            message = "afterScoreKey는 피지량, 댕김, 여드름, 붉은기 중 하나여야 합니다")
    private String afterScoreKey;

    @Min(value = 1, message = "점수는 1 이상이어야 합니다")
    @Max(value = 10, message = "점수는 10 이하여야 합니다")
    private Double afterScoreValue;
}