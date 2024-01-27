package com.umc5th.muffler.domain.goal.repository;

import com.umc5th.muffler.entity.CategoryGoal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryGoalRepository extends JpaRepository<CategoryGoal, Long> {
    @Query("SELECT cg FROM CategoryGoal cg WHERE cg.goal.id = :goalId AND cg.category.id = :categoryId")
    Optional<CategoryGoal> findCategoryGoalWithGoalIdAndCategoryId(@Param("goalId") Long goalId, @Param("categoryId") Long categoryId);
}
