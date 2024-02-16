package com.umc5th.muffler.domain.goal.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DatePath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc5th.muffler.domain.goal.dto.FinishedGoal;
import com.umc5th.muffler.domain.goal.dto.GoalTerm;
import com.umc5th.muffler.domain.goal.dto.QGoalTerm;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.QGoal;
import com.umc5th.muffler.entity.QMemberAlarm;
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
    public Slice<Goal> findByMemberIdAndDailyPlans(String memberId, Pageable pageable, LocalDate today, LocalDate endDate) {
        QGoal goal = QGoal.goal;

        List<Goal> goals = queryFactory
                .selectFrom(goal)
                .where(goal.member.id.eq(memberId),
                        dateNotBetween(today, goal.startDate, goal.endDate),
                        bfEndDate(endDate)
                ).orderBy(goal.endDate.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (goals.size() > pageable.getPageSize()) {
            goals.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(goals, pageable, hasNext);
    }

    @Override
    public List<FinishedGoal> findFinishedGoals(LocalDate date) {
        QGoal goal = QGoal.goal;
        QMemberAlarm memberAlarm = QMemberAlarm.memberAlarm;

        return queryFactory.select(
                    Projections.constructor(FinishedGoal.class,
                        goal.title.as("goalTitle"),
                        goal.icon.as("goalIcon"),
                        memberAlarm.token.as("token")
                    ))
                .from(goal)
                .join(memberAlarm).on(memberAlarm.member.id.eq(goal.member.id))
                .where(
                    goal.endDate.eq(date),
                    memberAlarm.token.isNotNull(),
                    memberAlarm.isGoalEndReportRemindAgree.eq(true)
                ).fetch();
    }

    private BooleanExpression dateNotBetween(LocalDate date, DatePath<LocalDate> startDate, DatePath<LocalDate> endDate) {
        return startDate.after(date).or(endDate.before(date));
    }

    private BooleanExpression bfEndDate(LocalDate endDate) {
        return endDate == null ? null : goal.endDate.before(endDate);
    }

    @Override
    public List<GoalTerm> findGoalsWithinDateRange(LocalDate startDate, LocalDate endDate) {
        QGoal goal = QGoal.goal;

        List<GoalTerm> goalTermList = queryFactory
                .select(new QGoalTerm(goal.startDate, goal.endDate))
                .from(goal)
                .where(goal.startDate.loe(endDate)
                        .and(goal.endDate.goe(startDate)))
                .fetch();

        return goalTermList;
    }
}
