package com.umc5th.muffler.domain.routine.repository;

import com.umc5th.muffler.entity.WeeklyRoutineExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeeklyRoutineRepository extends JpaRepository<WeeklyRoutineExpense, Long> {
}
