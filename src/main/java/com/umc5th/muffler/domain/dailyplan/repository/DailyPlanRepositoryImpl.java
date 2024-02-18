package com.umc5th.muffler.domain.dailyplan.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc5th.muffler.domain.dailyplan.dto.DailyPlanAlarm;
import com.umc5th.muffler.entity.QDailyPlan;
import com.umc5th.muffler.entity.QGoal;
import com.umc5th.muffler.entity.QMember;
import com.umc5th.muffler.entity.QMemberAlarm;
import com.umc5th.muffler.entity.constant.Rate;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    @Override
    public Map<LocalDate, Rate> findByGoalAndDateRangeGroupedByDate(Long goalId, LocalDate startDate, LocalDate endDate) {
        QDailyPlan dailyPlan = QDailyPlan.dailyPlan;

        List<Tuple> result = queryFactory
                .select(dailyPlan.date, dailyPlan.rate)
                .from(dailyPlan)
                .where(dailyPlan.goal.id.eq(goalId)
                        .and(dailyPlan.date.between(startDate, endDate)))
                .orderBy(dailyPlan.date.asc())
                .fetch();

        Map<LocalDate, Rate> map = new LinkedHashMap<>();
        result.forEach(tuple -> {
            LocalDate date = tuple.get(dailyPlan.date);
            Rate rate = tuple.get(dailyPlan.rate);
            map.put(date, rate);
        });
        return map;
    }

    @Override
    public List<DailyPlanAlarm> findDailyPlanAlarms(LocalDate date) {
        QMemberAlarm memberAlarm =  QMemberAlarm.memberAlarm;
        QMember member = QMember.member;
        QGoal goal = QGoal.goal;
        QDailyPlan dailyPlan = QDailyPlan.dailyPlan;

        return queryFactory
                .select(Projections.constructor(DailyPlanAlarm.class,
                        member.name.as("memberName"),
                        dailyPlan.budget.as("dailyBudget"),
                        memberAlarm.token.as("alarmToken")))
                .from(member)
                .join(memberAlarm).on(memberAlarm.id.eq(member.memberAlarm.id))
                .join(goal).on(goal.member.id.eq(member.id))
                .join(dailyPlan).on(dailyPlan.goal.id.eq(goal.id))
                .where(
                        dailyPlan.date.eq(date),
                        memberAlarm.token.isNotNull(),
                        memberAlarm.isDailyPlanRemindAgree.eq(true)
                )
                .fetch();
    }
}
