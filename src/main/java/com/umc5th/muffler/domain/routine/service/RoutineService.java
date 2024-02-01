package com.umc5th.muffler.domain.routine.service;

import static com.umc5th.muffler.entity.constant.RoutineType.MONTHLY;
import static com.umc5th.muffler.entity.constant.RoutineType.WEEKLY;
import static com.umc5th.muffler.global.response.code.ErrorCode.*;

import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.routine.dto.*;
import com.umc5th.muffler.domain.routine.repository.RoutineRepository;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.entity.constant.RoutineType;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.CommonException;
import com.umc5th.muffler.global.response.exception.ExpenseException;
import com.umc5th.muffler.global.response.exception.MemberException;
import com.umc5th.muffler.global.response.exception.RoutineException;
import com.umc5th.muffler.global.util.DateTimeProvider;
import com.umc5th.muffler.global.util.RoutineProcessor;
import com.umc5th.muffler.global.util.RoutineUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    // TODO : 소비 등록 서비스와 연결
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
        if (routine.getType() == MONTHLY && request.getMonthlyRepeatDay() != null) {
            routine.setMonthlyColumn(Integer.parseInt(request.getMonthlyRepeatDay()));
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

        routineDates.stream()
                .forEach(date -> addExpense(date, routine));
    }

    private void addExpense(LocalDate date, Routine routine) {
        expenseRepository.save(
                Expense.of(date, routine.getTitle(), routine.getMemo(), routine.getCost(), routine.getMember(), routine.getCategory())
        );
    }

    public RoutineResponse getAllRoutines(Pageable pageable, String memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Slice<Routine> routineList = routineRepository.findAllByMemberId(member.getId(), pageable);

        Map<Long, RoutineWeeklyDetailDto> weeklyDetailDto = getWeeklyRoutine(routineList);
        List<RoutineAll> routineInfoList  = RoutineConverter.toRoutineInfo(routineList, weeklyDetailDto);
        RoutineResponse response = RoutineConverter.toRoutineResponse(routineInfoList, routineList.hasNext());

        return response;
    }

    private static Map<Long, RoutineWeeklyDetailDto> getWeeklyRoutine(Slice<Routine> routineList) {
        return routineList.stream()
                .filter(routine -> routine.getType() == RoutineType.WEEKLY)
                .collect(Collectors.toMap(
                        Routine::getId,
                        RoutineConverter::getWeeklyDetail
                ));
    }

    public RoutineDetail getRoutine(String memberId, Long routineId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
        Routine routine = routineRepository.findByIdAndCategory(routineId).orElseThrow(() -> new RoutineException(ErrorCode.ROUTINE_NOT_FOUND));
        String categoryName = routine.getCategory().getName();

        return RoutineConverter.toRoutineDetail(routine, categoryName);
    }

    @Transactional
    public void delete(Long routineId, String memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));
        Routine routine = routineRepository.findById(routineId).orElseThrow(() -> new RoutineException(ROUTINE_NOT_FOUND));

        if(!Objects.equals(member.getId(), routine.getMember().getId())) {
            throw new CommonException(INVALID_PERMISSION);
        }

        routineRepository.delete(routine);
    }
}
