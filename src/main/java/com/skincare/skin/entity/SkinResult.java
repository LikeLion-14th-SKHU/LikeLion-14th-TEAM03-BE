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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public SkinResult(Onboarding onboarding, String cosmeticJson, String routinesJson) {
        this.onboarding = onboarding;
        this.cosmeticJson = cosmeticJson;
        this.routinesJson = routinesJson;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String cosmeticJson, String routinesJson) {
        this.cosmeticJson = cosmeticJson;
        this.routinesJson = routinesJson;
        this.updatedAt = LocalDateTime.now();
    }
}