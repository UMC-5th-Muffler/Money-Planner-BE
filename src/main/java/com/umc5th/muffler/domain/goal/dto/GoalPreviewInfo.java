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
public class GoalPreviewInfo {

    private String title;
    private String icon;
    private Long totalBudget;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long totalCost;
    private LocalDate endDate;
}
