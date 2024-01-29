package com.umc5th.muffler.domain.dailyplan.repository;

import com.umc5th.muffler.entity.DailyPlan;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyPlanRepository extends JpaRepository<DailyPlan, Long>, DailyPlanRepositoryCustom {
    Optional<DailyPlan> findByDate(LocalDate date);
    List<DailyPlan> findByGoalIdAndDateBetween(Long goalId, LocalDate startDate, LocalDate endDate);
}
