package com.skincare.plan.controller;

import com.skincare.plan.dto.PlanResultResponseDto;
import com.skincare.plan.dto.PlanFinishRequestDto;
import com.skincare.plan.service.PlanService;
import com.skincare.session.entity.Session;
import com.skincare.session.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plan")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final SessionService sessionService;

    // D-Day 종료 + AI 3차 호출
    @PostMapping("/finish")
    public ResponseEntity<PlanResultResponseDto> finishPlan(
            @RequestBody PlanFinishRequestDto request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        PlanResultResponseDto response =
                planService.finishPlan(session, request);

        return ResponseEntity.ok(response);
    }

    // 종료 결과 조회
    @GetMapping("/result")
    public ResponseEntity<PlanResultResponseDto> getPlanResult(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        PlanResultResponseDto response = planService.getPlanResult(session);

        return ResponseEntity.ok(response);
    }
}