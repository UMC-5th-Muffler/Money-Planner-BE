package com.umc5th.muffler.domain.category.dto;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.constant.CategoryType;
import com.umc5th.muffler.entity.constant.Status;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryConverter {
    public static Category toEntity(NewCategoryRequest dto, Long priority) {
        return Category.builder()
                .icon(dto.getIcon())
                .name(dto.getName())
                .priority(priority)
                .type(CategoryType.CUSTOM)
                .status(Status.ACTIVE)
                .build();
    }

    public static CategoryDTO toCategoryDTO(Category category) {
        return CategoryDTO.builder()
                .categoryId(category.getId())
                .icon(category.getIcon())
                .name(category.getName())
                .isVisible(category.getIsVisible())
                .priority(category.getPriority())
                .type(category.getType())
                .build();
    }

    public static List<OutlineCategoryDTO> toCategoryDtos(List<Category> categories) {
        return categories.stream()
                .map(category -> {
                    return new OutlineCategoryDTO(category.getId(), category.getName());
                })
                .collect(Collectors.toList());
    }
}
