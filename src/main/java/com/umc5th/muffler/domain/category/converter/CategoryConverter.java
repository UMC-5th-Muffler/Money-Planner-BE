package com.umc5th.muffler.domain.category.converter;

import com.umc5th.muffler.domain.category.dto.NewCategoryRequest;
import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.constant.Status;

public class CategoryConverter {
    public static Category toEntity(NewCategoryRequest dto) {
        return Category.builder()
                .icon(dto.getIcon())
                .name(dto.getCategoryName())
                .status(Status.ACTIVE)
                .build();
    }
}
