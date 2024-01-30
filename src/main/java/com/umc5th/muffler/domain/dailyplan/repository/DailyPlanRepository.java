package com.umc5th.muffler.domain.dailyplan.repository;

import com.umc5th.muffler.entity.DailyPlan;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyPlanRepository extends JpaRepository<DailyPlan, Long>, DailyPlanRepositoryCustom {
    List<DailyPlan> findByGoalIdAndDateBetween(Long goalId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT dp FROM DailyPlan dp WHERE dp.goal.member.id = :memberId AND dp.date = :date")
    Optional<DailyPlan> findByMemberIdAndDate(String memberId, LocalDate date);
}
