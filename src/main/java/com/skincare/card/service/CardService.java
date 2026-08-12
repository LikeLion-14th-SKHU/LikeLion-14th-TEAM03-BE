package com.skincare.card.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skincare.common.exception.CustomException;
import com.skincare.common.exception.ErrorCode;
import com.skincare.card.dto.CardResponseDto;
import com.skincare.card.dto.ConcernRequestDto;
import com.skincare.card.entity.SolutionCard;
import com.skincare.card.repository.SolutionCardRepository;
import com.skincare.onboarding.entity.Onboarding;
import com.skincare.onboarding.entity.SurveyResult;
import com.skincare.onboarding.repository.OnboardingRepository;
import com.skincare.onboarding.repository.SurveyResultRepository;
import com.skincare.session.entity.Session;
import com.skincare.skin.service.AiCallService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CardService {

    private final SolutionCardRepository solutionCardRepository;
    private final OnboardingRepository onboardingRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final AiCallService aiCallService;
    private final ObjectMapper objectMapper;

    // 초기 카드 생성 (온보딩 완료 시)
    @Transactional
    public void createInitialCard(Onboarding onboarding,
                                  Map<String, Object> cardData) {
        try {
            String prescribedJson = objectMapper.writeValueAsString(
                    cardData.get("prescribed_ingredients"));
            String excludedJson = objectMapper.writeValueAsString(
                    cardData.get("excluded_ingredients"));

            SolutionCard card = SolutionCard.builder()
                    .onboarding(onboarding)
                    .cardType("INITIAL")
                    .concernRaw(null)
                    .cardSummary((String) cardData.get("concern_summary"))
                    .ddayAtTime((int) onboarding.getDDay())
                    .status(null)
                    .prescribedIngredients(prescribedJson)
                    .excludedIngredients(excludedJson)
                    .build();
            solutionCardRepository.save(card);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.AI_CALL_FAILED, e);
        }
    }

    // D-Day 변경 카드 생성
    @Transactional
    public void createDdayChangeCard(Onboarding onboarding,
                                     Map<String, Object> cardData) {
        try {
            String prescribedJson = objectMapper.writeValueAsString(
                    cardData.get("prescribed_ingredients"));
            String excludedJson = objectMapper.writeValueAsString(
                    cardData.get("excluded_ingredients"));

            SolutionCard card = SolutionCard.builder()
                    .onboarding(onboarding)
                    .cardType("DDAY_CHANGE")
                    .concernRaw(null)
                    .cardSummary((String) cardData.get("concern_summary"))
                    .ddayAtTime((int) onboarding.getDDay())
                    .status(null)
                    .prescribedIngredients(prescribedJson)
                    .excludedIngredients(excludedJson)
                    .build();
            solutionCardRepository.save(card);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.AI_CALL_FAILED, e);
        }
    }

    // 새 고민 입력 + AI 2차 호출 (dday + skin 추가)
    @Transactional
    public Map<String, Object> addConcern(Session session,
                                          ConcernRequestDto request) {
        Onboarding onboarding = onboardingRepository
                .findBySessionAndIsActiveTrue(session)
                .orElseThrow(() -> new CustomException(ErrorCode.ONBOARDING_NOT_FOUND));

        SurveyResult survey = surveyResultRepository
                .findByOnboarding(onboarding)
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_RESULT_NOT_FOUND));

        // 기존 카드 목록 조회
        List<SolutionCard> cards = solutionCardRepository
                .findByOnboardingOrderByCreatedAtAsc(onboarding);

        // AI 2차 호출 (dday + skin 포함) ✅ 수정
        Map<String, Object> aiResponse = aiCallService.getMockAi2Response();
        // 실제 연동 시:
        // Map<String, Object> aiResponse = aiCallService
        //     .buildAi2Request(request.getNewConcern(), onboarding, survey, cards);
        // → POST /ai/concern 호출

        Map<String, Object> cardData = (Map<String, Object>) aiResponse.get("card");

        try {
            String prescribedJson = objectMapper.writeValueAsString(
                    cardData.get("prescribed_ingredients"));
            String excludedJson = objectMapper.writeValueAsString(
                    cardData.get("excluded_ingredients"));

            // 새 카드 저장 (cautions 포함) ✅ 수정
            SolutionCard newCard = SolutionCard.builder()
                    .onboarding(onboarding)
                    .cardType("UPDATE")
                    .concernRaw(request.getNewConcern())
                    .cardSummary((String) cardData.get("concern_summary"))
                    .ddayAtTime((int) onboarding.getDDay())
                    .status((String) cardData.get("action"))
                    .prescribedIngredients(prescribedJson)
                    .excludedIngredients(excludedJson)
                    .build();
            solutionCardRepository.save(newCard);

            // 결과 반환 (cautions 추가) ✅ 수정
            return Map.of(
                    "status", cardData.get("action"),
                    "message", cardData.get("response"),
                    "cautions", cardData.getOrDefault("cautions", List.of()),
                    "medicalReferral", "RECHECK".equals(cardData.get("action"))
            );
        } catch (Exception e) {
            throw new CustomException(ErrorCode.AI_CALL_FAILED, e);
        }
    }

    // 카드 목록 조회
    @Transactional(readOnly = true)
    public CardResponseDto getCards(Session session) {
        Onboarding onboarding = onboardingRepository
                .findBySessionAndIsActiveTrue(session)
                .orElseThrow(() -> new CustomException(ErrorCode.ONBOARDING_NOT_FOUND));

        List<SolutionCard> cards = solutionCardRepository
                .findByOnboardingOrderByCreatedAtAsc(onboarding);

        return new CardResponseDto(cards);
    }
}