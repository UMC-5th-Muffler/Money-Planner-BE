package com.umc5th.muffler.domain.routine.service;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.routine.dto.RoutineConverter;
import com.umc5th.muffler.domain.routine.dto.AddMonthlyRoutineRequest;
import com.umc5th.muffler.domain.routine.dto.AddWeeklyRoutineRequest;
import com.umc5th.muffler.domain.routine.repository.RoutineRepository;
import com.umc5th.muffler.entity.*;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.CategoryException;
import com.umc5th.muffler.global.response.exception.MemberException;
import com.umc5th.muffler.global.response.exception.RoutineException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;

@Service
@RequiredArgsConstructor
public class RoutineCreateService {

    private final MemberRepository memberRepository;
    private final RoutineRepository routineRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    // 반복 소비 내역(요일) 추가
    @Transactional
    public Routine addWeeklyRoutine(AddWeeklyRoutineRequest request) {

        Long memberId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        validateWeeklyRoutine(request);

        Routine newWeeklyRoutine = RoutineConverter.toWeeklyRoutine(request, member);
        member.addRoutine(newWeeklyRoutine);
        return routineRepository.save(newWeeklyRoutine);
    }

    // 반복 소비 내역(날짜) 추가
    @Transactional
    public Routine addMonthlyRoutine(AddMonthlyRoutineRequest request) {

        Long memberId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        validateMonthlyRoutine(request.getStartDate(), request.getDay());

        Routine newMonthlyRoutine = RoutineConverter.toMonthlyRoutine(request, member);
        member.addRoutine(newMonthlyRoutine);
        return routineRepository.save(newMonthlyRoutine);
    }

    // 지난 소비 내역 등록
    @Transactional
    public void addPastWeeklyExpenses(AddWeeklyRoutineRequest request) {
        LocalDate currentDate = LocalDate.now();

        Long memberId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Long categoryId = 1L;
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND));

        for (Integer dayOfWeek : request.getDayOfWeek()) {
            LocalDate nextDate = request.getStartDate().with(TemporalAdjusters.nextOrSame(DayOfWeek.of(dayOfWeek)));

            LocalDate effectiveEndDate = (request.getEndDate() == null || request.getEndDate().isAfter(currentDate))
                    ? currentDate : request.getEndDate();

            while (!nextDate.isAfter(effectiveEndDate)) {
                if ((nextDate.isBefore(currentDate) || nextDate.equals(currentDate)) && !nextDate.equals(request.getStartDate())) {
                    Expense expense = Expense.builder()
                            .date(nextDate)
                            .title(request.getTitle())
                            .cost(request.getCost())
                            .memo(request.getMemo())
                            .member(member)
                            .category(category)
                            .build();

                    // TODO: member.addExpense(expense); 추가
                    expenseRepository.save(expense);
                }

                nextDate = nextDate.plusWeeks(1);
            }
        }
    }

    @Transactional
    public void addPastMonthlyExpense(AddMonthlyRoutineRequest request) {
        LocalDate today = LocalDate.now();

        Long memberId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Long categoryId = 1L;
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND));

        LocalDate nextDate = getNextDate(request.getStartDate(), request.getDay());
        while (!nextDate.isAfter(today)) {
            Expense expense = Expense.builder()
                    .date(nextDate)
                    .title(request.getTitle())
                    .memo(request.getMemo())
                    .cost(request.getCost())
                    .member(member)
                    .category(category)
                    .build();

            // TODO: member.addExpense(expense); 추가
            expenseRepository.save(expense);

            nextDate = getNextDate(nextDate, request.getDay());
        }
    }

    private LocalDate getNextDate(LocalDate startDate, int day) {
        LocalDate nextDate = startDate.withDayOfMonth(day);
        if (!nextDate.isAfter(startDate)) {
            nextDate = nextDate.plusMonths(1);
        }
        return nextDate;
    }

    private void validateWeeklyRoutine(AddWeeklyRoutineRequest request) {
        for (int day : request.getDayOfWeek()) {
            if (day < 1 || day > 7) {
                throw new RoutineException(ErrorCode.INVALID_ROUTINE_INPUT, "daysOfWeek의 원소 값은 1부터 7까지의 정수여야 합니다.");
            }
        }

        if (request.getTerm() < 1 || request.getTerm() > 3) {
            throw new RoutineException(ErrorCode.INVALID_ROUTINE_INPUT, "term은 1, 2, 3 중 하나여야 합니다.");
        }

        if (request.getEndDate() != null) {
            if(!request.getEndDate().isAfter(request.getStartDate())) {
                throw new RoutineException(ErrorCode.INVALID_ROUTINE_INPUT, "반복 종료일은 반복 시작일 이후여야 합니다.");
            }
        }
    }

    private void validateMonthlyRoutine(LocalDate startDate, Integer day) {
        YearMonth monthOfStartDate = YearMonth.from(startDate);

        if (day != 1 && day != 31) {
            if(!(day >= 1 && day <= monthOfStartDate.lengthOfMonth())) {
                throw new RoutineException(ErrorCode.INVALID_ROUTINE_INPUT, "유효한 날짜가 아닙니다.");
            };
        }
    }
}
