package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.dailyplan.repository.DailyPlanRepository;
import com.umc5th.muffler.domain.expense.dto.AlarmControlDTO;
import com.umc5th.muffler.domain.expense.dto.ExpenseConverter;
import com.umc5th.muffler.domain.expense.dto.NewExpenseRequest;
import com.umc5th.muffler.domain.expense.dto.NewExpenseResponse;
import com.umc5th.muffler.domain.expense.dto.UpdateExpenseRequest;
import com.umc5th.muffler.domain.expense.dto.UpdateExpenseResponse;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.CategoryGoalRepository;
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
    private final CategoryRepository categoryRepository;
    private final CategoryGoalRepository  categoryGoalRepository;
    private final DailyPlanRepository dailyPlanRepository;

    public NewExpenseResponse enrollExpense(String memberId, NewExpenseRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.MEMBER_NOT_FOUND));
        Category category = categoryRepository.findCategoryWithNameAndMemberId(request.getCategoryName(), member.getId())
                .orElseThrow(() -> new ExpenseException(ErrorCode.CATEGORY_NOT_FOUND));
        DailyPlan dailyPlan = dailyPlanRepository.findByMemberIdAndDate(memberId, request.getExpenseDate())
                .orElseThrow(() -> new ExpenseException(ErrorCode.NO_DAILY_PLAN_GIVEN_DATE));
        if (dailyPlan.getIsZeroDay())
            throw new ExpenseException(ErrorCode.CANNOT_UPDATE_TO_ZERO_DAY);
        Expense expense = ExpenseConverter.toExpenseEntity(request, member, category);

        AlarmControlDTO dailyAlarm = handleDailyAlarm(dailyPlan, request.getExpenseCost());
        AlarmControlDTO categoryAlarm = handleCategoryAlarm(category, dailyPlan.getGoal().getId(), request.getExpenseCost(), memberId);

        expense = expenseRepository.save(expense);
        dailyPlan.addExpenseDifference(expense.getCost());
        dailyPlanRepository.save(dailyPlan);
        return new NewExpenseResponse(expense.getId(),dailyAlarm, categoryAlarm);
    }

    public UpdateExpenseResponse updateExpense(String memberId, UpdateExpenseRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.MEMBER_NOT_FOUND));
        Expense oldExpense = expenseRepository.findById(request.getExpenseId())
                .orElseThrow(() -> new ExpenseException(ErrorCode.EXPENSE_NOT_FOUND));
        if (!oldExpense.isOwnMember(memberId))
            throw new ExpenseException(ErrorCode.CANNOT_UPDATE_OTHER_MEMBER_EXPENSE);
        DailyPlan oldDailyPlan = dailyPlanRepository.findByMemberIdAndDate(memberId, oldExpense.getDate())
                .orElseThrow(() -> new ExpenseException(ErrorCode.NO_DAILY_PLAN_GIVEN_DATE));
        Long difference = request.getExpenseCost() - oldExpense.getCost();

        oldDailyPlan.addExpenseDifference(-oldExpense.getCost()); // 반드시 앞에서 빼야 함.
        DailyPlan newDailyPlan = syncDailyPlanWithRequest(oldExpense, oldDailyPlan, memberId, request);
        Category newCategory = syncCategoryWithRequest(oldExpense, memberId, request);

        AlarmControlDTO dailyAlarm = handleDailyAlarm(newDailyPlan, request.getExpenseCost());
        AlarmControlDTO categoryAlarm = handleCategoryAlarm(newCategory, newDailyPlan.getGoal().getId(), difference, memberId);

        newDailyPlan.addExpenseDifference(request.getExpenseCost());
        Expense newExpense = ExpenseConverter.toExpenseEntity(request, member, newCategory);
        expenseRepository.save(newExpense);
        return new UpdateExpenseResponse(dailyAlarm, categoryAlarm);
    }

    private DailyPlan syncDailyPlanWithRequest(Expense expense, DailyPlan dailyPlan, String memberId ,UpdateExpenseRequest request) {
        if (expense.isDateChanged(request.getExpenseDate())) {
            DailyPlan newDailyPlan = dailyPlanRepository.findByMemberIdAndDate(memberId, request.getExpenseDate())
                    .orElseThrow(() -> new ExpenseException(ErrorCode.NO_DAILY_PLAN_GIVEN_DATE));
            if (newDailyPlan.getIsZeroDay())
                throw new ExpenseException(ErrorCode.CANNOT_UPDATE_TO_ZERO_DAY);
            return newDailyPlan;
        }
        return dailyPlan;
    }

    private Category syncCategoryWithRequest(Expense expense, String memberId, UpdateExpenseRequest request) {
        if (expense.isCategoryChanged(request.getCategoryId())) {
            return categoryRepository.findCategoryWithCategoryIdAndMemberId(request.getCategoryId(), memberId)
                    .orElseThrow(() -> new ExpenseException(ErrorCode.CATEGORY_NOT_FOUND));
        }
        return expense.getCategory();
    }

    private AlarmControlDTO handleDailyAlarm(DailyPlan newDailyPlan, Long newExpenseCost ) {
        if (newDailyPlan.isPossibleToAlarm(newExpenseCost)) {
            return new AlarmControlDTO(newDailyPlan.getBudget(),
                    newDailyPlan.getTotalCost() + newExpenseCost - newDailyPlan.getBudget());
        }
        return null;
    }
    private AlarmControlDTO handleCategoryAlarm(Category category, Long goalId, Long difference, String memberId) {
        Optional<CategoryGoal> optionalCategoryGoal = categoryGoalRepository.findCategoryGoalWithGoalIdAndCategoryId(goalId, category.getId());
        if (optionalCategoryGoal.isPresent()) {
            CategoryGoal categoryGoal = optionalCategoryGoal.get();
            Goal goal = categoryGoal.getGoal();
            Long sumOfCategoryCost = expenseRepository.getSumOfCategoryCost(memberId, goal.getStartDate(), goal.getEndDate(), category.getId())
                    .orElse(0L);
            if (categoryGoal.isPossibleToAlarm(sumOfCategoryCost, difference)) {
                return new AlarmControlDTO(categoryGoal.getBudget(), sumOfCategoryCost + difference - categoryGoal.getBudget());
            }
        }
        return null;
    }

    public void deleteExpense(String memberId, Long expenseId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.MEMBER_NOT_FOUND));
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.EXPENSE_NOT_FOUND));
        if (!expense.isOwnMember(memberId))
            throw new ExpenseException(ErrorCode.CANNOT_UPDATE_OTHER_MEMBER_EXPENSE);
        DailyPlan dailyPlan = dailyPlanRepository.findByMemberIdAndDate(memberId, expense.getDate())
                .orElseThrow(() -> new ExpenseException(ErrorCode.NO_DAILY_PLAN_GIVEN_DATE));
        dailyPlan.addExpenseDifference(-expense.getCost());
        dailyPlanRepository.save(dailyPlan);
        expenseRepository.delete(expense);
    }
}
