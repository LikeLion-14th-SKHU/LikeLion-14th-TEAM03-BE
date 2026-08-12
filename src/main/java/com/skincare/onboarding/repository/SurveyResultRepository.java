package com.skincare.onboarding.repository;

import com.skincare.onboarding.entity.Onboarding;
import com.skincare.onboarding.entity.SurveyResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SurveyResultRepository extends JpaRepository<SurveyResult, Long> {

    Optional<SurveyResult> findByOnboarding(Onboarding onboarding);
}