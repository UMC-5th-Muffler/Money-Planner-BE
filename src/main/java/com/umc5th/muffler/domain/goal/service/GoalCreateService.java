package com.umc5th.muffler.domain.goal.service;

import static com.umc5th.muffler.global.response.code.ErrorCode.INVALID_GOAL_INPUT;
import static com.umc5th.muffler.global.response.code.ErrorCode.MEMBER_NOT_FOUND;

import com.umc5th.muffler.domain.goal.dto.GoalCreateRequest;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.MemberException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoalCreateService {

    private final MemberRepository memberRepository;
    private final GoalRepository goalRepository;

    @Transactional
    public void create(GoalCreateRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        validateGoalInput(request, member);

        List<DailyPlan> dailyPlans = createDailyPlans(request.getStartDate(), request.getDailyBudgets());
        Goal goal = Goal.of(request.getStartDate(), request.getEndDate(), request.getTitle(), request.getDetail(), request.getIcon(), request.getTotalBudget(), member);
        goal.setDailyPlans(dailyPlans);

        Goal savedGoal = goalRepository.save(goal);
        member.addGoal(savedGoal);
    }

    private List<DailyPlan> createDailyPlans(LocalDate startDate, List<Long> dailyBudgets) {
        return IntStream.range(0, dailyBudgets.size())
                .mapToObj(i -> DailyPlan.of(startDate.plusDays(i), dailyBudgets.get(i)))
                .collect(Collectors.toList());
    }

    private void validateGoalInput(GoalCreateRequest request, Member member) {
        validateGoalPeriod(member.getGoals(), request.getStartDate(), request.getEndDate());
        validateDailyPlans(request.getStartDate(), request.getEndDate(), request.getDailyBudgets(), request.getTotalBudget());
    }

    private void validateGoalPeriod(List<Goal> goals, LocalDate startDate, LocalDate endDate) {
        if (!startDate.isBefore(endDate)) {
            throw new GoalException(INVALID_GOAL_INPUT, "시작 날짜는 종료 날짜보다 이전이어야 합니다.");
        }

        for (Goal goal : goals) {
            if (!goal.getStartDate().isAfter(endDate) && !goal.getEndDate().isAfter(startDate)) {
                throw new GoalException(INVALID_GOAL_INPUT, "기존 목표 기간과 겹칠 수 없습니다.");
            }
        }
    }

    private void validateDailyPlans(LocalDate startDate, LocalDate endDate, List<Long> dailyBudgets, Long totalBudget) {
        if (dailyBudgets.size() != (ChronoUnit.DAYS.between(startDate, endDate) + 1)) {
            throw new GoalException(INVALID_GOAL_INPUT, "전체 목표 기간에 대해 하나의 일일 계획이 존재해야 합니다.");
        }

        long planSum = dailyBudgets.stream()
                .mapToLong(Long::longValue)
                .sum();

        if (planSum != totalBudget) {
            throw new GoalException(INVALID_GOAL_INPUT, "일일 계획 금액의 총 합이 목표 전체 금액과 같지 않습니다.");
        }
    }
}
