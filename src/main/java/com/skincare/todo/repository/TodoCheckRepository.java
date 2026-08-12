package com.skincare.todo.repository;

import com.skincare.onboarding.entity.Onboarding;
import com.skincare.todo.entity.TodoCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TodoCheckRepository extends JpaRepository<TodoCheck, Long> {

    // 오늘 체크 조회
    Optional<TodoCheck> findByOnboardingAndCheckDate(Onboarding onboarding,
                                                     LocalDate checkDate);

    // 전체 체크 목록 조회
    List<TodoCheck> findByOnboarding(Onboarding onboarding);

    // 세안 완료 횟수
    int countByOnboardingAndCleansingDoneTrue(Onboarding onboarding);

    // 기초화장품 완료 횟수
    int countByOnboardingAndSkincareDoneTrue(Onboarding onboarding);
}