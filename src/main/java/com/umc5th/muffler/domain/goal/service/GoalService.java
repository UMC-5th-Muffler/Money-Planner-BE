package com.umc5th.muffler.domain.goal.service;

import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.dto.*;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.*;
import com.umc5th.muffler.global.response.exception.CommonException;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.umc5th.muffler.global.response.code.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final MemberRepository memberRepository;
    private final GoalRepository goalRepository;
    private final ExpenseRepository expenseRepository;

    public List<Goal> getGoals(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        return member.getGoals();
    }

    @Transactional
    public void delete(Long goalId, String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalException(GOAL_NOT_FOUND));

        if (!Objects.equals(member.getId(), goal.getMember().getId())) {
            throw new CommonException(INVALID_PERMISSION);
        }

        goalRepository.delete(goal);
        member.removeGoal(goal);
    }

    public GoalReportResponse getReport(Long goalId, String memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findByIdWithCategoryGoals(goalId, memberId)
                .orElseThrow(() -> new GoalException(GOAL_NOT_FOUND));

        List<DailyPlan> dailyPlans = goal.getDailyPlans();
        List<CategoryGoal> categoryGoals = goal.getCategoryGoals();
        List<Expense> expenses = expenseRepository.findAllByMemberAndDateBetween(member, goal.getStartDate(), goal.getEndDate());

        return GoalConverter.getGoalReportResponse(goal, categoryGoals, dailyPlans, expenses);
    }

    public GoalGetResponse getGoalWithTotalCost(Long goalId, String memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findByIdWithCategoryGoals(goalId, memberId)
                .orElseThrow(() -> new GoalException(GOAL_NOT_FOUND));
        List<DailyPlan> dailyPlans = goal.getDailyPlans();

        return GoalConverter.getGoalWithTotalCostResponse(goal, dailyPlans);
    }

    @Transactional(readOnly = true)
    public GoalInfo getGoalNow(String memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Optional<Goal> goal = goalRepository.findByDateBetweenAndDailyPlans(LocalDate.now(), member.getId());
        if (!goal.isPresent()) {
            return new GoalInfo();
        }

        Goal progress = goal.get();
        Long totalCost = progress.getDailyPlans().stream().mapToLong(DailyPlan::getTotalCost).sum();

        return GoalConverter.getNowGoalResponse(progress, totalCost);
    }

    @Transactional(readOnly = true)
    public GoalPreviewResponse getGoalPreview(String memberId, Pageable pageable, LocalDate endDate) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        LocalDate today = LocalDate.now();
        Slice<Goal> goalList = goalRepository.findByMemberIdAndDailyPlans(member.getId(), pageable, today, endDate);
        if (goalList.isEmpty()) {
            return new GoalPreviewResponse();
        }

        Map<Goal, Long> goalAndTotalCost = new LinkedHashMap<>();

        goalList.stream()
                .filter(goal -> goal.getEndDate().isBefore(today))
                .forEach(goal -> calculateGoalCost(goalAndTotalCost, goal));
        List<Goal> futureGoals = goalList.stream()
                .filter(goal -> goal.getEndDate().isAfter(today))
                .collect(Collectors.toList());

        return GoalConverter.getGoalPreviewResponse(goalAndTotalCost, futureGoals, goalList.hasNext());
    }

    private void calculateGoalCost(Map<Goal, Long> goalAndTotalCost, Goal goal) {
        Long totalCost = goal.getDailyPlans().stream().mapToLong(DailyPlan::getTotalCost).sum();
        goalAndTotalCost.put(goal, totalCost);
    }

    @Transactional(readOnly = true)
    public GoalListResponse getGoalList(String memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        List<Goal> goalList = member.getGoals();

        if (goalList.isEmpty()) {
            return new GoalListResponse();
        }
        goalList.sort(Comparator.comparing(Goal::getEndDate).reversed());

        return GoalConverter.getGoalListResponse(goalList);
    }
}
