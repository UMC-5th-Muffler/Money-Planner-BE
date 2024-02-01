package com.umc5th.muffler.domain.routine.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc5th.muffler.entity.QRoutine;
import com.umc5th.muffler.entity.Routine;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static com.umc5th.muffler.entity.QRoutine.routine;

@RequiredArgsConstructor
public class RoutineRepositoryImpl implements RoutineRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Routine> findRoutinesWithWeeklyDetails(String memberId, Long endPointId, Pageable pageable) {
        QRoutine routine = QRoutine.routine;

        List<Routine> routines = queryFactory
                .selectFrom(routine)
                .leftJoin(routine.category).fetchJoin()
                .where(routine.member.id.eq(memberId),
                        ltRoutineId(endPointId)
                ).orderBy(routine.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (routines.size() > pageable.getPageSize()) {
            routines.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(routines, pageable, hasNext);
    }

    private BooleanExpression ltRoutineId(Long endPointId) {
        return endPointId == null ? null : routine.id.lt(endPointId);
    }
}
