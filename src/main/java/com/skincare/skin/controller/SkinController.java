package com.skincare.skin.controller;

import com.skincare.session.entity.Session;
import com.skincare.session.service.SessionService;
import com.skincare.skin.dto.SkinResultResponseDto;
import com.skincare.skin.service.SkinService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/skin")
@RequiredArgsConstructor
public class SkinController {

    private final SkinService skinService;
    private final SessionService sessionService;

    // 피부 결과 조회 (더보기 세션 1, 2)
    @GetMapping("/result")
    public ResponseEntity<SkinResultResponseDto> getSkinResult(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        SkinResultResponseDto response = skinService.getSkinResult(session);

        return ResponseEntity.ok(response);
    }
}