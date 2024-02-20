package com.umc5th.muffler.domain.routine.service;

import static com.umc5th.muffler.entity.constant.RoutineType.MONTHLY;
import static com.umc5th.muffler.entity.constant.RoutineType.WEEKLY;
import static com.umc5th.muffler.global.response.code.ErrorCode.EXPENSE_NOT_FOUND;
import static com.umc5th.muffler.global.response.code.ErrorCode.INVALID_ROUTINE_INPUT;
import static com.umc5th.muffler.global.response.code.ErrorCode.MEMBER_NOT_FOUND;
import static com.umc5th.muffler.global.response.code.ErrorCode.ROUTINE_NOT_FOUND;

import com.umc5th.muffler.domain.dailyplan.repository.JDBCDailyPlanRepository;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.dto.GoalTerm;
import com.umc5th.muffler.domain.goal.repository.GoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.routine.dto.RoutineAll;
import com.umc5th.muffler.domain.routine.dto.RoutineConverter;
import com.umc5th.muffler.domain.routine.dto.RoutineDetail;
import com.umc5th.muffler.domain.routine.dto.RoutineRequest;
import com.umc5th.muffler.domain.routine.dto.RoutineResponse;
import com.umc5th.muffler.domain.routine.repository.RoutineRepository;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.entity.WeeklyRepeatDay;
import com.umc5th.muffler.entity.constant.RoutineType;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.ExpenseException;
import com.umc5th.muffler.global.response.exception.MemberException;
import com.umc5th.muffler.global.response.exception.RoutineException;
import com.umc5th.muffler.global.util.DateTimeProvider;
import com.umc5th.muffler.global.util.RoutineProcessor;
import com.umc5th.muffler.global.util.RoutineUtils;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoutineService {
    private final DateTimeProvider dateTimeProvider;
    private final RoutineRepository routineRepository;
    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;
    private final GoalRepository goalRepository;
    private final JDBCDailyPlanRepository jdbcDailyPlanRepository;

    @Transactional
    public void create(Long expenseId, RoutineRequest request) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseException(EXPENSE_NOT_FOUND));

        Routine routine = Routine.of(request.getType(), expense.getDate(), expense.getTitle(), expense.getMemo(), expense.getCategory(), expense.getCost(), expense.getMember());
        setEndDate(request, routine);
        setRoutineColumnByType(request, routine);

        routineRepository.save(routine);
        if (routine.getStartDate().isBefore(dateTimeProvider.nowDate())) {
            addPastExpenses(routine);
        }
    }

    private static void setEndDate(RoutineRequest request, Routine routine) {
        if (request.getEndDate() == null) {
            return;
        }
        if(!routine.getStartDate().isBefore(request.getEndDate())) {
            throw new RoutineException(INVALID_ROUTINE_INPUT, "반복 종료일은 반복 시작일 이후여야 합니다.");
        }
        routine.setEndDate(request.getEndDate());
    }

    private static void setRoutineColumnByType(RoutineRequest request, Routine routine) {
        if (routine.getType() == WEEKLY && request.getWeeklyRepeatDays() != null && request.getWeeklyTerm() != null) {
            routine.setWeeklyColumn(
                    RoutineConverter.getWeeklyRepeatDayEntities(routine, request.getWeeklyRepeatDays()),
                    Integer.parseInt(request.getWeeklyTerm())
            );
            return;
        }
        if (routine.getType() == MONTHLY && request.getMonthlyRepeatType() != null) {
            routine.setMonthlyColumn(request.getMonthlyRepeatType());
            return;
        }
        throw new RoutineException(INVALID_ROUTINE_INPUT, "반복 설정 값을 모두 입력하지 않았습니다.");
    }

    private void addPastExpenses(Routine routine) {
        // startDate 포함X , endDate 포함O
        LocalDate startDate = routine.getStartDate();
        LocalDate endDate = dateTimeProvider.nowDate();

        RoutineProcessor processor = RoutineUtils.getProcessorForRoutineType(routine);
        List<LocalDate> routineDates = processor.getRoutineDates(startDate, endDate, routine);

        if (!routineDates.isEmpty()) {
            List<LocalDate> filteredRoutineDates = filterDatesByGoals(routineDates, startDate, endDate);

            if(!filteredRoutineDates.isEmpty()) {
                filteredRoutineDates.stream()
                        .forEach(date -> addExpense(date, routine));
                updateDailyTotalCost(filteredRoutineDates, routine);
            }
        }
    }

    private void addExpense(LocalDate date, Routine routine) {
        expenseRepository.save(
                Expense.of(date, routine.getTitle(), routine.getMemo(), routine.getCost(), routine.getMember(), routine.getCategory())
        );
    }

    private void updateDailyTotalCost(List<LocalDate> dates, Routine routine) {
        jdbcDailyPlanRepository.updateTotalCostForDailyPlans(routine.getMember().getId(), dates, routine.getCost());
    }

    private List<LocalDate> filterDatesByGoals(List<LocalDate> routineDates, LocalDate startDate, LocalDate endDate) {
        List<GoalTerm> goalList = goalRepository.findGoalsWithinDateRange(startDate, endDate);

        Set<LocalDate> goalPeriodsSet = new HashSet<>();
        for (GoalTerm goal : goalList) {
            LocalDate currentDate = goal.getStartDate();
            while (!currentDate.isAfter(goal.getEndDate())) {
                goalPeriodsSet.add(currentDate);
                currentDate = currentDate.plusDays(1);
            }
        }

        return routineDates.stream()
                .filter(goalPeriodsSet::contains)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoutineResponse getAllRoutines(Pageable pageable, Long endPointId, String memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Slice<Routine> routineList = routineRepository.findRoutinesWithCategory(member.getId(), endPointId, pageable);

        List<Long> weeklyRoutineIds = routineList.stream()
                .filter(r -> r.getType() == RoutineType.WEEKLY)
                .map(Routine::getId)
                .collect(Collectors.toList());

        Map<Long, List<WeeklyRepeatDay>> weeklyRepeatDaysMap = Collections.emptyMap();
        if (!weeklyRoutineIds.isEmpty()) {
            weeklyRepeatDaysMap = routineRepository.findWeeklyRepeatDays(weeklyRoutineIds);
        }

        List<RoutineAll> routineAllList  = RoutineConverter.toRoutineInfoList(routineList, weeklyRepeatDaysMap);

        return RoutineConverter.toRoutineResponse(routineAllList, routineList.hasNext());
    }

    @Transactional(readOnly = true)
    public RoutineDetail getRoutine(String memberId, Long routineId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        Routine routine = routineRepository.findByIdAndMemberIdWithCategory(routineId, memberId).orElseThrow(() -> new RoutineException(ErrorCode.ROUTINE_NOT_FOUND));

        return RoutineConverter.toRoutineDetail(routine);
    }

    @Transactional
    public void delete(Long routineId, String memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Routine routine = routineRepository.findByIdAndMemberId(routineId, memberId).orElseThrow(() -> new RoutineException(ROUTINE_NOT_FOUND));

        routineRepository.delete(routine);
    }
}
