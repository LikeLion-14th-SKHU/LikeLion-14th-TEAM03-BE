package com.skincare.onboarding.entity;

import com.skincare.session.entity.Session;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "onboardings")
@Getter
@NoArgsConstructor
public class Onboarding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "purpose", nullable = false, length = 100)
    private String purpose;

    @Column(name = "goal_date", nullable = false)
    private LocalDate goalDate;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "gender", nullable = false, length = 10)
    private String gender;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Onboarding(Session session, String name, String purpose,
                      LocalDate goalDate, Integer age, String gender) {
        this.session = session;
        this.name = name;
        this.purpose = purpose;
        this.goalDate = goalDate;
        this.age = age;
        this.gender = gender;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateGoalDate(LocalDate newGoalDate) {
        this.goalDate = newGoalDate;
        this.updatedAt = LocalDateTime.now();
    }

    public long getDDay() {
        return java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.now(), this.goalDate);
    }
}