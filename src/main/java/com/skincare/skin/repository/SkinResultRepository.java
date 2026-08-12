package com.skincare.skin.repository;

import com.skincare.onboarding.entity.Onboarding;
import com.skincare.skin.entity.SkinResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SkinResultRepository extends JpaRepository<SkinResult, Long> {

    Optional<SkinResult> findByOnboarding(Onboarding onboarding);
}