package com.skincare.plan.controller;

import com.skincare.common.response.ApiResponse;
import com.skincare.plan.dto.BeforeScoreResponseDto;
import com.skincare.plan.dto.PlanFinishRequestDto;
import com.skincare.plan.dto.PlanResultResponseDto;
import com.skincare.plan.service.PlanService;
import com.skincare.session.entity.Session;
import com.skincare.session.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Plan", description = "플랜 종료 API - D-Day 종료 및 리포트 조회")
@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final SessionService sessionService;

    @Operation(summary = "D-Day 종료 및 리포트 생성", description = "D-Day 종료 시 호출합니다. 슬라이더로 입력받은 afterScoreKey(피지량/댕김/여드름/붉은기 중 1개)와 afterScoreValue(1~10)를 전달하면 AI가 30일 여정 리포트를 생성합니다. 슬라이더 초기값은 온보딩 시 해당 항목 Before 점수로 설정하세요.")
    @PostMapping("/finish")
    public ResponseEntity<ApiResponse<PlanResultResponseDto>> finishPlan(
            @RequestBody PlanFinishRequestDto request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        PlanResultResponseDto response =
                planService.finishPlan(session, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "종료 리포트 조회", description = "D-Day 종료 후 생성된 리포트를 조회합니다. journeySummary, improvementPoints, recommendationNext, todoCompletionRate, afterScoreKey, afterScoreValue를 반환합니다.")
    @GetMapping("/result")
    public ResponseEntity<ApiResponse<PlanResultResponseDto>> getPlanResult(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        PlanResultResponseDto response = planService.getPlanResult(session);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ✅ before score 조회
    @Operation(summary = "Before Score 조회", description = "D-Day 종료 화면 진입 시 호출합니다. 온보딩 시 측정된 trouble_scores 중 가장 높은 항목(afterScoreKey)과 점수(beforeScoreValue)를 반환합니다. 슬라이더 초기값으로 사용하세요.")
    @GetMapping("/before-score")
    public ResponseEntity<ApiResponse<BeforeScoreResponseDto>> getBeforeScore(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        BeforeScoreResponseDto response = planService.getBeforeScore(session);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}