package com.umc5th.muffler.domain.goal.service;

import static com.umc5th.muffler.global.response.code.ErrorCode._GOAL_NOT_FOUND;
import static com.umc5th.muffler.global.response.code.ErrorCode._INVALID_DAILY_PLAN;
import static com.umc5th.muffler.global.response.code.ErrorCode._INVALID_GOAL_DATE;
import static com.umc5th.muffler.global.response.code.ErrorCode._INVALID_PERMISSION;
import static com.umc5th.muffler.global.response.code.ErrorCode._MEMBER_NOT_FOUND;

import com.umc5th.muffler.domain.goal.dto.GoalCreateRequest;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.global.response.exception.CommonException;
import com.umc5th.muffler.global.response.exception.GoalException;
import com.umc5th.muffler.global.response.exception.MemberException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final MemberRepository memberRepository;
    private final GoalRepository goalRepository;

    @Transactional
    public void create(GoalCreateRequest request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(_MEMBER_NOT_FOUND));
        validateDailyPlans(request.getStartDate(), request.getEndDate(), request.getDailyBudgets(), request.getTotalBudget());

        List<DailyPlan> dailyPlans = createDailyPlans(request.getStartDate(), request.getDailyBudgets());
        Goal goal = Goal.of(request.getStartDate(), request.getEndDate(), request.getTitle(), request.getDetail(), request.getIcon(), request.getTotalBudget(), member);
        goal.setDailyPlans(dailyPlans);

        Goal savedGoal = goalRepository.save(goal);
        member.addGoal(savedGoal);
    }

    public List<Goal> getGoals(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(_MEMBER_NOT_FOUND));
        return member.getGoals();
    }

    @Transactional
    public void delete(Long goalId, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(_MEMBER_NOT_FOUND));
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalException(_GOAL_NOT_FOUND));

        if (!Objects.equals(member.getId(), goal.getMember().getId())) {
            throw new CommonException(_INVALID_PERMISSION);
        }

        goalRepository.delete(goal);
        member.removeGoal(goal);
    }

    private void validateDailyPlans(LocalDate startDate, LocalDate endDate, List<Long> dailyBudgets, Long totalBudget) {
        if (!startDate.isBefore(endDate)
                || dailyBudgets.size() != (ChronoUnit.DAYS.between(startDate, endDate) + 1)
        ) {
            throw new GoalException(_INVALID_GOAL_DATE);
        }

        long planSum = dailyBudgets.stream()
                .mapToLong(Long::longValue)
                .sum();

        if (planSum != totalBudget) {
            throw new GoalException(_INVALID_DAILY_PLAN);
        }
    }

    private List<DailyPlan> createDailyPlans(LocalDate startDate, List<Long> dailyBudgets) {
        return IntStream.range(0, dailyBudgets.size())
                .mapToObj(i -> DailyPlan.of(startDate.plusDays(i), dailyBudgets.get(i)))
                .collect(Collectors.toList());
    }
}
