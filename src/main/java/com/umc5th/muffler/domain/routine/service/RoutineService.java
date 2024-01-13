package com.umc5th.muffler.domain.routine.service;

import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.routine.converter.RoutineConverter;
import com.umc5th.muffler.domain.routine.dto.MonthlyRoutineRequest;
import com.umc5th.muffler.domain.routine.dto.WeeklyRoutineRequest;
import com.umc5th.muffler.domain.routine.repository.MonthlyRoutineRepository;
import com.umc5th.muffler.domain.routine.repository.WeeklyRoutineRepository;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.MonthlyRoutineExpense;
import com.umc5th.muffler.entity.WeeklyRoutineExpense;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.MemberException;
import com.umc5th.muffler.global.response.exception.RoutineException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final MemberRepository memberRepository;
    private final WeeklyRoutineRepository weeklyRoutineRepository;
    private final MonthlyRoutineRepository monthlyRoutineRepository;
    private final GoalRepository goalRepository;

    // 반복 소비 내역(요일) 추가
    public WeeklyRoutineExpense addWeeklyRoutine(WeeklyRoutineRequest request) {

        Long memberId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        if(request.getEndDate() != null) {
            List<Goal> goals = goalRepository.findByMemberId(memberId);
            if(goals.isEmpty() || goals.stream().noneMatch(goal -> isDateWithinGoalPeriod(request.getEndDate(), goal))) {
                throw new RoutineException(ErrorCode.GOAL_NOT_FOUND);
            }
        }

        WeeklyRoutineExpense newWeeklyRoutineExpense = RoutineConverter.toWeeklyRoutine(request, member);

        return weeklyRoutineRepository.save(newWeeklyRoutineExpense);
    }

    // 반복 소비 내역(날짜) 추가
    public MonthlyRoutineExpense addMonthlyRoutine(MonthlyRoutineRequest request) {

        Long memberId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        if(request.getEndDate() != null) {
            List<Goal> goals = goalRepository.findByMemberId(memberId);
            if(goals.isEmpty() || goals.stream().noneMatch(goal -> isDateWithinGoalPeriod(request.getEndDate(), goal))) {
                throw new RoutineException(ErrorCode.GOAL_NOT_FOUND);
            }
        }

        MonthlyRoutineExpense newMonthlyRoutineExpense = RoutineConverter.toMonthlyRoutine(request, member);

        return monthlyRoutineRepository.save(newMonthlyRoutineExpense);
    }

    // 반복 종료 일이 목표 기간 내에 존재하는지 확인
    private boolean isDateWithinGoalPeriod(LocalDate date, Goal goal) {
        return !(date.isBefore(goal.getStartDate()) || date.isAfter(goal.getEndDate()));
    }
}
