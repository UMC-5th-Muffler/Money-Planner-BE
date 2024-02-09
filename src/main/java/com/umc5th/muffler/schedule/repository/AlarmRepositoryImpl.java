package com.umc5th.muffler.schedule.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.umc5th.muffler.alarm.dto.DailyPlanAlarm;
import com.umc5th.muffler.entity.QDailyPlan;
import com.umc5th.muffler.entity.QMember;
import com.umc5th.muffler.entity.QMemberAlarm;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AlarmRepositoryImpl implements AlarmRepository {
    private final JPAQueryFactory queryFactory;
    @Override
    public List<DailyPlanAlarm> findDailyPlanAlarms(LocalDate date) {
        QMemberAlarm memberAlarm =  QMemberAlarm.memberAlarm;
        QMember member = QMember.member;
        QDailyPlan dailyPlan = QDailyPlan.dailyPlan;

        return queryFactory
                .select(Projections.fields(DailyPlanAlarm.class,
                        member.name.as("memberName"),
                        dailyPlan.budget.as("dailyBudget"),
                        memberAlarm.token.as("alarmToken")))
                .from(member)
                .join(memberAlarm).on(memberAlarm.member.id.eq(member.id))
                .join(dailyPlan).on(dailyPlan.goal.member.id.eq(member.id))
                .where(
                        dailyPlan.date.eq(date),
                        memberAlarm.isDailyPlanRemindAgree.eq(true)
                )
                .fetch();
    }
}
