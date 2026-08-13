package com.skincare.plan.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skincare.card.entity.SolutionCard;
import com.skincare.card.repository.SolutionCardRepository;
import com.skincare.common.exception.CustomException;
import com.skincare.common.exception.ErrorCode;
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
    private final ObjectMapper objectMapper;

    // D-Day 종료 처리 + AI 3차 호출
    @Transactional
    public PlanResultResponseDto finishPlan(Session session,
                                            PlanFinishRequestDto request) {
        Onboarding onboarding = onboardingRepository
                .findBySessionAndIsActiveTrue(session)
                .orElseThrow(() -> new CustomException(ErrorCode.ONBOARDING_NOT_FOUND));

        SurveyResult survey = surveyResultRepository
                .findByOnboarding(onboarding)
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_RESULT_NOT_FOUND));

        // TodoList 진행률 계산
        long totalDays = Math.max(ChronoUnit.DAYS.between(
                onboarding.getCreatedAt().toLocalDate(),
                LocalDate.now()) + 1, 1);
        int cleansingDone = todoCheckRepository
                .countByOnboardingAndCleansingDoneTrue(onboarding);

        // ✅ Math.min 추가 (100% 초과 방지)
        int todoCompletionRate = Math.min(
                (int) ((cleansingDone / (double) totalDays) * 100), 100);

        // history_cards 조회
        List<SolutionCard> cards = solutionCardRepository
                .findByOnboardingOrderByCreatedAtAsc(onboarding);

        // AI 3차 호출
        Map<String, Object> aiResponse = aiCallService.getMockAi3Response();
        // 실제 연동 시:
        // Map<String, Object> aiResponse = aiCallService.callAi3(
        //     onboarding, survey, cards,
        //     cleansingDone, (int) totalDays,
        //     request.getAfterScoreKey(),
        //     request.getAfterScoreValue());

        Map<String, Object> journey = (Map<String, Object>) aiResponse.get("journey");

        // ✅ highlights JSON 문자열로 저장
        String improvementPoints = "";
        Object highlights = journey.get("highlights");
        if (highlights != null) {
            try {
                improvementPoints = objectMapper.writeValueAsString(highlights);
            } catch (Exception e) {
                improvementPoints = highlights.toString();
            }
        }

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

        onboarding.deactivate();

        return new PlanResultResponseDto(planResult);
    }

    // 종료 결과 조회
    @Transactional(readOnly = true)
    public PlanResultResponseDto getPlanResult(Session session) {
        // ✅ orElseThrow로 통일
        Onboarding onboarding = onboardingRepository
                .findBySessionAndIsActiveTrue(session)
                .orElseThrow(() -> new CustomException(ErrorCode.ONBOARDING_NOT_FOUND));

        PlanResult planResult = planResultRepository
                .findByOnboarding(onboarding)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_RESULT_NOT_FOUND));

        return new PlanResultResponseDto(planResult);
    }
}