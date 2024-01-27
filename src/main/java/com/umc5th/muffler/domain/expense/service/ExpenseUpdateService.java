package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.expense.dto.ExpenseConverter;
import com.umc5th.muffler.domain.expense.dto.NewExpenseRequest;
import com.umc5th.muffler.domain.expense.dto.NewExpenseResponse;
import com.umc5th.muffler.domain.expense.dto.UpdateExpenseRequest;
import com.umc5th.muffler.domain.expense.repository.ExpenseRepository;
import com.umc5th.muffler.domain.goal.repository.DailyPlanRepository;
import com.umc5th.muffler.domain.member.repository.MemberRepository;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.DailyPlan;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.ExpenseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Optional<CategoryGoal> optionalCategoryGoal = categoryGoalRepository.findCategoryGoalWithGoalIdAndCategoryId(
                category.getId(), dailyPlan.getGoal().getId());

        Expense expense = ExpenseConverter.toExpenseEntity(request, member, category);
        expenseRepository.save(expense);
        dailyPlan.addExpenseDifference(expense.getCost());

        NewExpenseResponseBuilder resBuilder = NewExpenseResponse.builder().expenseId(expense.getId());
        if (dailyPlan.isPossibleToAlarm()) {
            resBuilder = resBuilder.dailyBudgetAlarm(new AlarmControlDTO(dailyPlan.getBudget(), dailyPlan.getTotalCost()));
            dailyPlan.turnOffAlarm();
        }
        if (optionalCategoryGoal.isPresent()) {
            CategoryGoal categoryGoal = optionalCategoryGoal.get();
            Long sumOfCategoryCost = expenseRepository.getSumOfCategoryCost(memberId,
                    dailyPlan.getGoal().getStartDate(), dailyPlan.getGoal().getEndDate(), category.getId());
            if (categoryGoal.isPossibleToAlarm(sumOfCategoryCost)) {
                resBuilder = resBuilder.categoryBudgetAlarm(new AlarmControlDTO(categoryGoal.getBudget(), sumOfCategoryCost));
                categoryGoal.turnOffAlarm();
                categoryGoalRepository.save(categoryGoal);
            }
        }
        dailyPlanRepository.save(dailyPlan);
        return resBuilder.build();
    }

    public void updateExpense(String memberId, UpdateExpenseRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.MEMBER_NOT_FOUND));
        Expense oldExpense = expenseRepository.findExpenseByIdFetchMember(request.getExpenseId())
                .orElseThrow(() -> new ExpenseException(ErrorCode.EXPENSE_NOT_FOUND));
        if (!oldExpense.isOwnMember(memberId))
            throw new ExpenseException(ErrorCode.CANNOT_UPDATE_OTHER_MEMBER_EXPENSE);
        Category category = categoryRepository.findCategoryWithNameAndMemberId(request.getCategoryName(), member.getId())
                .orElseThrow(() -> new ExpenseException(ErrorCode.CATEGORY_NOT_FOUND));
        DailyPlan dailyPlan = dailyPlanRepository.findDailyPlanByDateAndMemberId(request.getExpenseDate(), memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.NO_DAILY_PLAN_GIVEN_DATE));

        Expense newExpense = ExpenseConverter.toExpenseEntity(request, member, category);
        long difference = request.getExpenseCost() - oldExpense.getCost();
        dailyPlan.addExpenseDifference(difference);
        expenseRepository.save(newExpense);
        dailyPlanRepository.save(dailyPlan);
    }
}
