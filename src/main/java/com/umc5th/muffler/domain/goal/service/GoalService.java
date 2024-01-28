package com.umc5th.muffler.domain.goal.service;

import static com.umc5th.muffler.global.response.code.ErrorCode.GOAL_NOT_FOUND;
import static com.umc5th.muffler.global.response.code.ErrorCode.INVALID_PERMISSION;
import static com.umc5th.muffler.global.response.code.ErrorCode.MEMBER_NOT_FOUND;

import com.umc5th.muffler.domain.dailyplan.repository.DailyPlanRepository;
import com.umc5th.muffler.domain.goal.dto.GoalConverter;
import com.umc5th.muffler.domain.goal.dto.GoalListResponse;
import com.umc5th.muffler.domain.goal.dto.GoalPreviewResponse;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.global.response.exception.CommonException;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.MemberException;

import java.time.LocalDate;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final MemberRepository memberRepository;
    private final GoalRepository goalRepository;
    private final DailyPlanRepository dailyPlanRepository;

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

    public GoalPreviewResponse getGoalPreview(String memberId) {

        Optional<List<Goal>> goals = goalRepository.findByMemberId(memberId);
        if (!goals.isPresent()) {
            return new GoalPreviewResponse();
        }

        List<Goal> goalList = goals.get();
        goalList.sort(Comparator.comparing(Goal::getStartDate).reversed());

        LocalDate today = LocalDate.now();
        Map<Goal, Long> goalAndTotalCost = new HashMap<>();
        List<Goal> futureGoals = new ArrayList<>();
        Goal progressGoal = null;
        Long totalCost = null;

        for (Goal goal : goalList) {
            if (goal.getEndDate().isBefore(today)) {
                calculateGoalCost(goalAndTotalCost, goal);
            } else if (!today.isBefore(goal.getStartDate()) && !today.isAfter(goal.getEndDate())) {
                progressGoal = goal;
                totalCost = goal.getDailyPlans().stream().mapToLong(DailyPlan::getTotalCost).sum();
            } else {
                futureGoals.add(goal);
            }
        }

        return GoalConverter.getGoalPreviousResponse(goalAndTotalCost, progressGoal, totalCost, futureGoals);
    }

    private void calculateGoalCost(Map<Goal, Long> goalAndTotalCost, Goal goal) {
        Long totalCost = goal.getDailyPlans().stream().mapToLong(DailyPlan::getTotalCost).sum();
        goalAndTotalCost.put(goal, totalCost);
    }

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
