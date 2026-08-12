package com.skincare.session.controller;

import com.skincare.common.exception.CustomException;
import com.skincare.common.exception.ErrorCode;
import com.skincare.session.dto.SessionResponseDto;
import com.skincare.session.entity.Session;
import com.skincare.session.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    // 세션 생성 or 기존 세션 반환
    @PostMapping
    public ResponseEntity<SessionResponseDto> createSession(
            HttpServletRequest request,
            HttpServletResponse response) {

        Session session = sessionService.getOrCreateSession(request, response);
        return ResponseEntity.ok(new SessionResponseDto(session));
    }

    // 세션 확인
    @GetMapping
    public ResponseEntity<SessionResponseDto> checkSession(
            HttpServletRequest request) {

        Session session = sessionService.findSession(request)
                .orElseThrow(() -> new CustomException(ErrorCode.SESSION_NOT_FOUND));

        return ResponseEntity.ok(new SessionResponseDto(session));
    }
}