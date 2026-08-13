package com.skincare.card.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skincare.card.entity.SolutionCard;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CardResponseDto {

    private final List<CardDto> cards;

    public CardResponseDto(List<SolutionCard> cards) {
        this.cards = cards.stream()
                .map(CardDto::new)
                .collect(Collectors.toList());
    }

    @Getter
    public static class CardDto {

        private static final ObjectMapper MAPPER = new ObjectMapper(); // ✅ static 상수

        private final Long cardId;
        private final String cardType;
        private final String cardSummary;
        private final Integer ddayAtTime;
        private final String status;
        private final List<String> prescribedIngredients;
        private final List<String> excludedIngredients;
        private final LocalDateTime createdAt;

        public CardDto(SolutionCard card) {
            this.cardId = card.getId();
            this.cardType = card.getCardType();
            this.cardSummary = card.getCardSummary();
            this.ddayAtTime = card.getDdayAtTime();
            this.status = card.getStatus();
            this.prescribedIngredients = parseJson(card.getPrescribedIngredients());
            this.excludedIngredients = parseJson(card.getExcludedIngredients());
            this.createdAt = card.getCreatedAt();
        }

        private List<String> parseJson(String json) {
            if (json == null || json.isBlank()) return List.of();
            try {
                return MAPPER.readValue(json, List.class); // ✅ static 상수 사용
            } catch (JsonProcessingException e) {
                return List.of();
            }
        }
    }
}