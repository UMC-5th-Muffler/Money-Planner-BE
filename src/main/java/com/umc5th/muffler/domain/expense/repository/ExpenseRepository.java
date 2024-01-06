package com.umc5th.muffler.domain.expense.repository;

import com.umc5th.muffler.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}
