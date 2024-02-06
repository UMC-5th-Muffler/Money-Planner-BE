package com.umc5th.muffler.domain.expense.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExpenseResponse {
    private Long expenseId;
    private List<ExpenseAlarm> alarms;
}
