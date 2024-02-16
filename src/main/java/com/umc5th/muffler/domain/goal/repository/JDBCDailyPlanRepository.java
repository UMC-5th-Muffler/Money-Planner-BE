package com.umc5th.muffler.domain.goal.repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JDBCDailyPlanRepository {
    private static final int BATCH_SIZE = 500;
    @Getter
    @AllArgsConstructor
    public static class UpdateTotalCost {
        private Long dailyPlanId;
        private Long totalCost;
        private LocalDateTime now;

        public void addCost(Long cost) {
            this.totalCost += cost;
        }
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int saveTotalCosts(List<UpdateTotalCost> updateTotalCosts) {
        int errorCount = 0;
        List<UpdateTotalCost> batchUpdateList = new ArrayList<>();

        for (UpdateTotalCost updateItem : updateTotalCosts ) {
            batchUpdateList.add(updateItem);
            if (batchUpdateList.size() == BATCH_SIZE) {
                errorCount += getErrorRows(batchUpdateList, this::saveBatchTotalCosts);
                batchUpdateList.clear();
            }
        }
        if (batchUpdateList.size() != 0) {
            errorCount += getErrorRows(batchUpdateList, this::saveBatchTotalCosts);
        }
        return updateTotalCosts.size() - errorCount;
    }

    private <T> int getErrorRows(List<T> data, Function<List<T>, int[]> updateOneFunction) {
        return Arrays.stream(updateOneFunction.apply(data))
                .filter(result -> result != 1)
                .sum();
    }

    private int[] saveBatchTotalCosts(List<UpdateTotalCost> batchTotalCost) {
        String sql = "UPDATE daily_plan SET total_cost = :totalCost, is_zero_day = false, "
                + "last_modified_at = :now WHERE id = :dailyPlanId";
        return namedParameterJdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(batchTotalCost));
    }
}
