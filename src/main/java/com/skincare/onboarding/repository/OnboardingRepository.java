package com.skincare.onboarding.repository;

import com.skincare.onboarding.entity.Onboarding;
import com.skincare.session.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OnboardingRepository extends JpaRepository<Onboarding, Long> {

    // 현재 활성 플랜 조회
    Optional<Onboarding> findBySessionAndIsActiveTrue(Session session);

    // 전체 활성 플랜 조회 (스케줄러용)
    List<Onboarding> findAllByIsActiveTrue();
}