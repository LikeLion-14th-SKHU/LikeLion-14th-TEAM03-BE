package com.skincare.mypage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skincare.common.exception.CustomException;
import com.skincare.common.exception.ErrorCode;
import com.skincare.mypage.dto.PlanHistoryResponseDto;
import com.skincare.onboarding.entity.Onboarding;
import com.skincare.onboarding.entity.SurveyResult;
import com.skincare.onboarding.repository.OnboardingRepository;
import com.skincare.onboarding.repository.SurveyResultRepository;
import com.skincare.plan.entity.PlanResult;
import com.skincare.plan.repository.PlanResultRepository;
import com.skincare.session.entity.Session;
import com.skincare.skin.dto.SkinResultResponseDto;
import com.skincare.skin.entity.SkinResult;
import com.skincare.skin.repository.SkinResultRepository;
import com.skincare.todo.repository.TodoCheckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final OnboardingRepository onboardingRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final SkinResultRepository skinResultRepository;
    private final PlanResultRepository planResultRepository;
    private final TodoCheckRepository todoCheckRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public Map<String, Object> getMypage(Session session) {
        Onboarding onboarding = onboardingRepository
                .findBySessionAndIsActiveTrue(session)
                .orElseThrow(() -> new CustomException(ErrorCode.ONBOARDING_NOT_FOUND));

        SurveyResult survey = surveyResultRepository
                .findByOnboarding(onboarding)
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_RESULT_NOT_FOUND));

        Map<String, Object> result = new HashMap<>();

        // 내 정보
        Map<String, Object> myInfo = new HashMap<>();
        myInfo.put("name", onboarding.getName());
        myInfo.put("baseType", survey.getBaseType());
        myInfo.put("flags", Map.of(
                "dehydrated", survey.getDehydrated(),
                "sensitive", survey.getSensitive(),
                "acne", survey.getAcneFlag(),
                "markProne", survey.getMarkProne()
        ));
        myInfo.put("purpose", onboarding.getPurpose());
        myInfo.put("goalDate", onboarding.getGoalDate());
        myInfo.put("dDay", onboarding.getDDay());
        result.put("myInfo", myInfo);

        // 피부 결과
        Optional<SkinResult> skinResult = skinResultRepository
                .findByOnboarding(onboarding);
        skinResult.ifPresent(sr ->
                result.put("skinResult", new SkinResultResponseDto(sr, objectMapper)));

        // TodoList 진행률
        long totalDays = Math.max(ChronoUnit.DAYS.between(
                onboarding.getCreatedAt().toLocalDate(),
                LocalDate.now()) + 1, 1);
        int cleansingDone = todoCheckRepository
                .countByOnboardingAndCleansingDoneTrue(onboarding);
        int skincareDone = todoCheckRepository
                .countByOnboardingAndSkincareDoneTrue(onboarding);

        result.put("todoStats", Map.of(
                "totalDays", totalDays,
                "cleansingDone", cleansingDone,
                "skincareDone", skincareDone,
                "cleansingRate", Math.min((int) ((cleansingDone / (double) totalDays) * 100), 100),
                "skincareRate", Math.min((int) ((skincareDone / (double) totalDays) * 100), 100)
        ));

        // D-Day 종료 결과 (있으면)
        Optional<PlanResult> planResult = planResultRepository
                .findByOnboarding(onboarding);
        planResult.ifPresent(pr -> {
            Map<String, Object> planResultMap = new HashMap<>();
            planResultMap.put("journeySummary", pr.getJourneySummary());
            planResultMap.put("improvementPoints", pr.getImprovementPoints());
            planResultMap.put("recommendationNext", pr.getRecommendationNext());
            planResultMap.put("todoCompletionRate", pr.getTodoCompletionRate());
            planResultMap.put("afterScoreKey", pr.getAfterScoreKey() != null ? pr.getAfterScoreKey() : "");
            planResultMap.put("afterScoreValue", pr.getAfterScoreValue() != null ? pr.getAfterScoreValue() : 0.0);
            result.put("planResult", planResultMap);
        });

        return result;
    }

    // ✅ 과거 플랜 히스토리 조회
    @Transactional(readOnly = true)
    public List<PlanHistoryResponseDto> getPlanHistory(Session session) {
        List<Onboarding> onboardings = onboardingRepository
                .findAllBySessionOrderByCreatedAtDesc(session);

        return onboardings.stream()
                .map(onboarding -> {
                    String baseType = surveyResultRepository
                            .findByOnboarding(onboarding)
                            .map(SurveyResult::getBaseType)
                            .orElse(null);

                    PlanResult planResult = planResultRepository
                            .findByOnboarding(onboarding)
                            .orElse(null);

                    return new PlanHistoryResponseDto(onboarding, baseType, planResult);
                })
                .collect(Collectors.toList());
    }
}