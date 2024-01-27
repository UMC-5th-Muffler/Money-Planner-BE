package com.umc5th.muffler.domain.expense.dto.homeDto;

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

    private Long goalId;
    private String goalTitle;
    private Long goalBudget;
    private LocalDate goalStartDate;
    private LocalDate goalEndDate;
    private Long totalCost;
    private List<WholeCalendarDailyInfo> dailyList;
    private List<CategoryCalendarInfo> categoryCalendarInfo;
    private OtherGoalsResponse otherGoalsInfo;
}
