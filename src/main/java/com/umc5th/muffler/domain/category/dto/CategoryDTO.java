package com.umc5th.muffler.domain.category.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.umc5th.muffler.entity.constant.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class CategoryDTO {
    private Long categoryId;
    private String name;
    private String icon;
    private Long priority;
    private Boolean isVisible;
    private CategoryType type;
}
