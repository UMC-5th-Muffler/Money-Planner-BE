package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.domain.category.repository.CategoryRepository;
import com.umc5th.muffler.domain.expense.dto.ExpenseConverter;
import com.umc5th.muffler.domain.expense.dto.NewExpenseRequest;
import com.umc5th.muffler.domain.expense.dto.NewExpenseResponse;
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

    public NewExpenseResponse enrollExpense(String memberId, NewExpenseRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.MEMBER_NOT_FOUND));
        Category category = categoryRepository.findCategoryWithNameAndMemberId(request.getCategoryName(), member.getId())
                .orElseThrow(() -> new ExpenseException(ErrorCode.CATEGORY_NOT_FOUND));
        DailyPlan dailyPlan = dailyPlanRepository.findDailyPlanByDateAndMemberId(request.getExpenseDate(), memberId)
                .orElseThrow(() -> new ExpenseException(ErrorCode.NO_DAILY_PLAN_GIVEN_DATE));

        Expense expense = ExpenseConverter.toExpenseEntity(request, member, category);
        expense = expenseRepository.save(expense);
        dailyPlan.addExpenseDifference(expense.getCost());
        dailyPlanRepository.save(dailyPlan);
        return new NewExpenseResponse(expense.getId());
    }
}
