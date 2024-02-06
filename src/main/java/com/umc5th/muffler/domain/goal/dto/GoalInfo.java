package com.umc5th.muffler.domain.goal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoalInfo {
    private Long goalId;
    private String title;
    private String icon;
    private Long totalBudget;
    private Long totalCost;
    private LocalDate endDate;
}
