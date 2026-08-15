package com.skincare.mypage.controller;

import com.skincare.common.response.ApiResponse;
import com.skincare.mypage.service.MypageService;
import com.skincare.session.entity.Session;
import com.skincare.session.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Mypage", description = "마이페이지 API - 내 정보 및 피부 결과 종합 조회")
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;
    private final SessionService sessionService;

    @Operation(summary = "마이페이지 조회", description = "내 정보(myInfo), 피부 결과(skinResult), 투두 달성률(todoStats), D-Day 종료 리포트(planResult)를 한번에 조회합니다. planResult는 D-Day 종료 전까지 null입니다.")
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