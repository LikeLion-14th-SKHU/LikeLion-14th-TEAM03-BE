package com.skincare.notification.controller;

import com.skincare.common.exception.CustomException;
import com.skincare.common.exception.ErrorCode;
import com.skincare.common.response.ApiResponse;
import com.skincare.notification.entity.Notification;
import com.skincare.notification.service.NotificationService;
import com.skincare.onboarding.entity.Onboarding;
import com.skincare.onboarding.repository.OnboardingRepository;
import com.skincare.session.entity.Session;
import com.skincare.session.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SessionService sessionService;
    private final OnboardingRepository onboardingRepository;

    // 알림 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNotifications(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        Onboarding onboarding = onboardingRepository
                .findBySessionAndIsActiveTrue(session)
                .orElseThrow(() -> new CustomException(ErrorCode.ONBOARDING_NOT_FOUND));

        List<Notification> notifications =
                notificationService.getNotifications(onboarding);

        List<Map<String, Object>> notiList = notifications.stream()
                .map(n -> Map.of(
                        "notiType", (Object) n.getNotiType(),
                        "message", n.getMessage(),
                        "isRead", n.getIsRead(),
                        "createdAt", n.getCreatedAt()
                ))
                .collect(Collectors.toList());

        // 조회 시 읽음 처리
        notificationService.markAllAsRead(onboarding);

        return ResponseEntity.ok(ApiResponse.success(Map.of("notifications", notiList)));
    }
}