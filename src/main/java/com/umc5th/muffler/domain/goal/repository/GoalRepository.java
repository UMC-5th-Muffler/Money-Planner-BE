package com.umc5th.muffler.domain.goal.repository;

import com.umc5th.muffler.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByMemberId(String memberId);

    @Query("SELECT goal from Goal goal where :date BETWEEN goal.startDate and goal.endDate AND goal.member.id = :memberId")
    Optional<Goal> findByDateBetween(LocalDate date, String memberId);

    @Query("SELECT goal FROM Goal goal " +
            "LEFT JOIN FETCH goal.categoryGoals " +
            "WHERE :date BETWEEN goal.startDate AND goal.endDate " +
            "AND goal.member.id = :memberId")
    Optional<Goal> findByDateBetweenJoin(@Param("date")LocalDate date, @Param("memberId")String memberId);

}
