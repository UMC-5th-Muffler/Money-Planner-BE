package com.umc5th.muffler.domain.goal.service;

import com.umc5th.muffler.domain.goal.repository.DailyPlanRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.DailyPlanException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DailyPlanService {
    private final MemberRepository memberRepository;
    private final DailyPlanRepository dailyPlanRepository;

    public void updateZeroDay(String memberId, LocalDate date) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new DailyPlanException(ErrorCode.MEMBER_NOT_FOUND));
        DailyPlan dailyPlan = dailyPlanRepository.findDailyPlanByDateAndMemberIdFetchGoalFetchGoalAndMember(date, memberId)
                .orElseThrow(() -> new DailyPlanException(ErrorCode.NO_DAILY_PLAN_GIVEN_DATE));
        System.out.println(date.toString());
        if (!member.getId().equals(dailyPlan.getGoal().getMember().getId())) {
            throw new DailyPlanException(ErrorCode.CANNOT_UPDATE_OTHER_MEMBER_DAILY_PLAN);
        }
        dailyPlan.toggleZeroDay();
        dailyPlanRepository.save(dailyPlan);
    }
}
