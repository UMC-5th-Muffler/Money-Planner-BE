package com.umc5th.muffler.domain.dailyplan.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.umc5th.muffler.domain.dailyplan.dto.DailyPlanAlarm;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.MemberAlarm;
import com.umc5th.muffler.fixture.GoalFixture;
import com.umc5th.muffler.fixture.MemberAlarmFixture;
import com.umc5th.muffler.fixture.MemberFixture;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
class FindDailyPlanAlarmTest {
    @Autowired private MemberRepository memberRepository;
    @Autowired private DailyPlanRepository dailyPlanRepository;
    @Autowired private GoalRepository goalRepository;

    @Transactional
    @Test
    void 토큰O_오늘_목표O_동의O() {
        Member member = MemberFixture.MEMBER_ONE;
        MemberAlarm memberAlarm = MemberAlarmFixture.ALL_AGREE;
        member.setMemberAlarm(memberAlarm);

        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 3)
        );
        member = memberRepository.save(member);
        goal = goalRepository.save(goal);

        List<DailyPlanAlarm> results = dailyPlanRepository.findDailyPlanAlarms(LocalDate.of(2024, 1, 1));
        assertEquals(1, results.size());
    }

    @Transactional
    @Test
    void 토큰O_오늘_목표O_동의X() {
        Member member = MemberFixture.MEMBER_ONE;
        MemberAlarm memberAlarm = MemberAlarmFixture.ALL_EXCEPT_DAILY_PLAN;
        member.setMemberAlarm(memberAlarm);

        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 3)
        );
        member = memberRepository.save(member);
        goal = goalRepository.save(goal);

        List<DailyPlanAlarm> results = dailyPlanRepository.findDailyPlanAlarms(LocalDate.of(2024, 1, 1));
        assertEquals(0, results.size());
    }
    @Transactional
    @Test
    void 토큰O_오늘_목표X_동의O() {
        Member member = MemberFixture.MEMBER_ONE;
        MemberAlarm memberAlarm = MemberAlarmFixture.ALL_AGREE;
        member.setMemberAlarm(memberAlarm);

        member = memberRepository.save(member);

        List<DailyPlanAlarm> results = dailyPlanRepository.findDailyPlanAlarms(LocalDate.of(2024, 1, 1));
        assertEquals(0, results.size());
    }

    @Transactional
    @Test
    void 토큰X_오늘_목표O_동의O() {
        Member member = MemberFixture.MEMBER_ONE;
        MemberAlarm memberAlarm = MemberAlarmFixture.ALL_EXCEPT_TOKEN;
        member.setMemberAlarm(memberAlarm);

        Goal goal = GoalFixture.createGoalRegardlessOfBudget(member,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 3)
        );
        member = memberRepository.save(member);
        goal = goalRepository.save(goal);

        List<DailyPlanAlarm> results = dailyPlanRepository.findDailyPlanAlarms(LocalDate.of(2024, 1, 1));
        assertEquals(0, results.size());
    }

}