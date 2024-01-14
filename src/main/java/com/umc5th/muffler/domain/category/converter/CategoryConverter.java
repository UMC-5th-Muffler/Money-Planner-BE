package com.umc5th.muffler.domain.category.converter;

import com.umc5th.muffler.domain.category.dto.NewCategoryRequest;
import com.umc5th.muffler.entity.Category;

public class CategoryConverter {
    public static Category toEntity(NewCategoryRequest dto) {
        return Category.builder()
                .icon(dto.getIcon())
                .name(dto.getCategoryName())
                .build();
    }
}
