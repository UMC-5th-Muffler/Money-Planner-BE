package com.umc5th.muffler.domain.routine.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc5th.muffler.entity.QRoutine;
import com.umc5th.muffler.entity.QWeeklyRepeatDay;
import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.entity.WeeklyRepeatDay;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.umc5th.muffler.entity.QRoutine.routine;

@RequiredArgsConstructor
public class RoutineRepositoryImpl implements RoutineRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Routine> findRoutinesWithCategory(String memberId, Long endPointId, Pageable pageable) {
        QRoutine routine = QRoutine.routine;

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
