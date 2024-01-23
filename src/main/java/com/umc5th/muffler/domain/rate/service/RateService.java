package com.umc5th.muffler.domain.rate.service;

import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.rate.dto.*;
import com.umc5th.muffler.domain.rate.repository.RateRepository;
import com.umc5th.muffler.entity.*;
import com.umc5th.muffler.entity.constant.Level;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.MemberException;
import com.umc5th.muffler.global.response.exception.RateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RateService {

    private final MemberRepository memberRepository;
    private final GoalRepository goalRepository;
    private final RateRepository rateRepository;

    public RateCriteriaResponse getRateCriteria(LocalDate date, String memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        Goal goal = findGoal(date, member.getId());
        DailyPlan dailyPlan = findDailyPlan(goal, date);
        Rate rate = dailyPlan.getRate();

        return RateConverter.toRateCriteriaResponse(dailyPlan, rate);
    }

    @Transactional
    public void createRate(RateCreateRequest request, String memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        Goal goal = findGoal(request.getDate(), member.getId());
        DailyPlan dailyPlan = findDailyPlan(goal, request.getDate());
        if (dailyPlan.getRate() != null) {
            throw new RateException(ErrorCode.RATE_ALREADY_EXISTS);
        }

        Rate rate = RateConverter.toRate(request);

        Rate newRate = rateRepository.save(rate);
        dailyPlan.setRate(newRate);
    }

    @Transactional
    public void updateRate(RateUpdateRequest request){
        Rate rate = rateRepository.findById(request.getRateId())
                .orElseThrow(() -> new RateException(ErrorCode.RATE_NOT_FOUND));

        rate.update(request.getMemo(), Level.valueOf(request.getTotalLevel()));
    }

    private Goal findGoal(LocalDate date, String memberId) {
        return goalRepository.findByDateBetween(date, memberId)
                .orElseThrow(() -> new GoalException(ErrorCode.NO_GOAL_IN_GIVEN_DATE));
    }

    private DailyPlan findDailyPlan(Goal goal, LocalDate date) {
        List<DailyPlan> dailyPlans = Optional.ofNullable(goal.getDailyPlans())
                .orElseThrow(() -> new RateException(ErrorCode.DAILYPLAN_NOT_FOUND));

        return dailyPlans.stream()
                .filter(dailyPlan -> dailyPlan.getDate().equals(date))
                .findAny()
                .orElseThrow(() -> new RateException(ErrorCode.DAILYPLAN_NOT_FOUND));
    }

}
