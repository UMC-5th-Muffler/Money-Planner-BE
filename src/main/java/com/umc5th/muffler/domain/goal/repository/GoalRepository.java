package com.umc5th.muffler.domain.goal.repository;

import com.umc5th.muffler.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    @Query("SELECT DISTINCT g FROM Goal g JOIN FETCH g.dailyPlans WHERE g.member.id = :memberId")
    Optional<List<Goal>> findByMemberId(String memberId);

    @Query("SELECT goal from Goal goal where :date BETWEEN goal.startDate and goal.endDate AND goal.member.id = :memberId")
    Optional<Goal> findByDateBetween(LocalDate date, String memberId);

    @Query("SELECT goal from Goal goal join fetch goal.dailyPlans where :date BETWEEN goal.startDate and goal.endDate AND goal.member.id = :memberId")
    Optional<Goal> findByDateBetweenAndDailyPlans(LocalDate date, String memberId);

    @Query("SELECT goal FROM Goal goal WHERE goal.member.id = :memberId " +
            "AND ((goal.startDate >= :startOfMonth AND goal.startDate <= :endOfMonth) " +
            "OR (goal.endDate >= :startOfMonth AND goal.endDate <= :endOfMonth))")
    Optional<List<Goal>> findGoalsByMonth(@Param("startOfMonth") LocalDate startOfMonth, @Param("endOfMonth") LocalDate endOfMonth, @Param("memberId") String memberId);
}
