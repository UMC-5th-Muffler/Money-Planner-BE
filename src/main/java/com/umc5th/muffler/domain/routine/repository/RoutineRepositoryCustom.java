package com.umc5th.muffler.domain.routine.repository;

import com.umc5th.muffler.domain.routine.dto.InsertableRoutine;
import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.entity.WeeklyRepeatDay;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Map;

public interface RoutineRepositoryCustom {
    List<InsertableRoutine> findInsertableRoutines(LocalDate today);

    Slice<Routine> findRoutinesWithCategory(String memberId, Long routineId, Pageable pageable);

    Map<Long, List<WeeklyRepeatDay>> findWeeklyRepeatDays(List<Long> weeklyRoutineIds);
}
