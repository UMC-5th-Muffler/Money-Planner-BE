package com.umc5th.muffler.domain.expense.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class UpdateExpenseResponse {
    private AlarmControlDTO dailyBudgetAlarm;
    private AlarmControlDTO categoryBudgetAlarm;
}
