package com.skincare.onboarding.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
    @Min(value = 1, message = "나이는 1 이상이어야 합니다")
    @Max(value = 100, message = "나이는 100 이하여야 합니다")
    private Integer age;

    @NotBlank(message = "성별을 입력해주세요")
    @Pattern(regexp = "^(MALE|FEMALE)$", message = "성별은 MALE 또는 FEMALE이어야 합니다")
    private String gender;

    @NotNull(message = "설문 답변을 입력해주세요")
    @Valid
    private SurveyAnswerDto surveyAnswers;

    @NotBlank(message = "피부 고민을 입력해주세요")
    private String concernRaw;
}