package com.umc5th.muffler.domain.category.dto;

import com.umc5th.muffler.domain.category.repository.dto.OutlinedCategoryProjection;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.Member;
import com.umc5th.muffler.entity.constant.CategoryType;
import com.umc5th.muffler.entity.constant.Status;

public class CategoryConverter {
    public static Category toEntity(NewCategoryRequest dto, Long priority, Member member) {
        return Category.builder()
                .icon(dto.getIcon())
                .name(dto.getName())
                .priority(priority)
                .type(CategoryType.CUSTOM)
                .status(Status.ACTIVE)
                .member(member)
                .build();
    }

    public static CategoryDTO toFullCategoryDTO(Category category) {
        return CategoryDTO.builder()
                .categoryId(category.getId())
                .icon(category.getIcon())
                .name(category.getName())
                .isVisible(category.getIsVisible())
                .priority(category.getPriority())
                .type(category.getType())
                .build();
    }
    public static CategoryDTO toOutlineCategoryDTO(OutlinedCategoryProjection projection) {
        return CategoryDTO.builder()
                .categoryId(projection.getId())
                .icon(projection.getIcon())
                .name(projection.getName())
                .build();
    }

    public static NewCategoryResponse toDTO(Category category) {
        return NewCategoryResponse.builder()
                .categoryId(category.getId())
                .build();
    }

}
