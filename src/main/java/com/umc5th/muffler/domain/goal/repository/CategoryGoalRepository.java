package com.umc5th.muffler.domain.goal.repository;

import com.umc5th.muffler.entity.CategoryGoal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryGoalRepository extends JpaRepository<CategoryGoal, Long> {
    Optional<CategoryGoal> findByGoalIdAndCategoryId(Long goalId, Long categoryId);
}
