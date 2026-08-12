package com.skincare.mypage.service;

import com.skincare.common.exception.CustomException;
import com.skincare.common.exception.ErrorCode;
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
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final OnboardingRepository onboardingRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final SkinResultRepository skinResultRepository;
    private final PlanResultRepository planResultRepository;
    private final TodoCheckRepository todoCheckRepository;

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
                result.put("skinResult", new SkinResultResponseDto(sr)));

        // TodoList 진행률
        long totalDays = ChronoUnit.DAYS.between(
                onboarding.getCreatedAt().toLocalDate(),
                LocalDate.now()) + 1;
        int cleansingDone = todoCheckRepository
                .countByOnboardingAndCleansingDoneTrue(onboarding);
        int skincareDone = todoCheckRepository
                .countByOnboardingAndSkincareDoneTrue(onboarding);

        result.put("todoStats", Map.of(
                "totalDays", totalDays,
                "cleansingDone", cleansingDone,
                "skincareDone", skincareDone,
                "cleansingRate", totalDays > 0
                        ? (int) ((cleansingDone / (double) totalDays) * 100) : 0,
                "skincareRate", totalDays > 0
                        ? (int) ((skincareDone / (double) totalDays) * 100) : 0
        ));

        // D-Day 종료 결과 (있으면)
        Optional<PlanResult> planResult = planResultRepository
                .findByOnboarding(onboarding);
        planResult.ifPresent(pr -> result.put("planResult", Map.of(
                "journeySummary", pr.getJourneySummary(),
                "improvementPoints", pr.getImprovementPoints(),
                "recommendationNext", pr.getRecommendationNext(),
                "todoCompletionRate", pr.getTodoCompletionRate()
        )));

        return result;
    }
}