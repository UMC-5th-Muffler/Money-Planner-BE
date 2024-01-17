package com.umc5th.muffler.domain.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WholeCalendarResponse {

    private LocalDate calendarDate;
    private Long goalId;
    private String goalTitle;
    private Long goalBudget;
    private LocalDate goalStartDate;
    private LocalDate goalEndDate;
    private Long totalCost;
    private List<CategoryInfoDto> categoryList;
    private List<DailyInfoDto> dailyList;
}
