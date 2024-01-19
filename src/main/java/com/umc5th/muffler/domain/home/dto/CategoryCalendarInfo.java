package com.umc5th.muffler.domain.home.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryCalendarInfo {

    private Long id;
    private String name;
    private Long categoryBudget;
    private Long categoryTotalCost;
    private List<DailyCategoryInfoDto> categoryGoalSummary;
    private List<Long> noGoalDailyTotalCost;
}
