package com.umc5th.muffler.domain.goal.repository;

import com.umc5th.muffler.entity.DailyPlan;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyPlanRepository extends JpaRepository<DailyPlan, Long> {
    @Query("SELECT dailyPlan FROM DailyPlan dailyPlan "
            + "WHERE dailyPlan.date = :date "
            + "AND dailyPlan.goal.member.id = :memberId")
    Optional<DailyPlan> findDailyPlanByDateAndMemberId(@Param("date") LocalDate date, @Param("memberId") String memberId);
}
