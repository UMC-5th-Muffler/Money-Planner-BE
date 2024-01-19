package com.umc5th.muffler.domain.home.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WholeCalendarResponse {

    private LocalDate calendarDate;
    private Long goalId;
    private String goalTitle;
    private Long goalBudget;
    private LocalDate goalStartDate;
    private LocalDate goalEndDate;
    private Long totalCost;
    private List<DailyInfoDto> dailyList;
    private List<CategoryCalendarInfo> categoryCalendarInfo;
}
