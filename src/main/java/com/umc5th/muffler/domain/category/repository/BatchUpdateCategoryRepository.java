package com.umc5th.muffler.domain.category.repository;

import static java.sql.Statement.EXECUTE_FAILED;
import static java.sql.Statement.SUCCESS_NO_INFO;

import com.umc5th.muffler.domain.category.dto.CategoryPriorityVisibilityDTO;
import com.umc5th.muffler.domain.category.dto.DefaultCategoryDTO;
import com.umc5th.muffler.global.response.code.ErrorCode;
import com.umc5th.muffler.global.response.exception.ExpenseException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BatchUpdateCategoryRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DefaultCategoryLoader defaultCategoryLoader;

    public int insertDefaultCategories(String memberId) {
        List<DefaultCategoryDTO> defaultCategoryDTOS = defaultCategoryLoader.getDefaultCategoryDTOS(memberId);
        return batchInsertCategories(defaultCategoryDTOS);
    }

    @Modifying(clearAutomatically = true)
    private int batchInsertCategories(List<DefaultCategoryDTO> defaultCategoryDTOS) {
        String sql = "INSERT INTO category(name, icon, priority, status, is_visible, type, member_id) "
                + "values (:name, :icon, :priority, :status, :isVisible, :type, :memberId) ";
        int failNum = Arrays.stream(
                        namedParameterJdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(defaultCategoryDTOS)))
                .filter(result -> result != SUCCESS_NO_INFO)
                .sum();
        if (failNum != 0)
            throw new ExpenseException(ErrorCode.CATEGORY_BATCH_INSERT_FAIL);
        return defaultCategoryDTOS.size();
    }

    @Modifying(clearAutomatically = true)
    public int batchUpdatePriorityAndVisibility(List<CategoryPriorityVisibilityDTO> dtos) {
        String sql = "UPDATE category SET priority = :priority, is_visible = :isVisible WHERE id = :categoryId";
        int failNum = Arrays.stream(namedParameterJdbcTemplate.batchUpdate(sql, SqlParameterSourceUtils.createBatch(dtos)))
                .filter(result -> result == EXECUTE_FAILED)
                .sum();
        if (failNum != 0)
            throw new ExpenseException(ErrorCode.CATEGORY_BATCH_INSERT_FAIL);
        return dtos.size();
    }
}
