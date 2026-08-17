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

    // ✅ 가장 최근 온보딩 조회 (비활성 포함)
    Optional<Onboarding> findTopBySessionOrderByCreatedAtDesc(Session session);
}