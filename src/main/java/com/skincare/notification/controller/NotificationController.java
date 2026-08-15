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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "Notifications", description = "알림 API - 알림 목록 조회 및 ON/OFF 설정")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SessionService sessionService;
    private final OnboardingRepository onboardingRepository;

    @Operation(summary = "알림 목록 조회", description = "받은 알림 목록을 조회합니다. 조회 시 자동으로 읽음 처리됩니다. TODO_INCOMPLETE(밤 9시 미완료), CONCERN_MISSING(7일 미입력) 두 가지 타입이 있습니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNotifications(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        Onboarding onboarding = onboardingRepository
                .findBySessionAndIsActiveTrue(session)
                .orElseThrow(() -> new CustomException(ErrorCode.ONBOARDING_NOT_FOUND));

        notificationService.markAllAsRead(onboarding);

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

        return ResponseEntity.ok(ApiResponse.success(Map.of("notifications", notiList)));
    }

    @Operation(summary = "알림 ON/OFF 토글", description = "알림 수신 여부를 ON/OFF 전환합니다. 호출할 때마다 현재 상태가 반전됩니다. notiEnabled: true면 ON, false면 OFF입니다.")
    @PatchMapping("/toggle")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleNoti(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        boolean notiEnabled = notificationService.toggleNoti(session);

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "notiEnabled", notiEnabled,
                "message", notiEnabled ? "알림이 활성화되었습니다" : "알림이 비활성화되었습니다"
        )));
    }
}