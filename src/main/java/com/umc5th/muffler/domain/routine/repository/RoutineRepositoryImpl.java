package com.umc5th.muffler.domain.routine.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc5th.muffler.domain.routine.dto.InsertableRoutine;
import com.umc5th.muffler.entity.QCategory;
import com.umc5th.muffler.entity.QDailyPlan;
import com.umc5th.muffler.entity.QGoal;
import com.umc5th.muffler.entity.QMember;
import com.umc5th.muffler.entity.QRoutine;
import com.umc5th.muffler.entity.QWeeklyRepeatDay;
import com.umc5th.muffler.entity.constant.RoutineType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RoutineRepositoryImpl implements RoutineRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<InsertableRoutine> findInsertableRoutines(LocalDate today) {
        QRoutine routine = QRoutine.routine;
        QMember member = QMember.member;
        QCategory category = QCategory.category;
        QGoal goal = QGoal.goal;
        QDailyPlan dailyPlan = QDailyPlan.dailyPlan;
        QWeeklyRepeatDay weeklyRepeatDay = QWeeklyRepeatDay.weeklyRepeatDay;

        int day = today.getDayOfMonth();
        DayOfWeek dayOfWeek = today.getDayOfWeek();

        List<InsertableRoutine> insertableRoutines = queryFactory
                .select(
                    Projections.fields(InsertableRoutine.class,
                        routine.id.as("routineId"),
                        routine.type.as("routineType"),
                        routine.title.as("routineTitle"),
                        routine.memo.as("routineMemo"),
                        routine.cost.as("routineCost"),
                        routine.startDate.as("routineStartDate"),
                        routine.endDate.as("routineEndDate"),
                        routine.weeklyTerm.as("routineWeeklyTerm"),
                        weeklyRepeatDay.dayOfWeek.as("routineDayOfWeek"),
                        routine.monthlyRepeatDay.as("routineDayOfMonth"),
                        dailyPlan.totalCost.as("dailyPlanTotalCost"),
                        member.id.as("memberId"),
                        category.id.as("categoryId"),
                        dailyPlan.id.as("dailyPlanId")
                    )
                )
                .from(routine)
                .join(member).on(member.id.eq(routine.member.id))
                .join(category).on(category.id.eq(routine.category.id))
                .join(goal).on(member.id.eq(goal.member.id))
                .join(dailyPlan).on(dailyPlan.goal.id.eq(goal.id))
                .leftJoin(weeklyRepeatDay).on(weeklyRepeatDay.routine.id.eq(routine.id))
                .where(
                        goal.startDate.loe(today),
                        goal.endDate.goe(today),
                        dailyPlan.date.eq(today),
                        routine.type.eq(RoutineType.MONTHLY).and(routine.monthlyRepeatDay.eq(day))
                                .or(routine.type.eq(RoutineType.WEEKLY).and(weeklyRepeatDay.dayOfWeek.eq(dayOfWeek)))
                )
                .fetch();
        return insertableRoutines.stream()
                .filter((insertableRoutine) -> {
                    if (today.isBefore(insertableRoutine.getRoutineStartDate())) {
                        return false;
                    }
                    if (insertableRoutine.getRoutineEndDate() != null && today.isAfter(insertableRoutine.getRoutineEndDate())) {
                        return false;
                    }
                    if (insertableRoutine.getRoutineType() == RoutineType.WEEKLY) {
                        long between = ChronoUnit.WEEKS.between(insertableRoutine.getRoutineStartDate(), today);
                        return between % insertableRoutine.getRoutineWeeklyTerm() == 0;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
}
