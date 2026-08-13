package com.skincare.plan.entity;

import com.skincare.onboarding.entity.Onboarding;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "plan_results")
@Getter
@NoArgsConstructor
public class PlanResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onboarding_id", nullable = false, unique = true)
    private Onboarding onboarding;

    @Column(name = "journey_summary", nullable = false, columnDefinition = "TEXT")
    private String journeySummary;

    @Column(name = "improvement_points", nullable = false, columnDefinition = "TEXT")
    private String improvementPoints;

    @Column(name = "recommendation_next", nullable = false, columnDefinition = "TEXT")
    private String recommendationNext;

    @Column(name = "todo_completion_rate", nullable = false)
    private Integer todoCompletionRate;

    @Column(name = "after_score_key", length = 20)
    private String afterScoreKey;

    @Column(name = "after_score_value")
    private Double afterScoreValue;

    @Column(name = "created_at", nullable = false, updatable = false) // ✅ 수정
    private LocalDateTime createdAt;

    @Builder
    public PlanResult(Onboarding onboarding, String journeySummary,
                      String improvementPoints, String recommendationNext,
                      Integer todoCompletionRate,
                      String afterScoreKey, Double afterScoreValue) {
        this.onboarding = onboarding;
        this.journeySummary = journeySummary;
        this.improvementPoints = improvementPoints;
        this.recommendationNext = recommendationNext;
        this.todoCompletionRate = todoCompletionRate;
        this.afterScoreKey = afterScoreKey;
        this.afterScoreValue = afterScoreValue;
        this.createdAt = LocalDateTime.now();
    }
}