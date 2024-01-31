package com.umc5th.muffler.domain.goal.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.QGoal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import static com.umc5th.muffler.entity.QGoal.goal;

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

    @Override
    public Slice<Goal> findByMemberIdAndDailyPlans(String memberId, Pageable pageable, LocalDate today, LocalDate startDate) {
        QGoal goal = QGoal.goal;

        List<Goal> goals = queryFactory
                .selectFrom(goal)
                .where(ltStartDate(startDate),
                        goal.member.id.eq(memberId)
                        .and(dateNotBetween(today, goal.startDate, goal.endDate))
                ).orderBy(goal.startDate.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (goals.size() > pageable.getPageSize()) {
            goals.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(goals, pageable, hasNext);
    }

    private BooleanExpression dateNotBetween(LocalDate date, DatePath<LocalDate> startDate, DatePath<LocalDate> endDate) {
        return startDate.after(date).or(endDate.before(date));
    }

    private BooleanExpression ltStartDate(LocalDate startDate) {
        return startDate == null ? null : goal.startDate.before(startDate);
    }
}
