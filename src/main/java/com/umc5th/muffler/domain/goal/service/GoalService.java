package com.umc5th.muffler.domain.goal.service;

import static com.umc5th.muffler.global.response.code.ErrorCode.GOAL_NOT_FOUND;
import static com.umc5th.muffler.global.response.code.ErrorCode.INVALID_PERMISSION;
import static com.umc5th.muffler.global.response.code.ErrorCode.MEMBER_NOT_FOUND;

import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.dto.GoalConverter;
import com.umc5th.muffler.domain.goal.dto.GoalReportResponse;
import com.umc5th.muffler.domain.goal.dto.GoalGetResponse;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.*;
import com.umc5th.muffler.global.response.exception.CommonException;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.MemberException;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Goal goal = goalRepository.findByIdWithJoin(goalId)
                .orElseThrow(() -> new GoalException(GOAL_NOT_FOUND));

        List<DailyPlan> dailyPlans = goal.getDailyPlans();
        List<CategoryGoal> categoryGoals = goal.getCategoryGoals();
        List<Expense> expenses = expenseRepository.findAllByMemberAndDateBetween(member, goal.getStartDate(), goal.getEndDate());

        return GoalConverter.getGoalReportResponse(goal, categoryGoals, dailyPlans, expenses);
    }

    public GoalGetResponse getGoalWithTotalCost(Long goalId){
        Goal goal = goalRepository.findByIdWithJoin(goalId)
                .orElseThrow(() -> new GoalException(GOAL_NOT_FOUND));
        List<DailyPlan> dailyPlans = goal.getDailyPlans();

        return GoalConverter.getGoalWithTotalCostResponse(goal, dailyPlans);
    }

}
