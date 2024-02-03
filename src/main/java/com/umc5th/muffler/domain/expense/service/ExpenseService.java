package com.umc5th.muffler.domain.expense.service;

import static com.umc5th.muffler.entity.constant.ExpenseAlarm.CATEGORY;
import static com.umc5th.muffler.entity.constant.ExpenseAlarm.DAILY;
import static com.umc5th.muffler.entity.constant.ExpenseAlarm.TOTAL;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.dailyplan.repository.DailyPlanRepository;
import com.umc5th.muffler.domain.expense.dto.AlarmControlDTO;
import com.umc5th.muffler.domain.expense.dto.ExpenseConverter;
import com.umc5th.muffler.domain.expense.dto.ExpenseResponse;
import com.umc5th.muffler.domain.expense.dto.ExpenseCreateRequest;
import com.umc5th.muffler.domain.expense.dto.ExpenseUpdateRequest;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.CategoryGoalRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.domain.routine.service.RoutineService;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.constant.Status;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.CategoryException;
import com.umc5th.muffler.global.response.exception.ExpenseException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {

    private final RoutineService routineService;
    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryGoalRepository  categoryGoalRepository;
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

        List<AlarmControlDTO> alarms = getAlarms(dailyPlan, category, request.getExpenseCost());
        Expense savedExpense = expenseRepository.save(ExpenseConverter.toExpenseEntity(request, member, category));
        dailyPlan.updateTotalCost(savedExpense.getCost());

        if (request.isRoutine()) {
            routineService.create(savedExpense.getId(), request.getRoutineRequest());
        }
        return new ExpenseResponse(alarms);
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
        List<AlarmControlDTO> alarm = updateCostAndDate(expense, dailyPlan, request);
        updateCategory(expense, request.getCategoryId());

        return new ExpenseResponse(alarm);
    }

    private List<AlarmControlDTO> getAlarms(DailyPlan dailyPlan, Category category, Long expenditure) {
        List<AlarmControlDTO> alarms = new ArrayList<>();
        setDailyAlarm(dailyPlan, expenditure, alarms);
        setCategoryAlarm(category, dailyPlan.getGoal(), expenditure, alarms);
        setGoalAlarm(dailyPlan.getGoal(), expenditure, alarms);
        return alarms;
    }

    private void updateTitleAndMemo(Expense expense, ExpenseUpdateRequest request) {
        expense.setTitleAndMemo(request.getExpenseTitle(), request.getExpenseMemo());
    }

    private List<AlarmControlDTO> updateCostAndDate(Expense expense, DailyPlan dailyPlan, ExpenseUpdateRequest request) {
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

    private void setDailyAlarm(DailyPlan dailyPlan, Long expenditure, List<AlarmControlDTO> alarms) {
        if (dailyPlan.isPossibleToAlarm(expenditure)) {
            alarms.add(new AlarmControlDTO(
                    DAILY, dailyPlan.getBudget(),
                    dailyPlan.getTotalCost() + expenditure - dailyPlan.getBudget()));
        }
    }

    private void setCategoryAlarm(Category category, Goal goal, Long expenditure, List<AlarmControlDTO> alarms) {
        categoryGoalRepository.findByGoalIdAndCategoryId(goal.getId(), category.getId())
                .ifPresent(categoryGoal -> {
                    Long totalCategoryExpense = expenseRepository.sumCategoryExpenseWithinGoal(goal.getMember().getId(), category, goal);

                    if (categoryGoal.isPossibleToAlarm(totalCategoryExpense, expenditure)) {
                        alarms.add(new AlarmControlDTO(
                                CATEGORY, categoryGoal.getBudget(),
                                totalCategoryExpense + expenditure - categoryGoal.getBudget()));
                    }
                });
    }

    private void setGoalAlarm(Goal goal, Long expenditure, List<AlarmControlDTO> alarms) {
        Long totalGoalExpense = expenseRepository.sumCostByMemberAndDateBetween(goal.getMember().getId(), goal.getStartDate(), goal.getEndDate());

        if (goal.isPossibleToAlarm(totalGoalExpense, expenditure)) {
            alarms.add(new AlarmControlDTO(
                    TOTAL, goal.getTotalBudget(),
                    totalGoalExpense + expenditure - goal.getTotalBudget()));
        }
    }

    private List<AlarmControlDTO> updateCostAndDateWithAlarm(Expense expense, DailyPlan dailyPlan, ExpenseUpdateRequest request, boolean dateChanged) {
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

        List<AlarmControlDTO> alarm = getDailyAlarm(dailyPlan, expenditure);
        dailyPlan.updateTotalCost(expenditure);
        return alarm;
    }

    private List<AlarmControlDTO> getDailyAlarm(DailyPlan dailyPlan, Long expenditure) {
        List<AlarmControlDTO> alarm = new ArrayList<>();
        setDailyAlarm(dailyPlan, expenditure, alarm);
        return alarm;
    }

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
}
