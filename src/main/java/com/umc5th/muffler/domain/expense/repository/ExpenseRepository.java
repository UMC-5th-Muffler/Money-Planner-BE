package com.umc5th.muffler.domain.expense.repository;

import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>, ExpenseRepositoryCustom {

    @Query("SELECT SUM(e.cost) FROM Expense e WHERE e.member = :member AND e.date BETWEEN :startDate AND :endDate")
    Long calculateTotalCostByMemberAndDateBetween(Member member, LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(e.cost) FROM Expense e WHERE e.member = :member AND e.date = :date")
    Long calculateTotalCostByMemberAndDate(@Param("member")Member member, @Param("date")LocalDate date);

    @Query("SELECT SUM(e.cost) FROM Expense e WHERE e.member.id = :memberId AND e.category.id = :categoryId AND e.date BETWEEN :startDate AND :endDate")
    Optional<Long> sumTotalCategoryCostByMemberAndDateBetween(String memberId, Long categoryId, LocalDate startDate, LocalDate endDate);

    Slice<Expense> findAllByMemberAndDate(Member member, LocalDate date, Pageable pageable);

    Slice<Expense> findAllByMemberAndDateBetween(Member member, LocalDate startDate, LocalDate endDate, Pageable pageable);
}
