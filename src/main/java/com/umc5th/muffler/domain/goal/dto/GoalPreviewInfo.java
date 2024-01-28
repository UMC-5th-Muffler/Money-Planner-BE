package com.umc5th.muffler.domain.goal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class GoalPreviewInfo {

    private String title;
    private String icon;
    private Long totalBudget;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long totalCost;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDate endDate;
}
