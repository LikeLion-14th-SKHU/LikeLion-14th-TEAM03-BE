package com.skincare.onboarding.controller;

import com.skincare.common.response.ApiResponse;
import com.skincare.onboarding.dto.GoalDateUpdateDto;
import com.skincare.onboarding.dto.OnboardingRequestDto;
import com.skincare.onboarding.dto.OnboardingResponseDto;
import com.skincare.onboarding.service.OnboardingService;
import com.skincare.session.entity.Session;
import com.skincare.session.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Onboarding", description = "온보딩 API - 피부 설문 및 D-Day 설정")
@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;
    private final SessionService sessionService;

    @Operation(summary = "온보딩 저장", description = "18문항 피부 설문 결과를 저장하고 AI 피부 진단을 실행합니다. 온보딩 완료 시 호출하세요.")
    @PostMapping
    public ResponseEntity<ApiResponse<OnboardingResponseDto>> saveOnboarding(
            @Valid @RequestBody OnboardingRequestDto request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        OnboardingResponseDto response =
                onboardingService.saveOnboarding(session, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "온보딩 조회", description = "현재 활성화된 온보딩 정보를 조회합니다. 피부 타입, D-Day, 알림 설정 등을 반환합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<OnboardingResponseDto>> getOnboarding(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        OnboardingResponseDto response =
                onboardingService.getOnboarding(session);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "D-Day 날짜 변경", description = "목표 날짜(D-Day)를 재설정합니다. 변경 시 AI가 새로운 기간에 맞는 루틴을 재생성합니다.")
    @PatchMapping("/goal-date")
    public ResponseEntity<ApiResponse<Void>> updateGoalDate(
            @Valid @RequestBody GoalDateUpdateDto request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        onboardingService.updateGoalDate(session, request.getGoalDate());

        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "새로 검사 (재시작)", description = "현재 플랜을 종료하고 새로운 온보딩을 시작할 수 있는 상태로 만듭니다. 이후 POST /api/onboarding 재호출 필요.")
    @PostMapping("/restart")
    public ResponseEntity<ApiResponse<Void>> restartOnboarding(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        onboardingService.restartOnboarding(session);

        return ResponseEntity.ok(ApiResponse.success());
    }
}