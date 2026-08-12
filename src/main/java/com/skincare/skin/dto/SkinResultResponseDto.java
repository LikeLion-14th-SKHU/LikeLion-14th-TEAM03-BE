package com.skincare.skin.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skincare.skin.entity.SkinResult;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SkinResultResponseDto {

    private final Object cosmetic;
    private final Object routines;
    private final LocalDateTime updatedAt;

    public SkinResultResponseDto(SkinResult skinResult) {
        ObjectMapper objectMapper = new ObjectMapper();
        Object cosmeticObj;
        Object routinesObj;

        try {
            cosmeticObj = objectMapper.readValue(skinResult.getCosmeticJson(), Object.class);
            routinesObj = objectMapper.readValue(skinResult.getRoutinesJson(), Object.class);
        } catch (JsonProcessingException e) {
            cosmeticObj = skinResult.getCosmeticJson();
            routinesObj = skinResult.getRoutinesJson();
        }

        this.cosmetic = cosmeticObj;
        this.routines = routinesObj;
        this.updatedAt = skinResult.getUpdatedAt();
    }
}