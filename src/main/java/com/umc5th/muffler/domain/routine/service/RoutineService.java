package com.umc5th.muffler.domain.routine.service;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.routine.converter.RoutineConverter;
import com.umc5th.muffler.domain.routine.dto.AddMonthlyRoutineRequest;
import com.umc5th.muffler.domain.routine.dto.AddWeeklyRoutineRequest;
import com.umc5th.muffler.domain.routine.repository.RoutineRepository;
import com.umc5th.muffler.entity.*;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.MemberException;
import com.umc5th.muffler.global.response.exception.RoutineException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final MemberRepository memberRepository;
    private final RoutineRepository routineRepository;
    private final GoalRepository goalRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    // 반복 소비 내역(요일) 추가
    @Transactional
    public RoutineExpense addWeeklyRoutine(AddWeeklyRoutineRequest request) {

        Long memberId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        for (int day : request.getDayOfWeek()) {
            if (day < 1 || day > 7) {
                throw new IllegalArgumentException("daysOfWeek의 원소 값은 1부터 7까지의 정수여야 합니다.");
            }
        }

        if (request.getTerm() < 1 || request.getTerm() > 3) {
            throw new IllegalArgumentException("term은 1, 2, 3 중 하나이어야 합니다.");
        }

        if (request.getEndDate() != null) {
            List<Goal> goals = goalRepository.findByMemberId(memberId);

            if (goals.isEmpty()) {
                throw new RoutineException(ErrorCode.GOAL_NOT_FOUND);
            }

            boolean isEndDateWithinAnyGoal = goals.stream()
                    .noneMatch(goal -> isDateWithinGoalPeriod(request.getEndDate(), goal));

            if (isEndDateWithinAnyGoal) {
                LocalDate nearestGoalEndDate = goals.stream()
                        .map(Goal::getEndDate)
                        .filter(endDate -> endDate.isBefore(request.getEndDate()))
                        .max(LocalDate::compareTo)
                        .orElse(null);

                if (nearestGoalEndDate != null) {
                    int maxWeekOffset = request.getTerm() - 1;
                    for (int weekOffset = 0; weekOffset <= maxWeekOffset; weekOffset++) {
                        for (Integer dayOfWeek : request.getDayOfWeek()) {
                            LocalDate checkedDate = request.getStartDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.of(dayOfWeek)))
                                    .plusWeeks(weekOffset);

                            if (checkedDate.isAfter(nearestGoalEndDate) && checkedDate.isBefore(request.getEndDate())) {
                                throw new RoutineException(ErrorCode.INVALID_ROUTINE_END_DATE);
                            }
                        }
                    }
                }
            }
        }

        RoutineExpense newRoutineExpense = RoutineConverter.toWeeklyRoutine(request, member);

        return routineRepository.save(newRoutineExpense);
    }

    // 반복 소비 내역(날짜) 추가
    @Transactional
    public RoutineExpense addMonthlyRoutine(AddMonthlyRoutineRequest request) {

        Long memberId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        if (request.getEndDate() != null) {
            List<Goal> goals = goalRepository.findByMemberId(memberId);
            if (goals.isEmpty() || goals.stream().noneMatch(goal -> isDateWithinGoalPeriod(request.getEndDate(), goal))) {
                throw new RoutineException(ErrorCode.INVALID_ROUTINE_END_DATE);
            }
        }

        RoutineExpense newMonthlyRoutineExpense = RoutineConverter.toMonthlyRoutine(request, member);

        return routineRepository.save(newMonthlyRoutineExpense);
    }

    // 반복 종료 일이 목표 기간 내에 존재하는지 확인
    private boolean isDateWithinGoalPeriod(LocalDate date, Goal goal) {
        return !(date.isBefore(goal.getStartDate()) || date.isAfter(goal.getEndDate()));
    }

    // 지난 소비 내역 등록
    @Transactional
    public void addPastExpenses(AddWeeklyRoutineRequest request) {
        LocalDate currentDate = LocalDate.now();

        Long memberId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Long categoryId = 1L;
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException("category")); // TODO: CustomException 사용

        for (Integer dayOfWeek : request.getDayOfWeek()) {
            LocalDate nextDate = request.getStartDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.of(dayOfWeek)));

            LocalDate effectiveEndDate = (request.getEndDate() == null || request.getEndDate().isAfter(currentDate))
                    ? currentDate : request.getEndDate();

            while (!nextDate.isAfter(effectiveEndDate)) {
                if (nextDate.isBefore(currentDate) || nextDate.equals(currentDate)) {
                    Expense expense = Expense.builder()
                            .date(nextDate)
                            .title(request.getTitle())
                            .cost(request.getCost())
                            .memo(request.getMemo())
                            .member(member)
                            .category(category)
                            .build();

                    expenseRepository.save(expense);
                }

                nextDate = nextDate.plusWeeks(1);
            }
        }
    }
}
