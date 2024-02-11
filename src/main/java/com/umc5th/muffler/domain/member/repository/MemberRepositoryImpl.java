package com.umc5th.muffler.domain.member.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc5th.muffler.domain.member.dto.TodayNotEnrolledMember;
import com.umc5th.muffler.domain.member.dto.YesterdayNotEnrolledMember;
import com.umc5th.muffler.entity.QDailyPlan;
import com.umc5th.muffler.entity.QExpense;
import com.umc5th.muffler.entity.QGoal;
import com.umc5th.muffler.entity.QMemberAlarm;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public List<TodayNotEnrolledMember> findTodayNotEnrolledMember(LocalDate today) {
        QMemberAlarm memberAlarm = QMemberAlarm.memberAlarm;
        QExpense expense = QExpense.expense;
        QGoal goal = QGoal.goal;
        QDailyPlan dailyPlan = QDailyPlan.dailyPlan;

        return queryFactory.select(
                        Projections.fields(TodayNotEnrolledMember.class,
                                memberAlarm.token.as("alarmToken")))
                .from(memberAlarm)
                .join(goal).on(goal.member.id.eq(memberAlarm.member.id))
                .join(dailyPlan).on(dailyPlan.goal.id.eq(goal.id))
                .where(
                        memberAlarm.token.isNotNull(),
                        memberAlarm.isTodayEnrollRemindAgree.eq(true),
                        dailyPlan.date.eq(today).and(dailyPlan.isZeroDay.eq(false)),
                        memberAlarm.member.id.notIn(
                                JPAExpressions.select(expense.member.id)
                                        .from(expense)
                                        .where(expense.date.eq(today)))
                )
                .fetch();
    }

    @Override
    public List<YesterdayNotEnrolledMember> findYesterdayNotEnrolledMember(LocalDate yesterday) {
        QMemberAlarm memberAlarm = QMemberAlarm.memberAlarm;
        QExpense expense = QExpense.expense;
        QGoal goal = QGoal.goal;
        QDailyPlan dailyPlan = QDailyPlan.dailyPlan;

        return queryFactory.select(
                        Projections.fields(YesterdayNotEnrolledMember.class,
                                memberAlarm.token.as("alarmToken")))
                .from(memberAlarm)
                .join(goal).on(goal.member.id.eq(memberAlarm.member.id))
                .join(dailyPlan).on(dailyPlan.goal.id.eq(goal.id))
                .where(
                        memberAlarm.token.isNotNull(),
                        memberAlarm.isYesterdayEnrollRemindAgree.eq(true),
                        dailyPlan.date.eq(yesterday).and(dailyPlan.isZeroDay.eq(false)),
                        memberAlarm.member.id.notIn(
                                JPAExpressions.select(expense.member.id)
                                        .from(expense)
                                        .where(expense.date.eq(yesterday)))
                )
                .fetch();
    }
}

