package com.skincare.todo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TodoCheckRequestDto {

    private Boolean cleansingDone;
    private Boolean skincareDone;
}