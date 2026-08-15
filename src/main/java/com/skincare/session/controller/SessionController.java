package com.skincare.session.controller;

import com.skincare.common.exception.CustomException;
import com.skincare.common.exception.ErrorCode;
import com.skincare.common.response.ApiResponse;
import com.skincare.session.dto.SessionResponseDto;
import com.skincare.session.entity.Session;
import com.skincare.session.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Session", description = "세션 관리 API")
@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @Operation(summary = "세션 생성", description = "앱 첫 진입 시 호출. 기존 세션이 있으면 반환, 없으면 새로 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<SessionResponseDto>> createSession(
            HttpServletRequest request,
            HttpServletResponse response) {

        Session session = sessionService.getOrCreateSession(request, response);
        return ResponseEntity.ok(ApiResponse.success(new SessionResponseDto(session)));
    }

    @Operation(summary = "세션 확인", description = "현재 세션이 유효한지 확인합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<SessionResponseDto>> checkSession(
            HttpServletRequest request) {

        Session session = sessionService.findSession(request)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        return ResponseEntity.ok(ApiResponse.success(new SessionResponseDto(session)));
    }
}