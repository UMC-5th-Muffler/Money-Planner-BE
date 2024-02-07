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

    // TODO : dailyPlan에서 GOAL을 타고 Member를 조회해야하는 문제...
    @Query("SELECT dp FROM DailyPlan dp WHERE dp.goal.member.id = :memberId AND dp.date = :date")
    Optional<DailyPlan> findByMemberIdAndDate(String memberId, LocalDate date);

    @Query("SELECT dailyPlan FROM DailyPlan dailyPlan "
            + "JOIN FETCH dailyPlan.goal "
            + "WHERE dailyPlan.date = :date "
            + "AND dailyPlan.goal.member.id = :memberId")
    Optional<DailyPlan> findDailyPlanWithGoalByDateAndMember(String memberId, LocalDate date);
}
