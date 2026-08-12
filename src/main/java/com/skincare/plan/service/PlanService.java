package com.skincare.plan.service;

import com.skincare.card.entity.SolutionCard;
import com.skincare.card.repository.SolutionCardRepository;
import com.skincare.onboarding.entity.Onboarding;
import com.skincare.onboarding.entity.SurveyResult;
import com.skincare.onboarding.repository.OnboardingRepository;
import com.skincare.onboarding.repository.SurveyResultRepository;
import com.skincare.plan.dto.PlanFinishRequestDto;
import com.skincare.plan.dto.PlanResultResponseDto;
import com.skincare.plan.entity.PlanResult;
import com.skincare.plan.repository.PlanResultRepository;
import com.skincare.session.entity.Session;
import com.skincare.skin.service.AiCallService;
import com.skincare.todo.repository.TodoCheckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanResultRepository planResultRepository;
    private final OnboardingRepository onboardingRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final SolutionCardRepository solutionCardRepository;
    private final TodoCheckRepository todoCheckRepository;
    private final AiCallService aiCallService;

    // D-Day 종료 처리 + AI 3차 호출
    @Transactional
    public PlanResultResponseDto finishPlan(Session session,
                                            PlanFinishRequestDto request) {
        Onboarding onboarding = onboardingRepository
                .findBySessionAndIsActiveTrue(session)
                .orElseThrow(() -> new IllegalArgumentException("온보딩 정보가 없습니다"));

        SurveyResult survey = surveyResultRepository
                .findByOnboarding(onboarding)
                .orElseThrow(() -> new IllegalArgumentException("설문 결과가 없습니다"));

        // TodoList 진행률 계산
        long totalDays = ChronoUnit.DAYS.between(
                onboarding.getCreatedAt().toLocalDate(),
                LocalDate.now()) + 1;
        int cleansingDone = todoCheckRepository
                .countByOnboardingAndCleansingDoneTrue(onboarding);
        int todoCompletionRate = totalDays > 0
                ? (int) ((cleansingDone / (double) totalDays) * 100) : 0;

        // history_cards 조회
        List<SolutionCard> cards = solutionCardRepository
                .findByOnboardingOrderByCreatedAtAsc(onboarding);

        // AI 3차 호출
        Map<String, Object> aiResponse = aiCallService.getMockAi3Response();
        // 실제 연동 시:
        // Map<String, Object> aiResponse = aiCallService
        //     .buildAi3Request(onboarding, survey, cards,
        //                      cleansingDone, (int) totalDays,
        //                      request.getAfterScoreKey(),
        //                      request.getAfterScoreValue());
        // → POST /ai/journey 호출

        Map<String, Object> journey = (Map<String, Object>) aiResponse.get("journey");

        // highlights 리스트 → 문자열 변환
        Object highlights = journey.get("highlights");
        String improvementPoints = highlights != null
                ? highlights.toString() : "";

        // plan_results 저장 (after_scores 포함)
        PlanResult planResult = PlanResult.builder()
                .onboarding(onboarding)
                .journeySummary((String) journey.get("summary"))
                .improvementPoints(improvementPoints)
                .recommendationNext((String) journey.get("next_step"))
                .todoCompletionRate(todoCompletionRate)
                .afterScoreKey(request.getAfterScoreKey())
                .afterScoreValue(request.getAfterScoreValue())
                .build();
        planResultRepository.save(planResult);

        // 플랜 종료 (is_active = false → 알림 자동 중단)
        onboarding.deactivate();

        return new PlanResultResponseDto(planResult);
    }

    // 종료 결과 조회
    @Transactional(readOnly = true)
    public PlanResultResponseDto getPlanResult(Session session) {
        Onboarding onboarding = onboardingRepository
                .findBySessionAndIsActiveTrue(session)
                .orElse(null);

        if (onboarding == null) {
            throw new IllegalArgumentException("플랜 정보가 없습니다");
        }

        PlanResult planResult = planResultRepository
                .findByOnboarding(onboarding)
                .orElseThrow(() -> new IllegalArgumentException("종료 결과가 없습니다"));

        return new PlanResultResponseDto(planResult);
    }
}