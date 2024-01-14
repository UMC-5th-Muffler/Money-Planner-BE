package com.umc5th.muffler.fixture;

import com.umc5th.muffler.entity.Category;
import com.umc5th.muffler.entity.constant.Status;

public class CategoryFixture {
    public static final Category CATEGORY_ONE = Category.builder()
            .id(2L)
            .name("카테고리1")
            .icon("ICON")
            .status(Status.ACTIVE)
            .build();
    public static final Category CATEGORY_TWO = Category.builder()
            .id(3L)
            .name("카테고리2")
            .icon("ICON")
            .status(Status.ACTIVE)
            .build();
}
