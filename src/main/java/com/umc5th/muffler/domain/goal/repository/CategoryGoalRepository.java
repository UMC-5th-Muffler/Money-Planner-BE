package com.umc5th.muffler.domain.goal.repository;

import com.umc5th.muffler.entity.CategoryGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryGoalRepository extends JpaRepository<CategoryGoal, Long> {

}
