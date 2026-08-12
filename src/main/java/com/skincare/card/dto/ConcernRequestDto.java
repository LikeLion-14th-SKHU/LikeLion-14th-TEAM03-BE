package com.skincare.card.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConcernRequestDto {

    @NotBlank(message = "고민 내용을 입력해주세요")
    private String newConcern;
}