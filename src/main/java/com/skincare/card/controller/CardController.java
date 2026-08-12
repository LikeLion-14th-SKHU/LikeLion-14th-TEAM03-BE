package com.skincare.card.controller;

import com.skincare.card.dto.CardResponseDto;
import com.skincare.card.dto.ConcernRequestDto;
import com.skincare.card.service.CardService;
import com.skincare.session.entity.Session;
import com.skincare.session.service.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final SessionService sessionService;

    // 새 고민 입력 + AI 2차 호출
    @PostMapping("/concern")
    public ResponseEntity<Map<String, Object>> addConcern(
            @Valid @RequestBody ConcernRequestDto request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        Map<String, Object> response =
                cardService.addConcern(session, request);

        return ResponseEntity.ok(response);
    }

    // 카드 목록 조회
    @GetMapping
    public ResponseEntity<CardResponseDto> getCards(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        Session session = sessionService
                .getOrCreateSession(httpRequest, httpResponse);

        CardResponseDto response = cardService.getCards(session);

        return ResponseEntity.ok(response);
    }
}