package com.skincare.onboarding.controller;

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

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;
    private final SessionService sessionService;

    // 온보딩 저장
    @PostMapping
    public ResponseEntity<OnboardingResponseDto> saveOnboarding(
            @Valid @RequestBody OnboardingRequestDto request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        OnboardingResponseDto response =
                onboardingService.saveOnboarding(session, request);

        return ResponseEntity.ok(response);
    }

    // 온보딩 조회
    @GetMapping
    public ResponseEntity<OnboardingResponseDto> getOnboarding(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        OnboardingResponseDto response =
                onboardingService.getOnboarding(session);

        return ResponseEntity.ok(response);
    }

    // D-Day 재설정
    @PatchMapping("/goal-date")
    public ResponseEntity<Void> updateGoalDate(
            @RequestBody Map<String, String> body,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        LocalDate newGoalDate = LocalDate.parse(body.get("goalDate"));
        onboardingService.updateGoalDate(session, newGoalDate);

        return ResponseEntity.ok().build();
    }

    // 새로 검사
    @PostMapping("/restart")
    public ResponseEntity<Void> restartOnboarding(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        onboardingService.restartOnboarding(session);

        return ResponseEntity.ok().build();
    }
}