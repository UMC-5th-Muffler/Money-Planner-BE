package com.umc5th.muffler.domain.goal.repository;

import static java.sql.Statement.CLOSE_CURRENT_RESULT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JDBCGoalRepository {
    private static final int BATCH_SIZE = 500;
    @Getter
    @AllArgsConstructor
    public static class UpdateTotalCost {
        private Long dailyPlanId;
        private Long totalCost;

        public void addCost(Long cost) {
            this.totalCost += cost;
        }
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int saveTotalCosts(List<UpdateTotalCost> updateTotalCosts) {
        int i = 0, errorCount = 0;
        List<UpdateTotalCost> batchUpdateList = new ArrayList<>();

        for (UpdateTotalCost updateItem : updateTotalCosts ) {
            batchUpdateList.add(updateItem);
            i++;
            if (i == BATCH_SIZE) {
                errorCount += Arrays.stream(saveBatchTotalCosts(batchUpdateList))
                        .filter(result-> result != CLOSE_CURRENT_RESULT).sum();
                i = 0;
            }
        }
        if (i != 0)
            errorCount += Arrays.stream(saveBatchTotalCosts(batchUpdateList))
                    .filter(result -> result != CLOSE_CURRENT_RESULT).sum();
        return errorCount;
    }

    private int[] saveBatchTotalCosts(List<UpdateTotalCost> batchTotalCost) {
        String sql = "UPDATE daily_plan SET total_cost = :totalCost WHERE id = :dailyPlanId";
        return namedParameterJdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(batchTotalCost));
    }
}
