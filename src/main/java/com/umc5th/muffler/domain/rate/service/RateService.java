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
import java.util.ArrayList;
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
        List<CategoryGoal> categoryGoals = goal.getCategoryGoals();
        Rate rate = dailyPlan.getRate();

        return RateConverter.toRateCriteriaResponse(categoryGoals, dailyPlan, rate);
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

        List<CategoryRate> categoryRates = createCategoryRates(request,goal);
        Rate rate = RateConverter.toRate(request);
        rate.setCategoryRates(categoryRates);

        Rate newRate = rateRepository.save(rate);
        dailyPlan.setRate(newRate);
    }

    @Transactional
    public void updateRate(RateUpdateRequest request, String memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        Rate rate = rateRepository.findById(request.getRateId())
                .orElseThrow(() -> new RateException(ErrorCode.RATE_NOT_FOUND));
        Goal goal = findGoal(request.getDate(), member.getId());

        rate.update(request.getMemo(), Level.valueOf(request.getTotalLevel()));
        List<CategoryRateUpdateRequest> categoryRateList = request.getCategoryRateList();

        if(categoryRateList!=null){
            updateCategoryRates(rate, categoryRateList, goal);
        }
    }

    private Goal findGoal(LocalDate date, String memberId) {
        return goalRepository.findByDateBetweenJoin(date, memberId)
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

    private CategoryGoal findCategoryGoal(Goal goal, Long categoryGoalId) {
        log.info("cateogyrGoal: {}", goal.getCategoryGoals());
        return Optional.ofNullable(goal.getCategoryGoals())
                .flatMap(categoryGoals -> categoryGoals.stream()
                        .filter(cg -> cg.getId().equals(categoryGoalId))
                        .findFirst())
                .orElseThrow(() -> new GoalException(ErrorCode.CATEGORY_GOAL_NOT_FOUND));
    }

    private List<CategoryRate> createCategoryRates(RateCreateRequest request, Goal goal) {
        List<CategoryRate> categoryRates = new ArrayList<>();
        List<CategoryRateCreateRequest> categoryRateList = request.getCategoryRateList();

        if(categoryRateList!=null){
            for (CategoryRateCreateRequest categoryRateCreateRequest : categoryRateList) {
                CategoryGoal categoryGoal = findCategoryGoal(goal, categoryRateCreateRequest.getCategoryGoalId());
                CategoryRate categoryRate = RateConverter.toCategoryRate(categoryRateCreateRequest, categoryGoal);
                categoryRates.add(categoryRate);
            }
        }
        return categoryRates;
    }

    private void updateCategoryRates(Rate rate, List<CategoryRateUpdateRequest> categoryRateList, Goal goal) {
        for (CategoryRateUpdateRequest categoryRateReq : categoryRateList) {
            if (categoryRateReq.getCategoryRateId() != null) { //기존 카테고리 평가가 있는 경우
                updateExistingCategoryRate(rate, categoryRateReq);
            } else { //기존 카테고리 평가가 없는 경우
                addNewCategoryRate(rate, categoryRateReq, goal);
            }
        }
    }

    private void updateExistingCategoryRate(Rate rate, CategoryRateUpdateRequest categoryRateReq) {
        if (rate.getCategoryRates() == null) {
            throw new RateException(ErrorCode.CATEGORY_RATE_NOT_FOUND);
        }
        CategoryRate categoryRate = rate.getCategoryRates().stream()
                .filter(cr -> cr.getId().equals(categoryRateReq.getCategoryRateId()))
                .findFirst()
                .orElseThrow(() -> new RateException(ErrorCode.CATEGORY_RATE_NOT_FOUND));

        categoryRate.update(Level.valueOf(categoryRateReq.getLevel()));
    }

    private void addNewCategoryRate(Rate rate, CategoryRateUpdateRequest categoryRateReq, Goal goal) {
        // 기존에 같은 (rate_id, category_goal_id)를 가진 CategoryRate가 있는지 확인
        if (checkIfCategoryRateExists(rate, categoryRateReq.getCategoryGoalId())) {
            throw new RateException(ErrorCode.CATEGORY_RATE_ALREADY_EXISTS);
        }

        CategoryGoal categoryGoal = findCategoryGoal(goal, categoryRateReq.getCategoryGoalId());
        CategoryRate newCategoryRate = RateConverter.toCategoryRate(categoryRateReq, categoryGoal);
        rate.addCategoryRate(newCategoryRate);
    }

    private static boolean checkIfCategoryRateExists(Rate rate, Long categoryGoalId) {
        if (rate.getCategoryRates() == null) {
            return false;
        }
        return rate.getCategoryRates().stream()
                .anyMatch(categoryRate ->
                        categoryRate.getCategoryGoal() != null &&
                        categoryRate.getCategoryGoal().getId().equals(categoryGoalId)
                );
    }

}
