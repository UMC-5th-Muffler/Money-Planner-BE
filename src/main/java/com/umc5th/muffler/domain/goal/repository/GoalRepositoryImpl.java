package com.umc5th.muffler.domain.goal.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.QGoal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GoalRepositoryImpl implements GoalRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Goal> findGoalsByYearMonth(String memberId, YearMonth yearMonth) {
        QGoal goal = QGoal.goal;
        LocalDate startOfMonth = yearMonth.atDay(1);
        LocalDate endOfMonth = yearMonth.atEndOfMonth();

        return queryFactory
                .selectFrom(goal)
                .where(goal.member.id.eq(memberId)
                        .and(goal.startDate.loe(endOfMonth))
                        .and(goal.endDate.goe(startOfMonth))
                ).orderBy(goal.startDate.asc())
                .fetch();
    }
}
