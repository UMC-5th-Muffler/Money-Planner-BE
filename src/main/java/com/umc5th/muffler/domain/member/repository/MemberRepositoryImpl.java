package com.umc5th.muffler.domain.member.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc5th.muffler.domain.member.dto.NotEnrolledMember;
import com.umc5th.muffler.entity.QDailyPlan;
import com.umc5th.muffler.entity.QExpense;
import com.umc5th.muffler.entity.QGoal;
import com.umc5th.muffler.entity.QMember;
import com.umc5th.muffler.entity.QMemberAlarm;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    private List<NotEnrolledMember> findNotEnrolledMember(LocalDate date, Predicate alarmAgreePredicate) {
        QMemberAlarm memberAlarm = QMemberAlarm.memberAlarm;
        QMember member = QMember.member;
        QExpense expense = QExpense.expense;
        QGoal goal = QGoal.goal;
        QDailyPlan dailyPlan = QDailyPlan.dailyPlan;

        return queryFactory.select(
                        Projections.constructor(NotEnrolledMember.class,
                                memberAlarm.token.as("alarmToken")))
                .from(memberAlarm)
                .join(member).on(member.memberAlarm.id.eq(memberAlarm.id))
                .join(goal).on(goal.member.id.eq(memberAlarm.member.id))
                .join(dailyPlan).on(dailyPlan.goal.id.eq(goal.id))
                .where(
                        memberAlarm.token.isNotNull(),
                        alarmAgreePredicate,
                        dailyPlan.date.eq(date).and(dailyPlan.isZeroDay.eq(false)),
                        member.id.notIn(
                                JPAExpressions.select(expense.member.id)
                                        .from(expense)
                                        .where(expense.date.eq(date)))
                )
                .fetch();
    }

    @Override
    public List<NotEnrolledMember> findTodayNotEnrolledMember(LocalDate today) {
        QMemberAlarm memberAlarm = QMemberAlarm.memberAlarm;
        return findNotEnrolledMember(today, memberAlarm.isTodayEnrollRemindAgree.eq(true));
    }

    @Override
    public List<NotEnrolledMember> findYesterdayNotEnrolledMember(LocalDate yesterday) {
        QMemberAlarm memberAlarm = QMemberAlarm.memberAlarm;
        return findNotEnrolledMember(yesterday, memberAlarm.isYesterdayEnrollRemindAgree.eq(true));
    }
}

