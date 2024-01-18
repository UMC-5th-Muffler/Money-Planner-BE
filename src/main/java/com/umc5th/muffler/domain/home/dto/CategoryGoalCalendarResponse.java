package com.umc5th.muffler.domain.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryGoalCalendarResponse {

    private Long categoryBudget;
    private Long categoryTotalCost;
    private List<DailyCategoryInfoDto> dailyList;
}