package com.umc5th.muffler.domain.expense.repository;

import static java.sql.Statement.EXECUTE_FAILED;
import static java.sql.Statement.SUCCESS_NO_INFO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JDBCExpenseRepository {
    private static final int BATCH_SIZE = 500;

    @Builder
    @Getter
    @AllArgsConstructor
    public static class InsertExpenseEntity {
        private String title;
        private String memo;
        private Long cost;
        private LocalDate date;

        private String memberId;
        private Long categoryId;

        private LocalDateTime now;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int saveAllExpense(List<InsertExpenseEntity> insertExpenses) {
        List<InsertExpenseEntity> batchExpenses = new ArrayList<>();
        int savedRows = 0;

        for (InsertExpenseEntity expense : insertExpenses) {
            batchExpenses.add(expense);
            if (batchExpenses.size() == BATCH_SIZE) {
                savedRows += getInsertRowCount(batchExpenses, this::saveBatch);
                batchExpenses.clear();
            }
        }
        // batch_size 만큼 넣고 남은 나머지를 저장한다.
        if (batchExpenses.size() != 0) {
            savedRows += getInsertRowCount(batchExpenses, this::saveBatch);
        }
        return savedRows;
    }

    private <T> int getInsertRowCount(List<T> data, Function<List<T>, int[]> batchInsertFunction) {
        return Arrays.stream(batchInsertFunction.apply(data))
                .filter(result -> result != EXECUTE_FAILED)
                .sum();
    }

    private int[] saveBatch(List<InsertExpenseEntity> expenses) {
        String sql = "INSERT INTO expense (title, memo, cost, member_id, category_id, date, created_at, last_modified_at) "
                + " VALUES (:title, :memo, :cost, :memberId, :categoryId, :date, :now ,:now)";
        return this.namedParameterJdbcTemplate.batchUpdate(sql,
                SqlParameterSourceUtils.createBatch(expenses));
    }
}
