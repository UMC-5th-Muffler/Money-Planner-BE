package com.umc5th.muffler.domain.goal.repository;

import com.umc5th.muffler.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
}
