package com.umc5th.muffler.domain.rate.service;

import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.rate.dto.RateConverter;
import com.umc5th.muffler.domain.rate.dto.RateCriteriaResponse;
import com.umc5th.muffler.domain.rate.repository.RateRepository;
import com.umc5th.muffler.entity.*;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.MemberException;
import com.umc5th.muffler.global.response.exception.RateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RateService {

    private final MemberRepository memberRepository;
    private final GoalRepository goalRepository;
    private final RateRepository rateRepository;

    public RateCriteriaResponse getEvalCategoryList(LocalDate date){
        Long memberId = 1L; // 임시
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Goal goal = goalRepository.findByDateBetweenJoin(date, memberId)
                .orElseThrow(() -> new GoalException(ErrorCode._NO_GOAL_IN_GIVEN_DATE));

        DailyPlan dailyPlan = findDailyPlan(goal, date);
        List<CategoryGoal> categoryGoals = goal.getCategoryGoals();
        Rate rate = dailyPlan.getRate();

        return RateConverter.toRateCriteriaResponse(categoryGoals, dailyPlan, rate);
    }

    private DailyPlan findDailyPlan(Goal goal, LocalDate date) {
        return goal.getDailyPlans().stream()
                .filter(dailyPlan -> dailyPlan.getDate().equals(date))
                .findAny()
                .orElseThrow(() -> new RateException(ErrorCode.DAILYPLAN_NOT_FOUND));
    }
}
