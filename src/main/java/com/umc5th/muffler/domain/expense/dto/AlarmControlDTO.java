package com.umc5th.muffler.domain.expense.dto;

import com.umc5th.muffler.entity.constant.ExpenseAlarm;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlarmControlDTO {
    private ExpenseAlarm alarmTitle;
    private Long budget;
    private Long excessAmount;
}
