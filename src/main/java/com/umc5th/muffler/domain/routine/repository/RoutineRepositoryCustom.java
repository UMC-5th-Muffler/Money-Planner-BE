package com.umc5th.muffler.domain.routine.repository;

import com.umc5th.muffler.domain.routine.dto.InsertableRoutine;
import java.time.LocalDate;
import java.util.List;

public interface RoutineRepositoryCustom {
    List<InsertableRoutine> findInsertableRoutines(LocalDate today);
}
