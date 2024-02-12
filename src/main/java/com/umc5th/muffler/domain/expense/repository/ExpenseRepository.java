package com.umc5th.muffler.domain.expense.repository;

import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>, ExpenseRepositoryCustom {

    Optional<Expense> findByIdAndMemberId(Long id, String memberId);

    @Query("SELECT e FROM Expense e JOIN FETCH e.category WHERE e.member = :member AND e.date BETWEEN :startDate AND :endDate")
    List<Expense> findAllByMemberAndDateBetween(Member member, LocalDate startDate, LocalDate endDate);

    @Query("SELECT e FROM Expense e JOIN FETCH e.category WHERE e.id = :id")
    Optional<Expense> findById(@Param("id") Long id);
  
    @Query("SELECT e FROM Expense e JOIN FETCH e.category WHERE e.id = :id")
    Optional<Expense> findByIdJoin(@Param("id") Long id);

    @Query("SELECT SUM(e.cost) FROM Expense e WHERE e.member.id = :memberId AND e.category.id = :categoryId AND e.date BETWEEN :startDate AND :endDate")
    Optional<Long> sumTotalCategoryCostByMemberAndDateBetween(String memberId, Long categoryId, LocalDate startDate, LocalDate endDate);
}
