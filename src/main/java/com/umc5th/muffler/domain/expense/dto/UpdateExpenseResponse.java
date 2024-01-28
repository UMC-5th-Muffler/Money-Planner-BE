package com.umc5th.muffler.domain.expense.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UpdateExpenseResponse {
    private AlarmControlDTO dailyBudgetAlarm;
    private AlarmControlDTO categoryBudgetAlarm;
}
