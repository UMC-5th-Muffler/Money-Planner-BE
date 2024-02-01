package com.umc5th.muffler.domain.category.dto;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.constant.Status;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryConverter {
    public static Category toEntity(NewCategoryRequest dto, Long count) {
        return Category.builder()
                .icon(dto.getIcon())
                .name(dto.getCategoryName())
                .status(Status.ACTIVE)
                .priority(count)
                .build();
    }

    public static List<CategoryDto> toCategoryDtos(List<Category> categories) {
        return categories.stream()
                .map(category -> {
                    return new CategoryDto(category.getId(), category.getName());
                })
                .collect(Collectors.toList());
    }
}
