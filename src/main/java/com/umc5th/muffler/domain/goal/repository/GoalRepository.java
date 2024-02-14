package com.umc5th.muffler.domain.goal.repository;

import com.umc5th.muffler.entity.Goal;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GoalRepository extends JpaRepository<Goal, Long>, GoalRepositoryCustom {
    @Query("SELECT goal from Goal goal where :date BETWEEN goal.startDate and goal.endDate AND goal.member.id = :memberId")
    Optional<Goal> findByDateBetween(@Param("date")LocalDate date, @Param("memberId")String memberId);

    @Query("SELECT goal from Goal goal join fetch goal.dailyPlans where :date BETWEEN goal.startDate and goal.endDate AND goal.member.id = :memberId")
    Optional<Goal> findByDateBetweenAndDailyPlans(LocalDate date, String memberId);
}
