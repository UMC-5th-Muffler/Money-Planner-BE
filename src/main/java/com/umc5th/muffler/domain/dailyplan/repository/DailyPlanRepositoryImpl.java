package com.umc5th.muffler.domain.dailyplan.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc5th.muffler.entity.QDailyPlan;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DailyPlanRepositoryImpl implements DailyPlanRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Tuple> findDateAndRateByGoalAndDateRange(Long goalId, LocalDate startDate, LocalDate endDate) {
        QDailyPlan dailyPlan = QDailyPlan.dailyPlan;

         return queryFactory
                .select(dailyPlan.date, dailyPlan.rate)
                .from(dailyPlan)
                .where(dailyPlan.goal.id.eq(goalId)
                        .and(dailyPlan.date.between(startDate, endDate))
                ).fetch();
    }
}
