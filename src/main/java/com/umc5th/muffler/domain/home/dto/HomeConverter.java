package com.umc5th.muffler.domain.home.dto;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.constant.Level;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeConverter {

    public static WholeCalendarResponse toWholeCalendar(
            LocalDate date, Goal goal, LocalDate startDate, LocalDate endDate, Long totalCost,
            List<Long> dailyBudgetList, List<Long> dailyTotalCostList, List<Boolean> isZeroDayList, List<CategoryCalendarInfo> categoryCalendarInfo)
    {
        List<DailyInfoDto> dailyInfoList = new ArrayList<>();
        for (int i = 0; i < dailyBudgetList.size(); i++) {
            Long dailyBudget = dailyBudgetList.get(i);
            Long dailyTotalCost = i < dailyTotalCostList.size() ? dailyTotalCostList.get(i) : 0L;
            Boolean isZeroDay = isZeroDayList.get(i);
//            Level dailyRate = i < dailyRateList.size() ? dailyRateList.get(i) : null;
            Level dailyRate = Level.HIGH; // 임시

            DailyInfoDto dailyInfo = DailyInfoDto.builder()
                    .dailyBudget(dailyBudget)
                    .dailyTotalCost(dailyTotalCost)
                    .dailyRate(dailyRate)
                    .isZeroDay(isZeroDay)
                    .build();

            dailyInfoList.add(dailyInfo);
        }

        return WholeCalendarResponse.builder()
                .calendarDate(date)
                .goalId(goal.getId())
                .goalTitle(goal.getTitle())
                .goalBudget(goal.getTotalBudget())
                .goalStartDate(startDate)
                .goalEndDate(endDate)
                .totalCost(totalCost)
                .categoryCalendarInfo(categoryCalendarInfo)
                .dailyList(dailyInfoList)
                .build();
    }
}
