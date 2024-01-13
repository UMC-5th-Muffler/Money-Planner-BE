package com.umc5th.muffler.domain.expense.repository;

import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByMemberAndDate(Member member, LocalDate date);
}
