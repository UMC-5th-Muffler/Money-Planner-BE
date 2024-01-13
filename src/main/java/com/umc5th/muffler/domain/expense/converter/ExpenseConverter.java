package com.umc5th.muffler.domain.expense.converter;

import com.umc5th.muffler.domain.expense.dto.NewExpenseRequest;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;

public class ExpenseConverter {
    public static Expense toExpenseEntity(NewExpenseRequest dto, Member member, Category category) {
        return Expense.builder()
            .title(dto.getExpenseTitle())
            .memo(dto.getExpenseMemo())
            .date(dto.getExpenseDate())
            .cost(dto.getExpenseCost())
            .category(category)
            .member(member)
            .build();
    }
}
