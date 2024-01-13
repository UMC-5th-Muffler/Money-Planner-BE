package com.umc5th.muffler.domain.routine.repository;

import com.umc5th.muffler.entity.WeeklyRoutineExpense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyRoutineRepository extends JpaRepository<WeeklyRoutineExpense, Long> {
}
