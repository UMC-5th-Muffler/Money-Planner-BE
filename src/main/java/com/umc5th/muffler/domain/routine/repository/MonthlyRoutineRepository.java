package com.umc5th.muffler.domain.routine.repository;

import com.umc5th.muffler.entity.MonthlyRoutineExpense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyRoutineRepository extends JpaRepository<MonthlyRoutineExpense, Long> {
}
