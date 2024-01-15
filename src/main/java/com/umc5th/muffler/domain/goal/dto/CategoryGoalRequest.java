package com.umc5th.muffler.domain.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryGoalRequest {
    private Long categoryId;
    private Long categoryBudget;
}
