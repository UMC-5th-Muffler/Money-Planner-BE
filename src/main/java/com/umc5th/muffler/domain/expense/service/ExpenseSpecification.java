package com.umc5th.muffler.domain.expense.service;

import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ExpenseSpecification {
    public static Specification<Expense> hasMember(Member member) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("member"), member);
    }

    public static Specification<Expense> isBetweenDates(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("date"), startDate, endDate);
    }

    public static Specification<Expense> hasCategory(Long categoryId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    }
}
