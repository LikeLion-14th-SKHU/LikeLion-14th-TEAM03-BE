package com.skincare.card.entity;

import com.skincare.onboarding.entity.Onboarding;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "solution_cards")
@Getter
@NoArgsConstructor
public class SolutionCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onboarding_id", nullable = false)
    private Onboarding onboarding;

    @Column(name = "card_type", nullable = false, length = 20)
    private String cardType; // INITIAL / UPDATE / DDAY_CHANGE

    @Column(name = "concern_raw", columnDefinition = "TEXT")
    private String concernRaw;

    @Column(name = "card_summary", nullable = false, columnDefinition = "TEXT")
    private String cardSummary;

    @Column(name = "dday_at_time", nullable = false)
    private Integer ddayAtTime;

    @Column(name = "status", length = 20)
    private String status; // MAINTAIN / REDUCE / PAUSE / RECHECK

    @Column(name = "prescribed_ingredients", columnDefinition = "TEXT")
    private String prescribedIngredients;

    @Column(name = "excluded_ingredients", columnDefinition = "TEXT")
    private String excludedIngredients;

    @Column(name = "created_at", nullable = false, updatable = false) // ✅ 수정
    private LocalDateTime createdAt;

    @Builder
    public SolutionCard(Onboarding onboarding, String cardType,
                        String concernRaw, String cardSummary,
                        Integer ddayAtTime, String status,
                        String prescribedIngredients,
                        String excludedIngredients) {
        this.onboarding = onboarding;
        this.cardType = cardType;
        this.concernRaw = concernRaw;
        this.cardSummary = cardSummary;
        this.ddayAtTime = ddayAtTime;
        this.status = status;
        this.prescribedIngredients = prescribedIngredients;
        this.excludedIngredients = excludedIngredients;
        this.createdAt = LocalDateTime.now();
    }
}