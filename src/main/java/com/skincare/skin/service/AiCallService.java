package com.skincare.skin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skincare.card.entity.SolutionCard;
import com.skincare.common.exception.CustomException;
import com.skincare.common.exception.ErrorCode;
import com.skincare.onboarding.entity.Onboarding;
import com.skincare.onboarding.entity.SurveyResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiCallService {

    private final ObjectMapper objectMapper;

    @Value("${ai.server.url:http://localhost:8001}")
    private String aiServerUrl;

    // =============================================
    // 실제 AI 호출 메서드
    // =============================================

    // AI 1차 실제 호출
    public Map<String, Object> callAi1(Onboarding onboarding,
                                       SurveyResult survey,
                                       List<SolutionCard> cards) {
        Map<String, Object> request = buildAiRequest(onboarding, survey, cards);
        return callAi(aiServerUrl + "/ai/recommend", request);
    }

    // AI 2차 실제 호출
    public Map<String, Object> callAi2(String newConcern,
                                       Onboarding onboarding,
                                       SurveyResult survey,
                                       List<SolutionCard> cards) {
        Map<String, Object> request = buildAi2Request(newConcern, onboarding, survey, cards);
        return callAi(aiServerUrl + "/ai/concern", request);
    }

    // AI 3차 실제 호출
    public Map<String, Object> callAi3(Onboarding onboarding,
                                       SurveyResult survey,
                                       List<SolutionCard> cards,
                                       int completedDays,
                                       int totalDays,
                                       String afterScoreKey,
                                       Double afterScoreValue) {
        Map<String, Object> request = buildAi3Request(
                onboarding, survey, cards,
                completedDays, totalDays,
                afterScoreKey, afterScoreValue);
        return callAi(aiServerUrl + "/ai/journey", request);
    }

    // 공통 HTTP 호출 (timeout 10초, 재시도 1회)
    private Map<String, Object> callAi(String url, Map<String, Object> request) {
        try {
            log.info("AI 호출 시작: {}", url);

            RestClient restClient = RestClient.builder()
                    .baseUrl(url)
                    .build();

            String responseStr = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(String.class);

            log.info("AI 호출 성공: {}", url);
            return objectMapper.readValue(responseStr, Map.class);

        } catch (Exception e) {
            log.error("AI 호출 실패: {}, error: {}", url, e.getMessage());
            throw new CustomException(ErrorCode.AI_CALL_FAILED, e);
        }
    }

    // =============================================
    // 요청 JSON 빌더
    // =============================================

    public Map<String, Object> buildAiRequest(Onboarding onboarding,
                                              SurveyResult survey,
                                              List<SolutionCard> cards) {
        Map<String, Object> request = new HashMap<>();
        Map<String, Object> skin = new HashMap<>();
        skin.put("base_type", survey.getBaseType());

        Map<String, Object> flags = new HashMap<>();
        flags.put("dehydrated", survey.getDehydrated());
        flags.put("sensitive", survey.getSensitive());
        flags.put("acne", survey.getAcneFlag());
        flags.put("mark_prone", survey.getMarkProne());
        skin.put("flags", flags);

        Map<String, Object> safety = new HashMap<>();
        safety.put("on_medication", survey.getOnMedication());
        safety.put("retinol_history", survey.getRetinolHistory());
        safety.put("inflammatory", survey.getInflammatory());
        skin.put("safety", safety);

        double sebumN = (survey.getSebumRaw() - 4) / 12.0;
        double hydraN = (survey.getHydraRaw() - 3) / 9.0;
        Map<String, Object> axisScores = new HashMap<>();
        axisScores.put("sebum", Math.round(sebumN * 100.0) / 100.0);
        axisScores.put("hydra", Math.round(hydraN * 100.0) / 100.0);
        skin.put("axis_scores", axisScores);

        Map<String, Object> troubleScores = new HashMap<>();
        troubleScores.put("피지량", survey.getTsSebum());
        troubleScores.put("모공",   survey.getTsPore());
        troubleScores.put("댕김",   survey.getTsDryness());
        troubleScores.put("붉은기", survey.getTsRedness());
        troubleScores.put("여드름", survey.getTsAcne());
        troubleScores.put("흔적",   survey.getTsMark());
        skin.put("trouble_scores", troubleScores);

        request.put("skin", skin);

        Map<String, Object> dday = new HashMap<>();
        dday.put("remaining_days", onboarding.getDDay());
        dday.put("event_type", onboarding.getPurpose());
        request.put("dday", dday);

        Map<String, Object> concern = new HashMap<>();
        concern.put("raw", survey.getConcernRaw());
        request.put("concern", concern);

        request.put("history_cards", buildHistoryCards(cards));
        return request;
    }

    public Map<String, Object> buildAi2Request(String newConcern,
                                               Onboarding onboarding,
                                               SurveyResult survey,
                                               List<SolutionCard> cards) {
        Map<String, Object> request = new HashMap<>();
        request.put("new_concern", newConcern);
        request.put("history_cards", buildHistoryCards(cards));

        Map<String, Object> dday = new HashMap<>();
        dday.put("remaining_days", onboarding.getDDay());
        request.put("dday", dday);

        Map<String, Object> skin = new HashMap<>();
        skin.put("base_type", survey.getBaseType());

        Map<String, Object> flags = new HashMap<>();
        flags.put("dehydrated", survey.getDehydrated());
        flags.put("sensitive", survey.getSensitive());
        flags.put("acne", survey.getAcneFlag());
        flags.put("mark_prone", survey.getMarkProne());
        skin.put("flags", flags);

        Map<String, Object> safety = new HashMap<>();
        safety.put("on_medication", survey.getOnMedication());
        safety.put("retinol_history", survey.getRetinolHistory());
        safety.put("inflammatory", survey.getInflammatory());
        skin.put("safety", safety);

        request.put("skin", skin);
        return request;
    }

    public Map<String, Object> buildAi3Request(Onboarding onboarding,
                                               SurveyResult survey,
                                               List<SolutionCard> cards,
                                               int completedDays,
                                               int totalDays,
                                               String afterScoreKey,
                                               Double afterScoreValue) {
        Map<String, Object> request = new HashMap<>();
        request.put("history_cards", buildHistoryCards(cards));

        Map<String, Object> todoStats = new HashMap<>();
        todoStats.put("total_days", totalDays);
        todoStats.put("completed_days", completedDays);
        todoStats.put("rate", totalDays > 0
                ? Math.round((completedDays / (double) totalDays) * 100.0) / 100.0 : 0.0);
        request.put("todo_stats", todoStats);

        Map<String, Object> beforeScores = new HashMap<>();
        beforeScores.put("피지량", survey.getTsSebum());
        beforeScores.put("모공",   survey.getTsPore());
        beforeScores.put("댕김",   survey.getTsDryness());
        beforeScores.put("붉은기", survey.getTsRedness());
        beforeScores.put("여드름", survey.getTsAcne());
        beforeScores.put("흔적",   survey.getTsMark());
        request.put("before_scores", beforeScores);

        if (afterScoreKey != null && afterScoreValue != null) {
            Map<String, Object> afterScores = new HashMap<>();
            afterScores.put(afterScoreKey, afterScoreValue);
            request.put("after_scores", afterScores);
        }

        request.put("event_type", onboarding.getPurpose());
        return request;
    }

    // =============================================
    // history_cards 빌더
    // =============================================

    private List<Map<String, Object>> buildHistoryCards(List<SolutionCard> cards) {
        if (cards == null || cards.isEmpty()) return List.of();

        List<Map<String, Object>> result = new ArrayList<>();

        cards.stream()
                .filter(c -> "INITIAL".equals(c.getCardType()))
                .findFirst()
                .ifPresent(c -> result.add(toHistoryCard(c)));

        cards.stream()
                .filter(c -> !"INITIAL".equals(c.getCardType()))
                .sorted(Comparator.comparing(SolutionCard::getCreatedAt).reversed())
                .limit(3)
                .forEach(c -> result.add(toHistoryCard(c)));

        return result;
    }

    private Map<String, Object> toHistoryCard(SolutionCard card) {
        Map<String, Object> map = new HashMap<>();
        map.put("date", card.getCreatedAt().toLocalDate().toString());
        map.put("dday_at_time", card.getDdayAtTime());
        map.put("summary", card.getCardSummary());
        map.put("prescribed_ingredients", parseJsonArray(card.getPrescribedIngredients()));
        map.put("excluded_ingredients", parseJsonArray(card.getExcludedIngredients()));
        return map;
    }

    private List<String> parseJsonArray(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return objectMapper.readValue(json, List.class);
        } catch (Exception e) {
            return List.of();
        }
    }

    // =============================================
    // Mock 데이터 (AI 서버 연결 전 테스트용)
    // =============================================

    public Map<String, Object> getMockResponse() {
        try {
            String mockJson = """
                {
                  "type_description": "세안 후 1~2시간만 지나도 T존을 포함한 얼굴 전반이 번들거리는 타입입니다.",
                  "cosmetic": {
                    "summary": "피지량이 8점, 모공이 7점으로 높게 나타났어요.",
                    "recommended": [
                      {
                        "ingredient": "나이아신아마이드",
                        "effect": "피지 조절에 널리 사용되는 성분이에요",
                        "timeline": "4주 전후부터 변화가 보고됩니다",
                        "cautions": ["고농도는 자극이 될 수 있어 저농도부터 시작하세요"],
                        "products": ["A205", "B103"]
                      }
                    ],
                    "excluded": [
                      {
                        "ingredient": "살리실산",
                        "reason": "적응 기간이 필요한데 D-Day까지 여유가 부족해요"
                      }
                    ],
                    "closing": "낮에는 자외선 차단제를 꼭 사용해 주세요."
                  },
                  "cleansing": {
                    "guide": ["기름은 기름으로 녹여야 합니다.", "약산성 클렌저를 사용하세요."],
                    "management": ["피지를 100% 없애려 하지 마세요."]
                  },
                  "skincare_order": {
                    "basic_steps": ["세안", "토너", "세럼·앰플", "크림", "선크림(낮)"],
                    "application_rules": ["묽은 것부터 바릅니다."]
                  },
                  "card": {
                    "type": "INITIAL",
                    "dday_at_time": 30,
                    "concern_summary": "턱 여드름 + 피지 과다",
                    "prescribed_ingredients": ["나이아신아마이드"],
                    "excluded_ingredients": ["살리실산"]
                  }
                }
                """;
            return objectMapper.readValue(mockJson, Map.class);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.AI_CALL_FAILED, e);
        }
    }

    public Map<String, Object> getMockAi2Response() {
        Map<String, Object> card = new HashMap<>();
        card.put("type", "UPDATE");
        card.put("dday_at_time", 25);
        card.put("concern_summary", "볼 부위 붉은기 발생");
        card.put("response", "초기 반응일 수 있어요. 사용 빈도를 주 2회로 줄여보세요.");
        card.put("action", "REDUCE");
        card.put("cautions", List.of("줄여도 붉은기가 3일 이상 지속되면 중단해 주세요"));
        card.put("prescribed_ingredients", List.of("히알루론산", "세라마이드"));
        card.put("excluded_ingredients", List.of("살리실산"));

        Map<String, Object> response = new HashMap<>();
        response.put("card", card);
        return response;
    }

    public Map<String, Object> getMockAi3Response() {
        Map<String, Object> journey = new HashMap<>();
        journey.put("summary", "결혼식까지 30일 동안 여드름과 피지 관리에 집중하셨어요.");
        journey.put("highlights", List.of(
                "세안 루틴을 22일 지키셨어요 (73%)",
                "피지량이 8점에서 6점으로 낮아졌습니다"
        ));
        journey.put("next_step", "이번에 미뤄둔 레티놀은 지금부터 시작하시면 좋아요.");
        journey.put("closing", "자외선 차단은 계속 지켜주세요.");

        Map<String, Object> response = new HashMap<>();
        response.put("journey", journey);
        return response;
    }
}