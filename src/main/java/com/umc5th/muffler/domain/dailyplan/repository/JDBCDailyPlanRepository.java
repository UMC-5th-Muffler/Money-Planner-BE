package com.umc5th.muffler.domain.dailyplan.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class JDBCDailyPlanRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    public void updateTotalCostForDailyPlans(String memberId, List<LocalDate> dates, Long costToAdd) {
        String sql = "UPDATE daily_plan dp JOIN goal g ON dp.goal_id = g.id " +
                "SET dp.total_cost = dp.total_cost + :costToAdd, dp.is_zero_day = false, " +
                "dp.last_modified_at = NOW() WHERE g.member_id = :memberId AND dp.date IN (:dates)";

        Map<String, Object> params = new HashMap<>();
        params.put("costToAdd", costToAdd);
        params.put("memberId", memberId);
        params.put("dates", dates);

        namedParameterJdbcTemplate.update(sql, params);
    }
}
