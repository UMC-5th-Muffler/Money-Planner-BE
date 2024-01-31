package com.umc5th.muffler.domain.goal.repository;

import com.umc5th.muffler.entity.Goal;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GoalRepository extends JpaRepository<Goal, Long>, GoalRepositoryCustom {

    @Query("SELECT g FROM Goal g JOIN FETCH g.dailyPlans WHERE g.member.id = :memberId AND NOT(g.startDate <= :today AND g.endDate >= :today)")
    Slice<Goal> findByMemberIdAndDailyPlans(String memberId, Pageable pageable, LocalDate today);

    @Query("SELECT goal from Goal goal where :date BETWEEN goal.startDate and goal.endDate AND goal.member.id = :memberId")
    Optional<Goal> findByDateBetween(@Param("date")LocalDate date, @Param("memberId")String memberId);

    @Query("SELECT goal from Goal goal join fetch goal.dailyPlans where :date BETWEEN goal.startDate and goal.endDate AND goal.member.id = :memberId")
    Optional<Goal> findByDateBetweenAndDailyPlans(LocalDate date, String memberId);
}
