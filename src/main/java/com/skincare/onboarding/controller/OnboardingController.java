package com.skincare.onboarding.controller;

import com.skincare.common.response.ApiResponse;
import com.skincare.onboarding.dto.GoalDateUpdateDto;
import com.skincare.onboarding.dto.OnboardingRequestDto;
import com.skincare.onboarding.dto.OnboardingResponseDto;
import com.skincare.onboarding.service.OnboardingService;
import com.skincare.session.entity.Session;
import com.skincare.session.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;
    private final SessionService sessionService;

    // 온보딩 저장
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

    // 온보딩 조회
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

    // D-Day 재설정
    @PatchMapping("/goal-date")
    public ResponseEntity<ApiResponse<Void>> updateGoalDate(
            @Valid @RequestBody GoalDateUpdateDto request, // ✅ DTO로 변경
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        onboardingService.updateGoalDate(session, request.getGoalDate());

        return ResponseEntity.ok(ApiResponse.success());
    }

    // 새로 검사
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