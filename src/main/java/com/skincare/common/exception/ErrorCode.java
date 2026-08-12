package com.skincare.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // Session
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "세션 정보가 없습니다"),
    SESSION_EXPIRED(HttpStatus.UNAUTHORIZED, "세션이 만료되었습니다"),

    // Onboarding
    ONBOARDING_NOT_FOUND(HttpStatus.NOT_FOUND, "온보딩 정보가 없습니다"),
    INVALID_GOAL_DATE(HttpStatus.BAD_REQUEST, "목표 날짜는 오늘 이후여야 합니다"),
    SURVEY_RESULT_NOT_FOUND(HttpStatus.NOT_FOUND, "설문 결과가 없습니다"),

    // 피부 결과
    SKIN_RESULT_NOT_FOUND(HttpStatus.NOT_FOUND, "피부 결과가 없습니다"),
    PLAN_EXPIRED(HttpStatus.FORBIDDEN, "종료된 플랜입니다"),

    // 고민 카드
    CARD_NOT_FOUND(HttpStatus.NOT_FOUND, "카드 정보가 없습니다"),

    // 종료 결과
    PLAN_RESULT_NOT_FOUND(HttpStatus.NOT_FOUND, "종료 결과가 없습니다"),

    // AI
    AI_CALL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AI 호출에 실패했습니다"),

    // 공통
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    // 서버 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return name();
    }
}