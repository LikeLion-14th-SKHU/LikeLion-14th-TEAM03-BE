package com.skincare.notification.service;

import com.skincare.card.entity.SolutionCard;
import com.skincare.card.repository.SolutionCardRepository;
import com.skincare.common.exception.CustomException;
import com.skincare.common.exception.ErrorCode;
import com.skincare.notification.entity.Notification;
import com.skincare.notification.repository.NotificationRepository;
import com.skincare.onboarding.entity.Onboarding;
import com.skincare.onboarding.repository.OnboardingRepository;
import com.skincare.session.entity.Session;
import com.skincare.todo.entity.TodoCheck;
import com.skincare.todo.repository.TodoCheckRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final OnboardingRepository onboardingRepository;
    private final TodoCheckRepository todoCheckRepository;
    private final SolutionCardRepository solutionCardRepository;

    // 알림 목록 조회
    @Transactional(readOnly = true)
    public List<Notification> getNotifications(Onboarding onboarding) {
        return notificationRepository
                .findByOnboardingOrderByCreatedAtDesc(onboarding);
    }

    // 알림 읽음 처리
    @Transactional
    public void markAllAsRead(Onboarding onboarding) {
        List<Notification> notifications = notificationRepository
                .findByOnboardingOrderByCreatedAtDesc(onboarding);
        notifications.forEach(Notification::markAsRead);
    }

    // ✅ 알림 ON/OFF 토글
    @Transactional
    public boolean toggleNoti(Session session) {
        Onboarding onboarding = onboardingRepository
                .findBySessionAndIsActiveTrue(session)
                .orElseThrow(() -> new CustomException(ErrorCode.ONBOARDING_NOT_FOUND));
        onboarding.toggleNoti();
        return onboarding.getNotiEnabled();
    }

    // TodoList 미완료 알림 - 매일 밤 9시
    @Scheduled(cron = "0 0 21 * * *")
    @Transactional
    public void checkTodoNotification() {
        log.info("TodoList 미완료 알림 스케줄러 실행: {}", LocalDateTime.now());
        LocalDate today = LocalDate.now();

        List<Onboarding> activeOnboardings = onboardingRepository
                .findAllByIsActiveTrue();

        for (Onboarding onboarding : activeOnboardings) {
            if (onboarding.getGoalDate().isBefore(today)) continue;

            // 알림 OFF면 스킵
            if (!onboarding.getNotiEnabled()) continue;

            // 오늘 이미 발송했으면 스킵
            boolean alreadyNotified = notificationRepository
                    .findByOnboardingOrderByCreatedAtDesc(onboarding)
                    .stream()
                    .anyMatch(n -> "TODO_INCOMPLETE".equals(n.getNotiType())
                            && n.getCreatedAt().toLocalDate().equals(today));
            if (alreadyNotified) continue;

            Optional<TodoCheck> todayCheck = todoCheckRepository
                    .findByOnboardingAndCheckDate(onboarding, today);

            boolean needNoti = todayCheck.isEmpty()
                    || (!todayCheck.get().getCleansingDone()
                    || !todayCheck.get().getSkincareDone());

            if (needNoti) {
                String message = "오늘 스킨케어 루틴을 아직 완료하지 않으셨어요! "
                        + "D-" + onboarding.getDDay() + " 남았어요 💪";
                Notification noti = Notification.builder()
                        .onboarding(onboarding)
                        .notiType("TODO_INCOMPLETE")
                        .message(message)
                        .build();
                notificationRepository.save(noti);
            }
        }
    }

    // 고민 7일 미입력 알림 - 매일 오전 10시 (7일 이후 1회만 발송) ✅
    @Scheduled(cron = "0 0 10 * * *")
    @Transactional
    public void checkConcernNotification() {
        log.info("고민 미입력 알림 스케줄러 실행: {}", LocalDateTime.now());
        LocalDate today = LocalDate.now();

        List<Onboarding> activeOnboardings = onboardingRepository
                .findAllByIsActiveTrue();

        for (Onboarding onboarding : activeOnboardings) {
            if (onboarding.getGoalDate().isBefore(today)) continue;

            // 알림 OFF면 스킵
            if (!onboarding.getNotiEnabled()) continue;

            // UPDATE 카드만 기준으로
            List<SolutionCard> cards = solutionCardRepository
                    .findByOnboardingOrderByCreatedAtAsc(onboarding);

            List<SolutionCard> updateCards = cards.stream()
                    .filter(c -> "UPDATE".equals(c.getCardType()))
                    .collect(Collectors.toList());

            if (updateCards.isEmpty()) continue;

            SolutionCard lastCard = updateCards.get(updateCards.size() - 1);
            LocalDate lastCardDate = lastCard.getCreatedAt().toLocalDate();
            long daysSinceLastCard = ChronoUnit.DAYS.between(lastCardDate, today);

            if (daysSinceLastCard >= 7) {
                // ✅ 마지막 카드 이후에 이미 알림 발송했으면 스킵
                boolean alreadyNotified = notificationRepository
                        .findByOnboardingOrderByCreatedAtDesc(onboarding)
                        .stream()
                        .anyMatch(n -> "CONCERN_MISSING".equals(n.getNotiType())
                                && n.getCreatedAt().toLocalDate().isAfter(lastCardDate));
                if (alreadyNotified) continue;

                String message = "일주일 동안 피부 고민을 적지 않으셨어요. "
                        + "요즘 피부는 어떠세요? 😊";
                Notification noti = Notification.builder()
                        .onboarding(onboarding)
                        .notiType("CONCERN_MISSING")
                        .message(message)
                        .build();
                notificationRepository.save(noti);
            }
        }
    }
}