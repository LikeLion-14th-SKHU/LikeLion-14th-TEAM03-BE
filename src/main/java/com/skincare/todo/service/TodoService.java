package com.skincare.todo.service;

import com.skincare.onboarding.entity.Onboarding;
import com.skincare.onboarding.repository.OnboardingRepository;
import com.skincare.session.entity.Session;
import com.skincare.todo.dto.TodoCheckRequestDto;
import com.skincare.todo.dto.TodoProgressResponseDto;
import com.skincare.todo.entity.TodoCheck;
import com.skincare.todo.repository.TodoCheckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoCheckRepository todoCheckRepository;
    private final OnboardingRepository onboardingRepository;

    // 오늘 체크 저장
    @Transactional
    public void saveCheck(Session session, TodoCheckRequestDto request) {
        Onboarding onboarding = onboardingRepository
                .findBySessionAndIsActiveTrue(session)
                .orElseThrow(() -> new IllegalArgumentException("온보딩 정보가 없습니다"));

        LocalDate today = LocalDate.now();

        // 오늘 체크 있으면 UPDATE, 없으면 INSERT
        Optional<TodoCheck> existing = todoCheckRepository
                .findByOnboardingAndCheckDate(onboarding, today);

        if (existing.isPresent()) {
            existing.get().update(
                    request.getCleansingDone(),
                    request.getSkincareDone()
            );
        } else {
            TodoCheck todoCheck = TodoCheck.builder()
                    .onboarding(onboarding)
                    .checkDate(today)
                    .cleansingDone(request.getCleansingDone() != null
                            ? request.getCleansingDone() : false)
                    .skincareDone(request.getSkincareDone() != null
                            ? request.getSkincareDone() : false)
                    .build();
            todoCheckRepository.save(todoCheck);
        }
    }

    // 진행률 조회
    @Transactional(readOnly = true)
    public TodoProgressResponseDto getProgress(Session session) {
        Onboarding onboarding = onboardingRepository
                .findBySessionAndIsActiveTrue(session)
                .orElseThrow(() -> new IllegalArgumentException("온보딩 정보가 없습니다"));

        // 총 경과 일수 (온보딩 시작일 ~ 오늘)
        long totalDays = ChronoUnit.DAYS.between(
                onboarding.getCreatedAt().toLocalDate(),
                LocalDate.now()) + 1;

        int cleansingDone = todoCheckRepository
                .countByOnboardingAndCleansingDoneTrue(onboarding);
        int skincareDone = todoCheckRepository
                .countByOnboardingAndSkincareDoneTrue(onboarding);

        return new TodoProgressResponseDto(totalDays, cleansingDone, skincareDone);
    }
}