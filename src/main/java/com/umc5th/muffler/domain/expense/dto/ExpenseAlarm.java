package com.umc5th.muffler.domain.expense.dto;

import com.umc5th.muffler.entity.constant.ExpenseAlarmTitle;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExpenseAlarm {
    private ExpenseAlarmTitle alarmTitle;
    private Long budget;
    private Long excessAmount;
}
