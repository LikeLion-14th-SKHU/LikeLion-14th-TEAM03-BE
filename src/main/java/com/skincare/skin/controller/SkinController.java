package com.skincare.skin.controller;

import com.skincare.common.response.ApiResponse;
import com.skincare.session.entity.Session;
import com.skincare.session.service.SessionService;
import com.skincare.skin.dto.SkinResultResponseDto;
import com.skincare.skin.service.SkinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Skin", description = "피부 결과 API - AI 진단 결과 조회")
@RestController
@RequestMapping("/api/skin")
@RequiredArgsConstructor
public class SkinController {

    private final SkinService skinService;
    private final SessionService sessionService;

    @Operation(summary = "피부 결과 조회", description = "AI 1차 분석 결과를 조회합니다. 추천 성분, 제외 성분, 세안법, 루틴 순서, 피부 타입 설명을 반환합니다. 더보기 화면에서 호출하세요.")
    @GetMapping("/result")
    public ResponseEntity<ApiResponse<SkinResultResponseDto>> getSkinResult(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        SkinResultResponseDto response = skinService.getSkinResult(session);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}