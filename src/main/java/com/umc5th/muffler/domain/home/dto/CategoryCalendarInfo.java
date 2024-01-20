package com.umc5th.muffler.domain.home.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryCalendarInfo {

    private Long id;
    private String name;
    private Long categoryBudget;
    private Long categoryTotalCost;
    private List<DailyCategoryInfoDto> categoryGoalSummary;
    private List<Long> noGoalDailyTotalCost;
}
