package com.umc5th.muffler.domain.dailyplan.repository;

import com.umc5th.muffler.entity.constant.Rate;
import java.time.LocalDate;
import java.util.List;

public interface DailyPlanRepositoryCustom {
    List<Rate> findRatesByGoalAndDateRange(Long goalId, LocalDate startDate, LocalDate endDate);
}
