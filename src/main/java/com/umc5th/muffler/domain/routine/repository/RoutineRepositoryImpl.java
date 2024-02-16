package com.umc5th.muffler.domain.routine.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc5th.muffler.entity.QRoutine;
import com.umc5th.muffler.entity.QWeeklyRepeatDay;
import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.entity.WeeklyRepeatDay;
import com.umc5th.muffler.entity.constant.RoutineType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.umc5th.muffler.entity.QRoutine.routine;

@RequiredArgsConstructor
public class RoutineRepositoryImpl implements RoutineRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Routine> findRoutinesWithWeeklyDetails(String memberId, Long endPointId, Pageable pageable) {
        QRoutine routine = QRoutine.routine;
        QWeeklyRepeatDay weeklyRepeatDay = QWeeklyRepeatDay.weeklyRepeatDay;

        List<Routine> routineList = queryFactory
                .selectFrom(routine)
                .leftJoin(routine.category).fetchJoin()
                .where(routine.member.id.eq(memberId), ltRoutineId(endPointId))
                .orderBy(routine.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<Long> weeklyRoutineIds = routineList.stream()
                .filter(r -> r.getType() == RoutineType.WEEKLY)
                .map(Routine::getId)
                .collect(Collectors.toList());

        if (!weeklyRoutineIds.isEmpty()) {
            Map<Long, List<WeeklyRepeatDay>> weeklyRepeatDaysMap = queryFactory
                    .selectFrom(weeklyRepeatDay)
                    .where(weeklyRepeatDay.routine.id.in(weeklyRoutineIds))
                    .fetch()
                    .stream()
                    .collect(Collectors.groupingBy(wrd -> wrd.getRoutine().getId()));

            routineList.stream()
                    .filter(r -> r.getType() == RoutineType.WEEKLY)
                    .forEach(r -> r.setWeeklyRepeatDays(weeklyRepeatDaysMap.getOrDefault(r.getId(), Collections.emptyList())));
        }

        boolean hasNext = routineList.size() > pageable.getPageSize();
        if (hasNext) {
            routineList = new ArrayList<>(routineList.subList(0, pageable.getPageSize()));
        }

        return new SliceImpl<>(routineList, pageable, hasNext);
    }

    private BooleanExpression ltRoutineId(Long endPointId) {
        return endPointId == null ? null : routine.id.lt(endPointId);
    }
}
