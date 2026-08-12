package com.skincare.todo.controller;

import com.skincare.common.response.ApiResponse;
import com.skincare.session.entity.Session;
import com.skincare.session.service.SessionService;
import com.skincare.todo.dto.TodoCheckRequestDto;
import com.skincare.todo.dto.TodoProgressResponseDto;
import com.skincare.todo.service.TodoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;
    private final SessionService sessionService;

    // 오늘 체크 저장
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

    // 진행률 조회
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