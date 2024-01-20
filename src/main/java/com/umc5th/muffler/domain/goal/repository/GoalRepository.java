package com.umc5th.muffler.domain.goal.repository;

import com.umc5th.muffler.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByMemberId(Long memberId);

    @Query("SELECT goal from Goal goal where :date BETWEEN goal.startDate and goal.endDate AND goal.member.id = :memberId")
    Optional<Goal> findByDateBetween(@Param("date") LocalDate date, @Param("memberId") Long memberId);
}
