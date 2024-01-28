package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.expense.dto.AlarmControlDTO;
import com.umc5th.muffler.domain.expense.dto.ExpenseConverter;
import com.umc5th.muffler.domain.expense.dto.NewExpenseRequest;
import com.umc5th.muffler.domain.expense.dto.NewExpenseResponse;
import com.umc5th.muffler.domain.expense.dto.UpdateExpenseRequest;
import com.umc5th.muffler.domain.expense.dto.UpdateExpenseResponse;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.CategoryGoalRepository;
import com.umc5th.muffler.domain.goal.repository.DailyPlanRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.CategoryGoal;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Goal;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.ExpenseException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseUpdateService {
    private final ExpenseRepository expenseRepository;
    private final MemberRepository memberRepository;
    private final DailyPlanRepository dailyPlanRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryGoalRepository  categoryGoalRepository;

    public NewExpenseResponse enrollExpense(String memberId, NewExpenseRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.MEMBER_NOT_FOUND));
        Category category = categoryRepository.findCategoryWithNameAndMemberId(request.getCategoryName(), member.getId())
                .orElseThrow(() -> new ExpenseException(ErrorCode.CATEGORY_NOT_FOUND));
        DailyPlan dailyPlan = dailyPlanRepository.findDailyPlanByDateAndMemberId(request.getExpenseDate(), memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.NO_DAILY_PLAN_GIVEN_DATE));
        AlarmControlDTO dailyAlarm = null, categoryAlarm;
        Expense expense = ExpenseConverter.toExpenseEntity(request, member, category);

        if (dailyPlan.isPossibleToAlarm(expense.getCost())) {
            dailyAlarm = new AlarmControlDTO(dailyPlan.getBudget(), dailyPlan.getTotalCost() + expense.getCost());
        }
        categoryAlarm = handleCategoryGoal(memberId, category, dailyPlan.getGoal(), 0L, expense);

        expense = expenseRepository.save(expense);
        dailyPlan.addExpenseDifference(expense.getCost());
        dailyPlanRepository.save(dailyPlan);
        return new NewExpenseResponse(expense.getId(), dailyAlarm, categoryAlarm);
    }

    public UpdateExpenseResponse updateExpense(String memberId, UpdateExpenseRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.MEMBER_NOT_FOUND));
        Expense oldExpense = expenseRepository.findExpenseByIdFetchMemberAndCategory(request.getExpenseId())
                .orElseThrow(() -> new ExpenseException(ErrorCode.EXPENSE_NOT_FOUND));
        if (!oldExpense.isOwnMember(memberId))
            throw new ExpenseException(ErrorCode.CANNOT_UPDATE_OTHER_MEMBER_EXPENSE);
        DailyPlan oldDailyPlan = dailyPlanRepository.findDailyPlanByDateAndMemberId(oldExpense.getDate(), memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.NO_DAILY_PLAN_GIVEN_DATE));

        Category newCategory = categoryRepository.findCategoryWithNameAndMemberId(request.getCategoryName(), member.getId())
                .orElseThrow(() -> new ExpenseException(ErrorCode.CATEGORY_NOT_FOUND));
        DailyPlan newDailyPlan = dailyPlanRepository.findDailyPlanByDateAndMemberId(request.getExpenseDate(), memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.NO_DAILY_PLAN_GIVEN_DATE));
        if (newDailyPlan.getIsZeroDay())
            throw new ExpenseException(ErrorCode.CANNOT_UPDATE_TO_ZERO_DAY);

        AlarmControlDTO dailyAlarm, categoryAlarm;
        Expense newExpense = ExpenseConverter.toExpenseEntity(request, member, newCategory);

        dailyAlarm = handleDailyPlan(oldExpense, oldDailyPlan, newDailyPlan, newExpense);
        categoryAlarm = handleCategoryGoal(memberId, newCategory, newDailyPlan.getGoal(), oldExpense.getCost(), newExpense);

        expenseRepository.save(newExpense);
        return new UpdateExpenseResponse(dailyAlarm, categoryAlarm);
    }

    private AlarmControlDTO handleDailyPlan(Expense oldExpense, DailyPlan oldDailyPlan,
                           DailyPlan newDailyPlan, Expense newExpense) {
        AlarmControlDTO dailyAlarm = null;
        if (oldDailyPlan.getId().equals(newDailyPlan.getId())) {
            long difference = newExpense.getCost() - oldExpense.getCost();
            if (oldDailyPlan.isPossibleToAlarm(difference))
                dailyAlarm = new AlarmControlDTO(oldDailyPlan.getBudget(), oldDailyPlan.getTotalCost() + difference);
            newDailyPlan.addExpenseDifference(difference);
            dailyPlanRepository.save(newDailyPlan);
        }else {
            if (newDailyPlan.isPossibleToAlarm(newExpense.getCost()))
                dailyAlarm = new AlarmControlDTO(newDailyPlan.getBudget(), newDailyPlan.getTotalCost() + newExpense.getCost());
            oldDailyPlan.addExpenseDifference(-oldExpense.getCost());
            newDailyPlan.addExpenseDifference(newExpense.getCost());
            dailyPlanRepository.save(newDailyPlan);
            dailyPlanRepository.save(oldDailyPlan);
        }
        return dailyAlarm;
    }

    private AlarmControlDTO handleCategoryGoal(String memberId, Category category, Goal goal, Long originalCost, Expense newExpense) {
        Optional<CategoryGoal> optionalCategoryGoal = categoryGoalRepository.findCategoryGoalWithGoalIdAndCategoryId(goal.getId(), category.getId());

        AlarmControlDTO categoryAlarm = null;
        if (optionalCategoryGoal.isPresent()) {
            CategoryGoal categoryGoal = optionalCategoryGoal.get();
            Long sumOfCategoryCost = expenseRepository.getSumOfCategoryCost(memberId, goal.getStartDate(), goal.getEndDate(), category.getId())
                    .orElse(0L);
            long diff = newExpense.getCost() - originalCost;
            if (categoryGoal.isPossibleToAlarm(sumOfCategoryCost, diff)) {
                categoryAlarm = new AlarmControlDTO(categoryGoal.getBudget(), sumOfCategoryCost + diff);
            }
        }
        return categoryAlarm;
    }

    public void deleteExpense(String memberId, Long expenseId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.MEMBER_NOT_FOUND));
        Expense expense = expenseRepository.findExpenseByIdFetchMember(expenseId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.EXPENSE_NOT_FOUND));
        if (!expense.isOwnMember(memberId))
            throw new ExpenseException(ErrorCode.CANNOT_UPDATE_OTHER_MEMBER_EXPENSE);
        DailyPlan dailyPlan = dailyPlanRepository.findDailyPlanByDateAndMemberId(expense.getDate(), memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.NO_DAILY_PLAN_GIVEN_DATE));
        dailyPlan.addExpenseDifference(-expense.getCost());
        dailyPlanRepository.save(dailyPlan);
        expenseRepository.delete(expense);
    }
}
