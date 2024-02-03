package com.umc5th.muffler.domain.routine.repository;

import com.umc5th.muffler.domain.routine.dto.InsertableRoutine;
import com.umc5th.muffler.entity.constant.RoutineType;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JDBCRoutineRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<InsertableRoutine> findInsertableRoutines(LocalDate today) {
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        int day = today.getDayOfMonth();

        String sql = "SELECT r.id, r.type, r.title, r.memo, r.cost, r.start_date, r.end_date, "
                + "r.monthly_repeat_day, r.weekly_term, wrd.day_of_week, "
                + "c.id, m.id, dp.id, dp.total_cost "
                + "FROM routine r "
                + "JOIN member m ON m.id = r.member_id "
                + "JOIN category c ON r.category_id = c.id "
                + "JOIN goal g ON m.id = g.member_id "
                + "JOIN daily_plan dp ON dp.goal_id = g.id "
                + "LEFT JOIN weekly_repeat_day wrd ON wrd.routine_id = r.id "
                + "WHERE :today BETWEEN g.start_date and g.end_date "
                + "AND dp.date = :today "
                + "AND ((r.type = 'MONTHLY' AND r.monthly_repeat_day = :day) OR "
                    + "(r.type = 'WEEKLY' AND  wrd.day_of_week = :dayOfWeek)) ";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("today", today)
                .addValue("day", day)
                .addValue("dayOfWeek", dayOfWeek);

        return namedParameterJdbcTemplate.query(sql, param, insertableRoutineRowMapper())
                .stream().filter((routine) -> {
                    if (today.isBefore(routine.getRoutineStartDate()))
                        return false;
                    if (routine.getRoutineEndDate() != null && today.isAfter(routine.getRoutineEndDate()))
                        return false;
                    if (routine.getRoutineType() == RoutineType.WEEKLY) {
                        long between = ChronoUnit.WEEKS.between(routine.getRoutineStartDate(), today);
                        return between % routine.getRoutineWeeklyTerm() == 0;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    private RowMapper<InsertableRoutine> insertableRoutineRowMapper() {
        return ((rs, rowNum) -> {
            String endDateString = rs.getString("r.end_date");

            return InsertableRoutine.builder()
                    .routineId(rs.getLong("r.id"))
                    .routineTitle("r.title")
                    .routineCost(rs.getLong("r.cost"))
                    .routineMemo(rs.getString("r.memo"))
                    .routineType(RoutineType.valueOf(rs.getString("r.type")))
                    .routineStartDate(LocalDate.parse(rs.getString("r.start_date")))
                    .routineEndDate((endDateString != null) ? LocalDate.parse(endDateString) : null)
                    .routineDayOfMonth(rs.getInt("r.monthly_repeat_day"))
                    .routineWeeklyTerm(rs.getInt("r.weekly_term"))
                    .routineDayOfWeek(DayOfWeek.of(rs.getInt("wrd.day_of_week") + 1))
                    .memberId(rs.getString("m.id"))
                    .categoryId(rs.getLong("c.id"))
                    .dailyPlanId(rs.getLong("dp.id"))
                    .dailyPlanTotalCost(rs.getLong("dp.total_cost"))
                    .build();
        });
    }
}
