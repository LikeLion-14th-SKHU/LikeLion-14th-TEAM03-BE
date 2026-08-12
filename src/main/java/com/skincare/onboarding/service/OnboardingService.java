package com.skincare.onboarding.service;

import com.skincare.card.service.CardService;
import com.skincare.common.exception.CustomException;
import com.skincare.common.exception.ErrorCode;
import com.skincare.onboarding.dto.OnboardingRequestDto;
import com.skincare.onboarding.dto.OnboardingResponseDto;
import com.skincare.onboarding.entity.Onboarding;
import com.skincare.onboarding.entity.SurveyResult;
import com.skincare.onboarding.repository.OnboardingRepository;
import com.skincare.onboarding.repository.SurveyResultRepository;
import com.skincare.session.entity.Session;
import com.skincare.skin.service.SkinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final OnboardingRepository onboardingRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final SurveyCalculationService surveyCalculationService;
    private final SkinService skinService;
    private final CardService cardService;

    // 온보딩 저장
    @Transactional
    public OnboardingResponseDto saveOnboarding(Session session,
                                                OnboardingRequestDto request) {
        // 목표 날짜 검증
        if (request.getGoalDate().isBefore(LocalDate.now())) {
            throw new CustomException(ErrorCode.INVALID_GOAL_DATE);
        }

        // 기존 활성 플랜 비활성화
        Optional<Onboarding> existing =
                onboardingRepository.findBySessionAndIsActiveTrue(session);
        existing.ifPresent(Onboarding::deactivate);

        // 온보딩 저장
        Onboarding onboarding = Onboarding.builder()
                .session(session)
                .name(request.getName())
                .purpose(request.getPurpose())
                .goalDate(request.getGoalDate())
                .age(request.getAge())
                .gender(request.getGender())
                .build();
        onboardingRepository.save(onboarding);

        // 설문 점수 계산 + 저장
        SurveyResult surveyResult = surveyCalculationService
                .calculate(onboarding, request.getSurveyAnswers(),
                        request.getConcernRaw());
        surveyResultRepository.save(surveyResult);

        // AI 1차 호출 + skin_results 저장 → card 데이터 반환
        Map<String, Object> cardData = skinService.generateSkinResult(onboarding);

        // 초기 카드 생성
        cardService.createInitialCard(onboarding, cardData);

        return new OnboardingResponseDto(onboarding, surveyResult);
    }

    // 온보딩 조회
    @Transactional(readOnly = true)
    public OnboardingResponseDto getOnboarding(Session session) {
        Onboarding onboarding = onboardingRepository
                .findBySessionAndIsActiveTrue(session)
                .orElseThrow(() -> new CustomException(ErrorCode.ONBOARDING_NOT_FOUND));

        SurveyResult surveyResult = surveyResultRepository
                .findByOnboarding(onboarding)
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_RESULT_NOT_FOUND));

        return new OnboardingResponseDto(onboarding, surveyResult);
    }

    // D-Day 재설정
    @Transactional
    public void updateGoalDate(Session session, LocalDate newGoalDate) {
        if (newGoalDate.isBefore(LocalDate.now())) {
            throw new CustomException(ErrorCode.INVALID_GOAL_DATE);
        }

        Onboarding onboarding = onboardingRepository
                .findBySessionAndIsActiveTrue(session)
                .orElseThrow(() -> new CustomException(ErrorCode.ONBOARDING_NOT_FOUND));

        onboarding.updateGoalDate(newGoalDate);

        // D-Day 변경 시 AI 재호출
        Map<String, Object> cardData = skinService.generateSkinResult(onboarding);

        // DDAY_CHANGE 카드 생성
        cardService.createDdayChangeCard(onboarding, cardData);
    }

    // 새로 검사 (플랜 종료)
    @Transactional
    public void restartOnboarding(Session session) {
        Onboarding onboarding = onboardingRepository
                .findBySessionAndIsActiveTrue(session)
                .orElseThrow(() -> new CustomException(ErrorCode.ONBOARDING_NOT_FOUND));

        onboarding.deactivate();
    }
}