package com.umc5th.muffler.domain.goal.repository;

import com.umc5th.muffler.entity.Goal;
import java.time.YearMonth;
import java.util.List;

public interface GoalRepositoryCustom {
    List<Goal> findGoalsByYearMonth(String memberId, YearMonth yearMonth);
}
