package com.umc5th.muffler.domain.routine.repository;

import com.umc5th.muffler.entity.Routine;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface RoutineRepositoryCustom {

    Slice<Routine> findRoutinesWithWeeklyDetails(String memberId, Long routineId, Pageable pageabl);
}
