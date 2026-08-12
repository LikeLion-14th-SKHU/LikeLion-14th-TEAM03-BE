package com.skincare.onboarding.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "survey_results")
@Getter
@NoArgsConstructor
public class SurveyResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onboarding_id", nullable = false, unique = true)
    private Onboarding onboarding;

    // 피부타입
    @Column(name = "base_type", nullable = false, length = 10)
    private String baseType;

    // 중첩속성
    @Column(name = "dehydrated", nullable = false)
    private Boolean dehydrated = false;

    @Column(name = "is_sensitive", nullable = false)
    private Boolean sensitive = false;

    @Column(name = "acne_flag", nullable = false)
    private Boolean acneFlag = false;

    @Column(name = "mark_prone", nullable = false)
    private Boolean markProne = false;

    // 안전 플래그
    @Column(name = "on_medication", nullable = false)
    private Boolean onMedication = false;

    @Column(name = "retinol_history", nullable = false, length = 20)
    private String retinolHistory;

    @Column(name = "inflammatory", nullable = false)
    private Boolean inflammatory = false;

    // raw scores (축별 원점수)
    @Column(name = "sebum_raw", nullable = false)
    private Double sebumRaw;

    @Column(name = "hydra_raw", nullable = false)
    private Double hydraRaw;

    @Column(name = "sens_raw", nullable = false)
    private Double sensRaw;

    @Column(name = "redness_raw", nullable = false)
    private Double rednessRaw;

    @Column(name = "acne_raw", nullable = false)
    private Double acneRaw;

    @Column(name = "mark_raw", nullable = false)
    private Double markRaw;

    // trouble scores (10점 환산 Before 값)
    @Column(name = "ts_sebum", nullable = false)
    private Double tsSebum;

    @Column(name = "ts_pore", nullable = false)
    private Double tsPore;

    @Column(name = "ts_dryness", nullable = false)
    private Double tsDryness;

    @Column(name = "ts_redness", nullable = false)
    private Double tsRedness;

    @Column(name = "ts_acne", nullable = false)
    private Double tsAcne;

    @Column(name = "ts_mark", nullable = false)
    private Double tsMark;

    // 피부 고민 서술형
    @Column(name = "concern_raw", nullable = false, columnDefinition = "TEXT")
    private String concernRaw;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public SurveyResult(Onboarding onboarding, String baseType,
                        Boolean dehydrated, Boolean sensitive,
                        Boolean acneFlag, Boolean markProne,
                        Boolean onMedication, String retinolHistory,
                        Boolean inflammatory,
                        Double sebumRaw, Double hydraRaw, Double sensRaw,
                        Double rednessRaw, Double acneRaw, Double markRaw,
                        Double tsSebum, Double tsPore, Double tsDryness,
                        Double tsRedness, Double tsAcne, Double tsMark,
                        String concernRaw) {
        this.onboarding = onboarding;
        this.baseType = baseType;
        this.dehydrated = dehydrated;
        this.sensitive = sensitive;
        this.acneFlag = acneFlag;
        this.markProne = markProne;
        this.onMedication = onMedication;
        this.retinolHistory = retinolHistory;
        this.inflammatory = inflammatory;
        this.sebumRaw = sebumRaw;
        this.hydraRaw = hydraRaw;
        this.sensRaw = sensRaw;
        this.rednessRaw = rednessRaw;
        this.acneRaw = acneRaw;
        this.markRaw = markRaw;
        this.tsSebum = tsSebum;
        this.tsPore = tsPore;
        this.tsDryness = tsDryness;
        this.tsRedness = tsRedness;
        this.tsAcne = tsAcne;
        this.tsMark = tsMark;
        this.concernRaw = concernRaw;
        this.createdAt = LocalDateTime.now();
    }
}