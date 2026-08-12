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
    private String status; // HOMECARE / MEDICAL_REFERRAL / MAINTAIN / REDUCE / PAUSE / RECHECK

    // history_cards 전달용 추가 필드
    @Column(name = "prescribed_ingredients", columnDefinition = "TEXT")
    private String prescribedIngredients; // JSON 배열 형태로 저장

    @Column(name = "excluded_ingredients", columnDefinition = "TEXT")
    private String excludedIngredients; // JSON 배열 형태로 저장

    @Column(name = "created_at", nullable = false)
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