package com.skincare.session.dto;

import com.skincare.session.entity.Session;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SessionResponseDto {

    private final String sessionId;
    private final LocalDateTime createdAt;
    private final boolean valid;

    public SessionResponseDto(Session session) {
        this.sessionId = session.getSessionUuid();
        this.createdAt = session.getCreatedAt();
        this.valid = !session.isExpired();
    }
}