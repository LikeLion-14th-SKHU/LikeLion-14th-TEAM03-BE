package com.skincare.card.repository;

import com.skincare.card.entity.SolutionCard;
import com.skincare.onboarding.entity.Onboarding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolutionCardRepository extends JpaRepository<SolutionCard, Long> {

    // 현재 플랜의 카드 목록 조회 (생성일 오름차순)
    List<SolutionCard> findByOnboardingOrderByCreatedAtAsc(Onboarding onboarding);
}