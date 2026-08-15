package com.skincare.card.controller;

import com.skincare.card.dto.CardResponseDto;
import com.skincare.card.dto.ConcernRequestDto;
import com.skincare.card.service.CardService;
import com.skincare.common.response.ApiResponse;
import com.skincare.session.entity.Session;
import com.skincare.session.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Cards", description = "솔루션 카드 API - 고민 입력 및 카드 목록 조회")
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final SessionService sessionService;

    @Operation(summary = "새 고민 입력", description = "루틴 중 발생한 피부 고민을 입력하면 AI가 즉시 분석해 MAINTAIN·REDUCE·PAUSE·RECHECK 중 하나로 대응 지침을 반환합니다.")
    @PostMapping("/concern")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addConcern(
            @Valid @RequestBody ConcernRequestDto request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        Map<String, Object> response =
                cardService.addConcern(session, request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "카드 목록 조회", description = "지금까지 생성된 솔루션 카드 목록을 조회합니다. INITIAL(최초), UPDATE(고민 입력), DDAY_CHANGE(날짜 변경) 타입으로 구분됩니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<CardResponseDto>> getCards(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        CardResponseDto response = cardService.getCards(session);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}