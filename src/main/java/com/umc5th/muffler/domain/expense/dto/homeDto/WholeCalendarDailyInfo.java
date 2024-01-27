package com.umc5th.muffler.domain.expense.dto.homeDto;

import com.umc5th.muffler.entity.constant.Level;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class WholeCalendarDailyInfo {

    private Long dailyBudget;
    private Long dailyTotalCost;
    private Level dailyRate;
    private Boolean isZeroDay;
}
