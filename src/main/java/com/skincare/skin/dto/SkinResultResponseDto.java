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
    private final Object productsDetail;       // ✅ 추가
    private final Boolean needsMedicalConsult; // ✅ 추가
    private final LocalDateTime updatedAt;

    public SkinResultResponseDto(SkinResult skinResult, ObjectMapper objectMapper) {
        Object cosmeticObj;
        Object routinesObj;
        Object productsDetailObj; // ✅ 추가

        try {
            cosmeticObj = objectMapper.readValue(skinResult.getCosmeticJson(), Object.class);
            routinesObj = objectMapper.readValue(skinResult.getRoutinesJson(), Object.class);

            // ✅ productsDetailJson null 체크
            productsDetailObj = skinResult.getProductsDetailJson() != null
                    ? objectMapper.readValue(skinResult.getProductsDetailJson(), Object.class)
                    : null;

        } catch (JsonProcessingException e) {
            cosmeticObj = skinResult.getCosmeticJson();
            routinesObj = skinResult.getRoutinesJson();
            productsDetailObj = skinResult.getProductsDetailJson();
        }

        this.cosmetic = cosmeticObj;
        this.routines = routinesObj;
        this.productsDetail = productsDetailObj;           // ✅ 추가
        this.needsMedicalConsult = skinResult.getNeedsMedicalConsult(); // ✅ 추가
        this.updatedAt = skinResult.getUpdatedAt();
    }
}