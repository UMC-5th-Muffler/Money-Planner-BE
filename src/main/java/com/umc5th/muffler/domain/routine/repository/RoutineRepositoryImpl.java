package com.umc5th.muffler.domain.routine.repository;

import static com.umc5th.muffler.entity.QRoutine.routine;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc5th.muffler.domain.routine.dto.InsertableRoutine;
import com.umc5th.muffler.entity.QCategory;
import com.umc5th.muffler.entity.QDailyPlan;
import com.umc5th.muffler.entity.QMember;
import com.umc5th.muffler.entity.QWeeklyRepeatDay;
import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.entity.WeeklyRepeatDay;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class RoutineRepositoryImpl implements RoutineRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<InsertableRoutine> findInsertableRoutines(LocalDate today) {
        QMember member = QMember.member;
        QCategory category = QCategory.category;
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
                .join(dailyPlan).on(dailyPlan.date.eq(today))
                .leftJoin(weeklyRepeatDay).on(weeklyRepeatDay.routine.id.eq(routine.id))
                .where(
                        dailyPlan.date.eq(today)
//                        routine.type.eq(RoutineType.MONTHLY).and(routine.monthlyRepeatDay.eq(day))
//                                .or(routine.type.eq(RoutineType.WEEKLY).and(weeklyRepeatDay.dayOfWeek.eq(dayOfWeek)))
                )
                .fetch();
        return insertableRoutines.stream()
                .filter((insertableRoutine) -> insertableRoutine.isValid(today))
                .collect(Collectors.toList());
    }

    @Override
    public Slice<Routine> findRoutinesWithCategory(String memberId, Long endPointId, Pageable pageable) {

        List<Routine> routineList = queryFactory
                .selectFrom(routine)
                .leftJoin(routine.category).fetchJoin()
                .where(routine.member.id.eq(memberId), ltRoutineId(endPointId))
                .orderBy(routine.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = routineList.size() > pageable.getPageSize();
        if (hasNext) {
            routineList = new ArrayList<>(routineList.subList(0, pageable.getPageSize()));
        }

        return new SliceImpl<>(routineList, pageable, hasNext);
    }

    private BooleanExpression ltRoutineId(Long endPointId) {
        return endPointId == null ? null : routine.id.lt(endPointId);
    }

    @Override
    public Map<Long, List<WeeklyRepeatDay>> findWeeklyRepeatDays(List<Long> weeklyRoutineIds) {
        QWeeklyRepeatDay weeklyRepeatDay = QWeeklyRepeatDay.weeklyRepeatDay;

        return queryFactory
                .selectFrom(weeklyRepeatDay)
                .where(weeklyRepeatDay.routine.id.in(weeklyRoutineIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(wrd -> wrd.getRoutine().getId()));
    };
}
