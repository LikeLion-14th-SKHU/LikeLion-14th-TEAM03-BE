package com.skincare.onboarding.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class OnboardingRequestDto {

    @NotBlank(message = "이름을 입력해주세요")
    private String name;

    @NotBlank(message = "목적을 입력해주세요")
    private String purpose;

    @NotNull(message = "목표 날짜를 입력해주세요")
    private LocalDate goalDate;

    @NotNull(message = "나이를 입력해주세요")
    @Positive(message = "나이는 양수여야 합니다")
    private Integer age;

    @NotBlank(message = "성별을 입력해주세요")
    private String gender; // MALE / FEMALE

    @NotNull(message = "설문 답변을 입력해주세요")
    @Valid
    private SurveyAnswerDto surveyAnswers;

    @NotBlank(message = "피부 고민을 입력해주세요")
    private String concernRaw;
}