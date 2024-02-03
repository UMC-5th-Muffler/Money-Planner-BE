package com.umc5th.muffler.domain.category.dto;

import com.umc5th.muffler.entity.constant.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class NewCategoryResponse {
    private Long categoryId;
    private Long priority;
    private Boolean isVisible;
    private CategoryType type;
}
