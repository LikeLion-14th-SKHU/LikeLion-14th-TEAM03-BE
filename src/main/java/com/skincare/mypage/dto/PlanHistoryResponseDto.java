package com.skincare.mypage.dto;

import com.skincare.onboarding.entity.Onboarding;
import com.skincare.plan.entity.PlanResult;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PlanHistoryResponseDto {

    private final Long onboardingId;
    private final String purpose;
    private final LocalDate goalDate;
    private final String baseType;
    private final Boolean isActive;
    private final String createdAt;
    private final String journeySummary;
    private final String improvementPoints;
    private final String recommendationNext;
    private final Integer todoCompletionRate;
    private final String afterScoreKey;
    private final Double afterScoreValue;

    public PlanHistoryResponseDto(Onboarding onboarding, String baseType, PlanResult planResult) {
        this.onboardingId = onboarding.getId();
        this.purpose = onboarding.getPurpose();
        this.goalDate = onboarding.getGoalDate();
        this.baseType = baseType;
        this.isActive = onboarding.getIsActive();
        this.createdAt = onboarding.getCreatedAt().toLocalDate().toString();

        if (planResult != null) {
            this.journeySummary = planResult.getJourneySummary();
            this.improvementPoints = planResult.getImprovementPoints();
            this.recommendationNext = planResult.getRecommendationNext();
            this.todoCompletionRate = planResult.getTodoCompletionRate();
            this.afterScoreKey = planResult.getAfterScoreKey();
            this.afterScoreValue = planResult.getAfterScoreValue();
        } else {
            this.journeySummary = null;
            this.improvementPoints = null;
            this.recommendationNext = null;
            this.todoCompletionRate = null;
            this.afterScoreKey = null;
            this.afterScoreValue = null;
        }
    }
}