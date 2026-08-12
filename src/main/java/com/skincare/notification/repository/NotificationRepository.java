package com.skincare.notification.repository;

import com.skincare.notification.entity.Notification;
import com.skincare.onboarding.entity.Onboarding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 현재 플랜 알림 목록 조회 (최신순)
    List<Notification> findByOnboardingOrderByCreatedAtDesc(Onboarding onboarding);

    // 읽지 않은 알림 수
    int countByOnboardingAndIsReadFalse(Onboarding onboarding);
}