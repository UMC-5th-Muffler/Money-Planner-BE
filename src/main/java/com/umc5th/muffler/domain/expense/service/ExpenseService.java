package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.dailyplan.repository.DailyPlanRepository;
import com.umc5th.muffler.domain.expense.dto.ExpenseAlarm;
import com.umc5th.muffler.domain.expense.dto.ExpenseConverter;
import com.umc5th.muffler.domain.expense.dto.ExpenseCreateRequest;
import com.umc5th.muffler.domain.expense.dto.ExpenseOverview;
import com.umc5th.muffler.domain.expense.dto.ExpenseResponse;
import com.umc5th.muffler.domain.expense.dto.ExpenseUpdateRequest;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.routine.service.RoutineService;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.constant.Status;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.CategoryException;
import com.umc5th.muffler.global.response.exception.ExpenseException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final RoutineService routineService;
    private final ExpenseAlarmService alarmService;
    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final DailyPlanRepository dailyPlanRepository;

    @Transactional
    public ExpenseResponse create(String memberId, ExpenseCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.MEMBER_NOT_FOUND));
        Category category = categoryRepository.findCategoryWithCategoryIdAndMemberId(request.getCategoryId(), member.getId())
                .orElseThrow(() -> new ExpenseException(ErrorCode.CATEGORY_NOT_FOUND));
        DailyPlan dailyPlan = dailyPlanRepository.findDailyPlanWithGoalByDateAndMember(memberId, request.getExpenseDate())
                .orElseThrow(() -> new ExpenseException(ErrorCode.NO_DAILY_PLAN_GIVEN_DATE));
        if (dailyPlan.getIsZeroDay()) {
            throw new ExpenseException(ErrorCode.CANNOT_UPDATE_TO_ZERO_DAY);
        }

        List<ExpenseAlarm> alarms = alarmService.getAlarms(dailyPlan, category, request.getExpenseCost());
        Expense savedExpense = expenseRepository.save(ExpenseConverter.toExpenseEntity(request, member, category));
        dailyPlan.updateTotalCost(savedExpense.getCost());

        if (request.isRoutine()) {
            routineService.create(savedExpense.getId(), request.getRoutineRequest());
        }
        return new ExpenseResponse(savedExpense.getId(), alarms);
    }

    @Transactional
    public ExpenseResponse update(String memberId, ExpenseUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.MEMBER_NOT_FOUND));
        Expense expense = expenseRepository.findByIdAndMemberId(request.getExpenseId(), memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.EXPENSE_NOT_FOUND));
        DailyPlan dailyPlan = dailyPlanRepository.findByMemberIdAndDate(memberId, expense.getDate())
                .orElseThrow(() -> new ExpenseException(ErrorCode.NO_DAILY_PLAN_GIVEN_DATE));

        updateTitleAndMemo(expense, request);
        List<ExpenseAlarm> alarm = updateCostAndDate(expense, dailyPlan, request);
        updateCategory(expense, request.getCategoryId());

        return new ExpenseResponse(expense.getId(), alarm);
    }

    private void updateTitleAndMemo(Expense expense, ExpenseUpdateRequest request) {
        expense.setTitleAndMemo(request.getExpenseTitle(), request.getExpenseMemo());
    }

    private List<ExpenseAlarm> updateCostAndDate(Expense expense, DailyPlan dailyPlan, ExpenseUpdateRequest request) {
        boolean dateChanged = expense.isDateChanged(request.getExpenseDate());
        boolean costChanged = expense.isCostChanged(request.getExpenseCost());

        if (!dateChanged && !costChanged) {
            return new ArrayList<>();
        }

        // date 변화 or cost 변화 둘중 하나는 꼭 있음 -> alarm check 필수
        return updateCostAndDateWithAlarm(expense, dailyPlan, request, dateChanged);
    }

    private void updateCategory(Expense expense, Long categoryId) {
        if (expense.isCategoryChanged(categoryId)) {
            Category category = categoryRepository.findByIdAndStatus(categoryId, Status.ACTIVE)
                    .orElseThrow(() -> new CategoryException(ErrorCode.CATEGORY_NOT_FOUND));
            expense.setCategory(category);
        }
    }

    private List<ExpenseAlarm> updateCostAndDateWithAlarm(Expense expense, DailyPlan dailyPlan, ExpenseUpdateRequest request, boolean dateChanged) {
        dailyPlan.updateTotalCost(-expense.getCost());
        Long expenditure = request.getExpenseCost();
        expense.setCost(expenditure);

        if (dateChanged) {
            dailyPlan = dailyPlanRepository.findByMemberIdAndDate(expense.getMember().getId(), request.getExpenseDate())
                    .orElseThrow(() -> new ExpenseException(ErrorCode.DAILYPLAN_NOT_FOUND));
            if (dailyPlan.getIsZeroDay()) {
                throw new ExpenseException(ErrorCode.CANNOT_UPDATE_TO_ZERO_DAY);
            }
            expense.setDate(request.getExpenseDate());
        }

        List<ExpenseAlarm> alarm = alarmService.getDailyAlarm(dailyPlan, expenditure);
        dailyPlan.updateTotalCost(expenditure);
        return alarm;
    }

    @Transactional
    public void delete(String memberId, Long expenseId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.MEMBER_NOT_FOUND));
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.EXPENSE_NOT_FOUND));
        if (!expense.isOwnMember(memberId))
            throw new ExpenseException(ErrorCode.CANNOT_UPDATE_OTHER_MEMBER_EXPENSE);
        DailyPlan dailyPlan = dailyPlanRepository.findByMemberIdAndDate(memberId, expense.getDate())
                .orElseThrow(() -> new ExpenseException(ErrorCode.NO_DAILY_PLAN_GIVEN_DATE));
        dailyPlan.updateTotalCost(-expense.getCost());
        dailyPlanRepository.save(dailyPlan);
        expenseRepository.delete(expense);
    }

    public ExpenseOverview getOverview(String memberId, YearMonth yearMonth) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.MEMBER_NOT_FOUND));

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        List<DailyPlan> dailies = dailyPlanRepository.findByMemberIdAndDateBetween(memberId, startDate, endDate);

        return ExpenseConverter.toExpenseOverview(dailies);
    }
}
