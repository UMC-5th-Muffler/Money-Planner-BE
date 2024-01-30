package com.umc5th.muffler.domain.dailyplan.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc5th.muffler.entity.QDailyPlan;
import com.umc5th.muffler.entity.constant.Rate;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DailyPlanRepositoryImpl implements DailyPlanRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Rate> findRatesByGoalAndDateRange(Long goalId, LocalDate startDate, LocalDate endDate) {
        QDailyPlan dailyPlan = QDailyPlan.dailyPlan;

        return queryFactory
                .select(dailyPlan.rate)
                .from(dailyPlan)
                .where(dailyPlan.goal.id.eq(goalId),
                        dailyPlan.date.between(startDate, endDate)
                ).orderBy(dailyPlan.date.asc())
                .fetch();
    }
}
