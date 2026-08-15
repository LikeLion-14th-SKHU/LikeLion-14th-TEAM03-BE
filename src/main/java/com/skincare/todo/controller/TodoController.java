package com.skincare.todo.controller;

import com.skincare.common.response.ApiResponse;
import com.skincare.session.entity.Session;
import com.skincare.session.service.SessionService;
import com.skincare.todo.dto.TodoCheckRequestDto;
import com.skincare.todo.dto.TodoProgressResponseDto;
import com.skincare.todo.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Todo", description = "투두리스트 API - 매일 루틴 체크 및 진행률 조회")
@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;
    private final SessionService sessionService;

    @Operation(summary = "오늘 투두 체크 저장", description = "오늘의 세안(cleansingDone)과 기초화장품(skincareDone) 완료 여부를 저장합니다. 오늘 이미 저장된 기록이 있으면 업데이트됩니다.")
    @PostMapping("/check")
    public ResponseEntity<ApiResponse<Void>> saveCheck(
            @RequestBody TodoCheckRequestDto request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        todoService.saveCheck(session, request);

        return ResponseEntity.ok(ApiResponse.success());
    }

    @Operation(summary = "투두 진행률 조회", description = "온보딩 시작일부터 오늘까지의 누적 달성률을 조회합니다. totalDays, cleansingRate, skincareRate를 반환합니다.")
    @GetMapping("/progress")
    public ResponseEntity<ApiResponse<TodoProgressResponseDto>> getProgress(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        TodoProgressResponseDto response = todoService.getProgress(session);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}