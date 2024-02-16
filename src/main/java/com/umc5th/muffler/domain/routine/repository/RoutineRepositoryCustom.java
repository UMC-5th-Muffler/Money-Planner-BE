package com.umc5th.muffler.domain.routine.repository;

import com.umc5th.muffler.entity.Routine;
import com.umc5th.muffler.entity.WeeklyRepeatDay;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Map;

public interface RoutineRepositoryCustom {

    Slice<Routine> findRoutinesWithCategory(String memberId, Long routineId, Pageable pageable);

    Map<Long, List<WeeklyRepeatDay>> findWeeklyRepeatDays(List<Long> weeklyRoutineIds);
}
