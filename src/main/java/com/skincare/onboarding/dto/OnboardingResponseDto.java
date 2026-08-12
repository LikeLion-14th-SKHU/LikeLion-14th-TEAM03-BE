package com.skincare.onboarding.dto;

import com.skincare.onboarding.entity.Onboarding;
import com.skincare.onboarding.entity.SurveyResult;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class OnboardingResponseDto {

    private final Long onboardingId;
    private final String name;
    private final String purpose;
    private final LocalDate goalDate;
    private final long dDay;
    private final Integer age;
    private final String gender;
    private final String baseType;
    private final FlagsDto flags;
    private final SafetyDto safety;
    private final LocalDateTime createdAt;

    public OnboardingResponseDto(Onboarding onboarding, SurveyResult survey) {
        this.onboardingId = onboarding.getId();
        this.name = onboarding.getName();
        this.purpose = onboarding.getPurpose();
        this.goalDate = onboarding.getGoalDate();
        this.dDay = onboarding.getDDay();
        this.age = onboarding.getAge();
        this.gender = onboarding.getGender();
        this.baseType = survey.getBaseType();
        this.flags = new FlagsDto(survey);
        this.safety = new SafetyDto(survey);
        this.createdAt = onboarding.getCreatedAt();
    }

    @Getter
    public static class FlagsDto {
        private final Boolean dehydrated;
        private final Boolean sensitive;
        private final Boolean acne;
        private final Boolean markProne;

        public FlagsDto(SurveyResult survey) {
            this.dehydrated = survey.getDehydrated();
            this.sensitive = survey.getSensitive();
            this.acne = survey.getAcneFlag();
            this.markProne = survey.getMarkProne();
        }
    }

    @Getter
    public static class SafetyDto {
        private final Boolean onMedication;
        private final String retinolHistory;
        private final Boolean inflammatory;

        public SafetyDto(SurveyResult survey) {
            this.onMedication = survey.getOnMedication();
            this.retinolHistory = survey.getRetinolHistory();
            this.inflammatory = survey.getInflammatory();
        }
    }
}