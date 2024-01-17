package com.umc5th.muffler.domain.expense.repository;

import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT SUM(e.cost) FROM Expense e WHERE e.member = :member AND e.date = :date")
    Long calculateTotalCostByMemberAndDate(@Param("member")Member member, @Param("date")LocalDate date);

    Slice<Expense> findAllByMemberAndDate(@Param("member")Member member, @Param("date")LocalDate date, Pageable pageable);
    @Query("SELECT SUM(e.cost) FROM Expense e WHERE e.member = :member AND e.date BETWEEN :startDate AND :endDate")
    Long calculateTotalCostByMemberAndDateBetween(@Param("member")Member member, @Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate);

    Slice<Expense> findAllByMemberAndDateBetween(Member member, LocalDate startDate, LocalDate endDate, Pageable pageable);

}
