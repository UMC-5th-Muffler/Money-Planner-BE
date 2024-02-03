package com.umc5th.muffler.domain.expense.repository;

import static java.sql.Statement.SUCCESS_NO_INFO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

        private LocalDateTime createAt;
        private LocalDateTime updateAt;
    }

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int saveAllExpense(List<InsertExpenseEntity> insertExpenses) {
        List<InsertExpenseEntity> batchExpenses = new ArrayList<>();
        int i = 0, savedRows = 0;

        for (InsertExpenseEntity expense : insertExpenses) {
            batchExpenses.add(expense);
            i++;
            if (i == BATCH_SIZE) {
                int[] results = saveBatch(batchExpenses);
                savedRows += Arrays.stream(results).filter(result -> result < 0).sum();
                batchExpenses.clear();
                i = 0;
            }
        }
        // batch_size 만큼 넣고 남은 나머지를 저장한다.
        if (i != 0)
            savedRows += Arrays.stream(saveBatch(batchExpenses))
                    .filter(result -> result != SUCCESS_NO_INFO).sum();
        return savedRows;
    }


    private int[] saveBatch(List<InsertExpenseEntity> expenses) {
        String sql = "INSERT INTO expense (title, memo, cost, member_id, category_id, date) "
                + " VALUES (:title, :memo, :cost, :memberId, :categoryId, :date)";
        return this.namedParameterJdbcTemplate.batchUpdate(sql,
                SqlParameterSourceUtils.createBatch(expenses));
    }
}
