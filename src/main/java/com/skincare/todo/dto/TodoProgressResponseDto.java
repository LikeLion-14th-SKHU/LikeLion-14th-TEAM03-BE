package com.skincare.todo.dto;

import lombok.Getter;

@Getter
public class TodoProgressResponseDto {

    private final long totalDays;
    private final int cleansingDone;
    private final int skincareDone;
    private final int cleansingRate;
    private final int skincareRate;

    public TodoProgressResponseDto(long totalDays, int cleansingDone,
                                   int skincareDone) {
        this.totalDays = totalDays;
        this.cleansingDone = cleansingDone;
        this.skincareDone = skincareDone;
        this.cleansingRate = totalDays > 0
                ? (int) ((cleansingDone / (double) totalDays) * 100) : 0;
        this.skincareRate = totalDays > 0
                ? (int) ((skincareDone / (double) totalDays) * 100) : 0;
    }
}