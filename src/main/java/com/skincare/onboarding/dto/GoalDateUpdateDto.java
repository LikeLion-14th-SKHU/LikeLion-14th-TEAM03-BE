package com.skincare.onboarding.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class GoalDateUpdateDto {

    @NotNull(message = "목표 날짜를 입력해주세요")
    private LocalDate goalDate;
}