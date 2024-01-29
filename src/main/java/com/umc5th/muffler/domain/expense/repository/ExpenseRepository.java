package com.umc5th.muffler.domain.expense.repository;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Expense;
import com.umc5th.muffler.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {

    @Query("SELECT e FROM Expense e JOIN FETCH e.category WHERE e.member = :member AND e.date = :date")
    Slice<Expense> findAllByMemberAndDate(@Param("member") Member member, @Param("date") LocalDate date, Pageable pageable);

    @Query("SELECT SUM(e.cost) FROM Expense e WHERE e.member = :member AND e.date = :date")
    Long calculateTotalCostByMemberAndDate(@Param("member")Member member, @Param("date")LocalDate date);

    List<Expense> findAllByMemberAndDateBetween(Member member, LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(e.cost) FROM Expense e WHERE e.member = :member AND e.date BETWEEN :startDate AND :endDate")
    Long calculateTotalCostByMemberAndDateBetween(@Param("member")Member member, @Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate);

    @Query("SELECT e FROM Expense e JOIN FETCH e.category WHERE e.member = :member AND e.date BETWEEN :startDate AND :endDate")
    Slice<Expense> findAllByMemberAndDateBetween(@Param("member")Member member, @Param("startDate")LocalDate startDate, @Param("endDate")LocalDate endDate, Pageable pageable);

    @EntityGraph(attributePaths = {"category"})
    Page<Expense> findAll(Specification<Expense> spec, Pageable pageable);

    @Query("SELECT e FROM Expense e JOIN FETCH e.category WHERE e.id = :id")
    Optional<Expense> findById(@Param("id") Long id);
}
