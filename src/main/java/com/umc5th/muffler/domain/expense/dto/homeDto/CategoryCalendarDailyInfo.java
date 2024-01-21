package com.umc5th.muffler.domain.expense.dto.homeDto;

import com.umc5th.muffler.entity.constant.Level;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCalendarDailyInfo {

    private Long dailyTotalCost;
    private Level dailyRate;
}