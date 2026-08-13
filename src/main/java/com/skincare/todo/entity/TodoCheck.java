package com.skincare.todo.entity;

import com.skincare.onboarding.entity.Onboarding;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "todo_checks",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"onboarding_id", "check_date"})
        })
@Getter
@NoArgsConstructor
public class TodoCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "onboarding_id", nullable = false)
    private Onboarding onboarding;

    @Column(name = "check_date", nullable = false)
    private LocalDate checkDate;

    @Column(name = "cleansing_done", nullable = false)
    private Boolean cleansingDone = false;

    @Column(name = "skincare_done", nullable = false)
    private Boolean skincareDone = false;

    @Column(name = "created_at", nullable = false, updatable = false) // ✅ 수정
    private LocalDateTime createdAt;

    @Builder
    public TodoCheck(Onboarding onboarding, LocalDate checkDate,
                     Boolean cleansingDone, Boolean skincareDone) {
        this.onboarding = onboarding;
        this.checkDate = checkDate;
        this.cleansingDone = cleansingDone;
        this.skincareDone = skincareDone;
        this.createdAt = LocalDateTime.now();
    }

    // ✅ null 체크 추가
    public void update(Boolean cleansingDone, Boolean skincareDone) {
        if (cleansingDone != null) this.cleansingDone = cleansingDone;
        if (skincareDone != null) this.skincareDone = skincareDone;
    }
}