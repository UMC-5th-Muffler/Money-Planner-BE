package com.umc5th.muffler.domain.goal.repository;

import com.umc5th.muffler.entity.Goal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GoalRepository extends JpaRepository<Goal, Long>, GoalRepositoryCustom {
    List<Goal> findByMemberId(String memberId);

    @Query("SELECT goal from Goal goal where :date BETWEEN goal.startDate and goal.endDate AND goal.member.id = :memberId")
    Optional<Goal> findByDateBetween(LocalDate date, String memberId);
}
