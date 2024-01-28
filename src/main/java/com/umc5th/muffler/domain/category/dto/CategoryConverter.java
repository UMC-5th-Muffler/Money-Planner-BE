package com.umc5th.muffler.domain.category.dto;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.constant.CategoryType;
import com.umc5th.muffler.entity.constant.Status;

public class CategoryConverter {
    public static Category toEntity(NewCategoryRequest dto) {
        return Category.builder()
                .icon(dto.getIcon())
                .name(dto.getCategoryName())
                .priority(dto.getPriority())
                .type(CategoryType.CUSTOM)
                .status(Status.ACTIVE)
                .build();
    }
    public static Category toEntity(Category original, UpdateCategoryRequest request) {
        return Category.builder()
                .id(original.getId())
                .name(request.getName())
                .icon(request.getIcon())
                .isVisible(request.getIsVisible())
                .priority(request.getPriority())
                .type(original.getType())
                .status(Status.ACTIVE)
                .member(original.getMember())
                .build();
    }

    public static CategoryDTO toCategoryDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .icon(category.getIcon())
                .name(category.getName())
                .isVisible(category.getIsVisible())
                .orderNumber(category.getPriority())
                .type(category.getType())
                .build();
    }
}
