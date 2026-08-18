package com.skincare.skin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skincare.card.entity.SolutionCard;
import com.skincare.card.repository.SolutionCardRepository;
import com.skincare.common.exception.CustomException;
import com.skincare.common.exception.ErrorCode;
import com.skincare.onboarding.entity.Onboarding;
import com.skincare.onboarding.entity.SurveyResult;
import com.skincare.onboarding.repository.OnboardingRepository;
import com.skincare.onboarding.repository.SurveyResultRepository;
import com.skincare.session.entity.Session;
import com.skincare.skin.dto.SkinResultResponseDto;
import com.skincare.skin.entity.SkinResult;
import com.skincare.skin.repository.SkinResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SkinService {

    private final SkinResultRepository skinResultRepository;
    private final OnboardingRepository onboardingRepository;
    private final SurveyResultRepository surveyResultRepository;
    private final SolutionCardRepository solutionCardRepository;
    private final AiCallService aiCallService;
    private final ObjectMapper objectMapper;

    // AI 1차 호출 + 결과 저장
    @Transactional
    public Map<String, Object> generateSkinResult(Onboarding onboarding) {
        SurveyResult survey = surveyResultRepository
                .findByOnboarding(onboarding)
                .orElseThrow(() -> new CustomException(ErrorCode.SURVEY_RESULT_NOT_FOUND));

        List<SolutionCard> cards = solutionCardRepository
                .findByOnboardingOrderByCreatedAtAsc(onboarding);

        // ✅ 실제 AI 연동
        Map<String, Object> aiResponse = aiCallService.callAi1(onboarding, survey, cards);

        try {
            String cosmeticJson = objectMapper
                    .writeValueAsString(aiResponse.get("cosmetic"));

            Map<String, Object> routinesMap = new HashMap<>();
            routinesMap.put("cleansing", aiResponse.get("cleansing"));
            routinesMap.put("skincare_order", aiResponse.get("skincare_order"));
            routinesMap.put("type_description", aiResponse.getOrDefault("type_description", ""));
            String routinesJson = objectMapper.writeValueAsString(routinesMap);

            // ✅ AI 서버에서 모든 추천 성분 제품 포함해서 내려줌 → 그대로 저장
            String productsDetailJson = aiResponse.get("products_detail") != null
                    ? objectMapper.writeValueAsString(aiResponse.get("products_detail"))
                    : null;

            Boolean needsMedicalConsult = (Boolean) aiResponse
                    .getOrDefault("needs_medical_consult", false);

            SkinResult skinResult = skinResultRepository
                    .findByOnboarding(onboarding)
                    .orElse(null);

            if (skinResult != null) {
                skinResult.update(cosmeticJson, routinesJson,
                        productsDetailJson, needsMedicalConsult);
            } else {
                skinResult = SkinResult.builder()
                        .onboarding(onboarding)
                        .cosmeticJson(cosmeticJson)
                        .routinesJson(routinesJson)
                        .productsDetailJson(productsDetailJson)
                        .needsMedicalConsult(needsMedicalConsult)
                        .build();
                skinResultRepository.save(skinResult);
            }

            return (Map<String, Object>) aiResponse.get("card");

        } catch (Exception e) {
            throw new CustomException(ErrorCode.AI_CALL_FAILED, e);
        }
    }

    // 피부 결과 조회
    @Transactional(readOnly = true)
    public SkinResultResponseDto getSkinResult(Session session) {
        Onboarding onboarding = onboardingRepository
                .findBySessionAndIsActiveTrue(session)
                .orElseThrow(() -> new CustomException(ErrorCode.ONBOARDING_NOT_FOUND));

        SkinResult skinResult = skinResultRepository
                .findByOnboarding(onboarding)
                .orElseThrow(() -> new CustomException(ErrorCode.SKIN_RESULT_NOT_FOUND));

        return new SkinResultResponseDto(skinResult, objectMapper);
    }
}