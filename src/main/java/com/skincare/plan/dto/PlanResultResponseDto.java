package com.skincare.plan.dto;

import com.skincare.plan.entity.PlanResult;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PlanResultResponseDto {

    private final String journeySummary;
    private final String improvementPoints;
    private final String recommendationNext;
    private final Integer todoCompletionRate;
    private final String afterScoreKey;   // 추가
    private final Double afterScoreValue; // 추가
    private final LocalDateTime createdAt;

    public PlanResultResponseDto(PlanResult planResult) {
        this.journeySummary = planResult.getJourneySummary();
        this.improvementPoints = planResult.getImprovementPoints();
        this.recommendationNext = planResult.getRecommendationNext();
        this.todoCompletionRate = planResult.getTodoCompletionRate();
        this.afterScoreKey = planResult.getAfterScoreKey();
        this.afterScoreValue = planResult.getAfterScoreValue();
        this.createdAt = planResult.getCreatedAt();
    }
}