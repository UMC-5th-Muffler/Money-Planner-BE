package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Expense;
import java.time.LocalDate;

public class ExpenseFixture {
    public static Expense EXPENSE_ONE = Expense.builder()
            .id(1L)
            .title("ONE")
            .cost(1L)
            .memo("ONE MEMO")
            .date(LocalDate.of(2024, 1, 1))
            .build();
    public static Expense EXPENSE_TWO = Expense.builder()
            .id(2L)
            .title("TWO")
            .cost(2L)
            .memo("TWO MEMO")
            .date(LocalDate.of(2024, 1, 12))
            .build();
    public static Expense EXPENSE_THREE = Expense.builder()
            .id(3L)
            .title("THREE")
            .cost(3L)
            .memo("THREE MEMO")
            .date(LocalDate.of(2024, 1, 31))
            .build();
}
