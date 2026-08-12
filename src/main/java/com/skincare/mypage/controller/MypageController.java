package com.skincare.mypage.controller;

import com.skincare.common.response.ApiResponse;
import com.skincare.mypage.service.MypageService;
import com.skincare.session.entity.Session;
import com.skincare.session.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;
    private final SessionService sessionService;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMypage(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        Map<String, Object> response = mypageService.getMypage(session);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}