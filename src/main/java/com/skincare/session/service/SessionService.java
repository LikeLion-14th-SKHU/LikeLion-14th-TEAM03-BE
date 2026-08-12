package com.skincare.session.service;

import com.skincare.session.entity.Session;
import com.skincare.session.repository.SessionRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private static final String COOKIE_NAME = "SESSION_ID";

    // 세션 생성 or 기존 세션 반환
    public Session getOrCreateSession(HttpServletRequest request,
                                      HttpServletResponse response) {
        // 쿠키에서 세션 UUID 조회
        Optional<String> uuidOpt = getCookieValue(request);

        if (uuidOpt.isPresent()) {
            Optional<Session> sessionOpt =
                    sessionRepository.findBySessionUuid(uuidOpt.get());

            if (sessionOpt.isPresent() && !sessionOpt.get().isExpired()) {
                return sessionOpt.get(); // 기존 세션 반환
            }
        }

        // 새 세션 생성
        String newUuid = UUID.randomUUID().toString();
        Session session = new Session(newUuid);
        sessionRepository.save(session);

        // 쿠키 설정
        Cookie cookie = new Cookie(COOKIE_NAME, newUuid);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60 * 24 * 30); // 30일
        cookie.setPath("/");
        response.addCookie(cookie);

        return session;
    }

    // 세션 확인
    public Optional<Session> findSession(HttpServletRequest request) {
        return getCookieValue(request)
                .flatMap(sessionRepository::findBySessionUuid)
                .filter(s -> !s.isExpired());
    }

    // 쿠키에서 SESSION_ID 값 추출
    private Optional<String> getCookieValue(HttpServletRequest request) {
        if (request.getCookies() == null) return Optional.empty();
        return Arrays.stream(request.getCookies())
                .filter(c -> COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }
}