package com.skincare.skin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skincare.card.entity.SolutionCard;
import com.skincare.card.repository.SolutionCardRepository;
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
                .orElseThrow(() -> new IllegalArgumentException("설문 결과가 없습니다"));

        // history_cards 조회
        List<SolutionCard> cards = solutionCardRepository
                .findByOnboardingOrderByCreatedAtAsc(onboarding);

        // Mock 데이터 사용 (AI 연동 전)
        Map<String, Object> aiResponse = aiCallService.getMockResponse();

        try {
            // cosmetic + cleansing + skincare_order + type_description 저장
            String cosmeticJson = objectMapper
                    .writeValueAsString(aiResponse.get("cosmetic"));
            String routinesJson = objectMapper.writeValueAsString(Map.of(
                    "cleansing", aiResponse.get("cleansing"),
                    "skincare_order", aiResponse.get("skincare_order"),
                    "type_description", aiResponse.get("type_description")
            ));

            // 기존 결과 있으면 UPDATE, 없으면 INSERT
            SkinResult skinResult = skinResultRepository
                    .findByOnboarding(onboarding)
                    .orElse(null);

            if (skinResult != null) {
                skinResult.update(cosmeticJson, routinesJson);
            } else {
                skinResult = SkinResult.builder()
                        .onboarding(onboarding)
                        .cosmeticJson(cosmeticJson)
                        .routinesJson(routinesJson)
                        .build();
                skinResultRepository.save(skinResult);
            }

            // card 정보 반환 (CardService에서 저장)
            return (Map<String, Object>) aiResponse.get("card");

        } catch (Exception e) {
            throw new RuntimeException("AI 결과 저장 실패", e);
        }
    }

    // 피부 결과 조회
    @Transactional(readOnly = true)
    public SkinResultResponseDto getSkinResult(Session session) {
        Onboarding onboarding = onboardingRepository
                .findBySessionAndIsActiveTrue(session)
                .orElseThrow(() -> new IllegalArgumentException("온보딩 정보가 없습니다"));

        SkinResult skinResult = skinResultRepository
                .findByOnboarding(onboarding)
                .orElseThrow(() -> new IllegalArgumentException("피부 결과가 없습니다"));

        return new SkinResultResponseDto(skinResult);
    }
}