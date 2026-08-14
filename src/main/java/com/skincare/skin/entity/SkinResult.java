package com.skincare.skin.entity;

import com.skincare.onboarding.entity.Onboarding;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "skin_results")
@Getter
@NoArgsConstructor
public class SkinResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onboarding_id", nullable = false, unique = true)
    private Onboarding onboarding;

    @Column(name = "cosmetic_json", nullable = false, columnDefinition = "LONGTEXT")
    private String cosmeticJson;

    @Column(name = "routines_json", nullable = false, columnDefinition = "LONGTEXT")
    private String routinesJson;

    // ✅ 추가
    @Column(name = "products_detail_json", columnDefinition = "LONGTEXT")
    private String productsDetailJson;

    // ✅ 추가
    @Column(name = "needs_medical_consult", nullable = false)
    private Boolean needsMedicalConsult = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public SkinResult(Onboarding onboarding, String cosmeticJson, String routinesJson,
                      String productsDetailJson, Boolean needsMedicalConsult) { // ✅ 추가
        this.onboarding = onboarding;
        this.cosmeticJson = cosmeticJson;
        this.routinesJson = routinesJson;
        this.productsDetailJson = productsDetailJson;
        this.needsMedicalConsult = needsMedicalConsult != null ? needsMedicalConsult : false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String cosmeticJson, String routinesJson,
                       String productsDetailJson, Boolean needsMedicalConsult) { // ✅ 추가
        this.cosmeticJson = cosmeticJson;
        this.routinesJson = routinesJson;
        this.productsDetailJson = productsDetailJson;
        this.needsMedicalConsult = needsMedicalConsult != null ? needsMedicalConsult : false;
        this.updatedAt = LocalDateTime.now();
    }
}