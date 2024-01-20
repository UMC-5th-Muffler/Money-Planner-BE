package com.umc5th.muffler.domain.rate.service;

import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.rate.dto.CategoryRateCreateRequest;
import com.umc5th.muffler.domain.rate.dto.RateConverter;
import com.umc5th.muffler.domain.rate.dto.RateCreateRequest;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    @Transactional
    public void createRate(RateCreateRequest request){
        Long memberId = 1L; // 임시
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Goal goal = goalRepository.findByDateBetweenJoin(request.getDate(), memberId)
                .orElseThrow(() -> new GoalException(ErrorCode._NO_GOAL_IN_GIVEN_DATE));

        DailyPlan dailyPlan = findDailyPlan(goal, request.getDate());
        if (dailyPlan.getRate() != null) {
            throw new RateException(ErrorCode.RATE_ALREADY_EXISTS);
        }

        List<CategoryRate> categoryRates = createCategoryRates(request,goal);

        Rate rate = RateConverter.toRate(request);
        rate.setCategoryRates(categoryRates);

        Rate newRate = rateRepository.save(rate);
        dailyPlan.setRate(newRate);
    }

    private DailyPlan findDailyPlan(Goal goal, LocalDate date) {
        List<DailyPlan> dailyPlans = Optional.ofNullable(goal.getDailyPlans())
                .orElseThrow(() -> new RateException(ErrorCode.DAILYPLAN_NOT_FOUND));

        return dailyPlans.stream()
                .filter(dailyPlan -> dailyPlan.getDate().equals(date))
                .findAny()
                .orElseThrow(() -> new RateException(ErrorCode.DAILYPLAN_NOT_FOUND));
    }

    private List<CategoryRate> createCategoryRates(RateCreateRequest request, Goal goal){
        List<CategoryGoal> categoryGoals = Optional.ofNullable(goal.getCategoryGoals())
                .orElse(Collections.emptyList());
        List<CategoryRate> categoryRates = new ArrayList<>();

        for (CategoryRateCreateRequest categoryRateCreateRequest : request.getCategoryRateList()) {
            CategoryGoal categoryGoal = categoryGoals.stream()
                    .filter(CategoryGoal -> CategoryGoal.getId().equals(categoryRateCreateRequest.getCategoryGoalId()))
                    .findAny()
                    .orElseThrow(() -> new GoalException(ErrorCode.CATEGORY_GOAL_NOT_FOUND));

            CategoryRate categoryRate = RateConverter.toCategoryRate(categoryRateCreateRequest, categoryGoal);
            categoryRates.add(categoryRate);
        }
        return categoryRates;
    }

}
