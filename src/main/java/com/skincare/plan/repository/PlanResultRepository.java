package com.skincare.plan.repository;

import com.skincare.onboarding.entity.Onboarding;
import com.skincare.plan.entity.PlanResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanResultRepository extends JpaRepository<PlanResult, Long> {

    Optional<PlanResult> findByOnboarding(Onboarding onboarding);
}